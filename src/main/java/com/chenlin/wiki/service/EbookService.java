package com.chenlin.wiki.service;

import com.chenlin.wiki.domain.Ebook;
import com.chenlin.wiki.domain.EbookExample;
import com.chenlin.wiki.mapper.EbookMapper;
import com.chenlin.wiki.req.EbookReq;
import com.chenlin.wiki.resp.EbookResp;
import com.chenlin.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);

        return list;
    }
}
