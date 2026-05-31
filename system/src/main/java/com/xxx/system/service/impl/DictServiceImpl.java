package com.xxx.system.service.impl;

import com.xxx.system.entity.Dict;
import com.xxx.system.mapper.DictMapper;
import com.xxx.system.service.DictService;
import com.xxx.system.vo.DictItemVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl implements DictService {

    @Resource
    private DictMapper dictMapper;

    @Override
    public List<DictItemVO> listByType(String dictType) {
        List<Dict> list = dictMapper.listByType(dictType);
        return list.stream()
                .map(d -> new DictItemVO(d.getDictCode(), d.getDictName()))
                .collect(Collectors.toList());
    }
}


