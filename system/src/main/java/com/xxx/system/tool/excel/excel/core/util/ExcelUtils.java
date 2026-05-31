package com.xxx.system.tool.excel.excel.core.util;

import cn.hutool.core.net.URLEncodeUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.xxx.system.tool.excel.excel.core.handler.SelectSheetWriteHandler;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelUtils {

    public static <T> void write(HttpServletResponse response, String filename, String sheetName, Class<T> clazz, List<T> data) throws IOException {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncodeUtil.encode(filename, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setCharacterEncoding("UTF-8");

        EasyExcel.write(response.getOutputStream(), clazz)
                .autoCloseStream(false)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(new SelectSheetWriteHandler(clazz))
                .registerConverter(new LongStringConverter())
                .sheet(sheetName)
                .doWrite(data);
    }

    public static <T> List<T> read(MultipartFile file, Class<T> clazz) throws IOException {
        return EasyExcel.read(file.getInputStream(), clazz, null)
                .autoCloseStream(false)
                .doReadAllSync();
    }
}


