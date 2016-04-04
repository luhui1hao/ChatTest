package com.handkoo.smartvideophone05.utils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
/*******************************************************
*���ڣ�		2015-6-27
*�������ƣ�	HK_MessagePkg_Util
*���ã�		��Ϣ��������
********************************************************/
public class HK_Message_XS_Util {
	public static Socket m_msgsocket = null;
	public static HK_Message_XS_Util instance = null;	
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	getInstance
	*���ߣ�		Sun_Skin
	*���������	@return
	*����ֵ��	HK_MessagePkg_Util
	*���ã�		��
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
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mGetByteFromPara
	*���ߣ�		Sun_Skin
	*���������	@param Packet_type
	*���������	@param StrData
	*����ֵ��	byte[]
	*���ã�		��
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
	*���ڣ�		2015-4-4
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mStopSocket
	*���ߣ�		Sun_Skin
	*����ֵ��	void
	*��ǣ�		��
	*���ã�		��
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
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mByteToShort
	*���ߣ�		Sun_Skin
	*���������	@param b
	*���������	@return
	*����ֵ��	short
	*��ǣ�		��
	*���ã�		��
	********************************************************/
	public  short mByteToShort(byte[] b) 
	{ 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff);// ���λ 
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
	
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mGetDataLen
	*���ߣ�		Sun_Skin
	*���������	@param b
	*���������	@return
	*����ֵ��	short
	*��ǣ�		��
	*���ã�		��
	********************************************************/
	public  short mGetDataLen(byte[] b)
    {
    	byte[] shortbyte = new byte[2];
    	shortbyte[0] = b[5];
    	shortbyte[1] = b[6];
    	return mByteToShort(shortbyte);    	
    }
	
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mShortToByte
	*���ߣ�		Sun_Skin
	*���������	@param number
	*���������	@return
	*����ֵ��	byte[]
	*��ǣ�		��
	*���ã�		��
	********************************************************/
	public  byte[] mShortToByte(short number) 
	{ 
        int temp = number; 
        byte[] b = new byte[2]; 
        for (int i = 0; i < b.length; i++) 
        { 
            b[i] = Integer.valueOf(temp & 0xff).byteValue();// 
            //�����λ���������λ 
            temp = temp >> 8; // ������8λ 
        } 
        return b; 
    } 
	
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mIntToByte
	*���ߣ�		Sun_Skin
	*���������	@param number
	*���������	@return
	*����ֵ��	byte[]
	*��ǣ�		��
	*���ã�		��
	********************************************************/
	public  byte[] mIntToByte(int number) 
	{ 
        int temp = number; 
        byte[] b = new byte[4]; 
        for (int i = 0; i < b.length; i++) { 
            b[i] = Integer.valueOf(temp & 0xff).byteValue();// �����λ���������λ 
            temp = temp >> 8; // ������8λ 
        } 
        return b; 
    }  
     
    
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mByteToInt
	*���ߣ�		Sun_Skin
	*���������	@param b
	*���������	@return
	*����ֵ��	int
	*��ǣ�		��
	*���ã�		��
	********************************************************/
	public  int mByteToInt(byte[] b)
    { 
        int s = 0; 
        int s0 = b[0] & 0xff;// ���λ 
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
    *���ڣ�		2015-6-27
    *�ļ�����	HK_Message_XS_Util.java
    *��������	mGetMsgType
    *���ߣ�		Sun_Skin
    *���������	@param head
    *���������	@return
    *����ֵ��	int
    *��ǣ�		��
    *���ã�		��
    ********************************************************/
    public  int mGetMsgType(byte[] head)
	{
		int type = head[0] & 0xff;
		return type;		
	}
	
    
	/*******************************************************
	*���ڣ�		2015-6-27
	*�ļ�����	HK_Message_XS_Util.java
	*��������	mGetMsgPort
	*���ߣ�		Sun_Skin
	*���������	@param head
	*���������	@return
	*����ֵ��	int
	*��ǣ�		��
	*���ã�		��
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
