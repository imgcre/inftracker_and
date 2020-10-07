package com.tars.hinker.healthy.myapplication.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.tars.hinker.healthy.myapplication.R;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private String TAG = "Main";
    private MqttClient mClient;
    private TextView mTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
//        mTextView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);//断开后，是否自动连接
        options.setCleanSession(true);//是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
        options.setConnectionTimeout(60);//设置超时时间，单位为秒
        //options.setUserName("test");//设置用户名。跟Client ID不同。用户名可以看做权限等级
        //options.setPassword("test");//设置登录密码
        options.setKeepAliveInterval(60);//心跳时间，单位为秒。即多长时间确认一次Client端是否在线
        options.setMaxInflight(10);//允许同时发送几条消息（未收到broker确认信息）
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);//选择MQTT版本
        MqttCallbackExtended callbacks = new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.i(TAG, "connect Complete" + Thread.currentThread().getId());
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection Lost ");
            }

            @Override
            public void messageArrived(String topic, final MqttMessage message) throws Exception {
                Log.i(TAG, "messageArrived: "+new String(message.getPayload()));
//                    Message msg=new Message();
//                    Bundle bindle=new Bundle();
//                    bindle.putString("Content",new String(message.getPayload()));
//                    msg.what=MSG_TYPE_TO_B;
//                    msg.setData(bindle);
//                    mHandler.sendMessage(msg);
//                mTextView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTextView.append(new String(message.getPayload()));
//                    }
//                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "delivery Complete ");//即服务器成功delivery消息
            }
        };

        try {
            mClient = new MqttClient("tcp://221.229.196.175:1883", "test", new MemoryPersistence());
            mClient.setCallback(callbacks);
            mClient.connect(options);
            mClient.subscribe(new String[]{
                    "devices/M5/+",
            });
        } catch(MqttException e) {
            e.printStackTrace();
        }


        return root;
    }
}