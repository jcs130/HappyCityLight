package com.zhongli.TwitterGetter.app;

import com.zhongli.TwitterGetter.service.ScanRegsThread;
import com.zhongli.TwitterGetter.service.StartRegThreads;
import com.zhongli.TwitterGetter.service.StreamsManagerThread;
import com.zhongli.TwitterGetter.service.ThreadsPool;

/**
 * ���Գ������
 * 
 * @author zhonglili
 *
 */
public class getterMain {

	public static void main(String[] args) {
		getterMain tm = new getterMain();
		// System.out.println(tm.getClass().getSimpleName());
		// tm.doSth();
		// �ָ����ݿ�Ϊ��ʼ״̬
		// tm.reSetDB();
		// ��ʼ��������߳�
		tm.startThreads(5000, 5000, 5000);
		// ���߳�ѭ��
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
	 * 1.ɨ��reg���ݿ⣬��ȡ����״̬Ϊ0�����򲢽����е�reg�����С�������(�����������)
	 * 2.����С��������������ݿ������Stream���飨�������������
	 * 3.��ѯ�������ȡ����״̬Ϊ0�����飬�½�Stream�����سǼ�����Щ���鲢���޸�״̬���������������
	 */
	private void startThreads(int time1, int time2, int time3) {
		// 1. ɨ�������򲢽��������
		ScanRegsThread sRegThread = new ScanRegsThread(time1);
		ThreadsPool.addThread(sRegThread);
		// 2.��ѯ���������ȡ����״̬Ϊ3�����鲢���޸�����������״̬
		StartRegThreads sSqureThread = new StartRegThreads(time2);
		ThreadsPool.addThread(sSqureThread);
		// 3.��ѯ�������ȡ����ʹ�ô�������0������״̬Ϊ0��ֹͣ�������飬�½���Ӧ��Stream�����߳�
		StreamsManagerThread smt = new StreamsManagerThread(time3);
		ThreadsPool.addThread(smt);

	}

}
