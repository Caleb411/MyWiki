package com.chenlin.wiki.controller;

import com.chenlin.wiki.req.UserLoginReq;
import com.chenlin.wiki.req.UserQueryReq;
import com.chenlin.wiki.req.UserResetPasswordReq;
import com.chenlin.wiki.req.UserSaveReq;
import com.chenlin.wiki.resp.CommonResp;
import com.chenlin.wiki.resp.UserLoginResp;
import com.chenlin.wiki.resp.UserQueryResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public CommonResp<PageResp<UserQueryResp>> list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp<?> save(@Valid @RequestBody UserSaveReq req) {  // 如果前端以json方式提交数据这里要加这个注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<?> resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp<?> resetPassword(@Valid @RequestBody UserResetPasswordReq req) {  // 如果前端以json方式提交数据这里要加这个注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<?> resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp<UserLoginResp> login(@Valid @RequestBody UserLoginReq req) {  // 如果前端以json方式提交数据这里要加这个注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);
        resp.setContent(userLoginResp);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<?> delete(@PathVariable Long id) {
        CommonResp<?> resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }
}
