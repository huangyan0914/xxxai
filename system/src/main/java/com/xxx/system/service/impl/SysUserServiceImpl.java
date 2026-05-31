package com.xxx.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.common.exception.BizException;
import com.xxx.system.dto.query.UserQueryDTO;
import com.xxx.system.entity.SysUser;
import com.xxx.system.mapper.SysUserMapper;
import com.xxx.system.service.SysUserService;
import com.xxx.system.vo.SysUserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public IPage<SysUserVO> pageUsers(Page<SysUserVO> page, UserQueryDTO query) {
        UserQueryDTO q = query != null ? query : new UserQueryDTO();
        IPage<SysUserVO> result = baseMapper.pageUsers(page, q);
//        fillAggregates(result.getRecords());
        return result;
    }

}

