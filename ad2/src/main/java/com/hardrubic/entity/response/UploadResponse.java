package com.hardrubic.entity.response;


public class UploadResponse extends CommonResponse {

    private Data data;

    public class Data {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
