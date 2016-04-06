package com.hardrubic.util.network;

/**
 * 文件下载结果
 */
public class HttpDownloadResult {
    private Boolean result;
    private String url;
    private String diskPath;
    private Exception exception;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("文件").append(url);
        if (result) {
            builder.append("下载成功");
            builder.append(" 路径：").append(diskPath);
        } else {
            builder.append("下载失败");
            builder.append(" 错误：").append(exception.getMessage());
        }
        return builder.toString();
    }
}
