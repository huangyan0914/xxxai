package com.xxx.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.qa.client.LazyCraftClient;
import com.xxx.qa.entity.QaUserToken;
import com.xxx.qa.mapper.QaUserTokenMapper;
import com.xxx.qa.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class TokenServiceImpl extends ServiceImpl<QaUserTokenMapper, QaUserToken>
        implements TokenService {

    @Resource
    private LazyCraftClient lazyCraftClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getOrInitToken(String userId) {
        QaUserToken record = getOne(
                new LambdaQueryWrapper<QaUserToken>()
                        .eq(QaUserToken::getUserId, userId)
        );

        if (record != null) {
            return record.getTempToken();
        }

        // 向 LazyCraft 初始化获取新 token
        String tempToken = lazyCraftClient.initToken(null);

        QaUserToken newRecord = new QaUserToken();
        newRecord.setUserId(userId);
        newRecord.setTempToken(tempToken);
        newRecord.setCreator(userId);
        newRecord.setCreateTime(LocalDateTime.now());
        save(newRecord);

        return tempToken;
    }
}

