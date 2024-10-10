package com.minimalist.common.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class FileEnum {

    /** 文件处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_FILE("文件不存在"),
        FILE_USED("文件已被使用"),
        FILE_UPLOAD_FAIL("文件上传失败，请重试"),
        FILE_DOWNLOAD_FAIL("文件下载失败，请重试"),
        ;
        private final String desc;
    }

    /** 存储类型 */
    @Getter
    @AllArgsConstructor
    public enum StorageType {
        LOCAL("local", "本地"),
        MINIO("minio", "MinIO"),
        ;
        private final String code;
        private final String desc;
    }

























    /** 文件状态 */
    @Getter
    @AllArgsConstructor
    public enum FileStatus {
        FILE_STATUS_0(0, "未使用"),
        FILE_STATUS_1(1, "已使用"),
        ;
        private final Integer code;
        private final String desc;
    }

    /**
     * 文件来源
     * code和存储路径
     */
    @Getter
    @AllArgsConstructor
    public enum FileSource {
        NOTICE_COVER_IMG(1, "notice/cover/", "公告封面图片"),
        NOTICE_CONTENT_IMG(2, "notice/content/", "公告内容图片"),
        ;
        private final Integer code;
        private final String path;
        private final String desc;

        /**
         * 根据文件来源获取存储路径
         * @param code 编码
         * @return 路径
         */
        public static String getPath(Integer code) {
            for (FileSource value : values()) {
                if (value.getCode().equals(code)) {
                    return value.getPath();
                }
            }
            //未获取到路径，返回公共目录
            return "common/";
        }
    }

    /** 文件管理平台 */
    @Getter
    @AllArgsConstructor
    public enum FilePlatform {
        LOCAL_1("local-1", "本地存储"),
        LOCAL_PLUS_1("local-plus-1", "本地存储升级版"),
        HUAWEI_OBJ_1("huawei-obs-1", "华为云OBS"),
        ALIYUN_OSS_1("aliyun-oss-1", "阿里云OSS"),
        QINIU_KIDO_1("qiniu-kodo-1", "七牛云KODO"),
        TENCENT_COS_1("tencent-cos-1", "腾讯云COS"),
        BAIDU_BOS_1("baidu-bos-1", "百度云BOS"),
        UPYUN_USS_1("upyun-uss-1", "又拍云USS"),
        MINIO_1("minio-1", "MINIO"),
        AWS_S3_1("aws-s3-1", "AWS S3"),
        FTP_1("ftp-1", "FTP"),
        SFTP_1("sftp-1", "SFTP"),
        WEBDAV_1("webdav-1", "WEBDAV"),
        GOOGLE_1("google-1", "谷歌云"),
        ;
        private final String code;
        private final String desc;
    }

}
