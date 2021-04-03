package com.chenlin.wiki.controller;

import com.chenlin.wiki.req.CategoryQueryReq;
import com.chenlin.wiki.req.CategorySaveReq;
import com.chenlin.wiki.resp.CommonResp;
import com.chenlin.wiki.resp.CategoryQueryResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp<PageResp<CategoryQueryResp>> list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp<?> save(@Valid @RequestBody CategorySaveReq req) {  // 如果前端以json方式提交数据这里要加这个注解
        CommonResp<?> resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<?> delete(@PathVariable Long id) {
        CommonResp<?> resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
