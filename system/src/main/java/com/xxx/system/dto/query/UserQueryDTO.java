package com.xxx.system.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户分页查询条件")
public class UserQueryDTO {

    @Schema(description = "登录账号，模糊")
    private String username;

    @Schema(description = "姓名，模糊")
    private String realName;

    @Schema(description = "用户状态，对应字典类型 user_status")
    private String status;

    @Schema(description = "按直接绑定的角色ID筛选")
    private Long roleId;

    @Schema(description = "按所属用户组ID筛选")
    private Long userGroupId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }
}

