package com.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.lib.sdk.bean.DayLightTimeBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XUtils {
    private static long lastClickTime;
    private static final int ONE_TB = 1024 * 1024 * 1024;
    private static final int ONE_GB = 1024 * 1024;
    private static final int ONE_MB = 1024;

    private static final String MATCH_URL_PATTERN = "^(https?:\\/\\/){0}([\\da-z|_\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
    private static final String MATCH_IP_PATTERN = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final String MATCH_SN_PATTERN = "^[A-Fa-f0-9]{16}$";
    public static final String MATCH_USERNAME_PATTERN = "^[A-Fa-f0-9_-]{3,20}$";
    private static final String MATCH_CODE_PATTERN = "^[0-9]{27}$";
    private static final String MATCH_EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)"
            + "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    private static final String MATCH_CORRECT_PWD_PATTERN = "(?![^a-zA-Z0-9]+$)(?![^a-zA-Z/D]+$)(?![^0-9/D]+$).{8,32}$";
    private static final String MATCH_CORRECT_DEV_PWD_PATTERN = "^[A-Za-z0-9]{1,15}$";
    private static final String MATCH_NUMBER_PATTERN = "[0-9]*";
    private static final String RANDOM_UUID = "random_uuid";

    /**
     * 判断字符串是否不为空
     *
     * @param s
     * @return
     */
    // && !"null".equals(s)
    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s) && !"undefined".equals(s) && s.trim().length() > 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return !notEmpty(s);
    }

    /**
     * 判断字符串是否为数字
     *
     * @param s
     * @return
     */
    public static boolean isInteger(String s) {
        try {
            Long.parseLong(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        if (context == null)
            return null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unkown";
        }
    }

    /**
     * 获取应用名称
     */
    public static String getAppName(Context context) {
        if (context == null)
            return null;
        PackageManager manager = context.getPackageManager();
        try {
            ApplicationInfo info = manager.getApplicationInfo(context.getPackageName(), 0);
            String appName = manager.getApplicationLabel(info).toString();
            return appName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Unkown";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的包名
     */
    public static String getPackageName(Context context) {
        if (context == null)
            return null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.packageName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unkown";
        }
    }

    public static boolean isMainProcessExist(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if(appProcess.processName.equalsIgnoreCase(getPackageName(context))) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否为主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if(appProcess.pid == pid) {
                if(appProcess.processName.equalsIgnoreCase(getPackageName(context))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPushProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if(appProcess.pid == pid) {
                if(appProcess.processName.equalsIgnoreCase(getPackageName(context) + ":push")) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断是否为主进程
     * @param context
     * @return
     */
    public static boolean isProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if(appProcess.pid == pid) {
                if(appProcess.processName.equalsIgnoreCase(getPackageName(context))) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return email == null ? false : isPatternMatches(email, MATCH_EMAIL_PATTERN);
    }

    /**
     * 判断手机格式是否正确
     *
     * @param phone：手机号
     * @return
     */
    public static boolean isPhoneNum(String phone) {
        // 简单处理，只要位数大于7的就算是正确了
        return phone != null && phone.length() > 6 && isPatternMatches(phone, MATCH_NUMBER_PATTERN);
    }

    /**
     * 判断是否为序列号
     *
     * @param sn
     * @return
     */
    public static boolean isSn(String sn) {
        return sn == null ? false : isPatternMatches(sn, MATCH_SN_PATTERN);
    }

    /**
     * 判断是否为设备验证码
     *
     * @param code
     * @return
     */
    public static boolean isDeviceCode(String code) {
        return code == null ? false : isPatternMatches(code, MATCH_CODE_PATTERN);
    }

    /**
     * 判断IP格式是否正确
     *
     * @param ip
     * @return
     */
    public static boolean isIP(String ip) {
        return ip == null ? false : isPatternMatches(ip, MATCH_IP_PATTERN);
    }

    /**
     * 判断URL格式是否正确
     *
     * @param url
     * @return
     */
    public static boolean isURL(String url) {
        return url == null ? false : isPatternMatches(url, MATCH_URL_PATTERN);
    }

    /**
     * 判断是否符合格式
     *
     * @param
     * @return
     */
    public static boolean isPatternMatches(CharSequence val, String match) {
        if (null == val) {
            return false;
        }

        Pattern p = Pattern.compile(match);
        Matcher m = p.matcher(val);
        if (m.matches())
            return true;
        else
            return false;
    }

    /**
     * 判断是否为快速双击
     *
     * @param
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获得mac地址
     *
     * @param
     * @return
     */
    public static String getMacAddress() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return null;
        }

        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            Log.i("test", "Mac:" + Mac + " Mac.length: " + Mac.length());

            if (Mac.length() > 1) {
                Mac = Mac.replaceAll(" ", "");
                result = "";
                String[] tmp = Mac.split(":");
                for (int i = 0; i < tmp.length; ++i) {
                    result += tmp[i];
                }
            }
            Log.i("test", result + " result.length: " + result.length());
        }
        return result;
    }

    /**
     * 执行命令cmd，只取结果中含有filter的这一行
     *
     * @param
     * @return
     */
    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                // result += line;
                Log.i("test", "line: " + line);
            }

            result = line;
            Log.i("test", "result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断后台服务是否正在运行
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 后去后台运行的actvitiy名
     *
     * @return
     */
    public static String getBackClassName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> task_info = manager.getRunningTasks(20);
        String className = "";
        for (int i = 0; i < task_info.size(); i++) {
            if ("com.mobile.myeye".equals(task_info.get(i).topActivity.getPackageName())) {
                className = task_info.get(i).topActivity.getClassName();
            }
        }
        return className;
    }

    /**
     * 执行命令cmd，只取结果中含有filter的这一行
     *
     * @param
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    /**
     * 处理适配某个高、宽大小的图片
     *
     * @param filePath
     * @param height
     * @param width
     * @return
     */
    public static Bitmap dealBitmapAdaptationSize(String filePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null)
            return null;
        Bitmap newBtimap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        if (!bitmap.equals(newBtimap)) {
            bitmap.recycle();
            bitmap = null;
        }
        return newBtimap;
    }

    /**
     * 获得屏幕像素
     *
     * @param
     * @return
     */
    public static final int sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String getMediaPath(Context context) {
        String path = "";
        File dirFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dirFile = Environment.getExternalStorageDirectory();
            if (dirFile == null || !dirFile.canWrite() || !dirFile.canRead()) {
                dirFile = context.getExternalFilesDir(null);
            }
        } else {
            String exStorageState = Environment.getExternalStorageState();
            if (exStorageState == null || exStorageState.equals(Environment.MEDIA_MOUNTED)
                    || exStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                dirFile = Environment.getExternalStorageDirectory();
            } else {
                dirFile = context.getExternalFilesDir(null);
            }
        }

        if (dirFile == null) {
            dirFile = context.getFilesDir();
        } else {
            path = dirFile.getAbsolutePath();
        }

        return path == null ? "" : path;
    }

    //私有目录
    public static String getAndroidPath(Context context) {
        String path = "";
        File dirFile = null;
        dirFile = context.getExternalFilesDir(null);
        if (dirFile == null) {
            dirFile = context.getFilesDir();
            path = dirFile.getAbsolutePath();
        } else {
            path = dirFile.getAbsolutePath();
        }

        return path == null ? "" : path;
    }


    /**
     * 将输入的时间转换为hh:mm:ss的格式
     *
     * @param time 单位：s
     * @return
     */
    public static String getUsedTime(int time) {
        String result = "";
        int hour = time / (60 * 60);
        if (hour > 0) {
            if (hour < 10) {
                result += "0";
            }
            result += time / (60 * 60) + ":";
        } else {
            result += "00:";
        }
        time = time % (60 * 60);
        int minute = time / 60;
        if (minute > 0) {
            if (minute < 10) {
                result += "0";
            }
            result += minute + ":";
        } else {
            result += "00:";
        }
        time = time % 60;
        if (time > 0) {
            if (time < 10) {
                result += "0";
            }
            result += time;
        } else {
            result += "00";
        }
        return result;
    }

    /**
     * 单位是分钟
     *
     * @param time
     * @return
     */
    public static int[] getUsedTimeV2(int time) {
        int times[] = new int[3];
        if (time >= 60) {
            times[1] = time / 60;
            times[2] = time % 60;
            if (times[1] > 0) {
                times[0] = times[1] / 24;
                times[1] = times[1] % 24;
            }
        } else {
            times[0] = 0;
            times[1] = 0;
            times[2] = time;
        }
        return times;
    }

    public static String date2Str(String formater, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(date);
    }

    public static Date str2Date(String date, String formater) {
        if (notEmpty(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat(formater);
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        } else {
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            if (width > height) {

                inSampleSize = Math.round((float) height / (float) reqHeight);

            } else {

                inSampleSize = Math.round((float) width / (float) reqWidth);

            }

        }

        return inSampleSize;

    }

    /**
     * 把bitmap变成灰色
     *
     * @param bitmap
     * @return
     */
    public static final Bitmap greyBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

    public static String getFileSize(int size) {
        String result = "";
        if (size > ONE_TB) {
            result = (size / ONE_TB) + "T";
        } else if (size > ONE_GB) {
            result = (size / ONE_GB) + "G";
        } else if (size > ONE_MB) {
            result = (size / ONE_MB) + "M";
        } else {
            if (size <= 0) {
                result = "";
            } else {
                result = size + "K";
            }
        }
        return result;
    }

    public static String getFileSizeByMB(int size) {
        if (size > 0) {
            if (size > 1024 * 1024) {
                return String.format("%.2f", (size / (1024.00 * 1024.00))) + "T";
            } else if (size > 1024) {
                return String.format("%.2f", (size / 1024.00)) + "G";
            } else {
                return size + "M";
            }
        }
        return "0M";
    }

    /**
     * 获取码流值
     *
     * @param size
     * @return
     */
    public static String getStreamSize(int size) {
        double stream = size * 8.0;
        if (stream < 1024) {
            return ((int) stream) + "b/s";
        } else if (stream < 1024 * 1024) {
            return String.format("%.1f", (stream / 1024)) + "Kb/s";
        } else if (stream < 1024 * 1024 * 50) {
            return String.format("%.1f", (stream / 1024.0 / 1024.0)) + "Mb/s";
        }
        return "";
    }

    /**
     * 为一位的数字前补0
     *
     * @param from
     * @return
     */
    public static String addZero(int from) {
        String str = String.valueOf(from);
        return str.length() > 1 ? str : "0" + str;
    }

    /**
     * 根据strId字符串 拼凑出视频文件名
     */
    public static String getVideoFileName(String strId) {
        String beginTimeStr = GetString(strId, "st_3_beginTime", "st_4_endTime");
        beginTimeStr = GetString(beginTimeStr, "[", "]");

        String beginStr[] = beginTimeStr.split(",");
        StringBuffer sb = new StringBuffer();
        sb.append("s-");
        String str;
        String splitStr;
        for (int i = 0; i < beginStr.length; i++) {
            str = beginStr[i];
            // System.out.println(str+""+str.split("=")[1]);
            if (str.contains("wday") || str.contains("isdst"))
                continue;
            splitStr = str.split("=")[1];
            if (Integer.valueOf(splitStr) < 10)
                splitStr = "0" + splitStr;
            sb.append(splitStr);
        }
        System.out.println(sb.toString());

        String endTimeStr = strId.substring(strId.indexOf("st_4_endTime"));
        System.out.println(endTimeStr);
        endTimeStr = GetString(endTimeStr, "[", "],");
        String endStr[] = endTimeStr.split(",");
        sb.append("_e-");
        for (int k = 0; k < endStr.length; k++) {
            str = endStr[k];
            if (str.contains("hour") || str.contains("minute") || str.contains("second")) {
                splitStr = str.split("=")[1];
                if (Integer.valueOf(splitStr) < 10)
                    splitStr = "0" + splitStr;
                sb.append(splitStr);
            }
        }
        sb.append(".mp4");
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static String GetString(String strId, String start, String end) {
        if (strId != null && strId.length() >= strId.indexOf(start) && strId.length() >= strId.indexOf(end))
            return strId.substring(strId.indexOf(start), strId.indexOf(end));
        return null;
    }

    /**
     * 判断此文件是否存在
     *
     * @param file 包含全路径的文件名
     * @return 存在返回文件大小，否则返回0
     */
    public static long isFileExists(String file) {
        File f = new File(file);
        if (f.exists()) {
            return f.length();
        }
        return 0;
    }

    /**
     * 初始化ssid，将ssid的双引号去掉
     *
     * @param ssid
     * @return
     */
    public static String initSSID(String ssid) {
        if (notEmpty(ssid) && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * @param capabilities
     * @return
     */
    public static int getEncrypPasswordType(String capabilities) {
        if (capabilities.contains("WPA2") && capabilities.contains("CCMP")) {
            // sEncrypType = "AES";
            // sAuth = "WPA2";
            return 1;
        } else if (capabilities.contains("WPA2") && capabilities.contains("TKIP")) {
            // sEncrypType = "TKIP";
            // sAuth = "WPA2";
            return 2;
        } else if (capabilities.contains("WPA") && capabilities.contains("TKIP")) {
            // EncrypType = "TKIP";
            // sAuth = "WPA";
            return 2;
        } else if (capabilities.contains("WPA") && capabilities.contains("CCMP")) {
            // sEncrypType = "AES";
            // sAuth = "WPA";
            return 1;
        } else if (capabilities.contains("WEP")) {
            return 3;
        } else {
            // sEncrypType = "NONE";
            // sAuth = "OPEN";
            return 0;
        }
    }

    /**
     * Ascii码值转成字符串
     *
     * @param value
     * @return
     */
    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        int pos = 0;
        int length = value.length();
        String str = null;
        do {
            str = value.substring(pos, pos + 2);
            sbu.append((char) Integer.parseInt(str, 16));
            pos += 2;
        } while (pos <= (length - 2));
        return sbu.toString();
    }

    public static boolean isTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            String _className = tasksInfo.get(0).topActivity.getClassName();
            if (context.getClass().getName().equals(_className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTopActivity(Context context,String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            String _className = tasksInfo.get(0).topActivity.getClassName();
            if (_className.equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取一个定义范围内的随机数
     *
     * @param start 随机数的开始范围
     * @param end   随机数的结束范围
     * @return
     */
    public static int getRamdom(int start, int end) {
        Random r = new Random();
        int result = r.nextInt(end) % (end - start + 1) + start;
        return result;
    }

    /**
     * 获取当前手机的系统语言国家
     *
     * @return 国家，如zh-cn，或cn等
     */
    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * 获取当前wifi连接状态
     *
     * @param context
     * @return
     */
    public static boolean getMobileConnectStatus(Context context) {
        NetworkInfo mMobile = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null)
            mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobile != null)
            return mMobile.isConnectedOrConnecting();
        else
            return false;
    }

    /**
     * 计算字符串byte数
     *
     * @param
     * @return
     */
    public static int strCountByByte(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    /**
     * 是否为中文
     *
     * @param
     * @return
     */
    public static boolean isZh() {
        String language = getLanguageEnv();

        if (language != null && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }

    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    /**
     * 数字转成二进制后某一位是0还是1
     *
     * @param binary 字符串
     * @param where  位数
     * @return
     */
    public static boolean isBinaryValueOne(int binary, int where) {
        String binaryStr = Integer.toBinaryString(binary);
        Log.e("www", "char--->" + binaryStr);
        if (binaryStr.length() < where + 1) {
            return false;
        }
        char ch = binaryStr.charAt(where);
        Log.e("www", "char--->" + ch);
        return ch == '1';
    }

    public static boolean isLandScape(Activity mActivity) {
        return null != mActivity
                && mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortrait(Activity mActivity) {
        return null != mActivity
                && mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * sn是否一致
     *
     * @param
     * @return
     */
    public static boolean isSameSN(String strOptDevID, String sn) {
        return (strOptDevID.equals(sn) || strOptDevID == sn) ? true : false;
    }

    /**
     * 获取APP版本
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        if (context == null)
            return 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取手机的UUID
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getMobileUUID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = telephonyManager.getDeviceId();
        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        String uuid = "";
        String uuid1 = "";
        if (deviceId != null) {
            uuid1 = deviceId;
        }

        if (!"9774d56d682e549c".equals(androidId)) {
            uuid = androidId;
        }

        return uuid + uuid1;
    }

    /**
     * 获取推送的Token
     *
     * @param context
     * @return
     */
    public static String getPushToken(Context context) {
        String uuid = null;
        try {
            uuid = getMobileUUID(context);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (uuid == null) {
            return null;
        }
        System.out.println("uuid" + uuid);
        String packageName = getPackageName(context);
        String token = uuid.replace("[", "").replace("]", "").replace("android-", "")
                + packageName.replace("com.mobile.", "");
        System.out.println("token" + token);
        return token.trim();
    }

    /**
     * 合法的密码
     *
     * @param pwd 密码
     * @return 是否合法
     */
    public static boolean isCorrectPassword(String pwd) {
        Pattern pattern = Pattern.compile(MATCH_CORRECT_PWD_PATTERN);
        Matcher isOk = pattern.matcher(pwd);
        if (isOk.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 合法的设备密码
     *
     * @param pwd 密码
     * @return 是否合法
     */
    public static boolean isCorrectDevPassword(String pwd) {
        Pattern pattern = Pattern.compile(MATCH_CORRECT_DEV_PWD_PATTERN);
        Matcher isOk = pattern.matcher(pwd);
        if (isOk.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证两次输入密码是否一致
     *
     * @param
     * @return
     */
    public static boolean contrast(String str1, String str2) {
        if (str1 == null && str2 != null)
            return false;
        else if (str1 != null && str2 == null)
            return false;
        else if (str1 == null && str2 == null)
            return true;
        else if (str1.equals(str2))
            return true;
        else
            return false;
    }

    /**
     * 是不是手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            mobiles = mobiles.replace(" ", "");
            mobiles = mobiles.replace("+86", "");
            Pattern p = Pattern.compile("[1,2,3,4,5,6,7,8,9]\\d{10}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static int convertDpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static int[] getButtonPixels(View view) {
        if (null == view)
            return null;
        int[] pixels = new int[view.getWidth() * view.getHeight()];
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        if (null == bitmap)
            return null;
        bitmap.getPixels(pixels, 0, view.getWidth(), 0, 0, view.getWidth(), view.getHeight());
        view.setDrawingCacheEnabled(false);
        return pixels;
    }

    public static byte[] getPixelsToDevice(View view) {
        int n = 0, m = 0;
        int[] pixels = XUtils.getButtonPixels(view);
        int w = view.getWidth();
        int h = view.getHeight();
        int bytesPerPixel = 1;
        int bytesPerRow = bytesPerPixel * w;
        byte[] pRet = new byte[w * h / 8];
        if (null == pixels)
            return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int offset = (int) (bytesPerRow * i) + (int) (bytesPerPixel * j);
                if (pixels[offset] != -1) {
                    pRet[m] |= (0x1 << (7 - n));
                    sb.append("*");
                } else {
                    sb.append(" ");
                }
                n++;
                if (n == 8) {
                    m++;
                    n = 0;
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
        return pRet;
    }

    /**
     * @param mask 掩码值
     * @return 解析后为1的位置 {@link ArrayList<Integer>}
     */
    public static ArrayList<Integer> getParseMask(int mask) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int index = 0;
        do {
            if ((mask & 0x01) == 1) {
                result.add(index++);
            }
        } while ((mask = mask >> 1) != 0);
        return result;
    }

    public static void alertIsWIFIConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mWifi.isConnectedOrConnecting()) {
            Toast.makeText(context, R.string.Consumption_Warning, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("NewApi")
    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int getScreenWidth(Activity activity) {
        if (null == activity)
            return 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (null != dm) {
            return dm.widthPixels;
        } else {
            return 0;
        }
    }

    public static int getScreenHeight(Activity activity) {
        if (null == activity)
            return 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (null != dm) {
            return dm.heightPixels + getNavigationBarHeight(activity);
        } else {
            return 0;
        }
    }

    public static int getScreenHeightNoStateBar(Activity activity) {
        if (null == activity)
            return 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (null != dm) {
            return dm.heightPixels;
        } else {
            return 0;
        }
    }

    /**
     * 0  TYPE_NO_PASSWORD,1,TYPE_WPA,2,TYPE_WEB
     *
     * @param capabilities
     * @return
     */
    public static final int getCapabilities(String capabilities) {
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return 1;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return 2;
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 判断手机是否安装某个应用
     *
     * @param context
     * @param appPackageName 应用包名
     * @return true：安装，false：未安装
     */
    public static boolean isApplicationAvilible(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static DayLightTimeBean getDayLightTimeInfo(TimeZone tz) {
        DayLightTimeBean dltInfo = new DayLightTimeBean();
        dltInfo.useDLT = tz.useDaylightTime();
        if (dltInfo.useDLT) {
            try {
                Date date = new Date();
                SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
                sdfYear.setTimeZone(tz);

                SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
                sdfDay.setTimeZone(tz);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                sdf.setTimeZone(tz);

                // 年份就取当年
                dltInfo.year = Integer.parseInt(sdfYear.format(date));

                // 先找到起始月份
                for ( int month = 1; month <= 12; month ++ ) {
                    String tmpStr = String.format("%04d-%02d-%02d", dltInfo.year, month, 1);

                    if ( dltInfo.beginMonth == 0 && tz.inDaylightTime(sdfDay.parse(tmpStr)) ) {
                        dltInfo.beginMonth = month;
                    }

                    if ( dltInfo.beginMonth > 0 && dltInfo.endMonth == 0 && !tz.inDaylightTime(sdfDay.parse(tmpStr)) ) {
                        // 这个只是可能的结束月份,实际的结束月份很可能是上个月
                        dltInfo.endMonth = month;
                    }

                    if (dltInfo.beginMonth > 0 && dltInfo.endMonth > 0 && tz.inDaylightTime(sdfDay.parse(tmpStr))) {
                        dltInfo.beginMonth = month;
                        break;
                    }
                }

                // 起始天肯定在起始月份当中的某一天,简单查找,就认为每个月都有31天吧
                if (dltInfo.beginMonth > 1) {
                    dltInfo.beginDay = 1;
                    for (int day = 1; day <= 31; day++) {
                        String tmpStr = String.format("%04d-%02d-%02d", dltInfo.year, dltInfo.beginMonth - 1, day);
                        if (tz.inDaylightTime(sdfDay.parse(tmpStr))) {
                            dltInfo.beginDay = day;
                            dltInfo.beginMonth = dltInfo.beginMonth - 1;
                            break;
                        }
                    }
                }

                // 结束的天,应该是上述结束月份中的1号,或者是之前一个月的某一天
                if ( dltInfo.endMonth > 1 ) {
                    dltInfo.endDay = 1;
                    // 上个月中查找是否存在结束日期,如果不存在就是之前找到的月份的1号,简单查找,就认为每个月都有31天吧
                    for ( int day = 1; day <= 31; day ++ ) {
                        int year = dltInfo.beginMonth > dltInfo.endMonth ? dltInfo.year + 1 : dltInfo.year;
                        String tmpStr = String.format("%04d-%02d-%02d", year, dltInfo.endMonth - 1, day);
                        if ( !tz.inDaylightTime(sdfDay.parse(tmpStr)) ) {
                            dltInfo.endMonth = dltInfo.endMonth - 1;
                            dltInfo.endDay = day;
                            break;
                        }
                    }
                }

                // 时/分暂不处理,暂定00:00

            } catch (Exception e) {

            }
        }
        return dltInfo;
    }
}
