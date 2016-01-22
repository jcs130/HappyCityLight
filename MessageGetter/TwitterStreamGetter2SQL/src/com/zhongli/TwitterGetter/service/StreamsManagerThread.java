package com.zhongli.TwitterGetter.service;

import java.util.ArrayList;

import com.zhongli.TwitterGetter.dao.InfoGetterDAO;
import com.zhongli.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.zhongli.TwitterGetter.model.EarthSqure;
import com.zhongli.TwitterGetter.service.twitter4j.TwitterStreamThread;
import com.zhongli.TwitterGetter.service.twitter4j.TwitterTools;


/**
 * 管理Stream监视线程的管理线程，查询区块表，获取所有使用次数大于0且运行状态为0（停止）的区块，新建相应的Stream监听线程
 * 
 * @author zhonglili
 *
 */
public class StreamsManagerThread extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private TwitterTools tt;

	public StreamsManagerThread(int time) {
		this.settName(this.getClass().getSimpleName());
		this.time = time;
		this.tt = new TwitterTools();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		InfoGetterDAO db = new InfoGetterDAO_MySQL();
		ArrayList<EarthSqure> ess;
		
		//需要修改县城侧罗防止由于死所带来的数据丢失问题
		while (isRunning) {
			try {
				// 从数据库中获取使用次数>0但是没有被监视的Stream区块
				ess = (ArrayList<EarthSqure>) db.getReadySqure();
				// 新建监视线程，每个监视线程监视5个区块
				buildStreamthreads(20, ess);
				sleep(2000);
			} catch (InterruptedException e) {
				isRunning = false;
				System.out.println("线程:" + this.gettName() + "结束....");
				// e.printStackTrace();
			}
		}

	}

	// 将多个区块合并为个线程
	private void buildStreamthreads(int size, ArrayList<EarthSqure> ess) {
		ArrayList<EarthSqure> smallList = new ArrayList<EarthSqure>();
		for (int i = 0; i < ess.size(); i++) {
			// 如果有线程的监视数量小于size，则将这个区块分配给该线程
			if (!insert2Thread(ess.get(i))) {
				if (smallList.size() < size) {
					smallList.add(ess.get(i));
				} else {
					TwitterStreamThread tst = new TwitterStreamThread(
							smallList, tt);
					ThreadsPool.addTwitterStreamThread(tst);
					try {
						sleep(time);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					smallList = new ArrayList<EarthSqure>();
				}

			}
		}
		if (smallList.size() > 0) {
			TwitterStreamThread tst = new TwitterStreamThread(smallList, tt);
			ThreadsPool.addTwitterStreamThread(tst);
		}

	}

	private boolean insert2Thread(EarthSqure earthSqure) {
		for (int j = 0; j < ThreadsPool.getTwitterStreamThreadsNum(); j++) {

		}
		return false;
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
