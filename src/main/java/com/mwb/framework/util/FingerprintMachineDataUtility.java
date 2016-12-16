package com.mwb.framework.util;

import java.util.Date;

public class FingerprintMachineDataUtility {
	
	/**
	 * 考勤机与PC时间初始时间的差值
	 * 考勤机传过来的时间都是基于2000-01-01 00:00:00
	 * 而java是基于1970-01-01 00:00:00开始的，所以计算的时候需要转换这部分差值
	 */
	public static final long DEVICE_PC_DELTA_TIME = 946656000000l; //
	
    
    public static final int FINGER_TEMPLE_SIZE = 1404; //指纹信息占的字节数
	
	public static final int PACK_FLAG_S = 0x1a2b3c4d; // 发送数据包的包头，是固定的
	
	public static final int PACK_FLAG_R = 0x4d3c2b1a; // 接收数据包的包头，是固定的
	
	public static final int BACKUPNUMBER_UPLOAD_FINGERPRINT_FLAG_MIN = 0; // 上次的是指纹的标识最小值
	
	public static final int BACKUPNUMBER_UPLOAD_FINGERPRINT_FLAG_MAX = 9; // 上次的是指纹的标识最大值
	
	public static final int VERIFYMODE_FINGERPRINT = 1; // 指纹打卡验证模式
	
	public static final long ALLOW_ERROR_TIME = 1 * 60 * 1000l; // 允许的误差时间  1分钟
	
	
	/**
	 * 获取发送给考勤机的校时时间，单位秒
	 * 
	 * @return
	 */
	public static long getCurrentTimeOfSendingDevice() {
		
		return convertDeviceTimeFromPC(new Date());
	}
	
	
	/**
	 * 把pc的时间转换成考勤机的时间，单位秒
	 * 
	 * 考勤机的时间都是基于2000-01-01 00:00:00
	 * 
	 * @param date PC的时间  基于1970-01-01 00:00:00开始的
	 * @return
	 */
	public static long convertDeviceTimeFromPC(Date date) {
		
		long pcTime = date.getTime();
		long deviceTime = pcTime - DEVICE_PC_DELTA_TIME;
		
		return deviceTime/1000;
	}
	
	/**
	 * 数据加密
	 * 加密方式
	 * 第一步：高低四位对调！
	 * 第二步：对调后低四位按位取反！
	 * 
	 * @param source
	 * @param buflen
	 * @return
	 */
	public static byte[] encrypt(byte[] source, int buflen) {

		int i;
		int len = buflen;

		byte[] des = new byte[buflen];

		byte ch;

		for (i = 0; i < len; i++) {
			ch = 0;
			ch |= (source[i] & 0x0f) << 4;
			ch |= (~((source[i] & 0xf0) >> 4)) & 0x0f;
			des[i] = ch;
		}
		return des;
	}
	
	/**
	 * 数据解密
	 * 
	 * @param source
	 * @param buflen
	 * @return
	 */
	public static byte[] decrypt(byte[] source, int buflen) {
		
		int i;
		int len = buflen;
		byte[] des = new byte[buflen];

		for(i=0;i<len;i++)
		{       
			byte ch = 0;
			ch |= (source[i] & 0xf0) >> 4;
			ch |= (~((source[i] & 0x0f) << 4)) & 0xf0;
			des[i] = ch;     
		}
		return des;
	}
	

	/**
	 * 获取回应考勤机的整个数据包
	 * 
	 * @param deviceID
	 * @param cmd
	 * @param cmdSerialNum
	 * @param step
	 * @param data
	 * @param dataLen 整个数据包中数据部分的字节数
	 * @return
	 */
	public static byte[] getEncryptAckPack(int deviceID, int cmd, int cmdSerialNum, int step, byte[] data, int dataLen) {
		
		byte[] headByte = NumberConvertUtility.int2Byte(PACK_FLAG_R);
		
		byte[] cmdByte = NumberConvertUtility.int2Byte(cmd);
		byte[] cmdSerialNumByte = NumberConvertUtility.int2Byte(cmdSerialNum);
		byte[] stepByte = NumberConvertUtility.int2Byte(step);
		
		byte[] deviceIDByte = NumberConvertUtility.int2Byte(deviceID);
		byte[] dataLenByte = NumberConvertUtility.int2Byte(dataLen);

		byte[] encryptDataByte = encrypt(data, dataLen);
		
		int packHeadAndDataLen = 24 + encryptDataByte.length;
		
		byte[] packHeadAndDataByte = new byte[packHeadAndDataLen];
		
		System.arraycopy(headByte, 0, packHeadAndDataByte, 0, 4);
		System.arraycopy(cmdByte, 0, packHeadAndDataByte, 4, 4);
		System.arraycopy(cmdSerialNumByte, 0, packHeadAndDataByte, 8, 4);
		System.arraycopy(stepByte, 0, packHeadAndDataByte,12, 4);
		System.arraycopy(deviceIDByte, 0, packHeadAndDataByte, 16, 4);
		
		System.arraycopy(dataLenByte, 0, packHeadAndDataByte, 20, 4);
		System.arraycopy(encryptDataByte, 0, packHeadAndDataByte, 24, encryptDataByte.length);
		
		long checksum = getChecksum(packHeadAndDataByte);
		
		byte[] checkSumBytes = NumberConvertUtility.int2Byte((int)checksum);
		
		byte[] packByte = new byte[packHeadAndDataLen + 4];
		
		System.arraycopy(packHeadAndDataByte, 0, packByte, 0, packHeadAndDataLen);
		System.arraycopy(checkSumBytes, 0, packByte, packHeadAndDataLen, 4);
		
		return packByte;
	}
	

	/**
	 * 获取PC向考勤机下发命令的整个数据包
	 * 
	 * @param deviceID
	 * @param cmd
	 * @param cmdSerialNum
	 * @param step
	 * @param data
	 * @param dataLen 整个数据包中数据部分的字节数
	 * @return
	 */
	public static byte[] getEncryptCmdPack(int deviceID, int cmd, int cmdSerialNum, int step, byte[] data, int dataLen) {
		
		byte[] headByte = NumberConvertUtility.int2Byte(PACK_FLAG_S);
		
		byte[] cmdByte = NumberConvertUtility.int2Byte(cmd);
		byte[] cmdSerialNumByte = NumberConvertUtility.int2Byte(cmdSerialNum);
		byte[] stepByte = NumberConvertUtility.int2Byte(step);
		
		byte[] deviceIDByte = NumberConvertUtility.int2Byte(deviceID);
		byte[] dataLenByte = NumberConvertUtility.int2Byte(dataLen);

		byte[] encryptDataByte = {};
		if (dataLen > 0) {
			encryptDataByte = encrypt(data, dataLen);
		}
		
		int packHeadAndDataLen = 24 + encryptDataByte.length;
		
		byte[] packHeadAndDataByte = new byte[packHeadAndDataLen];
		
		System.arraycopy(headByte, 0, packHeadAndDataByte, 0, 4);
		System.arraycopy(cmdByte, 0, packHeadAndDataByte, 4, 4);
		System.arraycopy(cmdSerialNumByte, 0, packHeadAndDataByte, 8, 4);
		System.arraycopy(stepByte, 0, packHeadAndDataByte,12, 4);
		System.arraycopy(deviceIDByte, 0, packHeadAndDataByte, 16, 4);
		
		System.arraycopy(dataLenByte, 0, packHeadAndDataByte, 20, 4);
		System.arraycopy(encryptDataByte, 0, packHeadAndDataByte, 24, encryptDataByte.length);
		
		long checksum = getChecksum(packHeadAndDataByte);
		
		byte[] checkSumBytes = NumberConvertUtility.int2Byte((int)checksum);
		
		byte[] packByte = new byte[packHeadAndDataLen + 4];
		
		System.arraycopy(packHeadAndDataByte, 0, packByte, 0, packHeadAndDataLen);
		System.arraycopy(checkSumBytes, 0, packByte, packHeadAndDataLen, 4);
		
		return packByte;
	}
	
	private static long getChecksum(byte[] bRefArr) {
		long iOutcome = 0;

		for (int i = 0; i < bRefArr.length; i++) {
			long numbeResult = bRefArr[i] & 0xff;
			iOutcome += numbeResult;
		}
		return iOutcome;
	}
	
}
