package com.minimalist.basic.entity.enums;

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
        FILE_THUMBNAILS_UPLOAD_FAIL("文件缩略图上传失败，请重试"),
        FILE_DOWNLOAD_FAIL("文件下载失败，请重试"),
        FILE_MOVE_FAIL("文件移动失败，请重试"),
        ;
        private final String desc;
    }

    /** 文件来源 */
    @Getter
    @AllArgsConstructor
    public enum FileSource {
        NOTICE_COVER_IMG(1, "系统公告封面图片"),
        NOTICE_CONTENT_IMG(2, "系统公告内容图片"),
        ;
        private final Integer code;
        private final String desc;
    }

}
