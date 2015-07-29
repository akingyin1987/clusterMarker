package com.king.clustermarker.map;


import com.baidu.mapapi.model.LatLng;

/**
 * @author yiyi.qi 每个聚合元素类的接口
 */
public interface ClusterItem {

	/**
	 * 返回聚合元素的地理位置
	 * 
	 * @return
	 */
	public LatLng getPosition();
}
