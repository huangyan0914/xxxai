package com.xxx.system.tool.excel.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** 金额转换器：分 -> 元（保留2位小数） */
public class MoneyConvert implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(Integer amountInCents, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        BigDecimal yuan = BigDecimal.valueOf(amountInCents).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return new WriteCellData<>(yuan.toString());
    }
}


