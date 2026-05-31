package com.xxx.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.system.dto.query.UserQueryDTO;
import com.xxx.system.entity.SysUser;
import com.xxx.system.vo.SysUserVO;


import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表，支持按账号、姓名、状态、角色、用户组筛选。
     * 结果中的 groupNames / directRoleNames 由聚合查询批量回填。
     */
    IPage<SysUserVO> pageUsers(Page<SysUserVO> page, UserQueryDTO query);

}

