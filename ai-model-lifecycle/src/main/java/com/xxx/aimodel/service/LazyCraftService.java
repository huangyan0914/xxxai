package com.xxx.aimodel.service;

import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface LazyCraftService {

    Object pageKnowledgeBases(LazyCraftPageQueryDTO query);

    Object pageModels(LazyCraftPageQueryDTO query);

    Object pageModelEvaluations(LazyCraftPageQueryDTO query);

    Object pageInferenceServices(LazyCraftPageQueryDTO query);

    Object pageFinetunes(LazyCraftPageQueryDTO query);

    Object pageDatasets(LazyCraftPageQueryDTO query);

    Object forward(HttpMethod method, String lazyCraftPath, Map<String, Object> body, Map<String, ?> query);

    Object upload(String lazyCraftPath, String fileFieldName, MultipartFile[] files, Map<String, Object> form);

    ResponseEntity<byte[]> download(String lazyCraftPath, Map<String, ?> query);
}

