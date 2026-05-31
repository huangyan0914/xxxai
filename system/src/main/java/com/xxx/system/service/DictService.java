package com.xxx.system.service;

import com.xxx.system.vo.DictItemVO;

import java.util.List;

public interface DictService {

    /**
     * 根据字典类型获取字典项列表
     * 支持：system_code（来自 t_s_dict）、project_type、resource_category、
     * publish_status、file_type、tag_type
     */
    List<DictItemVO> listByType(String dictType);
}


