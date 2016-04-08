package com.hardrubic.entity.response;


public class UploadAuthResponse extends CommonResponse {

    private Data data;

    public class Data {
        private String upload_auth;

        public String getUpload_auth() {
            return upload_auth;
        }

        public void setUpload_auth(String upload_auth) {
            this.upload_auth = upload_auth;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
