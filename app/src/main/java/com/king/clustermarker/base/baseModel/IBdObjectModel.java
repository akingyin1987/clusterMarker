package com.king.clustermarker.base.baseModel;

import java.util.List;

/**
 *  
 * User: Geek_Soledad(msdx.android@qq.com) 
 * Date: 2015-07-30 
 * Time: 17:44 
 * 百度地图浏览查询数据接口
 */
public interface IBdObjectModel {

    //查询所有有经纬度的标定管理对象
    List<BdModel>   queryBdObjcet();

    //通过状态查询
    List<BdModel>   queryBdObject(MapStatusModel   mapStatusModel);


}
