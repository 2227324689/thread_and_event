package com.example.demo.chains;


public class Request {

    private String fileName;

    public Request(String fileName) {
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Request{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
