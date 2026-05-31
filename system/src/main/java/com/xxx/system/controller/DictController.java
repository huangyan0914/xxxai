package com.xxx.system.controller;

import com.xxx.common.resp.Resp;
import com.xxx.system.service.DictService;
import com.xxx.system.vo.DictItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "字典", description = "字典查询接口，供前端下拉选择使用")
@RestController
@RequestMapping("/api/system/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @Operation(summary = "按类型查询字典", description = "根据字典类型返回字典项列表。支持：system_code、project_type、resource_category、publish_status、file_type、tag_type")
    @GetMapping("/{type}")
    public Resp<List<DictItemVO>> listByType(
            @Parameter(description = "字典类型", required = true) @PathVariable String type) {
        List<DictItemVO> list = dictService.listByType(type);
        return Resp.ok(list);
    }
}


