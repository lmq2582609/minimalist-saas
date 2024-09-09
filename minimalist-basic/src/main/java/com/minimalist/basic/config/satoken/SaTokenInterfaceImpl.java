package com.minimalist.basic.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.minimalist.basic.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * sa-token，自定义权限加载接口实现类
 */
@Component
public class SaTokenInterfaceImpl implements StpInterface {

    @Autowired
    private UserManager userManager;

    /**
     * 返回指定账号id所拥有的权限码集合，loginId即登陆时传入的UserId
     * @param loginId 用户ID
     * @param loginType 账号体系标识
     * @return 权限编码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return userManager.getUserPermissions(Long.parseLong(loginId.toString()));
    }

    /**
     * 返回指定账号id所拥有的角色标识集合，loginId即登陆时传入的ID
     * @param loginId 用户ID
     * @param loginType 账号体系标识
     * @return 角色编码集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userManager.getUserRoles(Long.parseLong(loginId.toString()));
    }

}
