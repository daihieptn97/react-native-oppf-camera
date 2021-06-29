package com.example.funsdkdemo.manager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.nio.ByteBuffer;

/**
 * @author hws
 * @class 录音
 * @time 2019-03-28 13:29
 */
public class RecordingManager {
    public static final int BIT_RATE = 128;
    private boolean threadExitFlag = false;
    private AudioRecord audioRecord = null;
    private OnRecordingListener listener;
    private ByteBuffer audioDataBuffer;
    private long startRecoringTime;
    private int recordMaxTime = Integer.MAX_VALUE;
    private RecordingTread recordingTread;
    private int bufferSizeInBytes = BIT_RATE;
    public RecordingManager(OnRecordingListener listener, int recordMaxTime) {
        this.listener = listener;
        this.recordMaxTime = recordMaxTime;
        audioDataBuffer = ByteBuffer.allocate(1024 * 1024 * recordMaxTime);
    }

    public boolean startRecording() {
        if (recordingTread == null) {
            recordingTread = new RecordingTread();
            return recordingTread.startRecording();
        }else {
            return false;
        }
    }

    public void stopRecording() {
        if (recordingTread != null) {
            recordingTread.stopRecording();
            recordingTread = null;
        }
    }

    class RecordingTread extends Thread {
        public synchronized boolean startRecording() {
            if (!isAlive()) {
                threadExitFlag = false;
                int minBufSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufSize);
                if (audioRecord == null || audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                    return false;
                } else {
                    if (bufferSizeInBytes == 0) {
                        bufferSizeInBytes = minBufSize;
                    }
                    startRecoringTime = System.currentTimeMillis();
                    super.start();
                    return true;
                }
            }else {
                return false;
            }
        }

        public synchronized void stopRecording() {
            threadExitFlag = true;
        }


        @Override
        public void run() {
            if (null == audioRecord) {
                return;
            }
            audioRecord.startRecording();
            byte[] audioData = new byte[bufferSizeInBytes];
            int readSize;
            while (!threadExitFlag) {
                int recordingTime = (int) ((System.currentTimeMillis() - startRecoringTime) / 1000);
                readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
                for(int i = 0;i < audioData.length;i++) {
                    audioData[i]= (byte) (audioData[i] * 3);
                }
                if (AudioRecord.ERROR_INVALID_OPERATION != readSize && readSize > 0) {
                    audioDataBuffer.put(audioData);
                    if (recordingTime > recordMaxTime) {
                        threadExitFlag = true;
                        break;
                    }
                    if (listener != null) {
                        listener.onRecording(recordingTime);
                    }
                } else {
                    SystemClock.sleep(5);
                }
            }
            if (audioRecord != null) {
                if (audioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                }
                audioRecord.release();
                audioRecord = null;
                if (listener != null) {
                    int dataLength = audioDataBuffer.position();
                    audioDataBuffer.flip();
                    listener.onComplete(audioDataBuffer,dataLength);
                }
            }
            recordingTread = null;
        }
    }

    public interface OnRecordingListener {
        void onRecording(int time);
        void onComplete(ByteBuffer audioData, int dataSize);
    }
}
