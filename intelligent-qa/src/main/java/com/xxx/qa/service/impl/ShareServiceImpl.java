package com.xxx.qa.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.common.exception.BizException;
import com.xxx.qa.client.LazyCraftClient;
import com.xxx.qa.dto.ShareCreateDTO;
import com.xxx.qa.dto.query.ShareQueryDTO;
import com.xxx.qa.entity.QaShare;
import com.xxx.qa.mapper.QaShareMapper;
import com.xxx.qa.service.ShareService;
import com.xxx.qa.service.TokenService;
import com.xxx.qa.vo.MessageVO;
import com.xxx.qa.vo.ShareDetailVO;
import com.xxx.qa.vo.ShareVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShareServiceImpl extends ServiceImpl<QaShareMapper, QaShare>
        implements ShareService {

    private static final Logger log = LoggerFactory.getLogger(ShareServiceImpl.class);

    @Resource
    private TokenService tokenService;

    @Resource
    private LazyCraftClient lazyCraftClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createShare(ShareCreateDTO dto, Long fromUserId) {
        QaShare share = new QaShare();
        share.setSessionId(dto.getSessionId());
        share.setSessionTitle(dto.getSessionTitle());
        share.setFromUserId(fromUserId.toString());
        share.setToUserId(dto.getToUserId());
        share.setCreator(fromUserId.toString());
        share.setCreateTime(LocalDateTime.now());
        save(share);
        return share.getId();
    }

    @Override
    public IPage<ShareVO> listSentShares(ShareQueryDTO query, Long userId) {
        Page<ShareVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        return baseMapper.pageSent(page, userId.toString());
    }

    @Override
    public IPage<ShareVO> listReceivedShares(ShareQueryDTO query, Long userId) {
        Page<ShareVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        return baseMapper.pageReceived(page, userId.toString());
    }

    @Override
    public ShareDetailVO getShareDetail(Long shareId, Long userId) {
        QaShare share = getById(shareId);
        if (share == null || share.getDeleted() == 1) {
            throw new BizException("分享记录不存在");
        }
        String userIdStr = userId.toString();
        if (!userIdStr.equals(share.getFromUserId()) && !userIdStr.equals(share.getToUserId())) {
            throw new BizException("无权查看该分享");
        }

        // 从分享者的 token 获取历史（分享者发起，所以用 fromUserId 的 token）
        String tempToken = tokenService.getOrInitToken(share.getFromUserId());
        List<MessageVO> messages = parseHistory(lazyCraftClient.getHistory(tempToken, share.getSessionId()));

        ShareVO shareVO = new ShareVO();
        shareVO.setId(share.getId());
        shareVO.setSessionId(share.getSessionId());
        shareVO.setSessionTitle(share.getSessionTitle());
        shareVO.setFromUserId(share.getFromUserId());
        shareVO.setToUserId(share.getToUserId());
        shareVO.setCreateTime(share.getCreateTime());

        ShareDetailVO detail = new ShareDetailVO();
        detail.setShare(shareVO);
        detail.setMessages(messages);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShare(Long shareId, Long userId) {
        QaShare share = getById(shareId);
        if (share == null || share.getDeleted() == 1) {
            throw new BizException("分享记录不存在");
        }
        if (!userId.toString().equals(share.getFromUserId())) {
            throw new BizException("只有转发方可以删除分享记录");
        }
        removeById(shareId);
    }

    private List<MessageVO> parseHistory(String raw) {
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray()) {
                return new ArrayList<>();
            }
            List<Map<String, Object>> items = objectMapper.convertValue(
                    data, new TypeReference<List<Map<String, Object>>>() {});
            List<MessageVO> result = new ArrayList<>();
            for (Map<String, Object> item : items) {
                MessageVO msg = new MessageVO();
                msg.setSpeakId(String.valueOf(item.getOrDefault("id", "")));
                msg.setRole(String.valueOf(item.getOrDefault("role", "")));
                msg.setContent(String.valueOf(item.getOrDefault("content", "")));
                msg.setCreateTime(String.valueOf(item.getOrDefault("create_time", "")));
                result.add(msg);
            }
            return result;
        } catch (Exception e) {
            log.error("Parse history for share detail failed", e);
            return new ArrayList<>();
        }
    }
}

