package com.xxx.system.tool.excel.excel.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.xxx.common.kv.KeyValue;
import com.xxx.common.util.collection.CollectionUtils;
import com.xxx.system.tool.excel.dict.core.DictFrameworkUtils;
import com.xxx.system.tool.excel.excel.core.annotations.ExcelColumnSelect;
import com.xxx.system.tool.excel.excel.core.function.ExcelColumnSelectFunction;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectSheetWriteHandler implements SheetWriteHandler {

    private static final Logger log = LoggerFactory.getLogger(SelectSheetWriteHandler.class);
    public static final int FIRST_ROW = 1;
    public static final int LAST_ROW = 2000;
    private static final String DICT_SHEET_NAME = "字典sheet";
    private final Map<Integer, List<String>> selectMap = new HashMap<>();

    public SelectSheetWriteHandler(Class<?> clazz) {
        int colIndex = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumnSelect.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                if (excelProperty != null && excelProperty.index() != -1) {
                    colIndex = excelProperty.index();
                }
                this.loadSelectOptions(colIndex, field);
            }
            ++colIndex;
        }
    }

    private void loadSelectOptions(int colIndex, Field field) {
        ExcelColumnSelect excelColumnSelect = field.getAnnotation(ExcelColumnSelect.class);
        String dictType = excelColumnSelect.dictType();
        String functionName = excelColumnSelect.functionName();
        Assert.isTrue(
                ObjectUtil.isNotEmpty(dictType) || ObjectUtil.isNotEmpty(functionName),
                "Field({}) 的 @ExcelColumnSelect 注解，dictType 和 functionName 不能同时为空",
                field.getName()
        );
        if (StrUtil.isNotEmpty(dictType)) {
            this.selectMap.put(colIndex, DictFrameworkUtils.getDictDataLabelList(dictType));
            return;
        }
        Map<String, ExcelColumnSelectFunction> functionBeans = SpringUtil.getApplicationContext().getBeansOfType(ExcelColumnSelectFunction.class);
        ExcelColumnSelectFunction selectFunction = CollUtil.findOne(functionBeans.values(), fn -> fn.getName().equals(functionName));
        Assert.notNull(selectFunction, "未找到对应的 function({})", functionName);
        this.selectMap.put(colIndex, selectFunction.getOptions());
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (CollUtil.isEmpty(this.selectMap)) {
            return;
        }
        DataValidationHelper validationHelper = writeSheetHolder.getSheet().getDataValidationHelper();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        List<KeyValue> sortedEntries = CollectionUtils.convertList(
                this.selectMap.entrySet(),
                entry -> new KeyValue(entry.getKey(), entry.getValue())
        );
        sortedEntries.sort(Comparator.comparing(kv -> ((List) kv.getValue()).size()));
        Sheet dictSheet = workbook.createSheet(DICT_SHEET_NAME);
        for (KeyValue entry : sortedEntries) {
            int colIndex = (Integer) entry.getKey();
            List<String> options = (List<String>) entry.getValue();
            for (int i = 0; i < options.size(); i++) {
                Row row = dictSheet.getRow(i);
                if (row == null) {
                    row = dictSheet.createRow(i);
                }
                row.createCell(colIndex).setCellValue(options.get(i));
            }
            SelectSheetWriteHandler.setColumnSelect(writeSheetHolder, workbook, validationHelper, entry);
        }
    }

    private static void setColumnSelect(WriteSheetHolder writeSheetHolder, Workbook workbook,
                                        DataValidationHelper validationHelper, KeyValue<Integer, List<String>> entry) {
        int colIndex = entry.getKey();
        String colName = ExcelUtil.indexToColName(colIndex);
        String formula = "字典sheet!$" + colName + "$1:$" + colName + "$" + entry.getValue().size();
        String namedRange = "dict" + colIndex;

        Name name = workbook.createName();
        name.setNameName(namedRange);
        name.setRefersToFormula(formula);

        DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(namedRange);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 2000, colIndex, colIndex);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        if (validation instanceof HSSFDataValidation) {
            validation.setSuppressDropDownArrow(false);
        } else {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        }
        validation.setErrorStyle(0);
        validation.createErrorBox("提示", "此值不存在于下拉选择中！");
        writeSheetHolder.getSheet().addValidationData(validation);
    }
}


