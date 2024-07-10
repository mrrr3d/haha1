package com.example.haha1;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.Date;

public class AmapLocate {

    public static String lastest_latitude = null;
    public static String lastest_longtitude = null;
    public static long lastest_timestamp = 0;
    public static String lastest_speed = null;
    public static AMapLocationClient mLocationClient = null;
    public static AMapLocationClientOption mLocationOption = null;
    public static AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    lastest_timestamp = amapLocation.getTime();
                    lastest_latitude = Double.toString(amapLocation.getLatitude());
                    lastest_longtitude = Double.toString(amapLocation.getLongitude());
                    lastest_speed = Float.toString(amapLocation.getSpeed());
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }

        }
    };
    public static void getAmapLocation(Context context, int interval) throws Exception {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(context);
        }
        mLocationOption = new AMapLocationClientOption();
//        mLocationOption.setOnceLocation(true);
//        mLocationOption.setOnceLocationLatest(false);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setLocationCacheEnable(false);
        mLocationOption.setNeedAddress(false);
        mLocationOption.setInterval(1000L * interval);

        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
    }
    public static boolean isNull () {
        if (AmapLocate.lastest_timestamp != 0 &&
                AmapLocate.lastest_latitude != null &&
                AmapLocate.lastest_longtitude != null &&
                AmapLocate.lastest_speed != null) {
            return false;
        } else {
            return true;
        }
    }
}
