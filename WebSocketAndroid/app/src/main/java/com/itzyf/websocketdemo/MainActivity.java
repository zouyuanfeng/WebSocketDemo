package com.itzyf.websocketdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/31 09:21
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    @BindView(R.id.list_view)
    ListView mListView;
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;

    private StompClient mStompClient;
    private Disposable mRestPingDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDatas = new ArrayList<>();
        mListView.setAdapter(mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, mDatas));


    }

    @Override
    protected void onDestroy() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        if (mRestPingDisposable != null) {
            mRestPingDisposable.dispose();
        }
        super.onDestroy();
    }

    /**
     * 连接到Stomp
     *
     * @param view
     */
    public void connectStomp(View view) {
        String url = "ws://" + BuildConfig.SOCKET_API + "/webSocketServer/websocket";
        Log.d(TAG, url);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, url);

        Disposable subscribe = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            addItem("连接成功");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            addItem("连接失败:" + lifecycleEvent.getException().getMessage());
                            break;
                        case CLOSED:
                            addItem("连接关闭");
                            break;
                        default:
                            break;
                    }
                });
        mStompClient.connect();


        // 订阅消息
        Disposable subscribe1 = mStompClient.topic("/topic/subscribeTest")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    addItem(new Gson().fromJson(topicMessage.getPayload(), ServerMessage.class).getResponseMessage());
                });

        //订阅单推的消息
        Disposable subscribe2 = mStompClient.topic("/user/queue/greetings")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    addItem(new Gson().fromJson(topicMessage.getPayload(), ServerMessage.class).getResponseMessage());
                });
    }

    /**
     * 断开连接
     *
     * @param view
     */
    public void disconnectStomp(View view) {
        if (mStompClient != null) {
            mStompClient.disconnect();
            addItem("连接断开");
        }
    }

    public void sendEchoViaStomp(View view) {
        if (mStompClient == null) {
            toast("请先连接STOMP");
            return;
        }
        ClientMessage message = new ClientMessage();
        message.setName("APP发送的消息：" + mTimeFormat.format(new Date()));

        Disposable subscribe = mStompClient.send("/app/sendTest", new Gson().toJson(message))
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    addItem("STOMP发送成功");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    addItem("STOMP发送失败：" + throwable.getMessage());
                });
    }

    /**
     * 使用REST调用接口发消息
     *
     * @param view
     */
    public void sendEchoViaRest(View view) {
        Disposable subscribe = RestClient.getInstance()
                .getApiService()
                .sendRestEcho("APP发送的消息： " + mTimeFormat.format(new Date()))
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "REST echo send successfully");
                    addItem("STOMP发送成功");
                }, throwable -> {
                    Log.e(TAG, "Error send REST echo", throwable);
                    addItem("STOMP发送失败：" + throwable.getMessage());
                });

    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void addItem(String msg) {
        mDatas.add(0, msg);
        mAdapter.notifyDataSetChanged();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void sendSingleMsg(View view) {
        if (mStompClient == null) {
            toast("请先连接STOMP");
            return;
        }
        ClientMessage message = new ClientMessage();
        message.setName("APP发送的消息：" + mTimeFormat.format(new Date()));
        message.setId("zou");
        Disposable subscribe = mStompClient.send("/app/msg/single", new Gson().toJson(message))
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    addItem("STOMP发送成功");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    addItem("STOMP发送失败：" + throwable.getMessage());
                });
    }
}
