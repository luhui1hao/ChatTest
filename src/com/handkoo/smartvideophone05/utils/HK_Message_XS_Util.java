package com.handkoo.smartvideophone05.utils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
/*******************************************************
*日期：		2015-6-27
*类型名称：	HK_MessagePkg_Util
*作用：		消息包处理类
********************************************************/
public class HK_Message_XS_Util {
	public static Socket m_msgsocket = null;
	public static HK_Message_XS_Util instance = null;	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	getInstance
	*作者：		Sun_Skin
	*输入参数：	@return
	*返回值：	HK_MessagePkg_Util
	*作用：		无
	********************************************************/
	public static synchronized HK_Message_XS_Util getInstance()
	{
		if(instance == null)
		{
			instance = new HK_Message_XS_Util();			
		}
		return instance;
	}
	
	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mGetByteFromPara
	*作者：		Sun_Skin
	*输入参数：	@param Packet_type
	*输入参数：	@param StrData
	*返回值：	byte[]
	*作用：		无
	********************************************************/
	public byte[] mGetByteFromPara(byte Packet_type, String StrData)
	{
		byte[] mCmd = null;
		if(((!StrData.equals("")) | (StrData != null)))
		{
			byte[] mData = null;
			try {
				mData = StrData.getBytes("GB2312");
				int len = mData.length;
				mCmd = new byte[6+len];
				mCmd[0] = '@';
				mCmd[1] = Packet_type;
				mCmd[2] = 1;
				mCmd[3] = 0;
				byte[] mLen = mShortToByte((short)(len));
				mCmd[4] = mLen[0];
				mCmd[5] = mLen[1];
				
				for(int i = 0; i<len; i++)
				{
					mCmd[6+i] = mData[i];
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			mCmd = new byte[6];
			mCmd[0] = '@';
			mCmd[1] = Packet_type;
			mCmd[2] = 1;
			mCmd[3] = 0;
			byte[] mLen = mShortToByte((short)(0));
			mCmd[4] = mLen[0];
			mCmd[5] = mLen[1];
		}
		return mCmd;		
	}
	
	/*******************************************************
	*日期：		2015-4-4
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mStopSocket
	*作者：		Sun_Skin
	*返回值：	void
	*标记：		无
	*作用：		无
	********************************************************/
	public void mStopMessageSocket()
	{
		Socket s = HK_Message_XS_Util.getMsgSocket();
		if(null != s)
		{
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = null;
		}		
	}
	
	//
	public static void setMsgSocket(Socket s)
	{
		if(null == s)
		{
			if(HK_Message_XS_Util.m_msgsocket != null)
			{
				try 
				{
					HK_Message_XS_Util.m_msgsocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			HK_Message_XS_Util.m_msgsocket = null;			
		}else
		{
			HK_Message_XS_Util.m_msgsocket = s;
		}		
	}
	
	
	//
	public static Socket getMsgSocket()
	{
		return HK_Message_XS_Util.m_msgsocket;
	}
	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mByteToShort
	*作者：		Sun_Skin
	*输入参数：	@param b
	*输入参数：	@return
	*返回值：	short
	*标记：		无
	*作用：		无
	********************************************************/
	public  short mByteToShort(byte[] b) 
	{ 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff);// 最低位 
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mGetDataLen
	*作者：		Sun_Skin
	*输入参数：	@param b
	*输入参数：	@return
	*返回值：	short
	*标记：		无
	*作用：		无
	********************************************************/
	public  short mGetDataLen(byte[] b)
    {
    	byte[] shortbyte = new byte[2];
    	shortbyte[0] = b[5];
    	shortbyte[1] = b[6];
    	return mByteToShort(shortbyte);    	
    }
	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mShortToByte
	*作者：		Sun_Skin
	*输入参数：	@param number
	*输入参数：	@return
	*返回值：	byte[]
	*标记：		无
	*作用：		无
	********************************************************/
	public  byte[] mShortToByte(short number) 
	{ 
        int temp = number; 
        byte[] b = new byte[2]; 
        for (int i = 0; i < b.length; i++) 
        { 
            b[i] = Integer.valueOf(temp & 0xff).byteValue();// 
            //将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
	
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mIntToByte
	*作者：		Sun_Skin
	*输入参数：	@param number
	*输入参数：	@return
	*返回值：	byte[]
	*标记：		无
	*作用：		无
	********************************************************/
	public  byte[] mIntToByte(int number) 
	{ 
        int temp = number; 
        byte[] b = new byte[4]; 
        for (int i = 0; i < b.length; i++) { 
            b[i] = Integer.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    }  
     
    
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mByteToInt
	*作者：		Sun_Skin
	*输入参数：	@param b
	*输入参数：	@return
	*返回值：	int
	*标记：		无
	*作用：		无
	********************************************************/
	public  int mByteToInt(byte[] b)
    { 
        int s = 0; 
        int s0 = b[0] & 0xff;// 最低位 
        int s1 = b[1] & 0xff; 
        int s2 = b[2] & 0xff; 
        int s3 = b[3] & 0xff; 
        s3 <<= 24; 
        s2 <<= 16; 
        s1 <<= 8; 
        s = s0 | s1 | s2 | s3; 
        return s; 
    }
    
    /*******************************************************
    *日期：		2015-6-27
    *文件名：	HK_Message_XS_Util.java
    *函数名：	mGetMsgType
    *作者：		Sun_Skin
    *输入参数：	@param head
    *输入参数：	@return
    *返回值：	int
    *标记：		无
    *作用：		无
    ********************************************************/
    public  int mGetMsgType(byte[] head)
	{
		int type = head[0] & 0xff;
		return type;		
	}
	
    
	/*******************************************************
	*日期：		2015-6-27
	*文件名：	HK_Message_XS_Util.java
	*函数名：	mGetMsgPort
	*作者：		Sun_Skin
	*输入参数：	@param head
	*输入参数：	@return
	*返回值：	int
	*标记：		无
	*作用：		无
	********************************************************/
	public  int mGetMsgPort(byte[] head)
	{
		byte[] ByteInt = new byte[4];
		for(int i = 0; i < 4; i++ )
		{
			ByteInt[i] = head[i+1];
		}
		int type = mByteToInt(ByteInt);
		return type;	
	}
	
}
