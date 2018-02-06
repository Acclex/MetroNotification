package com.traffic.locationremind.baidu.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.traffic.locationremind.baidu.location.activity.LocationApplication;
import com.traffic.locationremind.baidu.location.object.MarkObject;
import com.traffic.locationremind.baidu.location.object.NotificationObject;
import com.traffic.locationremind.common.util.CommonFuction;
import com.traffic.locationremind.common.util.NotificationUtil;
import com.traffic.locationremind.manager.bean.StationInfo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author baidu
 */
public class RemonderLocationService extends Service {

    private final static String TAG = "RemonderLocationService";
    public final static String CLOSE_REMINDER_SERVICE = "close.reminder.service";
    private UpdateBinder downLoadBinder=new UpdateBinder();
    private LocationService locationService;
    /**
     * 回调
     */
    private Callback callback;
    public static boolean state = false;
    /**
     * Timer实时更新数据的
     */
    private Timer mTimer=new Timer();
    private double longitude,latitude;
    /**
     *
     */
    private int num;
    private StationInfo mStationInfo;
    private List<MarkObject> mStationInfoList = null;//地图站台信息

    private NotificationUtil mNotificationUtil;
    private boolean isReminder = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("=====onBind=====");

        return downLoadBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.callback = null;


        return super.onUnbind(intent);
    }

    /**
     * 内部类继承Binder
     * @author lenovo
     *
     */
    public class UpdateBinder extends Binder {
        /**
         * 声明方法返回值是MyService本身
         * @return
         */
        public RemonderLocationService getService() {
            return RemonderLocationService.this;
        }
    }
    /**
     * 服务创建的时候调用
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // -----------location config ------------
        locationService = ((LocationApplication) getApplication()).locationService;
        // 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        /*
         * 执行Timer 2000毫秒后执行，5000毫秒执行一次
         */
        mNotificationUtil = new NotificationUtil(this);

    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            StationInfo mStationInfo = new StationInfo();
            double lot =0,lat = 0;
            if (null != location
                    && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                //sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                //sb.append(location.getTime());
                //sb.append("\nlocType : ");// 定位类型
                //sb.append(location.getLocType());
                //sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                //sb.append(location.getLocTypeDescription());
                //sb.append("\nlatitude : ");// 纬度
                lot = location.getLatitude();
                mStationInfo.setLat(""+location.getLatitude());
                //sb.append(location.getLatitude());
                //sb.append("\nlontitude : ");// 经度
                lat = location.getLongitude();
                mStationInfo.setLot(""+location.getLongitude());
                //sb.append(location.getLongitude());
                //sb.append("\nradius : ");// 半径
                //sb.append(location.getRadius());
                //sb.append("\nCountryCode : ");// 国家码
                //sb.append(location.getCountryCode());
                //sb.append("\nCountry : ");// 国家名称
                //sb.append(location.getCountry());
                //sb.append("\ncitycode : ");// 城市编码
                //sb.append(location.getCityCode());
                //sb.append("\ncity : ");// 城市
                mStationInfo.setCityNo(""+location.getCityCode());
                mStationInfo.setCname(location.getCity());
                //sb.append(location.getCity());
                //sb.append("\nDistrict : ");// 区
                //sb.append(location.getDistrict());
                //sb.append("\nStreet : ");// 街道
                //sb.append(location.getStreet());
                //sb.append("\naddr : ");// 地址信息
                //sb.append(location.getAddrStr());
                //sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                //sb.append(location.getUserIndoorState());
                //sb.append("\nDirection(not all devices have value): ");
                //sb.append(location.getDirection());// 方向
               // sb.append("\nlocationdescribe: ");
                //sb.append(location.getLocationDescribe());// 位置语义化信息
                //sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null
//                        && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
                //logMsg(sb.toString());
                /*
                 * 得到最新数据
                 */
                //mStationInfo.setStationInfo(sb.toString());
                //callback.setCurrentStation(mStationInfo);//
                if(mStationInfoList != null){
                    double dis = 0;
                    String startStation="",endStation="",currentStation="";
                    for(int n = 0;n<mStationInfoList.size();n++){
                        longitude = CommonFuction.convertToDouble(mStationInfoList.get(n).mStationInfo.getLot(),0);
                        latitude =  CommonFuction.convertToDouble(mStationInfoList.get(n).mStationInfo.getLat(),0);
                        dis = CommonFuction.getDistanceLat(longitude,latitude,lot,lat);
                        if(mStationInfoList.get(n).isStartStation){
                            startStation = mStationInfoList.get(n).mStationInfo.getCname();
                        }
                        if(mStationInfoList.get(n).isEndStation){
                            endStation = mStationInfoList.get(n).mStationInfo.getCname();
                        }
                        if(mStationInfoList.get(n).isCurrentStation){
                            currentStation = mStationInfoList.get(n).mStationInfo.getCname();
                        }
                        callback.setCurrentStation(startStation,endStation,currentStation);
                        if(dis<= CommonFuction.RANDDIS){//抵达某站台
                            for(int j = 0;j<mStationInfoList.size();j++){
                                mStationInfoList.get(j).isCurrentStation = false;
                            }
                            mStationInfoList.get(n).isCurrentStation = true;
                            currentStation = mStationInfoList.get(n).mStationInfo.getCname();
                            callback.setCurrentStation(startStation,endStation,currentStation);
                            CommonFuction.writeSharedPreferences(RemonderLocationService.this,CommonFuction.CURRENTSTATIONNAME,mStationInfoList.get(n).mStationInfo.getCname());
                            if(mStationInfoList.get(n).isEndStation){//到站通知
                                if(mTimer != null){
                                    mTimer.cancel();
                                    isReminder = false;
                                    CommonFuction.writeSharedPreferences(RemonderLocationService.this,CommonFuction.ISREMINDER,CommonFuction.FALSE);
                                    if(callback != null){
                                        callback.arriaved(true);
                                    }
                                }
                                break;
                            }
                            continue;
                        }
                    }
                }

            }
        }
    };

    /**
     * 提供接口回调方法
     * @param
     */

    public void setStartReminder(){
        if(!isReminder) {
            isReminder = true;
            mTimer.schedule(task, 0, 3000);
        }
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setCancleReminder(){
        if(mTimer != null){
            mTimer.cancel();
        }
        CommonFuction.writeSharedPreferences(this,CommonFuction.ISREMINDER,CommonFuction.FALSE);
    }

    /*(String lineName,
    String startStation,
    String endStation,
    String nextStation,
    String time)*/
    public void setNotification(boolean state) {
        mNotificationUtil = new NotificationUtil(this);
        boolean ismark = CommonFuction.getSharedPreferencesValue(this,CommonFuction.ISREMINDER).equals(CommonFuction.TRUE);
        Log.d(TAG,"isremark = "+ismark);
        if(ismark){
            NotificationObject mNotificationObject = new NotificationObject("1号地铁","老街","机场东","华强北","2分钟");
            mNotificationUtil.showNotification(NotificationUtil.notificationId,mNotificationObject);
        }
    }

    public void setEndStation(StationInfo mStationInfo){
        this.mStationInfo = mStationInfo;
        longitude = CommonFuction.convertToDouble(mStationInfo.getLot(),0);
        latitude = CommonFuction.convertToDouble(mStationInfo.getLat(),0);

    }

    /**
     *
     */
    TimerTask task = new TimerTask(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            num++;
            if(callback!=null){

                locationService.start();
            }

        }

    };


    /**
     * 回调接口
     *
     * @author lenovo
     *
     */
    public static interface Callback {
        /**
         * 得到实时更新的数据
         *
         * @return
         */
        void setCurrentStation(String startCname,String endName,String current);
        void arriaved(boolean state);
    }
    /**
     * 服务销毁的时候调用
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        System.out.println("=========onDestroy======");
        /**
         * 停止Timer
         */
        if(mTimer != null){
            mTimer.cancel();
        }
        if(locationService != null){
            locationService.unregisterListener(mListener); // 注销掉监听
            locationService.stop(); // 停止定位服务
        }

        super.onDestroy();
    }

    public void setStationInfoList(List<MarkObject> mStationInfoList){
        this.mStationInfoList = mStationInfoList;
        if(mStationInfoList != null && mStationInfoList.size() > 0){
            CommonFuction.writeSharedPreferences(this,CommonFuction.ISREMINDER,CommonFuction.TRUE);
            CommonFuction.writeSharedPreferences(this,CommonFuction.CURRENTLINEID,""+mStationInfoList.get(0).mStationInfo.getLineid());
            CommonFuction.writeSharedPreferences(this,CommonFuction.CURRENTCITYNO,""+mStationInfoList.get(0).mStationInfo.getCityNo());

            for(int n=0;n<mStationInfoList.size();n++){
                if(mStationInfoList.get(n).isStartStation){
                    CommonFuction.writeSharedPreferences(this,CommonFuction.STARTSTATIONNAME,mStationInfoList.get(n).mStationInfo.getCname());
                    continue;
                }
                if(mStationInfoList.get(n).isEndStation){
                    CommonFuction.writeSharedPreferences(this,CommonFuction.ENDSTATIONNAME,mStationInfoList.get(n).mStationInfo.getCname());
                    continue;
                }
                if(mStationInfoList.get(n).isCurrentStation){
                    CommonFuction.writeSharedPreferences(this,CommonFuction.CURRENTSTATIONNAME,mStationInfoList.get(n).mStationInfo.getCname());
                    continue;
                }

            }
        }

    }
}