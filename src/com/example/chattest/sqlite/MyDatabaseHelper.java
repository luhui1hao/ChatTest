package com.example.chattest.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.chattest.bean.Group;
import com.example.chattest.bean.Info;
import com.example.chattest.bean.Member;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	public static final String CREATE_GROUPS = "create table Groups ("
			+ "groupid integer, " + "groupname text) ";

	public static final String CREATE_MEMBERINGROUP = "create table MemberInGroup ("
			+ "groupid integer, "
			+ "groupname text, "
			+ "memberinthisgroup text) ";

	public static final String CREATE_GROUPCHATINFOS = "create table GroupChatInfos ("
			+ "id integer primary key autoincrement, " + "datatype text, "// 内容类型
			+ "sendtargettype text, "// group
			+ "groupid integer, "// 组id
			+ "thesendername text, "// 发送者的名字
			+ "datadetail text, " // 保存的文本信息
			+ "sendtime text, "// 信息发送时间
			+ "localfilename text, "// 用来保存本地音频和图片的地址
			+ "sendorrecvflag text, "// 表示是发送的还是接收的
			+ "readflag integer, "// 是否已读标识
			+ "seconds real)";// 用来保存音频时间

	public static final String CREATE_SINGLECHATINFOS = "create table SingleChatInfos ("
			+ "id integer primary key autoincrement, " + "datatype text, "// 内容类型
			+ "sendtargettype text, "// group
			+ "groupid integer, "// 组id
			+ "thesendername text, "// 发送者的名字
			+ "datadetail text, " // 保存的文本信息
			+ "sendtime text, "// 信息发送时间
			+ "localfilename text, "// 用来保存本地音频和图片的地址
			+ "sendorrecvflag text, "// 表示是发送的还是接收的
			+ "readflag integer, "// 是否已读标识
			+ "seconds real)";// 用来保存音频时间

	public static final String CREATE_SQLITESEQUENCE = "create table SQLiteSequence ("
			+ "name text, " + "seq integer) ";

	private static Context mContext;
	private static MyDatabaseHelper dbHelper = null;
	private static int version = 1;
	public static final String TAG = "MyDatabaseHelper";

	private MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	public static MyDatabaseHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new MyDatabaseHelper(context, "GroupChatSrv.db", null,
					version);
			return dbHelper;
		} else {
			return dbHelper;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_GROUPS);
		db.execSQL(CREATE_MEMBERINGROUP);
		db.execSQL(CREATE_GROUPCHATINFOS);
		db.execSQL(CREATE_SINGLECHATINFOS);
		db.execSQL(CREATE_SQLITESEQUENCE);
		Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		version = newVersion;
	}

	/**
	 * 存入Groups表
	 * 
	 * @param group
	 * @return
	 */
	public long insertGroup(Group group) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("groupid", group.getGroupId());
		values.put("groupname", group.getGroupNum());
		return db.insert("Groups", null, values);
	}

	/**
	 * 存入MemberInGroup表
	 */
	public long insertMember(Group group) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		// 遍历Members集合，有一个Member插一条记录
		for (Member member : group.getMembers()) {
			values.put("groupid", group.getGroupId());
			values.put("groupname", group.getGroupNum());
			values.put("memberinthisgroup", member.getName());
		}
		return db.insert("MemberInGroup", null, values);
	}

	/**
	 * 存入GroupChatInfos表
	 * @param info
	 * @return
	 */
	public long insertInfo(Info info) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("datatype", info.getFileType());
		values.put("sendtargettype", info.getSendType());
		values.put("groupid", info.getGroupId());
		values.put("thesendername", info.getIdentify());//thesendername
		values.put("datadetail", info.getDetail());
		values.put("sendtime", info.getTime());
		values.put("localfilename", info.getLocalFileName());
		values.put("sendorrecvflag", info.getSendOrRecvFlag());
		values.put("readflag", info.UNREAD);
		if (info.getFileType().equals("audio")) {
			values.put("seconds", info.getSeconds());
		}
		return db.insert("GroupChatInfos", null, values);
	}

	public void changeReadState(int id, int state) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("readflag", state);
		db.update("GroupChatInfos", values, "id = ?",
				new String[] { state + "" });
	}
	
	/**
	 * 用来删除数据库当中的组和成员信息
	 * @param group
	 */
	public void deleteGroupAndMember(Group group){
		//TODO
	}

	/**
	 * 清空GroupChatInfos表中所有数据
	 * 
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise. To remove all rows and get a count pass "1" as the
	 *         whereClause.
	 */
	public void clear() {
		// 删除表中所有数据
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("Groups", null, null);
		db.delete("MemberInGroup", null, null);
		db.delete("GroupChatInfos", null, null);
	}

}
