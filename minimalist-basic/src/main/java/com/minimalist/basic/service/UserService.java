package com.minimalist.basic.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.vo.user.*;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * 新增用户
     * @param userVO 用户实体
     */
    void addUser(UserVO userVO);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUserByUserId(Long userId);

    /**
     * 修改用户
     * @param userVO 用户数据
     */
    void updateUserByUserId(UserVO userVO);

    /**
     * 查询用户(分页)
     * @param queryVO 查询条件
     * @return 用户分页数据
     */
    PageResp<UserVO> getPageUserList(UserQueryVO queryVO);

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserByUserId(Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    MUser selectUserByUsername(String username);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户VO
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 获取图形验证码
     * @return 图形验证码
     */
    ImageCaptchaVO getImageCaptcha();

    /**
     * 校验图形验证码
     * @param captcha 验证码
     * @param captchaId 验证码ID
     * @return true通过校验  false未通过校验
     */
    boolean checkImageCaptcha(String captcha, String captchaId);

    /**
     * 用户登录
     * @param reqVO 用户登录信息
     * @return token
     */
    SaTokenInfo userLogin(UserLoginReqVO reqVO);

    /**
     * 重置密码
     * @param passwordVO 重置密码实体
     */
    void resetPassword(RePasswordVO passwordVO);

    /**
     * 用户设置 -> 修改用户信息
     * @param settingVO 用户信息
     */
    void updateUserInfo(UserSettingVO settingVO);

    /**
     * 修改用户头像
     * @param userAvatar 用户头像base64编码
     */
    void updateUserAvatar(String userAvatar);

}
