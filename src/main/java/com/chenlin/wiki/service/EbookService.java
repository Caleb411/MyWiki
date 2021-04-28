package com.chenlin.wiki.service;

import com.chenlin.wiki.domain.*;
import com.chenlin.wiki.mapper.ContentMapper;
import com.chenlin.wiki.mapper.DocMapper;
import com.chenlin.wiki.mapper.EbookMapper;
import com.chenlin.wiki.req.EbookQueryReq;
import com.chenlin.wiki.req.EbookSaveReq;
import com.chenlin.wiki.resp.EbookQueryResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.util.CopyUtil;
import com.chenlin.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);
    
    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategoryId2())) {
            criteria.andCategory2IdEqualTo(req.getCategoryId2());
        }
        PageHelper.startPage(req.getPage(), req.getSize()); // 只对接下来的一次查询有效
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            ebook.setId(snowFlake.nextId());
            ebook.setDocCount(0);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);
        } else {
            // 更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
        DocExample docExample = new DocExample();
        DocExample.Criteria docExampleCriteria = docExample.createCriteria();
        docExampleCriteria.andEbookIdEqualTo(id);
        List<Doc> docs = docMapper.selectByExample(docExample);
        if (docs.size() != 0) {
            List<String> ids = new ArrayList<>();
            for (Doc doc: docs) {
                ids.add(String.valueOf(doc.getId()));
            }
            docExampleCriteria.andIdIn(ids);
            docMapper.deleteByExample(docExample);
            ContentExample contentExample = new ContentExample();
            ContentExample.Criteria contentExampleCriteria = contentExample.createCriteria();
            contentExampleCriteria.andIdIn(ids);
            contentMapper.deleteByExample(contentExample);
        }
    }
}
