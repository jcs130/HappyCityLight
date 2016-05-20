/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.TwitterGetter.service;

import java.util.ArrayList;

import com.citydigitalpulse.collector.TwitterGetter.dao.InfoGetterDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.citydigitalpulse.collector.TwitterGetter.model.EarthSqure;
import com.citydigitalpulse.collector.TwitterGetter.model.RegInfo;

/**
 * 1.扫描Reg数据库，获取所有状态为3的区域并将所有的reg区域的小区域添加 2.根据小区域的坐标找到Stream区块并且修改Stream区块的状态
 * 
 * @author zhonglili
 *
 */
public class StartRegThreads extends ServiceThread {
	private InfoGetterDAO db;

	public StartRegThreads() {
		this.settName(this.getClass().getSimpleName());
		db = new InfoGetterDAO_MySQL();
	}

	@Override
	public void run() {
		super.run();
		ArrayList<RegInfo> regs;
		// 循环扫描
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
		// 执行新增监听线程操作
		StreamsManager smt = new StreamsManager();
		smt.startBuildThreads();
	}

	/**
	 * 终止线程的方法
	 */
	@Override
	public void stopMe() {
		if (this.isAlive()) {
			super.interrupt();
		}
	}

}
