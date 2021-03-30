package com.chenlin.wiki.controller;

import com.chenlin.wiki.req.EbookQueryReq;
import com.chenlin.wiki.req.EbookSaveReq;
import com.chenlin.wiki.resp.CommonResp;
import com.chenlin.wiki.resp.EbookQueryResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp<PageResp<EbookQueryResp>> list(@Valid EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp<?> save(@RequestBody EbookSaveReq req) {  // 如果前端以json方式提交数据这里要加这个注解
        CommonResp<?> resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<?> delete(@PathVariable Long id) {
        CommonResp<?> resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
