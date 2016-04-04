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
	private boolean isPreview = false;// �Ƿ���Ԥ����
	private Camera camera;// ����ϵͳ���õ������
	private int screenWidth, screenHeight;
	private Button chatGroupBtn;
	private Button flashLightBtn;// ����ƿ��ذ�ť
	private Button cameraBtn;// ���հ�ť
	private LinearLayout linearLayoutChat;
	public static List<Info> msgList = new ArrayList<Info>();
	private static MsgAdapter adapter;
	private static ListView listViewChat;
	private Button sendBtn;// ���Ͱ�ť
	private EditText editText;// �����
	private ButtonListener btnListener;
	private Camera.Parameters parameters;
	private Button dadianhuaBtn;
	private String picAddressDir;
	private byte[] picData;
	private MyDatabaseHelper dbHelper;
	public static final String TAG = "MainActivity";
	public static final int IMAGE_REQUEST = 1;
	private Button pickPicBtn;
	ArrayList<String> listfile = new ArrayList<String>();// ����ͼƬ��·��
	private Bundle bundle;
	private IntentFilter intentFilter;
	private UpdateBroadcastReceiver receiver;

	// recorderҪ�õ��ı���
	private AudioRecorderButton mAudioRecorderButton;
	private View animView;
	private View animViewLeft;

	// ��ʱ�õ�
	private int groupId = 100220;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// ����ͼƬ�洢·��
		picAddressDir = FileUtils.getPicDirAdd();
		// ע��㲥
		intentFilter = new IntentFilter();
		intentFilter.addAction("COM_HANDKOO_CREATE_GROUP_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_DELETE_GROUP_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_FORWARD_RESPONSE");
		intentFilter.addAction("COM_HANDKOO_CREATE_GROUP");
		intentFilter.addAction("COM_HANDKOO_DELETE_GROUP");
		intentFilter.addAction("COM_HANDKOO_FORWARD");
		receiver = new UpdateBroadcastReceiver();
		registerReceiver(receiver, intentFilter);
		// ��ʼ�����ݿ�
		initSQLite();
		// �������
		setCameraFun();

		// �ҵ�������沼��
		linearLayoutChat = (LinearLayout) findViewById(R.id.linearLayout_chat);
		// ��������������
		btnListener = new ButtonListener();
		// �ҵ����鰴ť�����󶨼�����
		chatGroupBtn = (Button) findViewById(R.id.chatgroup_btn_right_bar);
		chatGroupBtn.setOnClickListener(btnListener);
		// �ҵ�����ư�ť���������󶨼�����
		flashLightBtn = (Button) findViewById(R.id.flash_light_btn);
		flashLightBtn.setOnClickListener(btnListener);
		// �ҵ����հ�ť�������󶨼�����
		cameraBtn = (Button) findViewById(R.id.camera_btn);
		cameraBtn.setOnClickListener(btnListener);

		// ������������������
		adapter = new MsgAdapter(this, msgList);
		listViewChat = (ListView) findViewById(R.id.listView_chat);
		listViewChat.setAdapter(adapter);
		// �ҵ������
		editText = (EditText) findViewById(R.id.editText_chat);
		// �ҵ����Ͱ�ť�������󶨼�����
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(btnListener);

		// �ҵ���绰��ť�������󶨼�����
		dadianhuaBtn = (Button) findViewById(R.id.dadianhua_btn);
		dadianhuaBtn.setOnClickListener(btnListener);

		// �ҵ�ͼƬ��ť�������󶨼�����
		pickPicBtn = (Button) findViewById(R.id.pic_pic_btn);
		pickPicBtn.setOnClickListener(btnListener);

		// ��ʼ���������
		// ������Զ����Button��������Ӧʲô�¼����������д���ˣ�����ֻҪֱ�Ӵ������Ķ���ͺ��ˣ�ʲô������ʲô�Ķ����ð���
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
						// ���͵�������
						postInfo(info);
						// �������ݿ�
						dbHelper.insertInfo(info);
						// ########################################��������Msg������
						msgList.add(info);
						adapter.notifyDataSetChanged(); // ֪ͨ���µ�����
						listViewChat.setSelection(msgList.size()); // ��lisview����Ϊ���һ��
					}

				});
		// ��ListViewÿ��Item������Ӧ�¼�
		// listView��item����¼�
		listViewChat.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Info info = msgList.get(position);
				// �����������Ƶ��ʱ��
				if (info.getFileType().equals(Info.AUDIO_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_SENT) {
					// ���Ŷ�����֡������
					if (animView != null) {
						animView.setBackgroundResource(R.drawable.adj);
						animView = null;
					}
					animView = view.findViewById(R.id.id_recorder_anim);
					animView.setBackgroundResource(R.drawable.play_anim);
					AnimationDrawable animation = (AnimationDrawable) animView
							.getBackground();
					animation.start();
					// ����¼��
					MediaManager.playSound(info.getLocalFileName(),
							new MediaPlayer.OnCompletionListener() {

								public void onCompletion(MediaPlayer mp) {
									animView.setBackgroundResource(R.drawable.adj);
								}
							});
				} else if (info.getFileType().equals(Info.AUDIO_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_RECEIVED) {
					// ���Ŷ�����֡������
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
					// ����¼��
					MediaManager.playSound(info.getLocalFileName(),
							new MediaPlayer.OnCompletionListener() {

								public void onCompletion(MediaPlayer mp) {
									animViewLeft
											.setBackgroundResource(R.drawable.adj_left);
								}
							});
				}

				// ���������ͼƬ��ʱ��
				if (info.getFileType().equals(Info.IMAGE_TYPE)
						&& info.getSendOrRecvFlag() == Info.TYPE_SENT) {
					// ������һ��Activity��������Ƭ��������
					Intent picIntent = new Intent(MainActivity.this,
							PictureActivity.class);
					picIntent.putExtra("picAddress", info.getLocalFileName());
					startActivity(picIntent);
				}
			}
		});

		// ���Է���������Ҫɾ
		myTestFun();

		// ������Ϣ������
		startMsg();
	}

	private void initSQLite() {
		dbHelper = MyDatabaseHelper.getInstance(MainActivity.this);
		dbHelper.getWritableDatabase();
	}

	// ���Է���������Ҫɾ
	private void myTestFun() {
		findViewById(R.id.my_test_btn1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ��ȡ����������
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
							adapter.notifyDataSetChanged(); // ��������Ϣʱ��ˢ��ListView�е���ʾ
							listViewChat.setSelection(msgList.size()); // ��ListView��λ�����һ��
							editText.setText(""); // ���������е�����
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
		 * ��������Ϣʱ��ˢ��ListView�е���ʾ listViewChat.setSelection(msgList.size()); //
		 * ��ListView��λ�����һ�� editText.setText(""); // ���������е����� // �ͷ�Bitmap��Դ if
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
						adapter.notifyDataSetChanged(); // ֪ͨ���µ�����
						listViewChat.setSelection(msgList.size()); // ��lisview����Ϊ���һ��
					}
				});
		findViewById(R.id.my_test_btn4).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ɾ��������������
						dbHelper.clear();
						// ���ListView
						msgList.clear();
						adapter.notifyDataSetChanged();
					}
				});

	}

	private void setCameraFun() {
		// ��ȡ��Ļ�Ŀ�͸�
		getScreenWidthAndHeight();
		// ��ȡSurfaceView����
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		// ��ȡSurfaceHolder����
		surfaceHolder = surfaceView.getHolder();
		// ���ø�Surface����Ҫ�Լ�ά��������
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ΪSurfaceHolder���һ���ص�������
		surfaceHolder.addCallback(new Callback() {

			// ��������ǵ�surfaceView�����ı����õ�
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}

			// ���������surface ����������õ�
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// ��ʼ������ͷ
				initCamera();
			}

			// ����ǵ�surfaceView����ʱ���õ�.
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// ���camera��Ϊnull ,�ͷ�����ͷ
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
		// ��ȡ���ڹ�����
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		// ��ȡ��Ļ�Ŀ�͸�
		display.getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
	}

	private void initCamera() {
		if (!isPreview) {
			// �˴�Ĭ�ϴ򿪺�������ͷ
			// ͨ���ı�������Դ�ǰ������ͷ
			camera = Camera.open(0);
			// camera.setDisplayOrientation(90);
		}
		if (camera != null && !isPreview) {
			try {
				parameters = camera.getParameters();
				// // ����Ԥ����Ƭ�Ĵ�С
				// parameters.setPreviewSize(screenWidth, screenHeight);
				// // ����Ԥ����Ƭʱÿ����ʾ����֡����Сֵ�����ֵ
				// parameters.setPreviewFpsRange(4, 10);
				// // ����ͼƬ��ʽ
				// parameters.setPictureFormat(ImageFormat.JPEG);
				// // ����JPG��Ƭ������
				// parameters.set("jpeg-quality", 100);
				// // ������Ƭ�Ĵ�С
				// parameters.setPictureSize(screenWidth, screenHeight);
				// ͨ��SurfaceView��ʾȡ������
				camera.setPreviewDisplay(surfaceHolder);
				// ��ʼԤ��
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
					// ����Ϊ���ɼ�
					linearLayoutChat.setVisibility(View.GONE);
				} else if (isVisible == MY_INVISIBLE) {
					isVisible = MY_VISIBLE;
					// ����Ϊ�ɼ�
					linearLayoutChat.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.flash_light_btn:
				// �ж������״̬
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
					// ��������ͷ�Զ��Խ��������
					camera.autoFocus(autoFocusCallback);
				}
				break;
			case R.id.quit_camera_btn:
				break;
			case R.id.send_btn:
				// ��ȡ����������
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
					// ���͵�������
					postInfo(info);
					// ���ı����ݴ������ݿ�
					dbHelper.insertInfo(info);
					// ###################################����Text��Msg
					msgList.add(info);
					adapter.notifyDataSetChanged(); // ��������Ϣʱ��ˢ��ListView�е���ʾ
					listViewChat.setSelection(msgList.size()); // ��ListView��λ�����һ��
					editText.setText(""); // ���������е�����
				}
				break;
			case R.id.dadianhua_btn:
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("�Ƿ񲦴�˵绰��")
						.setMessage("13033516935")
						.setPositiveButton("��",
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

								}).setNegativeButton("��", null)
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
		// ���Զ��Խ�ʱ�����÷���
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()������Ҫ����3������������
				// ��1�������������û����¿���ʱ�����ü�����
				// ��2�����������������ȡԭʼ��Ƭʱ�����ü�����
				// ��3�����������������ȡJPG��Ƭʱ�����ü�����
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// ���¿���˲���ִ�д˴�����
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// �˴�������Ծ����Ƿ���Ҫ����ԭʼ��Ƭ��Ϣ
					}
				}, myJpegCallback);
			}
		}
	};

	PictureCallback myJpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			picData = data;
			// �˴��Ƕ�ͼƬ�Ĵ����߼�
			// �����������õ����ݴ���λͼ
			final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
					data.length);
			// ����/layout/save.xml�ļ���Ӧ�Ĳ�����Դ
			View saveDialog = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.dialog_picture, null);
			// ��ȡsaveDialog�Ի����ϵ�ImageView���
			ImageView show = (ImageView) saveDialog.findViewById(R.id.picture);
			// ��ʾ�ո��ĵõ���Ƭ
			show.setImageBitmap(bm);
			// ʹ�öԻ�����ʾsaveDialog���
			new AlertDialog.Builder(MainActivity.this)
					.setView(saveDialog)
					.setTitle("�Ƿ���ͼƬ��")
					.setCancelable(false)
					.setPositiveButton("����",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// ��ͼƬ���浽����
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
									// ����Info����
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
									// ���͵�������
									postInfo(info);
									// ��ͼƬ�洢��ַ�������ݿ�
									dbHelper.insertInfo(info);
									// ��ͼƬ���ͳ�ȥ�����������������ʾ����
									// ####################################����ͼƬMsg������
									msgList.add(info);
									adapter.notifyDataSetChanged(); // ��������Ϣʱ��ˢ��ListView�е���ʾ
									listViewChat.setSelection(msgList.size()); // ��ListView��λ�����һ��
									// �ͷ�Bitmap��Դ
									if (!bm.isRecycled()) {
										bm.recycle();
									}
								}
							}).setNegativeButton("ȡ��", null).show();
			// �������
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
		// ��List���
		msgList.clear();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
		// �����ݿ���ȡ�����ݣ������List
		// ��ѯchatContent�������е�����
		Cursor cursor = dbHelper.getReadableDatabase().query("GroupChatInfos",
				null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// ����Cursor����ȡ�����ݲ�����Info����
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

			adapter.notifyDataSetChanged(); // ��������Ϣʱ��ˢ��ListView�е���ʾ
			listViewChat.setSelection(msgList.size()); // ��ListView��λ�����һ��
		}
		cursor.close();
	}

	/**
	 * ����onNewIntent()��������������Intent�д���������
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
					// ���͵�������
					postInfo(info);
					// ��ͼƬ�洢��ַ�������ݿ�
					dbHelper.insertInfo(info);
				}
			}
		}
	}

	/**
	 * �㲥���������������ո���UI�Ĺ㲥
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
			// ���ݿ����
			dbHelper.insertGroup(groupCreate);
			dbHelper.insertMember(groupCreate);
		}

		private void deleteGroup(Object obj) {
			MessageGroup deleteGroup = (MessageGroup) obj;
			Group groupDelete = deleteGroup.getGroups().get(0);
			//���GroupId����ǰһ�£�ɾ�����ݿⲢ�Ƴ���ǰ
			// ���ݿ����
			dbHelper.deleteGroupAndMember(groupDelete);
			//
			finish();
		}

		private void receiveForward(Intent intent, Object obj) {
			MessageForward forwardInfo = (MessageForward) obj;
			Info info = forwardInfo.getInfos().get(0);
			info.setSendOrRecvFlag(Info.TYPE_RECEIVED);
			String fileType = info.getFileType();
			// �����text��Ϣ����ôֱ�ӽ������ݿ���������½���
			if (fileType == Info.TEXT_TYPE) {
				// ���ݿ����
				dbHelper.insertInfo(info);
				// ������Ϣ��Handle����UI����
				Message msg = uiHandler.obtainMessage();
				Bundle data = new Bundle();
				data.putSerializable("info", info);
				msg.setData(data);
				uiHandler.sendMessage(msg);
			}
			// �����pic��Ϣ����ô�ȸ��ݵ�ַ��������Ȼ���ٽ������ݿ��UI�Ĳ���
			else if (fileType == Info.IMAGE_TYPE) {
				// ֪ͨService��ͼƬ����Ƶ�ӷ�������������������Info����Ȼ����Service����֪ͨHandler���½���
				Intent downloadIntent = new Intent(MainActivity.this,
						DownloadService.class);
				intent.putExtra("picdetail", info.getDetail());
				intent.putExtra("info", info);
				startService(downloadIntent);
			}
			// �����audio��Ϣ����ô�ȸ��ݵ�ַ��������Ȼ���ٽ������ݿ��UI�Ĳ���
			else if (fileType == Info.AUDIO_TYPE) {
				// ֪ͨService��ͼƬ����Ƶ�ӷ�������������������Info����Ȼ����Service����֪ͨHandler���½���
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
			// ����UI�Ĵ���
			Bundle data = msg.getData();
			Info info = (Info) data.getSerializable("info");
			msgList.add(info);
			adapter.notifyDataSetChanged(); // ֪ͨ���µ�����
			listViewChat.setSelection(msgList.size()); // ��lisview����Ϊ���һ��
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
				// �жϷ��͵���ʲô���ݣ��Ӷ�������Ӧ��Info����
				if (info.equals(Info.TEXT_TYPE)) {
					info.setIdentify(info.getGroupId() + "");// groupid
					info.setDetail("detail");
				} else if (info.equals(Info.IMAGE_TYPE)) {
					//TODO ����ͼƬ
				} else if (info.equals(Info.AUDIO_TYPE)) {
					//TODO ������Ƶ
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
