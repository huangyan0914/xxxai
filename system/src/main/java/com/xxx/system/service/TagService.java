package com.xxx.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.system.entity.Tag;
import com.xxx.system.vo.TagVO;

public interface TagService extends IService<Tag> {

    IPage<TagVO> pageTags(Page<TagVO> page, String tagName, String tagType);

    TagVO getDetailById(Long id);

}

