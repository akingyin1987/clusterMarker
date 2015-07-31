package com.king.clustermarker.base;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.activeandroid.ActiveAndroid;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.DistanceUtil;
import com.king.clustermarker.R;
import com.king.clustermarker.base.baseModel.BdModel;
import com.king.clustermarker.base.baseModel.MapStatusModel;
import com.king.clustermarker.map.Cluster;
import com.king.clustermarker.map.ClusterClickListener;
import com.king.clustermarker.map.ClusterItem;
import com.king.clustermarker.map.ClusterOverlay;
import com.king.clustermarker.map.ClusterRender;

import com.king.clustermarker.map.RegionItem;
import com.king.clustermarker.map.onLoadFinish;

import java.util.ArrayList;
import java.util.List;

/**
 *  
 * User: Geek_Soledad(msdx.android@qq.com) 
 * Date: 2015-07-30 
 * Time: 18:07 
 * FIXME 
 */
public abstract class AbstractBaiduMapBrowseActivity extends BaseBaiduMapActivity implements ClusterRender
        ,ClusterClickListener ,onLoadFinish{

    public List<BdModel> markers = new ArrayList<>();

    public List<ClusterItem>  regionItemList = new ArrayList<>();


    public List<MapStatusModel> allMarkerOptions = new ArrayList<>();
    public static final String MAP_DATA_KEY = "map_data_key";

    public MyManager manager;

    // 点击Marker相关
    public View popView;
    public TextView titleTextView;
    public TextView infoTextView;
    public LinearLayout serchLayout;


    public Button operationButton;
    public Button navButton;
    public Button closeButton;
    private PopupWindow mPopupBottonWindow;

    public TextView lnglat_info;
    // 时况
    public ImageButton road_condition;

    public static final float minDistance = 500;
    public static final float normalDistance = 1500;

    public Marker mCurrentMarker = null;

    public int countloc = 0;
    public int taskId = 0;

    public int style = 0;

    public RadioButton rb_finished, rb_unfinsh;

    public View v_unfinsh, v_finished;

    public RadioGroup rg_search;

    public MapStatusModel mapStatusModel = MapStatusModel.ALL;

    private int clusterRadius = 80;

    public Drawable drawable,drawable1,drawable2,drawable3;

    public    ClusterOverlay clusterOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.clearCache();

        View mainView = LayoutInflater.from(this).inflate(R.layout.activity_browsemap, null);
        setContentView(mainView);
        myListener = new MyLocationListenner();

        initUi(mainView);
        MapConfigure();
        drawable = getApplication().getResources().getDrawable(
                R.drawable.m0);
        drawable1 =getApplication().getResources().getDrawable(
                R.drawable.m1);
        drawable2 = getApplication().getResources().getDrawable(
                R.drawable.m2);
        drawable3 =getApplication().getResources().getDrawable(
                R.drawable.m3);
        mBaidumap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                clusterOverlay = new ClusterOverlay(mBaidumap,regionItemList,manager, dp2px(getApplicationContext(), clusterRadius),AbstractBaiduMapBrowseActivity.this);
                clusterOverlay.setClusterRenderer(AbstractBaiduMapBrowseActivity.this);
                clusterOverlay.setOnLoadFinish(AbstractBaiduMapBrowseActivity.this);
                clusterOverlay.setOnClusterClickListener(AbstractBaiduMapBrowseActivity.this);

            }
        });
        showLoadDialog(null);
        new Thread() {

            @Override
            public void run() {

                super.run();
                initData(markers,regionItemList);
                initDataHandler.sendEmptyMessage(1);
            }

        }.start();
    }
    public   boolean  loadData = false;
    public Handler initDataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == 1) {
                loadData = true;
                if (null == manager) {
                    manager = new MyManager(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(manager);
                    manager.addToMap();
                }

                manager.removeFromMap();
                manager.removeAll();
                if(null != clusterOverlay){
                    clusterOverlay.addAllClusterItems(regionItemList);
                    clusterOverlay.assignClusters();

                }
                countloc = regionItemList.size();


            } else if (msg.what == -1) {
                hideDialog();
                finish();
                showToast("初始化数据出错了！");
            } else if (msg.what == 2) {

                hideDialog();



            } else if (msg.what == 3) {
                showToast("当前位置无法获取，距离条件无效");
            }
        }

    };


    public void initUi(View mainView) {

        initbaseView(true, mainView);

        rg_search = (RadioGroup) mainView.findViewById(R.id.rg_search);
        rb_finished = (RadioButton) mainView.findViewById(R.id.rb_finished);
        rb_unfinsh = (RadioButton) mainView.findViewById(R.id.rb_unfinsh);
        v_finished = (View) mainView.findViewById(R.id.v_finished);
        v_unfinsh = mainView.findViewById(R.id.v_unfinsh);
        rg_search.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                {
                    if (checkedId == R.id.rb_all) {
                        mapStatusModel = MapStatusModel.ALL;
                        manager.removeFromMap();
                        manager.removeAll();
                          ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_finished) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.FINISHED;
                        ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_last) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.LAST;
                           ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_unfinsh) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.UNFINISH;
                            ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_uploaded) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.UPLOADED;
                          ToMarkers(mapStatusModel);
                    }

                }
            }
        });

        lnglat_info = (TextView) mainView.findViewById(R.id.lnglat_info);
        popView = LayoutInflater.from(this).inflate(R.layout.openmap_detai, null);
        titleTextView = (TextView) popView.findViewById(R.id.detai_title);
        infoTextView = (TextView) popView.findViewById(R.id.detai_info);
        serchLayout = (LinearLayout) popView.findViewById(R.id.openmap_detai_serchlayout);
        //scopeSpinner = (Spinner) popView.findViewById(R.id.openmap_detai_scope);
        operationButton = (Button) popView.findViewById(R.id.openmap_detai_leftbtn);
        navButton = (Button) popView.findViewById(R.id.openmap_detai_middlebtn);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mCurrentMarker.getExtraInfo().getInt("index");
                BdModel bdModel = markers.get(index);
                navDevice(bdModel);
            }
        });

        closeButton = (Button) popView.findViewById(R.id.openmap_detai_rightbtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mPopupBottonWindow) {
                    mPopupBottonWindow.dismiss();
                }
            }
        });

        operationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mCurrentMarker) {
                    ToMarkers(mapStatusModel);
                    if (null != mPopupBottonWindow && mPopupBottonWindow.isShowing()) {
                        mPopupBottonWindow.dismiss();
                    }
                } else {
                    int index = mCurrentMarker.getExtraInfo().getInt("index");
                    BdModel bdModel = markers.get(index);
                    operationDevice(bdModel);
                }

            }
        });


    }


    public void MapConfigure() {
        initBaseMapConfigure(true);

        mLocClient.registerLocationListener(myListener);
        mLocClient.start();
        // 当前位置点击事件
        mBaidumap.setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
            @Override
            public boolean onMyLocationClick() {
                showPopupBottonWindow(null,null);
                return false;
            }
        });

        cameraButton.setVisibility(View.VISIBLE);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != manager){
                    manager.zoomToSpan();
                }
            }
        });

    }

    public  BitmapDescriptor    clusterDescriptor;
    public void showPopupBottonWindow(Marker marker,Cluster cluster) {
        if (null != mPopupBottonWindow && mPopupBottonWindow.isShowing()) {

            mPopupBottonWindow.dismiss();
        }
        if (null == marker) {
            mCurrentMarker = null;
            infoTextView.setVisibility(View.GONE);
            serchLayout.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            navButton.setVisibility(View.GONE);
            operationButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            titleTextView.setText("当前位置（精度:" + mBaidumap.getLocationData().accuracy + "米)");
        } else {
            mCurrentMarker = marker;

          if(cluster.getMoverlayOptions() instanceof MarkerOptions){
              clusterDescriptor = ((MarkerOptions) cluster.getMoverlayOptions()).getIcon();
          }
            mCurrentMarker.setIcon(readBitmap);
            operationButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            infoTextView.setVisibility(View.VISIBLE);
            serchLayout.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            navButton.setVisibility(View.VISIBLE);
            if (null != mBaidumap.getLocationData()) {
                LatLng ll = new LatLng(mBaidumap.getLocationData().latitude, mBaidumap.getLocationData().longitude);
                titleTextView.setText("距当前位置：" + String.format("%.2f", DistanceUtil.getDistance(ll, marker.getPosition())) + "(米)");
            }
            int index = marker.getExtraInfo().getInt("index");

            String  message = cluster.getClusterCount()==1?cluster.getBdModel().getBdDeviceInfo():"共"+cluster.getClusterCount()+"个";
            infoTextView.setText(Html.fromHtml(message));
        }
        if (null == mPopupBottonWindow) {
            mPopupBottonWindow = new PopupWindow(this);
            mPopupBottonWindow.setContentView(popView);
            mPopupBottonWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            mPopupBottonWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupBottonWindow.setFocusable(true);
            // mPopupBottonWindow = new PopupWindow(popView,
            // LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            mPopupBottonWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupBottonWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (null != mCurrentMarker && null != clusterDescriptor) {
                        mCurrentMarker.setIcon(clusterDescriptor);
                    }
                }
            });
        }
        if (mPopupBottonWindow.isShowing()) {
            mPopupBottonWindow.dismiss();
        } else {

            mPopupBottonWindow.showAtLocation(closeButton, Gravity.BOTTOM, 0, 0);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class MyManager extends OverlayManager {
        public MyManager(BaiduMap baiduMap) {
            super(baiduMap);
            listoverlayOptionses = new ArrayList<>();
        }

        private List<OverlayOptions> listoverlayOptionses;


        public void addOverlayOption(OverlayOptions overlayOptions) {
            listoverlayOptionses.add(overlayOptions);
        }

        public void removeOverlayOption(OverlayOptions overlayOptions) {
            listoverlayOptionses.remove(overlayOptions);
        }

        public void addAllOverlayOption(List<OverlayOptions> overlayOptionses) {
            listoverlayOptionses.addAll(overlayOptionses);
        }

        public void removeAll() {
            listoverlayOptionses.clear();
        }

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            return listoverlayOptionses;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
           try{
               Bundle   bundle = marker.getExtraInfo();
               int  index = bundle.getInt("index");
               Cluster   cluster = clusterOverlay.getCluster(index);
               if(null == cluster){
                   return  false;
               }
               showPopupBottonWindow(marker,cluster);
           }catch (Exception e){
                 e.printStackTrace();
           }


            return false;
        }

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            return false;
        }
    }


    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            if (null != location) {
                if (countinfo.getVisibility() == View.INVISIBLE) {
                    countinfo.setVisibility(View.VISIBLE);
                }

                locdata = new MyLocationData.Builder().direction(-1).accuracy(location.getRadius()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaidumap.setMyLocationData(locdata);


            }

        }

    }

    public Drawable getDrawAble(int clusterNum) {
        int radius = dp2px(getApplicationContext(), clusterRadius);
        if (clusterNum == 1) {
            return  null;

        } else if(clusterNum <5){
            return drawable;
        }else if (clusterNum < 10) {
            return  drawable1;

        } else if (clusterNum < 20) {
            return  drawable2;

        } else {
            return  drawable3;

        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {

    }

    public  void  ToMarkers(MapStatusModel mapStatusModel){
        showLoadDialog(null);
         regionItemList.clear();

         for(BdModel  bdModel : markers){

             if(mapStatusModel == MapStatusModel.ALL ||
                     bdModel.getMapStatus(0) == mapStatusModel){

                 RegionItem   item = new RegionItem(new LatLng(bdModel.bdlat,bdModel.bdlng),"i222",bdModel);
                 regionItemList.add(item);
             }
         }
        countloc = regionItemList.size();
        System.out.println("size==="+regionItemList.size());
        clusterOverlay.addAllClusterItems(regionItemList);

        clusterOverlay.assignClusters();
    }

    @Override
    public BitmapDescriptor getBitmapDes(MapStatusModel mapStatusModel) {
        switch (mapStatusModel){
            case FINISHED:

                return   greenBitmap;
            case  LAST:
                return  orangeBitmap;
            case UNFINISH:
                return   blackBitmap;
            case UPLOADED:
                return   blueBitmap;


        }
        return blackBitmap;
    }

    @Override
    public void onLoadFinish(int total) {
        countinfo.setText("定位总数：" + countloc+"    当前显示数:"+total);
        hideDialog();
    }

    @Override
    public void onLoadStart() {
        System.out.println("onStart");
        showLoadDialog(null);
    }

    // 操作设备
    public abstract void operationDevice(BdModel bdModel);

    public abstract void initData(List<BdModel> markers,List<ClusterItem>  regionItemList);

    // 导航
    public abstract void navDevice(BdModel bdModel);
}
