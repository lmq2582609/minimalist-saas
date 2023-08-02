package com.minimalist.basic.service;

import com.minimalist.basic.entity.mybatis.PageResp;
import com.minimalist.basic.entity.vo.file.*;
import java.util.List;

public interface FileService {

    /**
     * 单文件上传
     * @param fileUploadVO 文件及携带参数
     * @return 文件信息
     */
    FileUploadRespVO uploadFile(FileUploadVO fileUploadVO);

    /**
     * 上传文件
     * @param uploadBatchVO 文件及携带参数
     * @return 文件信息
     */
    List<FileUploadRespVO> uploadFileBatch(FileUploadBatchVO uploadBatchVO);

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    void deleteFile(Long fileId);

    /**
     * 查询文件列表(分页)
     * @param queryVO 查询条件
     * @return 文件分页数据
     */
    PageResp<FileVO> getPageFileList(FileQueryVO queryVO);

}
