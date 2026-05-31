package com.xxx.system.service;

import com.xxx.system.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileUploadVO upload(MultipartFile file);

    byte[] download(String fileId);

    void delete(String fileId);
}

