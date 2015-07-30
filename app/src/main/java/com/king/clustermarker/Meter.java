package com.king.clustermarker;

import com.king.clustermarker.base.baseModel.BaseImgTextItem;
import com.king.clustermarker.base.baseModel.BdModel;
import com.king.clustermarker.base.baseModel.MapStatusModel;
import com.king.clustermarker.base.baseModel.MarkerColor;

import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class Meter extends BdModel{

    @Override
    public MarkerColor getMarkerColor(int type) {
        return MarkerColor.BLACK;
    }

    public    MapStatusModel   mapStatusModel = MapStatusModel.FINISHED;

    @Override
    public MapStatusModel getMapStatus(int type) {
        return mapStatusModel;
    }

    @Override
    public String getBdDeviceInfo() {
        return "2222222222222222";
    }

    @Override
    public long getModelId() {
        return 0;
    }

    @Override
    public List<BaseImgTextItem> getBaseImgTextItems() {
        return null;
    }

    public   boolean  isLast = false;

    @Override
    public boolean isLastOperation() {
        return isLast;
    }
}
