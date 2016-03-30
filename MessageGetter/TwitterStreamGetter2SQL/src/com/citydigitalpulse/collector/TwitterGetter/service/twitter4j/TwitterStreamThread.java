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
package com.citydigitalpulse.collector.TwitterGetter.service.twitter4j;

import java.util.ArrayList;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.citydigitalpulse.collector.TwitterGetter.dao.InfoGetterDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.TwitterSaveDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.TwitterSavingDAOimpl;
import com.citydigitalpulse.collector.TwitterGetter.model.EarthSqure;
import com.citydigitalpulse.collector.TwitterGetter.service.ServiceThread;

/**
 * 将size个区块加入一个监视线程进行监视，并且允许修改这几个区域或是直接停止
 * 
 * @author zhonglili
 *
 */
public class TwitterStreamThread extends ServiceThread {
	private ArrayList<EarthSqure> watchList;
	private InfoGetterDAO db_info;
	private TwitterSaveDAO db_save;
	private TwitterTools tt;
	private TwitterStream twitterStream;
	private LocatedTwitterListener listener;

	public TwitterStreamThread(ArrayList<EarthSqure> watchList, TwitterTools tt) {
		this.watchList = new ArrayList<EarthSqure>();
		this.watchList.addAll(watchList);
		this.tt = tt;
		init();
	}

	// @Override
	// public void run() {
	// super.run();
	// }
	public void startListening() {
		ArrayList<double[]> locs = new ArrayList<double[]>();
		// 添加Squre
		for (int i = 0; i < watchList.size(); i++) {
			addLocationArea(locs, watchList.get(i).getSouth(), watchList.get(i)
					.getWest(), watchList.get(i).getNorth(), watchList.get(i)
					.getEast());
		}
		double[][] lo = locs.toArray(new double[locs.size()][2]);

		FilterQuery q = new FilterQuery();
		q.locations(lo);
		twitterStream.filter(q);
	}

	// 按照W,S,E,N的顺序添加
	private void addLocationArea(ArrayList<double[]> locs, double south,
			double west, double north, double east) {
		locs.add(new double[] { west, south });
		locs.add(new double[] { east, north });
	}

	// 返回监视列表的大小
	public int getWatchSize() {
		return watchList.size();
	}

	// 将线程名称存入数据库并且修改区块状态
	public void saveThreadname() {
		for (int i = 0; i < watchList.size(); i++) {
			watchList.get(i).setStreamState(1);
			watchList.get(i).setThreadName(this.gettName());
			db_info.changeSqureState(watchList.get(i).getSqureID(), 1,
					this.gettName());
		}

	}

	// 根据区块ID停止监听
	public void stopStreamSqure(int squreID) {
		for (int i = 0; i < watchList.size(); i++) {
			if (watchList.get(i).getSqureID() == squreID) {
				// 改变数据库这个区域的状态
				db_info.changeSqureState(squreID, 0, "none");
				watchList.remove(i);
				reStart();
				return;
			}
		}
	}

	// 当监视列表发生变化是重新启动监视
	private void reStart() {
		// 停止当前流
		twitterStream.cleanUp();
		twitterStream.shutdown();
		init();
		// 重新运行
		startListening();
	}

	private void init() {
		this.db_info = new InfoGetterDAO_MySQL();
		this.db_save = new TwitterSavingDAOimpl();
		ConfigurationBuilder cb = tt.getConfigurationBuilder();
		this.twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		this.listener = new LocatedTwitterListener(db_save, db_info);
		this.twitterStream.addListener(listener);
	}

	@Override
	public void stopMe() {
		// 停止当前流
		twitterStream.clearListeners();
		twitterStream.cleanUp();
		twitterStream.shutdown();
	}

}
