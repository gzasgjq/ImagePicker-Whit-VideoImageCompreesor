package com.gjq.demo.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.gjq.demo.R;
import com.dhh.rxlifecycle2.RxLifecycle;
import com.dhh.websocket.RxWebSocketUtil;
import com.dhh.websocket.WebSocketConsumer;
import com.dhh.websocket.WebSocketInfo;
import com.lzy.okgo.OkGo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketActivity extends BaseActivity {

    private TextView mTvResult;
    private MultiAutoCompleteTextView mEditUrl;
    private EditText mEditParam;
    private Button mBtnSend;

    private WebSocket mWebSocket;
    private Button mBtnConnectWeb;
    private Disposable mDisposable;
    private TextInputLayout mInputUrlHint;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sp;
    private Button mBtnConnect;

    private Handler handler;

    private Socket socket;
    private OutputStream socketOutputStream;
    private BufferedReader socketBufferReader;
    private Button mBtnSendSocket;

    public static void start(Context context) {
        Intent starter = new Intent(context, WebSocketActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        mInputUrlHint = findViewById(R.id.input_socket_url_hint);
        mEditUrl = findViewById(R.id.edit_socket_url);
        mEditParam = findViewById(R.id.edit_socket_param);
        mBtnSend = findViewById(R.id.btn_socket_send);
        mBtnSendSocket = findViewById(R.id.btn_socket_send_socket);
        mBtnConnectWeb = findViewById(R.id.btn_socket_connect_websocket);
        mBtnConnect = findViewById(R.id.btn_socket_connect_socket);
        mTvResult = findViewById(R.id.tv_socket_result);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        //加入自动提示的备选文本
        adapter.add("ws://192.168.21.74:6789");
        //这样一来在输入的时候就可以有自动提示了
        sp = getSharedPreferences("TestDemo", MODE_PRIVATE);
        Set<String> set = sp.getStringSet("urls", new HashSet<String>());
        if (set.isEmpty())
            adapter.add("192.168.21.74:6789");
        adapter.addAll(set);

        mEditUrl.setAdapter(adapter);

        mBtnConnectWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWeb();
            }
        });
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectSocket();
            }
        });
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        mBtnSendSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSocketdMsg();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 步骤3：断开客户端 & 服务器 连接
        try {
            if (socketOutputStream != null)
                socketOutputStream.close();
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            if (socketBufferReader != null)
                socketBufferReader.close();
            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            if (socket != null)
                socket.close();
            // 最终关闭整个Socket连接
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void connectSocket() {
        socket = null;
        Flowable.create(new FlowableOnSubscribe<Socket>() {
            @Override
            public void subscribe(FlowableEmitter<Socket> e) throws Exception {
                e.onNext(new Socket("192.168.21.74", 6789));
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mTvResult.append("\n Socket fail: " + throwable + "\n");
                    }
                })
                .subscribe(new Consumer<Socket>() {
                    @Override
                    public void accept(Socket socket) throws Exception {
                        WebSocketActivity.this.socket = socket;
                        mTvResult.append("\n Socket open \n");
                        // 判断客户端和服务器是否连接成功
                        if (socket != null && socket.isConnected()) {
                            // 步骤2：客户端 & 服务器 通信
                            // 通信包括：客户端 接收服务器的数据 & 发送数据 到 服务器
                            // 步骤1：创建输入流对象InputStream
                            InputStream is = null;
                            try {
                                is = socket.getInputStream();
                                // 步骤2：创建输入流读取器对象 并传入输入流对象
                                // 该对象作用：获取服务器返回的数据
                                InputStreamReader isr = new InputStreamReader(is);
                                socketBufferReader = new BufferedReader(isr);
                                // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                                Flowable.timer(1, TimeUnit.SECONDS)
                                        .compose(WebSocketActivity.this.<Long>bindOnDestroy())
                                        .map(new Function<Long, String>() {
                                            @Override
                                            public String apply(Long aLong) throws Exception {
                                                String data = socketBufferReader.readLine();
                                                return data;
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnError(new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                mTvResult.append("\n Socket ReadSteam fail: " + throwable + " \n");
                                            }
                                        })
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String data) throws Exception {
                                                mTvResult.append("\n Socket ReadSteam: " + data + " \n");
                                            }
                                        });
                                mTvResult.append("\n Socket ReadSteam success \n");
                            } catch (IOException e) {
                                e.printStackTrace();
                                mTvResult.append("\n Socket ReadSteam fail \n");
                            }
                        }
                    }
                });
    }

    private void sendSocketdMsg() {
        try {
            // 步骤1：从Socket 获得输出流对象OutputStream
            // 该对象作用：发送数据
            socketOutputStream = socket.getOutputStream();

            // 步骤2：写入需要发送的数据到输出流对象中
            socketOutputStream.write(("Carson_Ho" + "\n").getBytes("utf-8"));
            // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

            // 步骤3：发送数据到服务端
            socketOutputStream.flush();
            mTvResult.append("\n Socket SendMsg success\n");
        } catch (Exception e) {
            mTvResult.append("\n Socket SendMsg fail: " + e + " \n");
        }
    }

    private void connectWeb() {
        String url = mEditUrl.getText().toString();
        if (sp != null) {
            Set<String> set = sp.getStringSet("urls", new HashSet<String>());
            set.add(url);
            adapter.add(url);
            sp.edit().putStringSet("urls", set).apply();
        }
        // use WebSocketConsumer
        mDisposable = RxWebSocketUtil.getInstance().getWebSocketInfo(url)
                .compose(RxLifecycle.with(this).<WebSocketInfo>bindOnDestroy())
                .subscribe(new WebSocketConsumer() {
                    @Override
                    public void accept(WebSocketInfo webSocketInfo) throws Exception {
                        super.accept(webSocketInfo);
                        if (webSocketInfo.isOnOpen())
                            mTvResult.append("WebSocket open \n");
                        else
                            mTvResult.append("WebSocket close \n");
                    }

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        mWebSocket = webSocket;
                        if (mWebSocket != null) {
                            mWebSocket.send("Hello!");
                        }
                    }

                    @Override
                    public void onMessage(String text) {
                        mTvResult.append(text + "\n");
                    }

                    @Override
                    public void onMessage(ByteString bytes) {
                        mTvResult.append("webSocketInfo.getByteString():" + bytes + "\n");
                    }
                });

        //get StringMsg
        RxWebSocketUtil.getInstance().getWebSocketString(url)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mTvResult.append(s + "\n");
                    }
                });
    }

    private void send() {
        String msg = "hello~~~~";
        if (mWebSocket != null) {
            mWebSocket.send(msg);
        }
    }
}
