package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minimalist.basic.entity.enums.DeptEnum;
import com.minimalist.basic.entity.po.MDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.dept.DeptQueryVO;
import com.minimalist.common.mybatis.QueryCondition;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MDeptMapper extends BaseMapper<MDept> {

    /**
     * 根据部门ID查询直属下级数量
     * @param deptId 部门ID
     * @return 下级数量
     */
    default long selectChildrenCountByDeptId(Long deptId) {
        LambdaQueryWrapper<MDept> pQuery = new LambdaQueryWrapper<>();
        pQuery.eq(MDept::getParentDeptId, deptId);
        return selectCount(pQuery);
    }

    /**
     * 根据部门ID删除部门
     * @param deptId 部门ID
     * @return 受影响行数
     */
    default int deleteDeptByDeptId(Long deptId) {
        LambdaQueryWrapper<MDept> pQuery = new LambdaQueryWrapper<>();
        pQuery.eq(MDept::getDeptId, deptId);
        return delete(pQuery);
    }

    /**
     * 根据部门ID获取部门(单条)
     * @param deptId 部门ID
     * @return 部门实体
     */
    default MDept selectDeptByDeptId(Long deptId) {
        return selectOne(new LambdaQueryWrapper<MDept>().eq(MDept::getDeptId, deptId));
    }

    /**
     * 根据部门ID获取部门(批量)
     * @param deptIds 部门ID列表
     * @return 部门实体列表
     */
    default List<MDept> selectDeptByDeptIds(List<Long> deptIds) {
        return selectList(new LambdaQueryWrapper<MDept>().in(MDept::getDeptId, deptIds));
    }

    /**
     * 根据部门ID修改部门
     * @param mDept 部门实体
     * @return 受影响行数
     */
    default int updateDeptByDeptId(MDept mDept) {
        return update(mDept, new LambdaUpdateWrapper<MDept>().eq(MDept::getDeptId, mDept.getDeptId()));
    }

    /**
     * 根据条件查询部门列表
     * @param queryVO 查询参数
     * @return 部门列表
     */
    default List<MDept> selectDeptList(DeptQueryVO queryVO) {
        return selectList(new QueryCondition<MDept>()
                .likeNotNull(MDept::getDeptName, queryVO.getDeptName())
                .eqNotNull(MDept::getStatus, queryVO.getStatus()));
    }

    /**
     * 根据租户ID查询部门列表 -> 字典查询
     * @return 部门列表
     */
    default List<MDept> selectDeptDict() {
        LambdaQueryWrapper<MDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MDept::getDeptId, MDept::getParentDeptId, MDept::getDeptName, MDept::getDeptSort);
        queryWrapper.eq(MDept::getStatus, DeptEnum.DeptStatus.DEPT_STATUS_1.getCode());
        return selectList(queryWrapper);
    }

}
