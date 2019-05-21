package com.hua.networkinterceptor;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.hua.netfloatview.HttpInterceptor;
import com.hua.netfloatview.SpyXService;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    OkHttpClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }
//        jsonRecyclerView = findViewById(R.id.rv_json);
        findViewById(R.id.tv).setOnClickListener(v -> startService(new Intent(MainActivity.this, SpyXService.class)));
        findViewById(R.id.btn).setOnClickListener(v -> new Thread(this::request).start());
    }

    private void request(){
        if (client == null){
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
            clientBuilder.addInterceptor(new HttpInterceptor());
            client = clientBuilder.build();
        }

        // Create request for remote resource.
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/contributors")
                .build();
        try {
            Response response =client.newCall(request).execute();
            String json = response.body().string();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    jsonRecyclerView.bindJson("???");
//                }
//            });
            Log.i(TAG, "request: "+json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
