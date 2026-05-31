package com.xxx.qa.service;

public interface TokenService {

    /**
     * 获取指定用户的 LazyCraft TempToken，若不存在则初始化并入库。
     */
    String getOrInitToken(String userId);
}

