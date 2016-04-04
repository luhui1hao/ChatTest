package com.example.chattest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.chattest.bean.Group;
import com.example.chattest.bean.Info;
import com.example.chattest.bean.MessageForward;
import com.example.chattest.bean.MessageGroup;
import com.example.chattest.pickpicture.ImgFileListActivity;
import com.example.chattest.recorder.AudioRecorderButton;
import com.example.chattest.recorder.AudioRecorderButton.AudioFinishRecorderListener;
import com.example.chattest.recorder.MediaManager;
import com.example.chattest.service.DownloadService;
import com.example.chattest.sqlite.MyDatabaseHelper;
import com.example.chattest.utils.FileUtils;
import com.example.chattest.xml.Forward;
import com.handkoo.smartvideophone05.utils.HK_Message_Tool;
import com.handkoo.smartvideophone05.utils.HK_Message_XS_Util;

public class MainActivity extends BaseActivity {
	public static final int MY_VISIBLE = 1;
	public static final int MY_INVISIBLE = 0;
	public static final int FLASH_LIGHT_CLOSED = 0;
	public static final int FLASH_LIGHT_OPENED = 1;
	private int isVisible = MY_VISIBLE;
	private int flashLightState = FLASH_LIGHT_CLOSED;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean isPreview = false;// 是否在预览中
	private Camera camera;// 定义系统所用的照相机
	private int screenWidth, screenHeight;
	private Button chatGroupBtn;
	private Button flashLightBtn;// 闪光灯开关按钮
	private Button cameraBtn;// 拍照按钮
	private LinearLayout linearLayoutChat;
	public static List<Info> msgList = new ArrayList<Info>();
	private static MsgAdapter adapter;
	private static ListView listViewChat;
	private Button sendBtn;// 发送按钮
	private EditText editText;// 输入框
	private ButtonListener btnListener;
	private Camera.Parameters parameters;
	private Button dadianhuaBtn;
	private String picAddressDir;
	private byte[] picData;
	private MyDatabaseHelper dbHelper;
	public static final String TAG = "MainActivity";
	public static final int IMAGE_REQUEST = 1;
	private Button pickPicBtn;
	ArrayList<String> listfile = new ArrayList<String>();// 返回图片的路径
	private Bundle bundle;
	private IntentFilter intentFilter;
	private UpdateBroadcastReceiver receiver;

	// recorder要用到的变量
	private AudioRecorderButton mAudioRecorderButton;
	private View animView;
	private View animViewLeft;

	// 临时用的
	private int groupId = 100220;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// 设置图片存储路径
		picAddressDir = FileUtils.getPicDirAdd();
		// 注册广播
		intentFilter = new IntentFilter();
		intentFilter.addAction("COM_HANDKOO_CREATE_GROUP_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_DELETE_GROUP_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_FORWARD_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_CREATE_GROUP");
		intentFilter.addAction("COM_HANDKOO_DELETE_GROUP");
		intentFilter.addAction("COM_HANDKOO_FORWARD");
		receiver = new UpdateBroadcastReceiver();
		registerReceiver(receiver, intentFilter);
		// 初始化数据库
		initSQLite();
		// 设置相机
		setCameraFun();

		// 找到聊天界面布局
		linearLayoutChat = (LinearLayout) findViewById(R.id.linearLayout_chat);
		// 创建按键监听器
		btnListener = new ButtonListener();
		// 找到分组按钮，并绑定监听器
		chatGroupBtn = (Button) findViewById(R.id.chatgroup_btn_right_bar);
		chatGroupBtn.setOnClickListener(btnListener);
		// 找到闪光灯按钮，并给它绑定监听器
		flashLightBtn = (Button) findViewById(R.id.flash_light_btn);
		flashLightBtn.setOnClickListener(btnListener);
		// 找到拍照按钮并给它绑定监听器
		cameraBtn = (Button) findViewById(R.id.camera_btn);
		cameraBtn.setOnClickListener(btnListener);

		// 创建适配器并设置它
		adapter = new MsgAdapter(this, msgList);
		listViewChat = (ListView) findViewById(R.id.listView_chat);
		listViewChat.setAdapter(adapter);
		// 找到输入框
		editText = (EditText) findViewById(R.id.editText_chat);
		// 找到发送按钮并给它绑定监听器
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(btnListener);

		// 找到打电话按钮并给它绑定监听器
		dadianhuaBtn = (Button) findViewById(R.id.dadianhua_btn);
		dadianhuaBtn.setOnClickListener(btnListener);

		// 找到图片按钮并给它绑定监听器
		pickPicBtn = (Button) findViewById(R.id.pic_pic_btn);
		pickPicBtn.setOnClickListener(btnListener);

		// 初始化语音相关
		// 这个是自定义的Button，它会响应什么事件早就在里面写好了，所以只要直接创建它的对象就好了，什么监听器什么的都不用绑定了
		mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.anzhushuohua_btn);
		mAudioRecorderButton
				.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {

					public void onFinish(float seconds, String localfilename) {
						Info info = new Info();
						info.setFileType(Info.AUDIO_TYPE);
						info.setSendType(Info.GROUP);
						info.setGroupId(groupId);
						info.setIdentify(null);
						info.setDetail(localfilename);
						info.setTime("");
						info.setLocalFileName(localfilename);
						info.setSendOrRecvFlag(Info.TYPE_SENT);
						info.setSeconds(seconds);
						// 发送到服务器
						postInfo(info);
						// 存入数据库
						dbHelper.insertInfo(info);
						// ########################################发送语音Msg在这里
						msgList.add(info);
						adapter.notifyDataSetChanged(); // 通知更新的内容
						listViewChat.setSelection(msgList.size()); // 将lisview设置为最后一个
					}

				});
		// 给ListView每个Item设置响应事件
		// listView的item点击事件
		listViewChat.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Info info = msgList.get(position);
				// 当点击的是音频的时候
				if (info.getFileType().equals(Info.AUDIO_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_SENT) {
					// 播放动画（帧动画）
					if (animView != null) {
						animView.setBackgroundResource(R.drawable.adj);
						animView = null;
					}
					animView = view.findViewById(R.id.id_recorder_anim);
					animView.setBackgroundResource(R.drawable.play_anim);
					AnimationDrawable animation = (AnimationDrawable) animView
							.getBackground();
					animation.start();
					// 播放录音
					MediaManager.playSound(info.getLocalFileName(),
							new MediaPlayer.OnCompletionListener() {

								public void onCompletion(MediaPlayer mp) {
									animView.setBackgroundResource(R.drawable.adj);
								}
							});
				} else if (info.getFileType().equals(Info.AUDIO_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_RECEIVED) {
					// 播放动画（帧动画）
					if (animViewLeft != null) {
						animViewLeft.setBackgroundResource(R.drawable.adj_left);
						animViewLeft = null;
					}
					animViewLeft = view
							.findViewById(R.id.id_recorder_anim_left);
					animViewLeft
							.setBackgroundResource(R.drawable.play_anim_left);
					AnimationDrawable animation = (AnimationDrawable) animViewLeft
							.getBackground();
					animation.start();
					// 播放录音
					MediaManager.playSound(info.getLocalFileName(),
							new MediaPlayer.OnCompletionListener() {

								public void onCompletion(MediaPlayer mp) {
									animViewLeft
											.setBackgroundResource(R.drawable.adj_left);
								}
							});
				}

				// 当点击的是图片的时候
				if (info.getFileType().equals(Info.IMAGE_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_SENT) {
					// 进入另一个Activity用来对照片进行缩放
					Intent picIntent = new Intent(MainActivity.this,
							PictureActivity.class);
					picIntent.putExtra("picAddress", info.getLocalFileName());
					startActivity(picIntent);
				}
			}
		});

		// 测试方法，用完要删
		myTestFun();

		// 连接消息服务器
		startMsg();
	}

	private void initSQLite() {
		dbHelper = MyDatabaseHelper.getInstance(MainActivity.this);
		dbHelper.getWritableDatabase();
	}

	// 测试方法，用完要删
	private void myTestFun() {
		findViewById(R.id.my_test_btn1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 获取输入框的内容
						String content = editText.getText().toString();
						if (!"".equals(content)) {
							Info info = new Info();
							info.setFileType(Info.TEXT_TYPE);
							info.setSendType(Info.GROUP);
							info.setGroupId(groupId);
							info.setIdentify(null);
							info.setDetail(content);
							info.setTime("");
							info.setSendOrRecvFlag(Info.TYPE_RECEIVED);
							msgList.add(info);
							adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
							listViewChat.setSelection(msgList.size()); // 将ListView定位到最后一行
							editText.setText(""); // 清空输入框中的内容
						}
					}
				});
		/*
		 * findViewById(R.id.my_test_btn2).setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Bitmap testBitmap =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		 * Msg msg = new Msg(Msg.TYPE_RECEIVED, testBitmap, null);
		 * msgList.add(msg); adapter.notifyDataSetChanged(); //
		 * 当有新消息时，刷新ListView中的显示 listViewChat.setSelection(msgList.size()); //
		 * 将ListView定位到最后一行 editText.setText(""); // 清空输入框中的内容 // 释放Bitmap资源 if
		 * (!testBitmap.isRecycled()) { testBitmap.recycle(); } } });
		 */
		findViewById(R.id.my_test_btn3).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Info info = new Info();
						info.setFileType(Info.AUDIO_TYPE);
						info.setSendType(Info.GROUP);
						info.setGroupId(groupId);
						info.setIdentify(null);
						info.setDetail(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/aMyReceivedRecorder/abc.amr");
						info.setTime("");
						info.setLocalFileName(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/aMyReceivedRecorder/abc.amr");
						info.setSendOrRecvFlag(Info.TYPE_RECEIVED);
						info.setSeconds(3f);
						msgList.add(info);
						adapter.notifyDataSetChanged(); // 通知更新的内容
						listViewChat.setSelection(msgList.size()); // 将lisview设置为最后一个
					}
				});
		findViewById(R.id.my_test_btn4).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 删除表中所有数据
						dbHelper.clear();
						// 清空ListView
						msgList.clear();
						adapter.notifyDataSetChanged();
					}
				});

	}

	private void setCameraFun() {
		// 获取屏幕的宽和高
		getScreenWidthAndHeight();
		// 获取SurfaceView对象
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		// 获取SurfaceHolder对象
		surfaceHolder = surfaceView.getHolder();
		// 设置该Surface不需要自己维护缓冲区
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 为SurfaceHolder添加一个回调监听器
		surfaceHolder.addCallback(new Callback() {

			// 这个方法是当surfaceView发生改变后调用的
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}

			// 这个方法是surface 被创建后调用的
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 初始化摄像头
				initCamera();
			}

			// 这个是当surfaceView销毁时调用的.
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (camera != null) {
					if (isPreview) {
						camera.stopPreview();
					}
					camera.release();
					camera = null;
					isPreview = false;
				}
			}

		});
	}

	private void getScreenWidthAndHeight() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 获取窗口管理器
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		// 获取屏幕的宽和高
		display.getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
	}

	private void initCamera() {
		if (!isPreview) {
			// 此处默认打开后置摄像头
			// 通过改变参数可以打开前置摄像头
			camera = Camera.open(0);
			// camera.setDisplayOrientation(90);
		}
		if (camera != null && !isPreview) {
			try {
				parameters = camera.getParameters();
				// // 设置预览照片的大小
				// parameters.setPreviewSize(screenWidth, screenHeight);
				// // 设置预览照片时每秒显示多少帧的最小值和最大值
				// parameters.setPreviewFpsRange(4, 10);
				// // 设置图片格式
				// parameters.setPictureFormat(ImageFormat.JPEG);
				// // 设置JPG照片的质量
				// parameters.set("jpeg-quality", 100);
				// // 设置照片的大小
				// parameters.setPictureSize(screenWidth, screenHeight);
				// 通过SurfaceView显示取景画面
				camera.setPreviewDisplay(surfaceHolder);
				// 开始预览
				camera.startPreview();

			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}

	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chatgroup_btn_right_bar:
				if (isVisible == MY_VISIBLE) {
					isVisible = MY_INVISIBLE;
					// 设置为不可见
					linearLayoutChat.setVisibility(View.GONE);
				} else if (isVisible == MY_INVISIBLE) {
					isVisible = MY_VISIBLE;
					// 设置为可见
					linearLayoutChat.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.flash_light_btn:
				// 判断闪光灯状态
				if (flashLightState == FLASH_LIGHT_CLOSED) {
					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
					camera.setParameters(parameters);
					flashLightState = FLASH_LIGHT_OPENED;
					flashLightBtn.setBackgroundResource(R.drawable.flashopen);
				} else if (flashLightState == FLASH_LIGHT_OPENED) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
					flashLightState = FLASH_LIGHT_CLOSED;
					flashLightBtn.setBackgroundResource(R.drawable.flashclose);
				}
				break;
			case R.id.camera_btn:
				if (camera != null) {
					// 控制摄像头自动对焦后才拍照
					camera.autoFocus(autoFocusCallback);
				}
				break;
			case R.id.quit_camera_btn:
				break;
			case R.id.send_btn:
				// 获取输入框的内容
				String content = editText.getText().toString();
				if (!"".equals(content)) {
					Info info = new Info();
					info.setFileType(Info.TEXT_TYPE);
					info.setSendType(Info.GROUP);
					info.setGroupId(groupId);
					info.setIdentify(null);
					info.setDetail(content);
					info.setTime("");
					info.setLocalFileName(null);
					info.setSendOrRecvFlag(Info.TYPE_SENT);
					// 发送到服务器
					postInfo(info);
					// 将文本内容存入数据库
					dbHelper.insertInfo(info);
					// ###################################发送Text的Msg
					msgList.add(info);
					adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
					listViewChat.setSelection(msgList.size()); // 将ListView定位到最后一行
					editText.setText(""); // 清空输入框中的内容
				}
				break;
			case R.id.dadianhua_btn:
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("是否拨打此电话？")
						.setMessage("13033516935")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent dadianhuaIntent = new Intent(
												Intent.ACTION_CALL,
												Uri.parse("tel:"
														+ "13033516935"));
										MainActivity.this
												.startActivity(dadianhuaIntent);
									}

								}).setNegativeButton("否", null)
						.setCancelable(false).show();
				break;
			case R.id.pic_pic_btn:
				// Intent intent = new Intent(Intent.ACTION_PICK,
				// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// startActivityForResult(intent, IMAGE_REQUEST);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ImgFileListActivity.class);
				startActivity(intent);
				break;
			}
		}

	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		// 当自动对焦时激发该方法
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()方法需要传入3个监听器参数
				// 第1个监听器：当用户按下快门时激发该监听器
				// 第2个监听器：当相机获取原始照片时激发该监听器
				// 第3个监听器：当相机获取JPG照片时激发该监听器
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// 按下快门瞬间会执行此处代码
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// 此处代码可以决定是否需要保存原始照片信息
					}
				}, myJpegCallback);
			}
		}
	};

	PictureCallback myJpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			picData = data;
			// 此处是对图片的处理逻辑
			// 根据拍照所得的数据创建位图
			final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
					data.length);
			// 加载/layout/save.xml文件对应的布局资源
			View saveDialog = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.dialog_picture, null);
			// 获取saveDialog对话框上的ImageView组件
			ImageView show = (ImageView) saveDialog.findViewById(R.id.picture);
			// 显示刚刚拍得的照片
			show.setImageBitmap(bm);
			// 使用对话框显示saveDialog组件
			new AlertDialog.Builder(MainActivity.this)
					.setView(saveDialog)
					.setTitle("是否发送图片？")
					.setCancelable(false)
					.setPositiveButton("发送",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 将图片保存到本地
									String fileName = FileUtils
											.generatePicName();
									try {
										File picAdrFile = new File(
												picAddressDir);
										if (!picAdrFile.exists()) {
											picAdrFile.mkdirs();
										}
										File file = new File(picAddressDir,
												fileName);
										FileOutputStream os = new FileOutputStream(
												file);
										os.write(picData);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 生成Info对象
									Info info = new Info();
									info.setFileType(Info.IMAGE_TYPE);
									info.setSendType(Info.GROUP);
									info.setGroupId(groupId);
									info.setIdentify(null);
									info.setDetail(picAddressDir + "/"
											+ fileName);
									info.setTime("");
									info.setLocalFileName(picAddressDir + "/"
											+ fileName);
									info.setSendOrRecvFlag(Info.TYPE_SENT);
									//
									info.setBitmap(Bitmap.createScaledBitmap(
											bm, 200, 200, true));
									// 发送到服务器
									postInfo(info);
									// 将图片存储地址存入数据库
									dbHelper.insertInfo(info);
									// 将图片发送出去，并且在聊天框中显示出来
									// ####################################发送图片Msg在这里
									msgList.add(info);
									adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
									listViewChat.setSelection(msgList.size()); // 将ListView定位到最后一行
									// 释放Bitmap资源
									if (!bm.isRecycled()) {
										bm.recycle();
									}
								}
							}).setNegativeButton("取消", null).show();
			// 重新浏览
			camera.stopPreview();
			camera.startPreview();
			isPreview = true;
		}
	};

	// ********************************************************************
	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
		// 将List清空
		msgList.clear();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
		// 从数据库中取出数据，来填充List
		// 查询chatContent表中所有的数据
		Cursor cursor = dbHelper.getReadableDatabase().query("GroupChatInfos",
				null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// 遍历Cursor对象，取出数据并生成Info对象
				String datatype = cursor.getString(cursor
						.getColumnIndex("datatype"));
				Log.d("MainActivity", "datatype is " + datatype);
				int groupId = cursor.getInt(cursor.getColumnIndex("groupid"));
				Log.d("MainActivity", "groupid is " + groupId);
				String thesendername = cursor.getString(cursor
						.getColumnIndex("thesendername"));
				Log.d("MainActivity", "thesendername is " + thesendername);
				if (datatype.equals(Info.TEXT_TYPE)) {
					String datadetail = cursor.getString(cursor
							.getColumnIndex("datadetail"));
					Log.d("MainActivity", "text is " + datadetail);
					//
					Info info = new Info();
					info.setFileType(datatype);
					info.setSendType(Info.GROUP);
					info.setGroupId(groupId);
					info.setIdentify(null);
					info.setDetail(datadetail);
					info.setTime("");
					info.setLocalFileName(null);
					info.setSendOrRecvFlag(Info.TYPE_SENT);
					msgList.add(info);
				} else if (datatype.equals(Info.IMAGE_TYPE)) {
					String localfilename = cursor.getString(cursor
							.getColumnIndex("localfilename"));
					Log.d("MainActivity", "localfilename is " + localfilename);
					//
					Info info = new Info();
					info.setFileType(datatype);
					info.setSendType(Info.GROUP);
					info.setGroupId(groupId);
					info.setIdentify(null);
					info.setDetail(localfilename);
					info.setTime("");
					info.setLocalFileName(localfilename);
					info.setSendOrRecvFlag(Info.TYPE_SENT);
					//
					info.setBitmap(Bitmap.createScaledBitmap(
							BitmapFactory.decodeFile(localfilename), 200, 200,
							true));
					msgList.add(info);
				} else if (datatype.equals(Info.AUDIO_TYPE)) {
					String localfilename = cursor.getString(cursor
							.getColumnIndex("localfilename"));
					Log.d("MainActivity", "localfilename is " + localfilename);
					float seconds = cursor.getFloat(cursor
							.getColumnIndex("seconds"));
					Log.d("MainActivity", "seconds is " + seconds);
					//
					Info info = new Info();
					info.setFileType(datatype);
					info.setSendType(Info.GROUP);
					info.setGroupId(groupId);
					info.setIdentify(null);
					info.setDetail(localfilename);
					info.setTime("");
					info.setLocalFileName(localfilename);
					info.setSendOrRecvFlag(Info.TYPE_SENT);
					info.setSeconds(seconds);
					msgList.add(info);
				}
			} while (cursor.moveToNext());

			adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
			listViewChat.setSelection(msgList.size()); // 将ListView定位到最后一行
		}
		cursor.close();
	}

	/**
	 * 重载onNewIntent()方法，用来接收Intent中传来的数据
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		bundle = getIntent().getExtras();
		if (bundle != null) {
			Log.e("TAG", bundle.getStringArrayList("files").toString());
			if (bundle.getStringArrayList("files") != null) {
				listfile = bundle.getStringArrayList("files");
				for (String temp : listfile) {
					Info info = new Info();
					info.setFileType(Info.IMAGE_TYPE);
					info.setSendType(Info.GROUP);
					info.setGroupId(groupId);
					info.setIdentify(null);
					info.setDetail(temp);
					info.setTime("");
					info.setLocalFileName(temp);
					info.setSendOrRecvFlag(Info.TYPE_SENT);
					// 发送到服务器
					postInfo(info);
					// 将图片存储地址存入数据库
					dbHelper.insertInfo(info);
				}
			}
		}
	}

	/**
	 * 广播接收器，用来接收更新UI的广播
	 * 
	 * @author luhui1hao
	 *
	 */
	class UpdateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionName = intent.getAction();
			Object obj = intent.getSerializableExtra("obj");
			if (actionName.equals("COM_HANDKOO_CREATE_GROUP_RESPONSE")) {
			} else if (actionName.equals("COM_HANDKOO_DELETE_GROUP_RESPONSE")) {
			} else if (actionName.equals("COM_HANDKOO_FORWARD_RESPONSE")) {
			} else if (actionName.equals("COM_HANDKOO_CREATE_GROUP")) {
				createGroup(obj);//
			} else if (actionName.equals("COM_HANDKOO_DELETE_GROUP")) {
				deleteGroup(obj);//
			} else if (actionName.equals("COM_HANDKOO_FORWARD")) {
				receiveForward(intent, obj);
			}
		}

		private void createGroup(Object obj) {
			MessageGroup createGroup = (MessageGroup) obj;
			Group groupCreate = createGroup.getGroups().get(0);
			// 数据库操作
			dbHelper.insertGroup(groupCreate);
			dbHelper.insertMember(groupCreate);
		}

		private void deleteGroup(Object obj) {
			MessageGroup deleteGroup = (MessageGroup) obj;
			Group groupDelete = deleteGroup.getGroups().get(0);
			//如果GroupId跟当前一致，删除数据库并推出当前
			// 数据库操作
			dbHelper.deleteGroupAndMember(groupDelete);
			//
			finish();
		}

		private void receiveForward(Intent intent, Object obj) {
			MessageForward forwardInfo = (MessageForward) obj;
			Info info = forwardInfo.getInfos().get(0);
			info.setSendOrRecvFlag(Info.TYPE_RECEIVED);
			String fileType = info.getFileType();
			// 如果是text信息，那么直接进行数据库操作并更新界面
			if (fileType == Info.TEXT_TYPE) {
				// 数据库操作
				dbHelper.insertInfo(info);
				// 发送消息让Handle进行UI操作
				Message msg = uiHandler.obtainMessage();
				Bundle data = new Bundle();
				data.putSerializable("info", info);
				msg.setData(data);
				uiHandler.sendMessage(msg);
			}
			// 如果是pic信息，那么先根据地址下下来，然后再进行数据库和UI的操作
			else if (fileType == Info.IMAGE_TYPE) {
				// 通知Service将图片或音频从服务器下载下来，生成Info对象，然后在Service里面通知Handler更新界面
				Intent downloadIntent = new Intent(MainActivity.this,
						DownloadService.class);
				intent.putExtra("picdetail", info.getDetail());
				intent.putExtra("info", info);
				startService(downloadIntent);
			}
			// 如果是audio信息，那么先根据地址下下来，然后再进行数据库和UI的操作
			else if (fileType == Info.AUDIO_TYPE) {
				// 通知Service将图片或音频从服务器下载下来，生成Info对象，然后在Service里面通知Handler更新界面
				Intent downloadIntent = new Intent(MainActivity.this,
						DownloadService.class);
				intent.putExtra("audiodetail", info.getDetail());
				intent.putExtra("info", info);
				startService(downloadIntent);
			}
		}
	}

	public static Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 进行UI的处理
			Bundle data = msg.getData();
			Info info = (Info) data.getSerializable("info");
			msgList.add(info);
			adapter.notifyDataSetChanged(); // 通知更新的内容
			listViewChat.setSelection(msgList.size()); // 将lisview设置为最后一个
		}
	};

	private void postInfo(Info info) {
		new PostThread(info).start();
	}

	private class PostThread extends Thread {
		Info info;

		public PostThread(Info info) {
			this.info = info;
		}

		@Override
		public void run() {
			super.run();
			if(info.getSendType().equals(Info.GROUP)){
				// 判断发送的是什么数据，从而生成相应的Info对象
				if (info.equals(Info.TEXT_TYPE)) {
					info.setIdentify(info.getGroupId() + "");// groupid
					info.setDetail("detail");
				} else if (info.equals(Info.IMAGE_TYPE)) {
					//TODO 发送图片
				} else if (info.equals(Info.AUDIO_TYPE)) {
					//TODO 发送音频
				}
			}
			List<Info> infos = new ArrayList<Info>();
			infos.add(info);
			MessageForward msg = new MessageForward();
			msg.setInfos(infos);

			String xml = Forward.pack(msg, 1);
			Log.i(TAG, xml);

			byte[] mData = HK_Message_XS_Util.getInstance().mGetByteFromPara(
					(byte) 37, xml);
			HK_Message_Tool.mSendMsgPkg(mData);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
		unregisterReceiver(receiver);
		stopMsg();
	}

	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

}
