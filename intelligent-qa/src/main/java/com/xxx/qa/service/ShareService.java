package com.xxx.qa.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.qa.dto.ShareCreateDTO;
import com.xxx.qa.dto.query.ShareQueryDTO;
import com.xxx.qa.vo.ShareDetailVO;
import com.xxx.qa.vo.ShareVO;

public interface ShareService {

    Long createShare(ShareCreateDTO dto, Long fromUserId);

    IPage<ShareVO> listSentShares(ShareQueryDTO query, Long userId);

    IPage<ShareVO> listReceivedShares(ShareQueryDTO query, Long userId);

    ShareDetailVO getShareDetail(Long shareId, Long userId);

    void deleteShare(Long shareId, Long userId);
}

