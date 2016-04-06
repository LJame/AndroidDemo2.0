package com.hardrubic.util.network.entity;


public class HttpDownloadInfo {

    private String url;         //带下载URL
    private String targetPath;  //保存路径

    public HttpDownloadInfo(String url, String targetPath) {
        this.url = url;
        this.targetPath = targetPath;
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
}
