package com.example.haha1;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendService extends Service {
    private Handler handler;
    private Runnable runnable;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss


    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        Notification notification = OpenNotificationsUtil.createNotification(this, "服务常驻通知", "定位服务正在运行中...", 0);
        startForeground(OpenNotificationsUtil.OPEN_SERVICE_NOTIFICATION_ID, notification);//显示常驻通知
        try {
            AmapLocate.getAmapLocation(getApplicationContext(), MainActivity.send_interval);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        scheduleTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        handler.removeCallbacks(runnable);
        if (AmapLocate.mLocationClient != null) {
            AmapLocate.mLocationClient.stopLocation();
            AmapLocate.mLocationClient.onDestroy();
            AmapLocate.mLocationClient = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void scheduleTask() {
        runnable = new Runnable() {
            @Override
            public void run() {
                new Thread() {
                    public void run () {
                        try {
//                            AmapLocate.getAmapLocation(getApplicationContext());
//                            Thread.sleep(5000);
//                            if (AmapLocate.mLocationClient != null) {
//                                AmapLocate.mLocationClient.stopLocation();
//                                AmapLocate.mLocationClient.onDestroy();
//                                AmapLocate.mLocationClient = null;
//                            }
                            String key = "RGUGttyfgYTghtyuGHYhyTYy";
                            String msg = Build.BRAND + ",";
                            if (!AmapLocate.isNull()) {
                                msg += simpleDateFormat.format(AmapLocate.lastest_timestamp) + ",";
                                msg += AmapLocate.lastest_longtitude + ",";
                                msg += AmapLocate.lastest_latitude + ",";
                                msg += AmapLocate.lastest_speed;
                            }
                            else {
                                msg += "null";
                            }
                            SendUtil.sendMsgUdp(key + msg,
                                    MainActivity.server_ip,
                                    MainActivity.server_port);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                handler.postDelayed(this, 1000L * MainActivity.send_interval);
            }
        };
        handler.postDelayed(runnable, 1000);
    }
}
