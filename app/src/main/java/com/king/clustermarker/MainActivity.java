package com.king.clustermarker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.king.clustermarker.base.AbstractBaiduMapBrowseActivity;
import com.king.clustermarker.base.baseModel.MapStatusModel;
import com.king.clustermarker.map.ClusterClickListener;
import com.king.clustermarker.map.ClusterItem;
import com.king.clustermarker.map.ClusterOverlay;
import com.king.clustermarker.map.ClusterRender;
import com.king.clustermarker.map.RegionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity  implements BaiduMap.OnMapLoadedCallback
,ClusterRender,ClusterClickListener {

    public MapView   mapView;

    public BaiduMap    baiduMap;

    private int clusterRadius = 80;

    public    Drawable   drawable,drawable1,drawable2,drawable3;

    public    MyManager   manager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_marker_cluster);
        mapView = (MapView)findViewById(R.id.map_mapview);
        baiduMap = mapView.getMap();
        // 初始化地图等级
        baiduMap.setOnMapLoadedCallback(this);
        MapStatus ms = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mu = MapStatusUpdateFactory.newMapStatus(ms);
        baiduMap.animateMapStatus(mu);
        drawable = getApplication().getResources().getDrawable(
                R.drawable.m0);
        drawable1 =getApplication().getResources().getDrawable(
                R.drawable.m1);
        drawable2 = getApplication().getResources().getDrawable(
                R.drawable.m2);
        drawable3 =getApplication().getResources().getDrawable(
                R.drawable.m3);

    }


    public   static    int  minlat = (int) (39.780000 * 1E6);
    public   static    int  minlng = (int) (116.220000 * 1E6);
    public   static    int  Interval = 370000;


    public    class   MyManager  extends OverlayManager{

        private   List<OverlayOptions>  overlayOptionsList;

        public MyManager(BaiduMap baiduMap) {
            super(baiduMap);
            overlayOptionsList = new ArrayList<>();
        }

        public   void   addOverlay(OverlayOptions   overlayOptions){
            overlayOptionsList.add(overlayOptions);
        }

        public  void   removeOverlay(OverlayOptions  overlayOptions){
            overlayOptionsList.remove(overlayOptions);
        }

        public  void   clean(){
            overlayOptionsList.clear();
        }

        public   MyManager (BaiduMap  baiduMap,List<OverlayOptions>  overlayOptions){
            super(baiduMap);
            this.overlayOptionsList = new ArrayList<>();
            this.overlayOptionsList.addAll(overlayOptions);
        }

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            return overlayOptionsList;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            System.out.println("marker="+marker.getTitle());
            return false;
        }

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            return false;
        }
    }



    @Override
    public void onMapLoaded() {
         System.out.println("onMapLoaded");
        manager = new MyManager(baiduMap);
        baiduMap.setOnMarkerClickListener(manager);
        manager.addToMap();
        List<ClusterItem>   regionItems = new ArrayList<>();
        Random r = new  Random();
        for(int i=0 ; i<4000;i++){

            int rlat = r.nextInt(Interval);
            int rlng = r.nextInt(Interval);
            int lat = minlat + rlat;
            int lng = minlng + rlng;

            RegionItem   item = new RegionItem(new LatLng(lat/1E6,lng/1E6),"i"+i,null);
           regionItems.add(item);
        }
//        ClusterOverlay  clusterOverlay = new ClusterOverlay(baiduMap,regionItems,manager,
//                dp2px(getApplicationContext(), clusterRadius),this);
//        clusterOverlay.setClusterRenderer(this);
//        clusterOverlay.setOnClusterClickListener(this);
    }



    public Drawable getDrawAble(int clusterNum) {
        int radius = dp2px(getApplicationContext(), clusterRadius);
        if (clusterNum == 1) {
            return drawable;
//            return getApplication().getResources().getDrawable(
//                    R.drawable.icon_openmap_mark);
        } else if (clusterNum < 5) {
            return  drawable1;
//            BitmapDrawable drawable = new BitmapDrawable(drawCircle(radius,
//                    Color.argb(159, 210, 154, 6)));
//            return drawable;
        } else if (clusterNum < 10) {
            return  drawable2;
//            BitmapDrawable drawable = new BitmapDrawable(drawCircle(radius,
//                    Color.argb(199, 217, 114, 0)));
//            return drawable;
        } else {
            return  drawable3;
//            BitmapDrawable drawable = new BitmapDrawable(drawCircle(radius,
//                    Color.argb(235, 215, 66, 2)));
//            return drawable;
        }

    }



    private Bitmap drawCircle(int radius, int color) {

        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
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
        String content = "";
        for (ClusterItem clusterItem : clusterItems) {
            RegionItem item = (RegionItem) clusterItem;
            content += item.getTitle() + " ";
        }
        Toast.makeText(this,content,Toast.LENGTH_LONG).show();
    }

    @Override
    public BitmapDescriptor getBitmapDes(MapStatusModel mapStatusModel) {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mapView){
            mapView.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mapView){
            mapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != mapView){
            mapView.onResume();
        }
    }
}
