package com.xxx.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "字典项视图对象（供前端下拉使用）")
public class DictItemVO {

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "字典名称")
    private String name;

    public DictItemVO() {
    }

    public DictItemVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


