package com.king.clustermarker.base.baseModel;

import java.io.Serializable;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;


/**
 * 标定基础对象
 * @author Administrator
 *
 */
public abstract class BdModel  extends  Model implements  Serializable{
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name="bdlat")
    public     double      bdlat; //百度经纬度
    
    @Column(name="bdlng")
    public     double      bdlng;
    
    @Column(name="rfid")
    public     String      rfid;//RFID
    
    @Column(name="block0")
    public     String      block0;//block0
    
    
    
    
    public    abstract   MarkerColor       getMarkerColor(int  type);
    
    public    abstract   MapStatusModel         getMapStatus(int   type);
    
    public    abstract      String      getBdDeviceInfo();
    
    
    public    abstract      long     getModelId();
    
    
    public  abstract  List<BaseImgTextItem>  getBaseImgTextItems();   
    
    public  abstract    boolean    isLastOperation();
    

}
