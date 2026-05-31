package com.xxx.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.system.dto.query.UserQueryDTO;
import com.xxx.system.entity.SysUser;
import com.xxx.system.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按条件分页查询用户列表，支持账号/姓名模糊、状态精确、角色ID及用户组ID关联过滤。
     *
     * @param page 分页参数
     * @param q    查询条件
     */
    IPage<SysUserVO> pageUsers(Page<SysUserVO> page, @Param("q") UserQueryDTO query);

}

