package com.king.clustermarker.base;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.DistanceUtil;
import com.king.clustermarker.R;
import com.king.clustermarker.base.baseModel.BdModel;
import com.king.clustermarker.base.baseModel.MapStatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 *  
 * User: Geek_Soledad(msdx.android@qq.com) 
 * Date: 2015-07-30 
 * Time: 18:07 
 * FIXME 
 */
public abstract class AbstractBaiduMapBrowseActivity extends BaseBaiduMapActivity {

    public List<BdModel> markers = new ArrayList<>();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.clearCache();

        View mainView = LayoutInflater.from(this).inflate(R.layout.activity_browsemap, null);
        setContentView(mainView);
        myListener = new MyLocationListenner();

        initUi(mainView);
        MapConfigure();

        mBaidumap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

            }
        });
        showLoadDialog(null);
        new Thread() {

            @Override
            public void run() {

                super.run();
                initData(markers);
                initDataHandler.sendEmptyMessage(1);
            }

        }.start();
    }

    public Handler initDataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == 1) {
                if (null == manager) {
                    manager = new MyManager(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(manager);
                    manager.addToMap();
                }
                manager.removeFromMap();
                manager.removeAll();

                ToMarkers(MapStatusModel.ALL);

            } else if (msg.what == -1) {
                hideDialog();
                finish();
                showToast("初始化数据出错了！");
            } else if (msg.what == 2) {

                hideDialog();

                countinfo.setText("定位总数：" + countloc);

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
                        //   ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_finished) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.FINISHED;
                        //  ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_last) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.LAST;
                        //   ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_unfinsh) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.UNFINISH;
                        //    ToMarkers(mapStatusModel);
                    } else if (checkedId == R.id.rb_uploaded) {
                        manager.removeFromMap();
                        manager.removeAll();
                        mapStatusModel = MapStatusModel.UPLOADED;
                        //   ToMarkers(mapStatusModel);
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
                showPopupBottonWindow(null);
                return false;
            }
        });

        cameraButton.setVisibility(View.GONE);

    }

    public void showPopupBottonWindow(Marker marker) {
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
            infoTextView.setText(Html.fromHtml(markers.get(index).getBdDeviceInfo()));
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
                    if (null != mCurrentMarker) {
                        int index = mCurrentMarker.getExtraInfo().getInt("index");
                        BdModel marderVo = markers.get(index);
                        if (marderVo.isLastOperation()) {
                            mCurrentMarker.setIcon(orangeBitmap);
                            return;
                        }
                        switch (marderVo.getMarkerColor(style)) {
                            case BLACK:
                                mCurrentMarker.setIcon(blackBitmap);
                                break;
                            case BLUE:
                                mCurrentMarker.setIcon(blueBitmap);
                                break;
                            case GREEN:
                                mCurrentMarker.setIcon(greenBitmap);
                                break;
                            case ORANGE:
                                mCurrentMarker.setIcon(orangeBitmap);
                                break;
                            default:
                                break;
                        }
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
            System.out.println("onMarkerClick====" + marker.getTitle());
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

                countInfo();
            }

        }

    }


    public void countInfo() {


        countinfo.setText("定位总数:" + countloc);
    }

    // 操作设备
    public abstract void operationDevice(BdModel bdModel);

    public abstract void initData(List<BdModel> markers);

    // 导航
    public abstract void navDevice(BdModel bdModel);
}
