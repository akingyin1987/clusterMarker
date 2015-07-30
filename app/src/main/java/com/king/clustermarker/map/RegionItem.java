package com.king.clustermarker.map;

import com.baidu.mapapi.model.LatLng;
import com.king.clustermarker.base.baseModel.BdModel;

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String mTitle;
    private BdModel bdModel;

    public RegionItem(LatLng latLng, String title, BdModel bdModel) {
        mLatLng = latLng;
        mTitle = title;
        this.bdModel = bdModel;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }

    @Override
    public BdModel getBdModel() {
        return bdModel;
    }

    public String getTitle() {
        return mTitle;
    }

}
