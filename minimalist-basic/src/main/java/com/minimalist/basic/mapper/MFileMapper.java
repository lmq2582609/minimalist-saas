package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.vo.file.FileQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MFile;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MFileMapper extends BaseMapper<MFile> {
    /**
     * 根据fileUrl修改文件状态
     * @param userId 用户ID
     * @param status 状态
     * @param urlList 文件url列表
     */
    default void updateStatusByFileUrl(Long userId, List<String> urlList, Integer status) {
        MFile file = new MFile();
        file.setStatus(status);
        file.setUpdateId(userId);
        file.setUpdateTime(LocalDateTime.now());
        updateByQuery(file, QueryWrapper.create().in(MFile::getFileUrl, urlList));
    }

    /**
     * 查询文件列表(分页)
     * @param queryVO 查询条件
     * @return 文件分页数据
     */
    default Page<MFile> selectPageFileList(FileQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MFile::getStatus, queryVO.getStatus())
                        .eq(MFile::getFileSource, queryVO.getFileSource())
                        .like(MFile::getFileName, queryVO.getFileName())
                        .orderBy(MFile::getCreateTime, false));
    }

    /**
     * 根据文件ID查询文件
     * @param fileId 文件ID
     * @return 文件实体
     */
    default MFile selectFileByFileId(Long fileId) {
        return selectOneByQuery(QueryWrapper.create().eq(MFile::getFileId, fileId));
    }

    /**
     * 根据文件UID查询文件
     * @param fileIdList 文件ID列表
     * @return 文件列表
     */
    default List<MFile> selectFileByFileIds(List<Long> fileIdList) {
        return selectListByQuery(QueryWrapper.create().in(MFile::getFileId, fileIdList));
    }

    /**
     * 根据文件ID修改文件状态
     * @param fileIdList 文件ID列表
     * @param fileStatus 文件状态
     */
    default void updateFileStatusByFileIds(Long userId, List<Long> fileIdList, Integer fileStatus) {
        MFile file = new MFile();
        file.setStatus(fileStatus);
        file.setUpdateId(userId);
        file.setUpdateTime(LocalDateTime.now());
        updateByQuery(file, QueryWrapper.create().in(MFile::getFileId, fileIdList));
    }

}
