package com.zhongli.TwitterGetter.app;

import com.zhongli.TwitterGetter.service.ScanRegsThread;
import com.zhongli.TwitterGetter.service.StartRegThreads;
import com.zhongli.TwitterGetter.service.StreamsManagerThread;
import com.zhongli.TwitterGetter.service.ThreadsPool;

/**
 * 测试程序入口
 * 
 * @author zhonglili
 *
 */
public class getterMain {

	public static void main(String[] args) {
		getterMain tm = new getterMain();
		// System.out.println(tm.getClass().getSimpleName());
		// tm.doSth();
		// 恢复数据库为初始状态
		// tm.reSetDB();
		// 开始管理各个线程
		tm.startThreads(5000, 5000, 5000);
		// 主线程循环
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 1.扫描reg数据库，获取所有状态为0的区域并将所有的reg区域的小区域添加(间隔————)
	 * 2.根据小区域的坐标向数据库中添加Stream区块（间隔————）
	 * 3.查询区块表，获取所有状态为0的区块，新建Stream监听县城监视这些区块并且修改状态（间隔————）
	 */
	private void startThreads(int time1, int time2, int time3) {
		// 1. 扫描新区域并建立区块表
		ScanRegsThread sRegThread = new ScanRegsThread(time1);
		ThreadsPool.addThread(sRegThread);
		// 2.查询大区域表，获取所有状态为3的区块并且修改区域和区块的状态
		StartRegThreads sSqureThread = new StartRegThreads(time2);
		ThreadsPool.addThread(sSqureThread);
		// 3.查询区块表，获取所有使用次数大于0且运行状态为0（停止）的区块，新建相应的Stream监听线程
		StreamsManagerThread smt = new StreamsManagerThread(time3);
		ThreadsPool.addThread(smt);

	}

}
