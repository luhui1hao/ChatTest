package com.example.chattest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.example.chattest.view.ZoomImageView;

public class PictureActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.picture_activity_layout);
		findViewById(R.id.fanhui_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Intent intent = getIntent();
		String picAddress = intent.getStringExtra("picAddress");
		ZoomImageView zoomImageView = (ZoomImageView)findViewById(R.id.zoom_imageview);
		Bitmap bm = BitmapFactory.decodeFile(picAddress);
		zoomImageView.setImageBitmap(bm);
	}
}
