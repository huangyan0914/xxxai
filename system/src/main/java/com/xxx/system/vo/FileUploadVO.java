package com.xxx.system.vo;

public class FileUploadVO {

    private String fileId;
    private String fileName;
    private Long size;
    private String type;
    private String url;

    public FileUploadVO() {
    }

    public FileUploadVO(String fileId, String fileName, Long size, String type, String url) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.size = size;
        this.type = type;
        this.url = url;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

