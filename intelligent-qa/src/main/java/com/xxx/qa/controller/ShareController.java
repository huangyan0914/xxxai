package com.xxx.qa.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.common.auth.UserContext;
import com.xxx.common.resp.Resp;
import com.xxx.qa.dto.ShareCreateDTO;
import com.xxx.qa.dto.query.ShareQueryDTO;
import com.xxx.qa.service.ShareService;
import com.xxx.qa.vo.ShareDetailVO;
import com.xxx.qa.vo.ShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "智能问答-分享")
@RestController
@RequestMapping("/api/qa/shares")
public class ShareController {

    @Resource
    private ShareService shareService;

    @Operation(summary = "转发会话给指定用户")
    @PostMapping
    public Resp<Long> createShare(
            @RequestBody @Valid ShareCreateDTO dto) {
        Long userId = UserContext.getUserId();
        return Resp.ok(shareService.createShare(dto, userId));
    }

    @Operation(summary = "我发出的分享列表")
    @GetMapping("/sent")
    public Resp<IPage<ShareVO>> listSentShares(
            ShareQueryDTO query) {
        Long userId = UserContext.getUserId();
        return Resp.ok(shareService.listSentShares(query, userId));
    }

    @Operation(summary = "我收到的分享列表")
    @GetMapping("/received")
    public Resp<IPage<ShareVO>> listReceivedShares(
            ShareQueryDTO query) {
        Long userId = UserContext.getUserId();
        return Resp.ok(shareService.listReceivedShares(query, userId));
    }

    @Operation(summary = "查看分享详情（含会话历史）")
    @GetMapping("/{shareId}")
    public Resp<ShareDetailVO> getShareDetail(
            @PathVariable Long shareId) {
        Long userId = UserContext.getUserId();
        return Resp.ok(shareService.getShareDetail(shareId, userId));
    }

    @Operation(summary = "删除分享记录")
    @DeleteMapping("/{shareId}")
    public Resp<Void> deleteShare(
            @PathVariable Long shareId) {
        Long userId = UserContext.getUserId();
        shareService.deleteShare(shareId, userId);
        return Resp.ok();
    }
}

