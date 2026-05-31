package com.xxx.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.system.entity.Tag;
import com.xxx.system.vo.TagVO;
import org.apache.ibatis.annotations.Param;

public interface TagMapper extends BaseMapper<Tag> {

    IPage<TagVO> pageWithUsage(
            Page<TagVO> page,
            @Param("tagName") String tagName,
            @Param("tagType") String tagType
    );
}

