package com.xxx.system.tool.excel.excel.core.convert;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xxx.system.tool.excel.dict.core.DictFrameworkUtils;
import com.xxx.system.tool.excel.excel.core.annotations.DictFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictConvert implements Converter<Object> {

    private static final Logger log = LoggerFactory.getLogger(DictConvert.class);

    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> readCellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        String dictType = getType(excelContentProperty);
        String label = readCellData.getStringValue();
        String value = DictFrameworkUtils.parseDictDataValue(dictType, label);
        if (value == null) {
            log.error("[convertToJavaData][type({}) 解析不掉 label({})]", dictType, label);
            return null;
        }
        Class<?> clazz = excelContentProperty.getField().getType();
        return Convert.convert(clazz, value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object value, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        String dictType = getType(excelContentProperty);
        String rawValue = String.valueOf(value);
        String label = DictFrameworkUtils.parseDictDataLabel(dictType, rawValue);
        if (label == null) {
            log.error("[convertToExcelData][type({}) 转换不了 label({})]", dictType, rawValue);
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(label);
    }

    private static String getType(ExcelContentProperty excelContentProperty) {
        return excelContentProperty.getField().getAnnotation(DictFormat.class).value();
    }
}


