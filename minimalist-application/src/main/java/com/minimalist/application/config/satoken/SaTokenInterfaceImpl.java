package com.minimalist.application.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.basic.config.redis.RedisManager;
import com.minimalist.basic.utils.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

/**
 * sa-token，自定义权限加载接口实现类
 */
@Component
public class SaTokenInterfaceImpl implements StpInterface {

    @Autowired
    private RedisManager redisManager;

    /**
     * 返回指定账号id所拥有的权限码集合，loginId即登陆时传入的UserId
     * @param loginId 用户ID
     * @param loginType 账号体系标识
     * @return 权限编码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //从redis获取权限数据，在请求getUserInfo接口时会set权限数据
        Set<String> permCodes = redisManager.get(StrUtil.indexedFormat(RedisKeyConstant.USER_PERM_CACHE_KEY, Long.parseLong(loginId.toString())));
        return CollectionUtil.newArrayList(permCodes);
    }

    /**
     * 返回指定账号id所拥有的角色标识集合，loginId即登陆时传入的ID
     * @param loginId 用户ID
     * @param loginType 账号体系标识
     * @return 角色编码集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        //从redis获取角色数据，在请求getUserInfo接口时会set角色数据
        Set<String> roleCodes = redisManager.get(StrUtil.indexedFormat(RedisKeyConstant.USER_ROLE_CACHE_KEY, Long.parseLong(loginId.toString())));
        return CollectionUtil.newArrayList(roleCodes);
    }

}
