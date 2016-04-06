package com.hardrubic.util.network;

/**
 * 文件下载结果
 */
public class HttpDownloadResult {
    private Boolean result;
    private String url;
    private String targetPath;  //目标保存路径
    private String savePath;    //最终保存路径
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

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
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
            builder.append(" 路径：").append(savePath);
        } else {
            builder.append("下载失败");
            builder.append(" 错误：").append(exception.getMessage());
        }
        return builder.toString();
    }
}
