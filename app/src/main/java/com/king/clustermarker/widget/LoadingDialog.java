package com.king.clustermarker.widget;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.king.clustermarker.R;

/**
 * 
 * 
 * 说明 加载对话框
 */
public class LoadingDialog extends Dialog {

    private TextView content;

	public LoadingDialog(Context context) {

	super(context,R.style.MyDialogStyle);
	init();
	
    }

    public LoadingDialog(Context context, String message) {
	super(context,R.style.MyDialogStyle);
	
    }

    public LoadingDialog(Context context, int theme, String message) {
	super(context,R.style.MyDialogStyle);
	
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);


    }

    public void init() {
	setContentView(R.layout.loadingdialog);
	setCanceledOnTouchOutside(false);
	content = (TextView) findViewById(R.id.tips_msg);
	//content.setText(this.message);
    }

    public void setText(String message) {
	
	content.setText(message);
	
    }

    public void setText(int resId) {
	         setText(getContext().getResources().getString(resId));
    }
}