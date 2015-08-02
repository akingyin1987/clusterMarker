package com.king.clustermarker.map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.king.clustermarker.R;
import com.king.clustermarker.base.AbstractBaiduMapBrowseActivity;
import com.king.clustermarker.base.baseModel.MapStatusModel;
/**
 * @author yiyi.qi 用于展示聚合的overlay
 */
public class ClusterOverlay implements BaiduMap.OnMapStatusChangeListener,
        BaiduMap.OnMarkerClickListener {
    private List<ClusterItem> mPoints;
    private BaiduMap bdMap;
    private List<Cluster> mClusters;
    private int mClusterSize;
    private Projection mProjection;
    private Context mContext;
    private ExecutorService executor;
    private float level = 0;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private LatLngBounds bounds;
    private LatLng centerLatLng;
    private onLoadFinish  mloadFinish;

    public   int      getTotal(){
        return mPoints.size();
    }

    public   Cluster    getCluster(int  postion){
        if(postion >=0 && postion<mClusters.size()){
            return mClusters.get(postion);
        }
        return  null;
    }

    private AbstractBaiduMapBrowseActivity.MyManager manager;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {

            System.out.println("加载到MAP");
            addClusterToMap();
            if(null != mloadFinish){
                mloadFinish.onLoadFinish(tempPoints.size());
            }
        }
    };

    /**
     * @param bdMap
     * @param clusterSize 聚合范围的大小
     * @param context
     */
    public ClusterOverlay(BaiduMap bdMap, AbstractBaiduMapBrowseActivity.MyManager manager, int clusterSize, Context context) {
        this(bdMap, null, manager, clusterSize, context);

    }

    /**
     * @param bdMap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(BaiduMap bdMap, List<ClusterItem> clusterItems, AbstractBaiduMapBrowseActivity.MyManager manager,
                          int clusterSize, Context context) {
        if (clusterItems != null) {
            mPoints = new ArrayList<>();
            mPoints.addAll(clusterItems);
        } else {
            mPoints = new ArrayList<>();
        }
        this.manager = manager;
        mContext = context;
        mClusters = new ArrayList<>();
        this.bdMap = bdMap;

        this.bdMap.setOnMapStatusChangeListener(this);
        this.bdMap.setOnMarkerClickListener(this);
        mProjection = this.bdMap.getProjection();
        mClusterSize = clusterSize;
        executor = Executors.newFixedThreadPool(2);
        bounds = bdMap.getMapStatus().bound;

        assignClusters();

    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    public  void  setOnLoadFinish(onLoadFinish   loadFinish){
        mloadFinish = loadFinish;
    }

    /**
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        mPoints.add(item);
        assignSingleCluster(item);

    }

    public  void  addAllClusterItems(List<ClusterItem>  list){
        mPoints.clear();
        tempPoints.clear();

        mPoints.addAll(list);
        System.out.println("共==" + mPoints.size());

    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    /**
     * 将聚合元素添加至地图上
     */
    private synchronized void addClusterToMap() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                manager.removeAll();
                manager.removeFromMap();
                int index = 0;
                for (Cluster cluster : mClusters) {
                    addSingleClusterToMap(cluster, index);
                    index++;
                }

                manager.addToMap();
            }
        });


        //manager.zoomToSpan();
    }

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster,int index) {
        LatLng latlng = cluster.getCenterLatLng();
//        TextOptions   textOptions = new TextOptions();
//       textOptions.position(latlng);
//        textOptions.fontSize(50);
//        textOptions.bgColor( Color.argb(159, 210, 154, 6));
//        textOptions.text(String.valueOf(cluster.getClusterCount()));

        MarkerOptions markerOptions = new MarkerOptions();
        MapStatusModel   mapStatusModel = null == cluster.getBdModel()?null:cluster.getBdModel().getMapStatus(1);

        markerOptions.anchor(0.5f, 0.5f)
                .icon(getBitmapDes(cluster.getClusterCount(),mapStatusModel,cluster.islast())).position(latlng);
        Bundle  bundle = new Bundle();
        bundle.putInt("index",index);
        markerOptions.extraInfo(bundle);
        manager.addOverlayOption(markerOptions);
        cluster.setOverlayOptions(markerOptions);
        //cluster.setMarker(marker);
    }

    /**
     * 获取每个聚合点的绘制样式
     */


    private BitmapDescriptor getBitmapDes(int num,MapStatusModel mapStatusModel,boolean  islast) {

        if(num == 1){
            return  mClusterRender.getBitmapDes(mapStatusModel,islast);
        }
        TextView textView = new TextView(mContext);

        String tile = String.valueOf(num);
        textView.setText(tile);

        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        if (mClusterRender != null && mClusterRender.getDrawAble(num) != null) {
            textView.setBackgroundDrawable(mClusterRender.getDrawAble(num));
        } else {
            textView.setBackgroundResource(R.drawable.defaultcluster);
        }
        return BitmapDescriptorFactory.fromView(textView);
    }

    /**
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {
        MarkerOptions overlayOptions = (MarkerOptions) cluster.getMoverlayOptions();
        MapStatusModel   mapStatusModel = null == cluster.getBdModel()?null:cluster.getBdModel().getMapStatus(1);
        overlayOptions.icon(getBitmapDes(cluster.getClusterCount(),mapStatusModel,cluster.islast()));
//        TextOptions   textOptions = (TextOptions)cluster.getMoverlayOptions();
//        textOptions.text(String.valueOf(cluster.getClusterCount()));
//        textOptions.fontSize(50);
//
//        textOptions.bgColor( Color.argb(159, 210, 154, 6));
    }

    /**
     * 对点进行聚合
     */
    List<ClusterItem>   tempPoints = new ArrayList<>();
    public void assignClusters() {
        tempPoints.clear();
        if(null != mloadFinish){
            mloadFinish.onLoadStart();
            System.out.println("开始");
        }
        for (Cluster cluster : mClusters) {
            cluster.onDestory();
        }
        mClusters.clear();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    for (ClusterItem clusterItem : mPoints) {
                        LatLng latlng = clusterItem.getPosition();

                        if (bounds.contains(latlng)) {
                            tempPoints.add(clusterItem);
                        }
                    }
                    int  size = tempPoints.size();
                    for(ClusterItem  clusterItem : tempPoints){

                            LatLng latlng = clusterItem.getPosition();
                            Point point = mProjection.toScreenLocation(latlng);
                            if(size >300){

                                Cluster cluster = getCluster(point);
                                if (cluster != null) {
                                    cluster.addClusterItem(clusterItem);
                                } else {
                                    cluster = new Cluster(point, latlng);
                                    mClusters.add(cluster);
                                    cluster.addClusterItem(clusterItem);
                                }
                            }else{
                                Cluster  cluster = new Cluster(point,latlng);
                                cluster.addClusterItem(clusterItem);
                                mClusters.add(cluster);
                            }
                    }
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
     */
    private void assignSingleCluster(ClusterItem clusterItem) {
        LatLng latlng = clusterItem.getPosition();
        Point point = mProjection.toScreenLocation(latlng);
        Cluster cluster = getCluster(point);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            updateCluster(cluster);
        } else {
            cluster = new Cluster(point, latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            addSingleClusterToMap(cluster,mClusters.size()-1);
        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param point
     * @return
     */
    private Cluster getCluster(Point point) {
        for (Cluster cluster : mClusters) {
            Point poi = cluster.getCenterPoint();
            double distance = getDistanceBetweenTwoPoints(point.x, point.y,
                    poi.x, poi.y);
            if (distance < mClusterSize) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * 两点的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double getDistanceBetweenTwoPoints(double x1, double y1, double x2,
                                               double y2) {
        double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return distance;
    }

    public double getDistanceBetweenTwoPoints(LatLng l1, LatLng l2) {
        return DistanceUtil.getDistance(l1, l2);
    }


    //点击事件
    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mClusterClickListener == null) {
            return false;
        }
        for (Cluster cluster : mClusters) {
            if (arg0.equals(cluster.getMarker())) {
                mClusterClickListener.onClick(arg0, cluster.getClusterItems());
                return false;
            }
        }
        return false;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        //放大缩小完成后对聚合点进行重新计算
        float leveltemp = mapStatus.zoom;

        if (leveltemp != level) {
            bounds = mapStatus.bound;
            assignClusters();
            level = leveltemp;
            centerLatLng = mapStatus.bound.getCenter();
        } else {
            bounds = mapStatus.bound;
            //当缩放等级不变的时候，看中心点是否发生变化
            if (null == centerLatLng) {
                assignClusters();
            } else if (centerLatLng.latitude != bounds.getCenter().latitude ||
                    centerLatLng.longitude != bounds.getCenter().longitude) {
                assignClusters();
            }
            centerLatLng = bounds.getCenter();
        }
    }


}
