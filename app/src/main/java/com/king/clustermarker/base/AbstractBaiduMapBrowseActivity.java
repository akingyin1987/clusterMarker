package com.king.clustermarker.base;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.overlayutil.OverlayManager;
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
public abstract  class AbstractBaiduMapBrowseActivity  {

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





    public    class    MyManager  extends OverlayManager{
        public MyManager(BaiduMap baiduMap) {
            super(baiduMap);
            listoverlayOptionses = new ArrayList<>();
        }

        private   List<OverlayOptions>    listoverlayOptionses ;


        public   void   addOverlayOption(OverlayOptions   overlayOptions){
            listoverlayOptionses.add(overlayOptions);
        }

        public   void   removeOverlayOption(OverlayOptions  overlayOptions){
            listoverlayOptionses.remove(overlayOptions);
        }

        public   void   addAllOverlayOption(List<OverlayOptions>  overlayOptionses){
            listoverlayOptionses.addAll(overlayOptionses);
        }

        public   void   clean(){
            listoverlayOptionses.clear();
        }

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            return listoverlayOptionses;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            System.out.println("onMarkerClick===="+marker.getTitle());
            return false;
        }

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            return false;
        }
    }
}
