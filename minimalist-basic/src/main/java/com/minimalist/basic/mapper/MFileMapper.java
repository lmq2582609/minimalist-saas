package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.config.mybatis.QueryCondition;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.vo.file.FileQueryVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-18
 */
public interface MFileMapper extends BaseMapper<MFile> {

    /**
     * 根据fileUrl修改文件状态
     * @param userId 用户ID
     * @param status 状态
     * @param urlList 文件url列表
     * @return 受影响行数
     */
    default int updateStatusByFileUrl(Long userId, Byte status, List<String> urlList) {
        MFile file = new MFile();
        file.setStatus(status);
        file.setUpdateId(userId);
        file.setUpdateTime(LocalDateTime.now());
        return update(file, new LambdaUpdateWrapper<MFile>().in(MFile::getFileUrl, urlList));
    }

    /**
     * 查询文件列表(分页)
     * @param queryVO 查询条件
     * @return 文件分页数据
     */
    default Page<MFile> selectPageFileList(FileQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MFile>()
                        .eqNotNull(MFile::getStatus, queryVO.getStatus())
                        .likeNotNull(MFile::getFileName, queryVO.getFileName())
                        .orderByDescc(MFile::getCreateTime)
                );
    }

    /**
     * 根据文件ID查询文件
     * @param fileId 文件ID
     * @return 文件实体
     */
    default MFile selectFileByFileId(Long fileId) {
        return selectOne(new LambdaQueryWrapper<MFile>().eq(MFile::getFileId, fileId));
    }

    /**
     * 根据文件URL查询文件
     * @param urlList url列表
     * @return 文件列表
     */
    default List<MFile> selectFileByFileUrl(List<String> urlList) {
        return selectList(new LambdaQueryWrapper<MFile>().in(MFile::getFileUrl, urlList));
    }

}
