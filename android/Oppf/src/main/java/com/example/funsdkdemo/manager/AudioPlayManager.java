package com.example.funsdkdemo.manager;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import java.nio.ByteBuffer;

/**
 * @author hws
 * @class pcm音频数据播放
 * @time 2019-03-28 14:10
 */
public class AudioPlayManager {
    private boolean threadExitFlag = false;
    private AudioTrack audioTrack = null;
    private ByteBuffer audioDataBuffer;
    private int dataSize;
    private AudioPlayThread audioPlayThread;
    private OnAudioPlayListener listener;
    private int bufferSizeInBytes = RecordingManager.BIT_RATE;
    private boolean isPlaying;
    public AudioPlayManager(ByteBuffer audioDataBuffer, int dataSize, OnAudioPlayListener listener) {
        this.audioDataBuffer = ByteBuffer.allocate(audioDataBuffer.capacity());
        this.audioDataBuffer.put(audioDataBuffer.array());
        this.audioDataBuffer.flip();
        this.dataSize = dataSize;
        this.listener = listener;
    }

    public boolean startPlay() {
        if (isPlaying) {
            return false;
        }
        if (audioPlayThread == null) {
            audioPlayThread = new AudioPlayThread();
            return audioPlayThread.startPlay();
        }else {
            return false;
        }
    }

    public void stopPlay() {
        if (audioPlayThread != null) {
            audioPlayThread.stopPlay();
            audioPlayThread = null;
        }
    }
    class AudioPlayThread extends Thread {
        public synchronized boolean startPlay() {
            if (audioDataBuffer == null || dataSize <= 0) {
                return false;
            }
            try {
                threadExitFlag = false;
                int minBufSize = AudioTrack.getMinBufferSize(
                        8000,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        8000,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        minBufSize, AudioTrack.MODE_STREAM);
                if (audioTrack == null || audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
                    return false;
                } else {
                    if (bufferSizeInBytes == 0) {
                        bufferSizeInBytes = minBufSize;
                    }
                    float maxVol = AudioTrack.getMaxVolume();
                    audioTrack.setStereoVolume(maxVol,maxVol);
                    audioTrack.play();
                    isPlaying = true;
                    super.start();
                    return true;
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public synchronized void stopPlay() {
            threadExitFlag = true;
            isPlaying = false;
            if (audioTrack != null) {
                audioTrack.stop();
            }
        }


        @Override
        public void run() {
            if (null == audioTrack) {
                isPlaying = false;
                return;
            }
            try {
                byte[] audioData = new byte[bufferSizeInBytes];
                int writeSize = 0;
                long startPlayTimes = System.currentTimeMillis();
                while (!threadExitFlag && writeSize < dataSize) {
                    if (writeSize + bufferSizeInBytes < audioDataBuffer.capacity()) {
                        audioDataBuffer.get(audioData, 0, bufferSizeInBytes);
                        audioTrack.write(audioData, 0, bufferSizeInBytes);
                        writeSize += bufferSizeInBytes;
                    }else {
                        break;
                    }
                    if (listener != null) {
                        listener.onPlayTime((int) ((System.currentTimeMillis() - startPlayTimes) / 1000));
                    }
                }
                if (audioTrack != null) {
                    if (audioTrack.getState() == AudioRecord.RECORDSTATE_RECORDING) {
                        audioTrack.stop();
                    }
                    audioTrack.release();
                    audioTrack = null;
                }
                audioDataBuffer.clear();
                audioPlayThread = null;
                if (listener != null) {
                    listener.onPlayCompleted();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            isPlaying = false;
        }
    }

    public interface OnAudioPlayListener {
        void onPlayTime(int time);
        void onPlayCompleted();
    }
}
