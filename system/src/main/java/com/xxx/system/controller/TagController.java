package com.xxx.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.common.resp.Resp;
import com.xxx.system.entity.Tag;
import com.xxx.system.service.TagService;
import com.xxx.system.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@io.swagger.v3.oas.annotations.tags.Tag(name = "标签", description = "标签增删改查与按需打标")
@RestController
@RequestMapping("/api/system/tags")
public class TagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "分页查询", description = "按标签名称、类型筛选，分页返回标签列表")
    @GetMapping
    public Resp<IPage<TagVO>> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") long pageSize,
            @Parameter(description = "标签名称，模糊查询") @RequestParam(required = false) String tagName,
            @Parameter(description = "标签类型") @RequestParam(required = false) String tagType) {
        Page<TagVO> page = new Page<>(pageNum, pageSize);
        IPage<TagVO> result = tagService.pageTags(page, tagName, tagType);
        return Resp.ok(result);
    }

    @Operation(summary = "按ID查询详情", description = "根据主键查询标签详情")
    @GetMapping("/{id}")
    public Resp<TagVO> getById(
            @Parameter(description = "标签主键ID", required = true) @PathVariable Long id) {
        TagVO vo = tagService.getDetailById(id);
        return Resp.ok(vo);
    }

    @Operation(summary = "新增标签", description = "创建标签")
    @PostMapping
    public Resp<Boolean> create(@RequestBody Tag tag) {
        boolean ok = tagService.save(tag);
        return ok ? Resp.ok(true) : Resp.fail("新增失败");
    }

    @Operation(summary = "更新标签", description = "根据ID更新标签信息")
    @PutMapping("/{id}")
    public Resp<Boolean> update(
            @Parameter(description = "标签主键ID", required = true) @PathVariable Long id,
            @RequestBody Tag tag) {
        tag.setId(id);
        boolean ok = tagService.updateById(tag);
        return ok ? Resp.ok(true) : Resp.fail("更新失败");
    }

    @Operation(summary = "删除标签", description = "根据ID逻辑删除标签")
    @DeleteMapping("/{id}")
    public Resp<Boolean> delete(
            @Parameter(description = "标签主键ID", required = true) @PathVariable Long id) {
        boolean ok = tagService.removeById(id);
        return ok ? Resp.ok(true) : Resp.fail("删除失败");
    }

    @Operation(summary = "批量删除标签", description = "根据ID列表批量逻辑删除标签")
    @DeleteMapping("/batch")
    public Resp<Boolean> deleteBatch(
            @Parameter(description = "标签主键ID列表", required = true) @RequestBody java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Resp.fail("ID列表不能为空");
        }
        boolean ok = tagService.removeByIds(ids);
        return ok ? Resp.ok(true) : Resp.fail("批量删除失败");
    }

}

