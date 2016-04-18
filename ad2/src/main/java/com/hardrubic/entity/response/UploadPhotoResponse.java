package com.hardrubic.entity.response;


public class UploadPhotoResponse {

    private String upload_auth;
    private Integer upload_result;

    public String getUpload_auth() {
        return upload_auth;
    }

    public void setUpload_auth(String upload_auth) {
        this.upload_auth = upload_auth;
    }

    public Integer getUpload_result() {
        return upload_result;
    }

    public void setUpload_result(Integer upload_result) {
        this.upload_result = upload_result;
    }
}
