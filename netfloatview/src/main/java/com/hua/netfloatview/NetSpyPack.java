package com.hua.netfloatview;

public class NetSpyPack {
    private String code;
    private String method;
    private String url;
    private String header;
    private String request;
    private String response;
    private long startTime;
    private long endTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "NetSpyPack{" +
                "code='" + code + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", header='" + header + '\'' +
                ", request='" + request + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
