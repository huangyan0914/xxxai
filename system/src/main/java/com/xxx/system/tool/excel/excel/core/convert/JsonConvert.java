///*
// * Decompiled with CFR 0.152.
// *
// * Could not load the following classes:
// *  com.alibaba.excel.converters.Converter
// *  com.alibaba.excel.enums.CellDataTypeEnum
// *  com.alibaba.excel.metadata.GlobalConfiguration
// *  com.alibaba.excel.metadata.data.WriteCellData
// *  com.alibaba.excel.metadata.property.ExcelContentProperty
// *  com.xxx.cloud.framework.common.util.json.JsonUtils
// */
//package com.xxx.system.tool.excel.excel.core.convert;
//
//import com.alibaba.excel.converters.Converter;
//import com.alibaba.excel.enums.CellDataTypeEnum;
//import com.alibaba.excel.metadata.GlobalConfiguration;
//import com.alibaba.excel.metadata.data.WriteCellData;
//import com.alibaba.excel.metadata.property.ExcelContentProperty;
//import com.xxx.cloud.framework.common.util.json.JsonUtils;
//
//public class JsonConvert
//implements Converter<Object> {
//    public Class<?> supportJavaTypeKey() {
//        throw new UnsupportedOperationException("\u6682\u4e0d\u652f\u6301\uff0c\u4e5f\u4e0d\u9700\u8981");
//    }
//
//    public CellDataTypeEnum supportExcelTypeKey() {
//        throw new UnsupportedOperationException("\u6682\u4e0d\u652f\u6301\uff0c\u4e5f\u4e0d\u9700\u8981");
//    }
//
//    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
//        return new WriteCellData(JsonUtils.toJsonString((Object)object));
//    }
//}
//

