package com.example.haha1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import android.Manifest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable runnable;

    public TextView tv1;
    public Button btn_get_location, btn_snd, btn_start_service, btn_stop_service;

    @AfterPermissionGranted(111)
    private void dealBasicPermissions () {
        String[] perms = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.i ("perms", "basic perms got");
        }
        else {
            EasyPermissions.requestPermissions(this, "need basic perms", 111, perms);
        }
    }
    @AfterPermissionGranted(222)
    private void dealLocationPermissions () {
        String[] perms = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.i ("perms", "normal perms got");
            dealBackLocationPermissions();
//            EasyPermissions.requestPermissions(this,
//                    "need background location perm", 333,
//                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION});
        }
        else {
            EasyPermissions.requestPermissions(this, "need normal location perms", 222, perms);
        }
    }
    @AfterPermissionGranted(333)
    private void dealBackLocationPermissions () {
        String[] perms = {
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.i ("perms", "background location perms got");
        }
        else {
            EasyPermissions.requestPermissions(this, "need background location perms", 333, perms);
        }
    }
//    @AfterPermissionGranted(123)
//    private void requestPermission () {
//        String[] permissions = {
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.FOREGROUND_SERVICE
//        };
//
//        if (EasyPermissions.hasPermissions(this, permissions)) {
//            Log.i("getpermi", "已获得权限，可以定位");
//        } else {
//            //false 无权限
//            EasyPermissions.requestPermissions(this, "需要权限", 123, permissions);
//        }
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkingAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android6.0及以上先获取权限再定位
//            requestPermission();
            dealBasicPermissions();
//            dealLocationPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isIgnoringBatteryOptimizations(Context context){
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (powerManager != null)
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());

        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AMapLocationClient.updatePrivacyShow(this,true,true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        checkingAndroidVersion();

        if (!isIgnoringBatteryOptimizations(getApplicationContext())) {
            requestIgnoreBatteryOptimizations();
        }

        handler = new Handler();
        tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        btn_snd = findViewById(R.id.btn_snd);
        btn_get_location = findViewById(R.id.btn_get_location);
        btn_start_service = findViewById(R.id.btn_start_service);
        btn_stop_service = findViewById(R.id.btn_stop_service);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dispSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        btn_snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run () {
                        try {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                            String key = "RGUGttyfgYTghtyuGHYhyTYy";
                            String msg = Build.BRAND + ",";
                            if (AmapLocate.mLocationClient != null) {
                                msg += simpleDateFormat.format(AmapLocate.lastest_timestamp) + ",";
                                msg += AmapLocate.lastest_longtitude + ",";
                                msg += AmapLocate.lastest_latitude;
                            }
                            else {
                                msg += "null!!";
                            }
                            sendMsgUdp(key + msg);
                            displayOnScreen(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        btn_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curr_location;
                if (AmapLocate.mLocationClient == null) {
                    curr_location = "start_the_service_first!";
                }
                else {
                    curr_location = "isStarted: " + AmapLocate.mLocationClient.isStarted() +
                            "\ntime: " + dispSimpleDateFormat.format(AmapLocate.lastest_timestamp) +
                            "\nlongti: " + AmapLocate.lastest_longtitude +
                            "\nlati: " + AmapLocate.lastest_latitude + "\n";
                }
                displayOnScreen(curr_location);
            }
        });
        btn_start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dealLocationPermissions();
                    startForegroundService(new Intent(getApplicationContext(), SendService.class));
                    displayOnScreen("service is started!");
                }
            }
        });
        btn_stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), SendService.class));
                displayOnScreen("service is stopped!");
            }
        });
        tv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tv1.setText("");
                return false;
            }
        });

    }
    public void sendMsg (String msg) throws IOException {
        Socket socket = new Socket();
        SocketAddress socAddress = new InetSocketAddress("xx.xx.xx.xx", 20000);
        socket.connect(socAddress, 5000);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(msg);
        pw.flush();
        socket.shutdownOutput();
        socket.close();
    }

    public void sendMsgUdp (String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("xx.xx.xx.xx");
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, 20000);
        socket.send(packet);
    }

    @SuppressLint("SetTextI18n")
    public void displayOnScreen (String msg) {
        String data = tv1.getText().toString();
        tv1.setText(msg + "\n" + data);
    }
}
