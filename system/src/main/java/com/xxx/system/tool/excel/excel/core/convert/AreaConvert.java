///*
// * Decompiled with CFR 0.152.
// *
// * Could not load the following classes:
// *  cn.hutool.core.convert.Convert
// *  com.alibaba.excel.converters.Converter
// *  com.alibaba.excel.enums.CellDataTypeEnum
// *  com.alibaba.excel.metadata.GlobalConfiguration
// *  com.alibaba.excel.metadata.data.ReadCellData
// *  com.alibaba.excel.metadata.property.ExcelContentProperty
// *  com.xxx.cloud.framework.ip.core.Area
// *  com.xxx.cloud.framework.ip.core.utils.AreaUtils
// *  lombok.Generated
// *  org.slf4j.Logger
// *  org.slf4j.LoggerFactory
// */
//package com.xxx.system.tool.excel.excel.core.convert;
//
//import cn.hutool.core.convert.Convert;
//import com.alibaba.excel.converters.Converter;
//import com.alibaba.excel.enums.CellDataTypeEnum;
//import com.alibaba.excel.metadata.GlobalConfiguration;
//import com.alibaba.excel.metadata.data.ReadCellData;
//import com.alibaba.excel.metadata.property.ExcelContentProperty;
//import com.xxx.cloud.framework.ip.core.Area;
//import com.xxx.cloud.framework.ip.core.utils.AreaUtils;
//import lombok.Generated;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class AreaConvert
//implements Converter<Object> {
//    @Generated
//    private static final Logger log = LoggerFactory.getLogger(AreaConvert.class);
//
//    public Class<?> supportJavaTypeKey() {
//        throw new UnsupportedOperationException("\u6682\u4e0d\u652f\u6301\uff0c\u4e5f\u4e0d\u9700\u8981");
//    }
//
//    public CellDataTypeEnum supportExcelTypeKey() {
//        throw new UnsupportedOperationException("\u6682\u4e0d\u652f\u6301\uff0c\u4e5f\u4e0d\u9700\u8981");
//    }
//
//    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
//        String string = readCellData.getStringValue();
//        Area area = AreaUtils.parseArea((String)string);
//        if (area == null) {
//            log.error("[convertToJavaData][label({}) \u89e3\u6790\u4e0d\u6389]", (Object)string);
//            return null;
//        }
//        Class<?> clazz = excelContentProperty.getField().getType();
//        return Convert.convert(clazz, (Object)area.getId());
//    }
//}
//

