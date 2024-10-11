package com.minimalist.common.module.entity.enums;

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

    /**
     * 文件来源 - 以 /结尾
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

}
