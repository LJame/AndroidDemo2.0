package com.hardrubic.util.network;

public class HttpDownloadResult {
    private boolean isFinish;
    private double percent;
    private String diskPath;//保持路径

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    @Override
    public String toString() {
        return "HttpDownloadResult{" +
                "isFinish=" + isFinish +
                ", percent=" + percent +
                ", diskPath='" + diskPath + '\'' +
                '}';
    }
}
