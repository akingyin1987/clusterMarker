package com.king.clustermarker.base;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;

import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.king.clustermarker.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 
 * @Description: TODO(百度地图基础类--Activity)
 * @author king
 * @date 2014-8-22 上午10:07:15
 * @version V1.0
 */
public class BaseBaiduMapActivity extends Activity {

	public MapView mMapView;
	public BaiduMap mBaidumap = null;

	public MyLocationData locdata = null; // 定位数据
	public MyLocationConfiguration locConfig = null;

	protected LocationClient mLocClient;
	public BDLocationListener myListener = null;

	// 定位模式
	public LocationMode mCurrentMode = LocationMode.NORMAL;

	// 地图Marker相关

	protected BitmapDescriptor blackBitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc_black);
	protected BitmapDescriptor blueBitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc_blue);
	protected BitmapDescriptor greenBitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc_green);
	protected BitmapDescriptor orangeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc_orange);
	protected BitmapDescriptor readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);

	// 地图类型切换相关（普通与卫星）
	public View poptypeView;
	public RadioGroup radioGroup;
	public RadioButton satelliteButton;
	public RadioButton layer_3dButton;
	public ImageButton map_layers;

	private PopupWindow mPopupWindow;
	// 时况
	public ImageButton road_condition;

	public ImageButton locationButton;// 定位切换相关

	public ImageButton cameraButton;// 查看所有

	public TextView countinfo;// 统计详情

	public Marker mCurrentMarker = null;

	public OverlayManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@SuppressLint("InflateParams")
	public void initbaseView(boolean openloc, View mainView) {
		mMapView = (MapView) mainView.findViewById(R.id.map_content);

		mBaidumap = mMapView.getMap();
		road_condition = (ImageButton) mainView.findViewById(R.id.road_condition);
		poptypeView = LayoutInflater.from(this).inflate(R.layout.map_layer, null);
		radioGroup = (RadioGroup) poptypeView.findViewById(R.id.layer_selector);
		satelliteButton = (RadioButton) poptypeView.findViewById(R.id.layer_satellite);
		layer_3dButton = (RadioButton) poptypeView.findViewById(R.id.layer_3d);
		map_layers = (ImageButton) mainView.findViewById(R.id.map_layers);
		locationButton = (ImageButton) mainView.findViewById(R.id.loc_follow_compass);
		countinfo = (TextView) mainView.findViewById(R.id.lnglat_info);
		cameraButton = (ImageButton) mainView.findViewById(R.id.loc_seeall);
		if (openloc) {
			locationButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					switch (mCurrentMode) {
					case NORMAL:
						mCurrentMode = LocationMode.FOLLOWING;
						locationButton.setImageResource(R.drawable.main_icon_follow);
						break;
					case COMPASS:
						mCurrentMode = LocationMode.NORMAL;
						locationButton.setImageResource(R.drawable.main_icon_location);
						if (null != manager) {
							manager.zoomToSpan();
						}

						break;
					case FOLLOWING:
						mCurrentMode = LocationMode.COMPASS;
						locationButton.setImageResource(R.drawable.main_icon_compass);
						break;
					default:
						break;
					}

					mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
				}
			});
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.layer_satellite) {
					mBaidumap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				} else if (checkedId == R.id.layer_3d) {
					mBaidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				}

			}
		});
		map_layers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initPopupWindow(map_layers, 0, 0);

			}
		});
		if (mBaidumap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
			layer_3dButton.setChecked(true);
		} else {
			satelliteButton.setChecked(true);
		}
		road_condition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBaidumap.isTrafficEnabled()) {
					road_condition.setImageResource(R.drawable.main_icon_roadcondition_off);
					mBaidumap.setTrafficEnabled(false);
				} else {
					road_condition.setImageResource(R.drawable.main_icon_roadcondition_on);
					mBaidumap.setTrafficEnabled(true);
				}

			}
		});
		

	}

	/**
	 * 初始化菜单
	 */
	@SuppressWarnings("deprecation")
	public void initPopupWindow(View v, int xoff, int yoff) {

		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(poptypeView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		}
		if (mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		} else {
			mPopupWindow.showAsDropDown(v, xoff, yoff);

		}
	}

	public void initBaseMapConfigure(boolean openloc) {

		// 普通地图
		mBaidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		locConfig = new MyLocationConfiguration(mCurrentMode, true, null);
		mBaidumap.setMyLocationConfigeration(locConfig);

		// 初始化地图等级
		MapStatus ms = new MapStatus.Builder().zoom(18).build();
		MapStatusUpdate mu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaidumap.animateMapStatus(mu);
		if (openloc) {
			// 定位初始化
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(5000);
			option.setIsNeedAddress(true);
			option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
			option.setProdName("watersys");
			option.SetIgnoreCacheException(true);
			mLocClient.setLocOption(option);
			// 开启定位层
			mBaidumap.setMyLocationEnabled(true);
		}

	}

	@Override
	protected void onResume() {
		if (null != mMapView) {
			mMapView.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (null != mMapView) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mMapView) {
			mMapView.onDestroy();
			mMapView = null;
		}

		if (null != blackBitmap) {
			blackBitmap.recycle();
			blackBitmap = null;
		}
		if (null != blueBitmap) {
			blueBitmap.recycle();
			blackBitmap = null;
		}
		if (null != orangeBitmap) {
			orangeBitmap.recycle();
			orangeBitmap = null;
		}
		if (null != readBitmap) {
			readBitmap.recycle();
			readBitmap = null;
		}
		if (null != greenBitmap) {
			greenBitmap.recycle();
			greenBitmap = null;
		}
	
		try {
			
			if(null != mLocClient && mLocClient.isStarted()){
			    mLocClient.stop();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
