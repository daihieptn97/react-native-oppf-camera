package com.manager.db;

/**
 * Created by hws on 2018-05-25.
 */

public class Define {
    public static final int MEDIA_TYPE_DEVICE = 0;

    public static final int MEDIA_TYPE_CLOUD = 1;

    public static final int MEDIA_TYPE_CLOUD_ALARM_MSG = 2;

    public static final int ON_DOWNLOAD_IN_DOWNLOAD_LIST = -1;

    public static final int LOGIN_NONE = 0;

    public static final int LOGIN_BY_INTERNET = 1;

    public static final int LOGIN_BY_LOCAL = 2;

    public static final int LOGIN_BY_AP = 3;

    public static final int LOGIN_BY_TEST = 4;

    public static final int LOGIN_BY_WECHAT = 5;

    public static final int LOGIN_BY_ACCOUNT = 6;

    public static final int LOGIN_BY_FACEBOOK = 7;

    public static final int LOGIN_BY_GOOGLE = 8;

    public static final int LOGIN_BY_CODE = 9;//通过验证码方式登录

    public static final int DOWNLOAD_VIDEO_BY_FILE = 0;//按文件下载

    public static final int DOWNLOAD_VIDEO_BY_CLOUD = 1;//按云视频下载

    public static final int DOWNLOAD_VIDEO_BY_TIME = 2;//按时间下载

    public static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";

    public static final int SERVER_PORT = 15010;

    public static final String IS_FISH = "is_fish";

    public static final String IS_FISH_SW_360 = "is_fish_sw_360";

    public static final String IS_FISH_SW_180 = "is_fish_sw_180";

    public static final String IS_FISH_HW = "is_fish_hw";

    public static final String IS_DISTORTION_CAMERA = "is_distortion_cm";

    public static final float WND_SCALE_1_1 = 1;// 宽 高比例

    public static final int ALERT_lINE_TYPE = 0;

    public static final int ALERT_AREA_TYPE = 1;

    public static final int STOLEN_GOODS_TYPE = 2;

    public static final int GOODS_RETENTION_TYPE = 3;
	
	public static final long SHARE_CODE_VALID_TIME = 1800;//分享消息码有效时间 单位 秒
	 
    public static final int CONNECT_TYPE_P2P = 0;

    public static final int CONNECT_TYPE_TRANSMIT = 1;

    public static final int CONNECT_TYPE_IP = 2;

    public static final int CONNECT_TYPE_RPS = 5;

    public static final int CONNECT_TYPE_RTS_P2P = 6;

    public static final int CONNECT_TYPE_RTS = 7;

    public static final int UPGRADE_TYPE_NONE = 0;//本地选择文件更新

    public static final int UPGRADE_TYPE_CLOUD = 1;//设备连接升级服务器升级

    public static final int UPGRADE_TYPE_FILE_DOWNLOAD = 2;//本地升级,但升级文件还没有下载下来

    public static final int UPGRADE_TYPE_LOCAL_FILE = 3;//本地升级,升级文件已经下载下来了

    public static final int MEDIA_DATA_TYPE_DISPLAY = 0;//解码显示

    public static final int MEDIA_DATA_TYPE_YUV_NOT_DISPLAY = 1;//解码后YUV数据 不显示

    public static final int MEDIA_DATA_TYPE_NOT_DECODE = 2;//解码前的数据

    public static final int FRAME_TYPE_UNKNOWN = 0;

    public static final int FFRAME_TYPE_VIDEO = 1;

    public static final int FFRAME_TYPE_AUDIO = 2;

    public static final int FFRAME_TYPE_DATA = 3;

    public static final int FRAME_TYPE_VIDEO_I_FRAME = 0;

    public static final int FRAME_TYPE_VIDEO_P_FRAME = 1;

    public static final int FRAME_TYPE_VIDEO_B_FRAME = 2;

    public static final int FRAME_TYPE_VIDEO_S_FRAME = 3;

    public static final int FRAME_TYPE_DATA_TEXT = 5;

    public static final int FRAME_TYPE_DATA_INTL = 6;
}
