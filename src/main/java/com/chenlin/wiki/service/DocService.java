package com.chenlin.wiki.service;

import com.chenlin.wiki.domain.*;
import com.chenlin.wiki.exception.BusinessException;
import com.chenlin.wiki.exception.BusinessExceptionCode;
import com.chenlin.wiki.mapper.ContentMapper;
import com.chenlin.wiki.mapper.DocMapper;
import com.chenlin.wiki.mapper.DocMapperCust;
import com.chenlin.wiki.mapper.EbookMapper;
import com.chenlin.wiki.req.DocQueryReq;
import com.chenlin.wiki.req.DocSaveReq;
import com.chenlin.wiki.resp.DocQueryResp;
import com.chenlin.wiki.resp.PageResp;
import com.chenlin.wiki.util.CopyUtil;
import com.chenlin.wiki.util.RedisUtil;
import com.chenlin.wiki.util.RequestContext;
import com.chenlin.wiki.util.SnowFlake;
import com.chenlin.wiki.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    
    @Resource
    private DocMapper docMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WsService wsService;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andEbookIdEqualTo(ebookId);
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize()); // 只对接下来的一次查询有效
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content); // 包含大字段
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria docExampleCriteria = docExample.createCriteria();
        docExampleCriteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
        ContentExample contentExample = new ContentExample();
        ContentExample.Criteria contentExampleCriteria = contentExample.createCriteria();
        contentExampleCriteria.andIdIn(ids);
        contentMapper.deleteByExample(contentExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else{
            return content.getContent();
        }
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        Ebook ebook = ebookMapper.selectByPrimaryKey(docDb.getEbookId());

        // 调用异步化启动新线程时继续使用当前线程的日志流水号
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【" + ebook.getName() + "】中的【" + docDb.getName() + "】被点赞啦！", logId);
    }

    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
