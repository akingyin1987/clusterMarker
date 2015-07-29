package com.king.clustermarker.map;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Point;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

class Cluster {
	private Point mPoint;
	private LatLng mLatLng;
	private List<ClusterItem> mClusterItems;
	private Marker mMarker;

	Cluster(Point point, LatLng latLng) {
		mPoint = point;
		mLatLng = latLng;
		mClusterItems = new ArrayList<ClusterItem>();
	}

	void addClusterItem(ClusterItem clusterItem) {
		mClusterItems.add(clusterItem);
	}

	int getClusterCount() {
		return mClusterItems.size();
	}

	Point getCenterPoint() {
		return mPoint;
	}

	LatLng getCenterLatLng() {
		return mLatLng;
	}

	void setMarker(Marker marker) {
		mMarker = marker;
	}

	Marker getMarker() {
		return mMarker;
	}

	List<ClusterItem> getClusterItems() {
		return mClusterItems;
	}
}
