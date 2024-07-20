package com.example.myapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.preference.PreferenceManager;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.overlay.Marker;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Locate extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
//地图显示声明
    MapView map = null;
    private Marker currentMarker;

//MQTT连接设置
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private ScheduledExecutorService scheduler;
    private final String productKey = "k1k6ds6gn6q";
    private final String deviceName = "android";
    private final String deviceSecret = "fa44b82ed49b19d47583bb8252007d7a";
    private final String sub_topic = "/sys/k1k6ds6gn6q/android/thing/service/property/set";

//界面图标相关声明
    private ImageView Viewfall;
    private TextView textViewloc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.map);
        textViewloc = findViewById(R.id.title_layout);
        Viewfall = findViewById(R.id.fall);
        //显示地图
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //退出登录
        ImageButton exit= (ImageButton) this.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent=new Intent(Locate.this,MainActivity.class);
                    startActivity(intent);
                }
        });
        //phoonecall
        ImageButton call=(ImageButton)this.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
        //mqtt连接
        mqtt_init();
        start_reconnect();
        //消息处理
        handler = new Handler() {
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 3:
                        String message = msg.obj.toString();
                        Log.d("MQTT", "handleMessage: " + message);
                        try {
                            JSONObject jsonObjectALL = new JSONObject(message);
                            JSONObject items = jsonObjectALL.getJSONObject("items");
                            JSONObject obj_la = items.getJSONObject("latitude");
                            JSONObject obj_lo = items.getJSONObject("longitude");
                            JSONObject obj_fa = items.getJSONObject("fall");
                            double latitude = obj_la.getDouble("value");
                            double longitude = obj_lo.getDouble("value");
                            int fall = obj_fa.getInt("value");
                            //地图位置显示
                            onLocationChanged(latitude,longitude);
                            //摔倒预警
                            if(fall==1){
                                //textViewfall.setText("FALL!!");
                                Viewfall.setVisibility(View.VISIBLE);
                            }
                            else{
                                //textViewfall.setText("SAFE!");
                                Viewfall.setVisibility(View.INVISIBLE);
                            }
                            Log.d("MQTT", "latitude: " + latitude);
                            Log.d("MQTT", "longitude: " + longitude);
                            Log.d("MQTT", "fall: " + fall);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("MQTT", "JSON Parsing error: " + e.getMessage());
                        }
                        break;
                    case 30:
                        Toast.makeText(Locate.this, "连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 31:
                        Toast.makeText(Locate.this, "连接成功", Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe(sub_topic, 1);
                            Log.d("MQTT", "Subscribed to topic: " + sub_topic);
                        } catch (MqttException e) {
                            e.printStackTrace();
                            Log.e("MQTT", "Subscription failed: " + e.getMessage());
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }
    
    //打电话
    private void makePhoneCall() {
        String phoneNumber ="83497886";
        if (phoneNumber.trim().isEmpty()) {
            Toast.makeText(Locate.this, "NONE!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(Locate.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Locate.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            String dial = "tel:" + phoneNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }


    //位置显示
    public void onLocationChanged(double a,double b) {
        GeoPoint startPoint = new GeoPoint(a, b);
        // 设置地图中心点
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(startPoint);
        // 清除之前的标记
        if (currentMarker != null) {
            map.getOverlays().remove(currentMarker);
        }
        // 添加新标记
        currentMarker = new Marker(map);
        currentMarker.setPosition(startPoint);
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(currentMarker);
        //地址反编码
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(a, b, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressName = address.getAddressLine(0);
                currentMarker.setTitle(addressName);
                textViewloc.setText(addressName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to get address for the location", Toast.LENGTH_LONG).show();
        }
        map.invalidate();
    }
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }



    //MQTT初始化
    private void mqtt_init() {
        try {
            String clientId = "a1MoTKOqkVK.test_device1";
            Map<String, String> params = new HashMap<>(16);
            params.put("productKey", productKey);
            params.put("deviceName", deviceName);
            params.put("clientId", clientId);
            String timestamp = String.valueOf(System.currentTimeMillis());
            params.put("timestamp", timestamp);

            String host_url = "tcp://" + productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";
            String client_id = clientId + "|securemode=2,signmethod=hmacsha1,timestamp=" + timestamp + "|";
            String user_name = deviceName + "&" + productKey;
            String password = AliyunIoTSignUtil.sign(params, deviceSecret, "hmacsha1");

            client = new MqttClient(host_url, client_id, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(user_name);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "connectionLost----------", cause);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "deliveryComplete---------" + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    Log.d("MQTT", "messageArrived---------- Topic: " + topicName + " Message: " + message.toString());
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = message.toString();
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //mqtt连接
    private void mqtt_connect() {
        new Thread(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 31;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 30;
                handler.sendMessage(msg);
            }
        }).start();
    }
    private void start_reconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            if (!client.isConnected()) {
                mqtt_connect();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}