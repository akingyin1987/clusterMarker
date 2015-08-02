package com.king.clustermarker.map;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.king.clustermarker.base.baseModel.MapStatusModel;

/**
 * @author yiyi.qi 聚合点的渲染规则
 */
public interface ClusterRender {
	/**
	 * 根据聚合点的元素数目返回渲染背景样式
	 * 
	 * @param clusterNum
	 * @return
	 */
	public Drawable getDrawAble(int clusterNum);

	public BitmapDescriptor  getBitmapDes(MapStatusModel  mapStatusModel,boolean  islast);
}
