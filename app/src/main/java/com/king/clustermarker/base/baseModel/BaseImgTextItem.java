package com.king.clustermarker.base.baseModel;

import java.io.Serializable;
import android.text.TextUtils;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;


public abstract class BaseImgTextItem extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	// 本地图片绝对路径

	@Column(name = "localPath")
	public String localPath;

	// 服务器路径相对路径
	@Column(name = "webPath")
	public String webPath;

	// 文本描述
	@Column(name = "textdesc")
	public String textdesc;

	@Column(name = "originalLocalPath")
	public String originalLocalPath;// 原始图片
	
	@Column(name="copyName")
	public String  copyName;//复制图片名称

	// 顺序
	@Column(name = "sort")
	public int sort;
	
	@Column(name="style")
	public    int    style;//1=文字 2=图片 3=视频
	
	//是否被选中
	private   boolean   ischecked;
	
	public    int    copy;

	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	// 图文是否为空

	public boolean isEmptyImageText() {
		if (TextUtils.isEmpty(textdesc) && (TextUtils.isEmpty(localPath))) {
			return true;
		}
		return false;
	}


}
