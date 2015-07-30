package com.king.clustermarker;


import com.baidu.mapapi.model.LatLng;
import com.king.clustermarker.base.AbstractBaiduMapBrowseActivity;
import com.king.clustermarker.base.baseModel.BdModel;
import com.king.clustermarker.base.baseModel.MapStatusModel;
import com.king.clustermarker.map.ClusterItem;
import com.king.clustermarker.map.RegionItem;


import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2015/7/30.
 */
public class TestBaiduMap  extends AbstractBaiduMapBrowseActivity  {
    @Override
    public void operationDevice(BdModel bdModel) {

    }

    public   static    int  minlat = (int) (39.780000 * 1E6);
    public   static    int  minlng = (int) (116.220000 * 1E6);
    public   static    int  Interval = 370000;
    @Override
    public void initData(List<BdModel> markers,List<ClusterItem>  regionItemList) {
        Random r = new  Random();

        for(int i=0 ; i<4000;i++){

            int rlat = r.nextInt(Interval);
            int rlng = r.nextInt(Interval);
            int lat = minlat + rlat;
            int lng = minlng + rlng;
            Meter  meter = new Meter();
            meter.mapStatusModel = i%3==0?MapStatusModel.FINISHED:i%3==1?MapStatusModel.UNFINISH:MapStatusModel.UPLOADED;
            if(i == 3999){
                meter.isLast = true;
            }
            meter.bdlat = lat/1E6;
            meter.bdlng = lat/1E6;
            markers.add(meter);
            RegionItem   item = new RegionItem(new LatLng(lat/1E6,lng/1E6),"i"+i,meter);
            regionItemList.add( item);
        }

    }

    @Override
    public void navDevice(BdModel bdModel) {

    }
}
