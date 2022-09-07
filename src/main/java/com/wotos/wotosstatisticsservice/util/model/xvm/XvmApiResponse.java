package com.wotos.wotosstatisticsservice.util.model.xvm;

import java.util.Map;

public class XvmApiResponse<T> {

    private final T data;
    private final Map<String, String> headers; // This can probably be better

    public XvmApiResponse(T data, Map<String, String> headers) {
        this.data = data;
        this.headers = headers;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
