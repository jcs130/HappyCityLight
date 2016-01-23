package com.citydigitalpulse.messagegetter.TwitterGetter.app;

import com.citydigitalpulse.messagegetter.TwitterGetter.service.ScanRegsThread;
import com.citydigitalpulse.messagegetter.TwitterGetter.service.StartRegThreads;
import com.citydigitalpulse.messagegetter.TwitterGetter.service.StreamsManagerThread;
import com.citydigitalpulse.messagegetter.TwitterGetter.service.ThreadsPool;

/**
 * 
 * @author zhonglili
 *
 */
public class getterMain {

	public static void main(String[] args) {
		getterMain tm = new getterMain();
		// System.out.println(tm.getClass().getSimpleName());
		// tm.doSth();
		// tm.reSetDB();
		tm.startThreads(5000, 5000, 5000);
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void startThreads(int time1, int time2, int time3) {
		ScanRegsThread sRegThread = new ScanRegsThread(time1);
		ThreadsPool.addThread(sRegThread);
		StartRegThreads sSqureThread = new StartRegThreads(time2);
		ThreadsPool.addThread(sSqureThread);
		StreamsManagerThread smt = new StreamsManagerThread(time3);
		ThreadsPool.addThread(smt);

	}

}
