package com.hua.netfloatview;



import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


public class HttpInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        NetSpyPack pack = new NetSpyPack();
        pack.setStartTime(System.currentTimeMillis());
        Request request = chain.request();
        final String url = request.url().url().toString();
        RequestBody requestBody = request.body();
        pack.setUrl(url);
        pack.setMethod(request.method());
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            pack.setRequest(buffer.readString(charset));
        }
        Headers headers = request.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, count = headers.size(); i < count; i++) {
//            LogUtil.d("REQ HEADER->" + headers.name(i) + " : " + headers.value(i));
            sb.append(headers.name(i));
            sb.append(":");
            sb.append(headers.value(i));
            sb.append("\n");
        }
        pack.setHeader(sb.toString());

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            pack.setCode(""+response.code());
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
//            LogUtil.d("RESP-> " + buffer.clone().readString(charset));
            pack.setResponse(buffer.clone().readString(charset));
        } else {
//            LogUtil.e("RESP-> " + url + ":: is null.");
        }
        pack.setEndTime(System.currentTimeMillis());
        EventPostUtil.getInstance().post(pack);
        return response;
    }
}
