package com.xxx.system.tool.excel.dict.core;

import com.xxx.system.service.DictService;
import com.xxx.system.vo.DictItemVO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DictFrameworkUtils {
    private static final Logger log = LoggerFactory.getLogger(DictFrameworkUtils.class);
    private static DictService dictService;
    private static LoadingCache<String, List<DictItemVO>> GET_DICT_DATA_CACHE;

    public static void init(DictService service) {
        dictService = service;
        GET_DICT_DATA_CACHE = CacheBuilder.newBuilder()
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<DictItemVO>>() {
                    @Override
                    public List<DictItemVO> load(String dictType) {
                        return dictService.listByType(dictType);
                    }
                });
        log.info("[init][初始化 DictFrameworkUtils 成功]");
    }

    public static void clearCache() {
        GET_DICT_DATA_CACHE.invalidateAll();
    }

    public static String parseDictDataLabel(String dictType, Integer value) {
        if (value == null) {
            return null;
        }
        return parseDictDataLabel(dictType, String.valueOf(value));
    }

    public static String parseDictDataLabel(String dictType, String value) {
        List<DictItemVO> list = GET_DICT_DATA_CACHE.getUnchecked(dictType);
        DictItemVO item = list.stream()
                .filter(d -> Objects.equals(d.getCode(), value))
                .findFirst().orElse(null);
        return item != null ? item.getName() : null;
    }

    public static List<String> getDictDataLabelList(String dictType) {
        List<DictItemVO> list = GET_DICT_DATA_CACHE.getUnchecked(dictType);
        return list.stream().map(DictItemVO::getName).collect(Collectors.toList());
    }

    public static String parseDictDataValue(String dictType, String label) {
        List<DictItemVO> list = GET_DICT_DATA_CACHE.getUnchecked(dictType);
        DictItemVO item = list.stream()
                .filter(d -> Objects.equals(d.getName(), label))
                .findFirst().orElse(null);
        return item != null ? item.getCode() : null;
    }
}


