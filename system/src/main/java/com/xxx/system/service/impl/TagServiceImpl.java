package com.xxx.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.common.exception.BizException;
import com.xxx.system.entity.Tag;
import com.xxx.system.mapper.TagMapper;
import com.xxx.system.service.TagService;
import com.xxx.system.vo.TagVO;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public IPage<TagVO> pageTags(Page<TagVO> page, String tagName, String tagType) {
        return baseMapper.pageWithUsage(page, tagName, tagType);
    }

    @Override
    public TagVO getDetailById(Long id) {
        Tag tag = getById(id);
        if (tag == null) {
            throw new BizException("标签不存在");
        }
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setTagName(tag.getTagName());
        vo.setTagType(tag.getTagType());
        vo.setDescription(tag.getDescription());
        vo.setCreateTime(tag.getCreateTime());
        vo.setUpdateTime(tag.getUpdateTime());
        vo.setUsageCount(0L);
        return vo;
    }

}

