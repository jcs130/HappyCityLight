package com.zhongli.TwitterGetter.service;

import java.util.ArrayList;

import com.zhongli.TwitterGetter.dao.InfoGetterDAO;
import com.zhongli.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.zhongli.TwitterGetter.model.EarthSqure;
import com.zhongli.TwitterGetter.model.RegInfo;


/**
 * 1.扫描Reg数据库，获取所有状态为3的区域并将所有的reg区域的小区域添加 2.根据小区域的坐标找到Stream区块并且修改Stream区块的状态
 * 
 * @author zhonglili
 *
 */
public class StartRegThreads extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private InfoGetterDAO db;

	public StartRegThreads(int time) {
		this.settName(this.getClass().getSimpleName());
		this.time = time;
		db = new InfoGetterDAO_MySQL();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		ArrayList<RegInfo> regs;
		// 循环扫描
		while (isRunning) {
			// 1.扫描Reg数据库，获取所有状态为3的区域并将所有的reg区域的小区域添加
			regs = (ArrayList<RegInfo>) db.getRegInfo(3);
			// 2.根据小区域的坐标找到Stream区块并且修改Stream区块的状态
			for (int i = 0; i < regs.size(); i++) {
				ArrayList<EarthSqure> ess = (ArrayList<EarthSqure>) db
						.getStreamSqures(regs.get(i));
				// 循环将区块状态设置使用次数+1
				for (int j = 0; j < ess.size(); j++) {
					// 根据行列修改区块在数据库中的状态,设置使用次数+1
					db.squreAddUseTime(ess.get(j).getRow(), ess.get(j).getCol());
				}
				// 修改大区域状态为正在监听状态（1）
				db.changeRegState(regs.get(i).getRegID(), 1);
			}
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				System.out.println("线程:" + this.gettName() + "结束....");
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 终止线程的方法
	 */
	@Override
	public void stopMe() {
		isRunning = false;
		if (this.isAlive()) {
			super.interrupt();
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
