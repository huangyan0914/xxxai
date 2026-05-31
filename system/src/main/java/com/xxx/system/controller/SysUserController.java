package com.xxx.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.common.resp.Resp;
import com.xxx.system.dto.query.UserQueryDTO;
import com.xxx.system.service.SysUserService;
import com.xxx.system.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "公共-用户数据")
@RestController
@RequestMapping("/api/system/sysusers")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Resp<IPage<SysUserVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long pageSize,
            UserQueryDTO query) {
        Page<SysUserVO> page = new Page<>(pageNum, pageSize);
        return Resp.ok(sysUserService.pageUsers(page, query));
    }

}

