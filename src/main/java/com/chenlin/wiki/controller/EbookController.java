package com.chenlin.wiki.controller;

import com.chenlin.wiki.domain.Ebook;
import com.chenlin.wiki.req.EbookReq;
import com.chenlin.wiki.resp.CommonResp;
import com.chenlin.wiki.resp.EbookResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp<PageResp<EbookResp>> list(EbookReq req) {
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
