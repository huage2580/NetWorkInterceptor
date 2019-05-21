package com.hua.netfloatview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.util.LinkedList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SpyXService extends Service implements View.OnClickListener, SpyAdapter.OnItemClick {
    private static final String TAG = "SpyXService";

    private Disposable disposable;
    private ImageView floatView = null;//小图标
    private View listView = null;//列表
    private View detailView = null;//详情
    private SpyAdapter adapter = new SpyAdapter();
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams lp = null;

    private float[] recordXY =new float[]{0,0};
    private float[] rawXY = new float[]{0,0};

    private LinkedList<NetSpyPack> netSpyPacks = new LinkedList<>();

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createFloatView();
        setUpWatcher();
        adapter.setOnItemClick(this);
    }

    private void setUpWatcher() {
        disposable = EventPostUtil.getInstance().toObservable(NetSpyPack.class).observeOn(AndroidSchedulers.mainThread()).subscribe(netSpyPack -> {
//            Log.d(TAG, "netSpyPack() called"+netSpyPack.toString());
            netSpyPacks.add(netSpyPack);
            adapter.setPackList(netSpyPacks);
            adapter.notifyDataSetChanged();
        });
    }

    private void createFloatView() {
        floatView = new ImageView(this);
        floatView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        floatView.setImageResource(R.drawable.net2);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        // layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            lp.type =WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            lp.type =WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        windowManager.addView(floatView,lp);
        //onTouch
        floatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rawXY[0] = x;
                        rawXY[1] = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveBy(x-recordXY[0],y-recordXY[1]);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (rawXY[0] == x && rawXY[1] == y){
                            v.performClick();
                        }
                        break;
                }
                recordXY[0] = x;
                recordXY[1] = y;
                return true;
            }
        });
        //onClick
        floatView.setOnClickListener(this);
    }


    private View loadView(int layout){
        int width = (int) getResources().getDimension(R.dimen.width_dialog);
        int height = (int) getResources().getDimension(R.dimen.height_dialog);
        View tmp = LayoutInflater.from(this).inflate(layout,null,false);
        tmp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams(width,height, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        lp2.gravity = Gravity.CENTER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            lp2.type =WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            lp2.type =WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        windowManager.addView(tmp,lp2);
        return tmp;
    }

    /**
     * 拖动悬浮窗
     * @param x x
     * @param y y
     */
    private void moveBy(float x, float y) {
        lp.x = (int) (lp.x+x);
        lp.y = (int) (lp.y+y);
        windowManager.updateViewLayout(floatView,lp);
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        windowManager.removeView(floatView);
        if (listView!=null){
            windowManager.removeView(listView);
        }
        if (detailView!=null){
            windowManager.removeView(detailView);
        }
        windowManager = null;
    }

    /**
     * 悬浮窗点击，出列表
     */
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        showListView();
    }

    private void showListView() {
        if (listView != null){
            return;
        }
        listView = loadView(R.layout.dialog_spy_list);
        listView.findViewById(R.id.close).setOnClickListener(v -> {
            windowManager.removeView(listView);
            listView = null;
        });
        listView.findViewById(R.id.clear).setOnClickListener(v -> {netSpyPacks.clear();adapter.setPackList(netSpyPacks);adapter.notifyDataSetChanged();});
        RecyclerView list = listView.findViewById(R.id.rcv_net_spy);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter.setPackList(netSpyPacks);
        list.setAdapter(adapter);
    }

    /**
     * 具体的网络请求被点击
     */
    @Override
    public void onClick(int index) {
        showDetailView(netSpyPacks.get(index));
    }

    private void showDetailView(NetSpyPack netSpyPack) {
        if (detailView !=null){
            return;
        }
        detailView = loadView(R.layout.dialog_spy_detail);
        detailView.findViewById(R.id.close).setOnClickListener(v->{
            windowManager.removeView(detailView);
            detailView = null;
        });
        detailView.findViewById(R.id.copy).setOnClickListener(v->{
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(netSpyPack.toString());
        });
        //填充数据
        detailView.findViewById(R.id.status).setEnabled(netSpyPack.getCode().equals("200"));
        TextView title = detailView.findViewById(R.id.title);
        TextView method = detailView.findViewById(R.id.method);
        TextView cost = detailView.findViewById(R.id.cost);
        TextView url = detailView.findViewById(R.id.url);
        method.setText(netSpyPack.getMethod().toUpperCase());
        String[] segs = netSpyPack.getUrl().split("/");
        title.setText("["+netSpyPack.getCode()+"]"+segs[segs.length-1]);
        cost.setText((netSpyPack.getEndTime()-netSpyPack.getStartTime())+"ms");
        url.setText(netSpyPack.getUrl());
        //header
        TextView header = detailView.findViewById(R.id.header);
        TextView req = detailView.findViewById(R.id.tv_request);
        TextView rep = detailView.findViewById(R.id.tv_response);

        header.setText(netSpyPack.getHeader());
        if (netSpyPack.getHeader().isEmpty()){
            header.setVisibility(View.GONE);
        }
        JsonRecyclerView request = detailView.findViewById(R.id.rv_json_request);
        JsonRecyclerView response = detailView.findViewById(R.id.rv_json_response);
        try {
            request.bindJson(netSpyPack.getRequest());
        }catch (Exception e){
            req.setText(netSpyPack.getRequest());
            req.setVisibility(View.VISIBLE);
        }
        try {
            response.bindJson(netSpyPack.getResponse());
        }catch (Exception e){
            rep.setText(netSpyPack.getResponse());
            rep.setVisibility(View.VISIBLE);
        }

    }
}
