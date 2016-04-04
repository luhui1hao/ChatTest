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
			+ "id integer primary key autoincrement, " + "datatype text, "// ��������
			+ "sendtargettype text, "// group
			+ "groupid integer, "// ��id
			+ "thesendername text, "// �����ߵ�����
			+ "datadetail text, " // ������ı���Ϣ
			+ "sendtime text, "// ��Ϣ����ʱ��
			+ "localfilename text, "// �������汾����Ƶ��ͼƬ�ĵ�ַ
			+ "sendorrecvflag text, "// ��ʾ�Ƿ��͵Ļ��ǽ��յ�
			+ "readflag integer, "// �Ƿ��Ѷ���ʶ
			+ "seconds real)";// ����������Ƶʱ��

	public static final String CREATE_SINGLECHATINFOS = "create table SingleChatInfos ("
			+ "id integer primary key autoincrement, " + "datatype text, "// ��������
			+ "sendtargettype text, "// group
			+ "groupid integer, "// ��id
			+ "thesendername text, "// �����ߵ�����
			+ "datadetail text, " // ������ı���Ϣ
			+ "sendtime text, "// ��Ϣ����ʱ��
			+ "localfilename text, "// �������汾����Ƶ��ͼƬ�ĵ�ַ
			+ "sendorrecvflag text, "// ��ʾ�Ƿ��͵Ļ��ǽ��յ�
			+ "readflag integer, "// �Ƿ��Ѷ���ʶ
			+ "seconds real)";// ����������Ƶʱ��

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
	 * ����Groups��
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
	 * ����MemberInGroup��
	 */
	public long insertMember(Group group) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		// ����Members���ϣ���һ��Member��һ����¼
		for (Member member : group.getMembers()) {
			values.put("groupid", group.getGroupId());
			values.put("groupname", group.getGroupNum());
			values.put("memberinthisgroup", member.getName());
		}
		return db.insert("MemberInGroup", null, values);
	}

	/**
	 * ����GroupChatInfos��
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
	 * ����ɾ�����ݿ⵱�е���ͳ�Ա��Ϣ
	 * @param group
	 */
	public void deleteGroupAndMember(Group group){
		//TODO
	}

	/**
	 * ���GroupChatInfos������������
	 * 
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise. To remove all rows and get a count pass "1" as the
	 *         whereClause.
	 */
	public void clear() {
		// ɾ��������������
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("Groups", null, null);
		db.delete("MemberInGroup", null, null);
		db.delete("GroupChatInfos", null, null);
	}

}
