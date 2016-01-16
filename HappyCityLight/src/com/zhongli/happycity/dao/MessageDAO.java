package com.zhongli.happycity.dao;

import java.util.ArrayList;

import com.zhongli.happycity.model.message.MarkMessageObj;
import com.zhongli.happycity.model.message.MarkMsg2Web;
import com.zhongli.happycity.model.message.MarkRecordObj;

/**
 * 用来从标记数据库中存取消息的方法类
 * 
 * @author zhonglili
 *
 */
public interface MessageDAO {
	/**
	 * 获取一条新的待标记信息， 需不需要在提取之后做一个标记防止重复读取？ 需不需要建立一个缓存区，一次性读取若干条消息，然后在等待全部标记完成？
	 */
	public ArrayList<MarkMessageObj> getNewMarkingMsg(int limit, String queryOption);

	/**
	 * 从缓存区中获取一条数据
	 * 
	 * @return
	 */
	public MarkMsg2Web getOneNewMsg();

	/**
	 * 对一条数据标记
	 * 
	 * @param user_id
	 * @param message_id
	 * @param text_emotion
	 * @param media_emotion
	 */
	public void recordForMessage(long user_id, long message_id, String text_emotion, ArrayList<String> media_emotion);

	/**
	 * 根据message id 得到message内容
	 * 
	 * @param message_id
	 * @return
	 */
	public MarkMessageObj getMessageInfo(long message_id);

	/**
	 * 更新MarkMessage表
	 * 
	 * @param message_id
	 */
	public void updateMarkMessage(long message_id);

	/**
	 * 更新user detail
	 * 
	 * @param message_id
	 * @param user_id
	 */
	public void updateUserMarkDetail(long message_id, long user_id);

	/**
	 * 创建user detail
	 * 
	 * @param user_id
	 */
	public void createUserMarkDetail(long user_id);

	/**
	 * 取最近count条标记的数据
	 * 
	 * @param count
	 * @param user_id
	 * @return
	 */
	public ArrayList<MarkRecordObj> getRecentRecords(int count, long user_id);

	/**
	 * Sum of records
	 * 
	 * @param user_id
	 * @return
	 */
	public int getRecordCount(long user_id);

	/**
	 * 向数据库中添加新的待标记数据
	 * @param msg_id
	 * @param full_msg_id
	 * @param text
	 * @param media_types
	 * @param media_urls
	 * @param media_urls_local
	 * @param mark_times
	 * @param lang
	 * @param message_from
	 */
	public void insertNewMessage(long msg_id, long full_msg_id, String text,
			String media_types, String media_urls, String media_urls_local,
			int mark_times, String lang, String message_from);
}
