package com.king.clustermarker.map;

import com.baidu.mapapi.map.Marker;

import java.util.List;


/**
 * @author yiyi.qi 聚合点的点击监听
 */
public interface ClusterClickListener {

	/**
	 * 点击聚合点的回调处理函数
	 * 
	 * @param marker
	 *            点击的聚合点
	 * @param clusterItems
	 *            聚合点所包含的元素
	 */
	public void onClick(Marker marker, List<ClusterItem> clusterItems);
}
