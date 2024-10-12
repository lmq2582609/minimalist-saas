package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.minimalist.basic.entity.vo.user.*;
import com.minimalist.basic.service.UserService;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.tenant.TenantIgnore;
import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/basic/user")
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUser")
    @Operation(summary = "添加用户")
    public ResponseEntity<Void> addUser(@RequestBody @Validated(Add.class) UserVO userVO) {
        userService.addUser(userVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteUserByUserId")
    @Operation(summary = "删除用户 -> 根据用户ID删除")
    public ResponseEntity<Void> deleteUserByUserId(@RequestParam("userId")
                                            @NotNull(message = "用户ID不能为空")
                                            @Parameter(name = "userId", required = true, description = "用户ID") Long userId) {
        userService.deleteUserByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateUserByUserId")
    @Operation(summary = "修改用户")
    public ResponseEntity<Void> updateUserByUserId(@RequestBody @Validated(Update.class) UserVO userVO) {
        userService.updateUserByUserId(userVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageUserList")
    @Operation(summary = "查询用户(分页)")
    public ResponseEntity<PageResp<UserVO>> getPageUserList(UserQueryVO queryVO) {
        return ResponseEntity.ok(userService.getPageUserList(queryVO));
    }

    @GetMapping("/getUserByUserId/{userId}")
    @Operation(summary = "根据用户ID查询用户")
    public ResponseEntity<UserVO> getUserByUserId(@PathVariable(value = "userId")
                                                  @NotNull(message = "用户ID不能为空")
                                                  @Parameter(name = "userId", description = "用户ID", required = true) Long userId) {
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }

    @TenantIgnore
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息(登录后获取，含角色、权限、菜单、部门等)")
    public ResponseEntity<UserInfoVO> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo(StpUtil.getLoginIdAsLong()));
    }

    @SaIgnore
    @GetMapping("/getImageCaptcha")
    @Operation(summary = "获取图形验证码")
    public ResponseEntity<ImageCaptchaVO> getImageCaptcha() {
        return ResponseEntity.ok(userService.getImageCaptcha());
    }

    @SaIgnore
    @TenantIgnore
    @PostMapping("/login")
    @Operation(summary = "用户登录，返回token")
    public ResponseEntity<SaTokenInfo> login(@RequestBody @Valid UserLoginReqVO reqVO) {
        return ResponseEntity.ok(userService.userLogin(reqVO));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public ResponseEntity<Void> logout() {
        StpUtil.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid RePasswordVO passwordVO) {
        userService.resetPassword(passwordVO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateUserAvatar")
    @Operation(summary = "修改用户头像")
    public ResponseEntity<Void> updateUserAvatar(@RequestParam("userAvatar")
                                                 @NotBlank(message = "用户头像不能为空")
                                                 @Parameter(name = "userAvatar", description = "用户头像base64编码", required = true) String userAvatar) {
        userService.updateUserAvatar(userAvatar);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateUserInfo")
    @Operation(summary = "修改用户基本信息")
    public ResponseEntity<Void> updateUserInfo(@RequestBody @Valid UserSettingVO settingVO) {
        userService.updateUserInfo(settingVO);
        return ResponseEntity.ok().build();
    }

}
