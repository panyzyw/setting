package com.yyd.breathled;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
/***************************************
 *
 * @author GuoYaoWei
 *
 *用法：
 *一、设置状态
 *  1、new BreathLed()
 *  2、openDev
 *	3、方法setOnoff(String Led, String on);  Led= "chest"/"ear", on = "on"/"off"
 *	        四种组合分别控制耳朵，胸前灯的亮灭
 *	4、closeDev();
 *	5、返回值 ：等于0位成功； <0 设置失败错误（一般是形参错误）
 *	每次控制如上四步即可
 *
 *二、获取状态
 *  与 “一 ”类似，open->set/get->close
 *  1、new BreathLed()
 *  2、openDev()
 *	3、方法int getOnoff(String Led);  Led= "chest"/"ear"
 *	        分别获取耳朵，胸前灯的亮灭状态
 *	4、closeDev();
 *  返回值：0->灭   1->亮   ；<0 设置失败错误（一般是形参错误）
 */
public class BreathLed{
	static {
		System.loadLibrary("BreathLed");
	}

	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	public BreathLed(){
	}
	public native int openDev();
	public native int closeDev();
	public native int setOnoff(String Led, String on);
	public native int setColor(String Led, String color);
	public native int getOnoff(String Led);
	public native int getColor(String Led);
}


