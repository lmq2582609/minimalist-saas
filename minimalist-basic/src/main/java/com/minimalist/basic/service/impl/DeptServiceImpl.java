package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.DeptEnum;
import com.minimalist.basic.entity.po.MDept;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.entity.vo.dept.DeptQueryVO;
import com.minimalist.basic.entity.vo.dept.DeptVO;
import com.minimalist.basic.mapper.MDeptMapper;
import com.minimalist.basic.mapper.MUserDeptMapper;
import com.minimalist.basic.service.DeptService;
import com.minimalist.basic.entity.constant.CommonConstant;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private MDeptMapper deptMapper;

    @Autowired
    private MUserDeptMapper userDeptMapper;

    /**
     * 添加部门
     * @param deptVO 部门数据
     */
    @Override
    public void addDept(DeptVO deptVO) {
        MDept mDept = BeanUtil.copyProperties(deptVO, MDept.class);
        mDept.setDeptId(UnqIdUtil.uniqueId());
        //新增
        int insertCount = deptMapper.insert(mDept);
        Assert.isTrue(insertCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 删除部门 -> 根据部门ID删除
     * @param deptId 部门ID
     */
    @Override
    public void deleteDeptByDeptId(Long deptId) {
        //检查是否包含下级，包含下级不允许删除
        long childrenCount = deptMapper.selectChildrenCountByDeptId(deptId);
        Assert.isFalse(childrenCount > 0, () -> new BusinessException(DeptEnum.ErrorMsg.CONTAIN_CHILDREN.getDesc()));
        int deleteCount = deptMapper.deleteDeptByDeptId(deptId);
        Assert.isTrue(deleteCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 修改部门 -> 根据部门ID修改
     * @param deptVO 部门数据
     */
    @Override
    public void updateDeptByDeptId(DeptVO deptVO) {
        //查询部门
        MDept mDept = deptMapper.selectDeptByDeptId(deptVO.getDeptId());
        Assert.notNull(mDept, () -> new BusinessException(DeptEnum.ErrorMsg.NONENTITY_DEPT.getDesc()));
        MDept newDept = BeanUtil.copyProperties(deptVO, mDept.getClass());
        //乐观锁字段赋值
        newDept.updateBeforeSetVersion(mDept.getVersion());
        //修改
        int updateCount = deptMapper.updateDeptByDeptId(newDept);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 查询部门列表（全部不分页） -> 部门管理使用
     * @param queryVO 查询参数
     * @return 部门树
     */
    @Override
    public List<DeptVO> getDeptList(DeptQueryVO queryVO) {
        return deptToTree(deptMapper.selectDeptList(queryVO));
    }

    /**
     * 根据部门ID查询部门
     * @param deptId 部门ID
     * @return 部门数据
     */
    @Override
    public DeptVO getDeptByDeptId(Long deptId) {
        return BeanUtil.copyProperties(deptMapper.selectDeptByDeptId(deptId), DeptVO.class);
    }

    /**
     * 查询部门列表 -> 其他地方使用(只获取正常状态的部门)
     * @return 部门树
     */
    @Override
    public List<DeptVO> getEnableDeptList() {
        //部门树
        return deptToTree(deptMapper.selectDeptDict());
    }

    /**
     * 根据部门ID查询部门
     * @param deptIds 部门ID列表
     * @return 用户所属部门列表
     */
    @Override
    public List<DeptVO> getDeptByDeptIds(List<Long> deptIds) {
        List<MDept> deptList = deptMapper.selectDeptByDeptIds(deptIds);
        return BeanUtil.copyToList(deptList, DeptVO.class);
    }

    /**
     * 转换部门树
     * @param deptList 部门数据集合
     * @return 部门数据集合
     */
    private List<DeptVO> deptToTree(List<MDept> deptList) {
        if (CollectionUtil.isEmpty(deptList)) { return CollectionUtil.list(false); }
        //查找顶级节点
        List<DeptVO> rootNodeList = deptList.stream()
                .filter(p -> ObjectUtil.isNotNull(p.getParentDeptId()))
                .filter(p -> CommonConstant.ZERO == p.getParentDeptId())
                .map(p -> BeanUtil.copyProperties(p, DeptVO.class))
                .sorted(Comparator.comparing(DeptVO::getDeptSort))
                .toList();
        //查找子节点
        findChildren(deptList, rootNodeList);
        return rootNodeList;
    }

    /**
     * 查找子节点
     * @param deptList 部门数据集合
     * @param rootNodeList 部门顶级节点数据集合
     */
    private void findChildren(List<MDept> deptList, List<DeptVO> rootNodeList) {
        //遍历顶级节点
        rootNodeList.forEach(node -> {
            //存储子节点
            List<DeptVO> childrenNodes = CollectionUtil.list(false);
            //从部门数据集合中查找子节点
            deptList.forEach(p -> {
                //节点是否关联
                if (node.getDeptId().equals(p.getParentDeptId())) {
                    childrenNodes.add(BeanUtil.copyProperties(p, DeptVO.class));
                }
                //递归
                findChildren(deptList, childrenNodes);
                //菜单显示排序
                childrenNodes.sort(Comparator.comparing(DeptVO::getDeptSort));
            });
            //将查询到的子节点挂在顶级节点上
            if (CollectionUtil.isNotEmpty(childrenNodes)) {
                node.setChildren(childrenNodes);
            }
        });
    }

}
