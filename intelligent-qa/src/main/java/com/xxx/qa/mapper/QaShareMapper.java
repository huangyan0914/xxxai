package com.xxx.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.qa.entity.QaShare;
import com.xxx.qa.vo.ShareVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QaShareMapper extends BaseMapper<QaShare> {

    IPage<ShareVO> pageSent(Page<ShareVO> page, @Param("fromUserId") String fromUserId);

    IPage<ShareVO> pageReceived(Page<ShareVO> page, @Param("toUserId") String toUserId);
}

