package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.dept.DeptQueryVO;
import com.minimalist.basic.entity.vo.dept.DeptVO;
import java.util.List;

public interface DeptService {

    /**
     * 添加部门
     * @param deptVO 部门数据
     */
    void addDept(DeptVO deptVO);

    /**
     * 删除部门 -> 根据部门ID删除
     * @param deptId 部门ID
     */
    void deleteDeptByDeptId(Long deptId);

    /**
     * 修改部门 -> 根据部门ID修改
     * @param deptVO 部门数据
     */
    void updateDeptByDeptId(DeptVO deptVO);

    /**
     * 查询部门列表（全部不分页） -> 部门管理使用
     * @param queryVO 查询参数
     * @return 部门树
     */
    List<DeptVO> getDeptList(DeptQueryVO queryVO);

    /**
     * 根据部门ID查询部门
     * @param deptId 部门ID
     * @return 部门数据
     */
    DeptVO getDeptByDeptId(Long deptId);

    /**
     * 查询部门列表 -> 其他地方使用(只获取正常状态的部门)
     * @return 部门树
     */
    List<DeptVO> getEnableDeptList();

    /**
     * 根据用户ID查询用户所属部门
     * @param userId 用户ID
     * @return 用户所属部门列表
     */
    List<DeptVO> getDeptByUserId(Long userId);

}
