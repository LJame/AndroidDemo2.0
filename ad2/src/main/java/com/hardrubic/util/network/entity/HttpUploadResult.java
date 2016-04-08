package com.hardrubic.util.network.entity;

/**
 * 文件上传结果
 */
public class HttpUploadResult {
    private boolean result;
    private String fileName;    //上传文件名
    private String filePath;    //上传文件路径
    private Exception exception;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
