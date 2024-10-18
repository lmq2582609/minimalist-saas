package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.dept.DeptQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MDept;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 部门表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MDeptMapper extends BaseMapper<MDept> {

    /**
     * 根据部门ID查询直属下级数量
     * @param deptId 部门ID
     * @return 下级数量
     */
    default long selectChildrenCountByDeptId(Long deptId) {
        return selectCountByQuery(QueryWrapper.create().where("FIND_IN_SET("  + deptId +", ancestors)"));
    }

    /**
     * 根据部门ID删除部门
     * @param deptId 部门ID
     */
    default void deleteDeptByDeptId(Long deptId) {
        deleteByQuery(QueryWrapper.create()
                .eq(MDept::getDeptId, deptId));
    }

    /**
     * 根据部门ID获取部门(单条)
     * @param deptId 部门ID
     * @return 部门实体
     */
    default MDept selectDeptByDeptId(Long deptId) {
        return selectOneByQuery(QueryWrapper.create().eq(MDept::getDeptId, deptId));
    }

    /**
     * 根据部门ID获取部门(批量)
     * @param deptIds 部门ID列表
     * @return 部门实体列表
     */
    default List<MDept> selectDeptByDeptIds(List<Long> deptIds) {
        return selectListByQuery(QueryWrapper.create().in(MDept::getDeptId, deptIds));
    }

    /**
     * 根据部门ID修改部门
     * @param mDept 部门实体
     */
    default void updateDeptByDeptId(MDept mDept) {
        updateByQuery(mDept,
                QueryWrapper.create().eq(MDept::getDeptId, mDept.getDeptId()));
    }

    /**
     * 根据条件查询部门列表
     * @param queryVO 查询参数
     * @return 部门列表
     */
    default List<MDept> selectDeptList(DeptQueryVO queryVO) {
        return selectListByQuery(QueryWrapper.create()
                .eq(MDept::getStatus, queryVO.getStatus())
                .like(MDept::getDeptName, queryVO.getDeptName()));
    }

    /**
     * 根据租户ID查询部门列表 -> 字典查询
     * @return 部门列表
     */
    default List<MDept> selectDeptDict() {
        return selectListByQuery(QueryWrapper.create()
                .eq(MDept::getStatus, StatusEnum.STATUS_1.getCode()));
    }

    /**
     * 根据部门ID查询所有子集部门
     * @param deptId 部门ID
     * @return 子集部门列表
     */
    default List<MDept> selectChildrenDeptByDeptId(Long deptId) {
        return selectListByQuery(QueryWrapper.create().where("FIND_IN_SET("  + deptId +", ancestors)"));
    }
}
