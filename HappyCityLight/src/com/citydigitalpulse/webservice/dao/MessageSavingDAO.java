package com.citydigitalpulse.webservice.dao;

/**
 * 
 * @author zhonglili
 *
 */
public interface MessageSavingDAO {
	/**
	 * Update the emotion text 更新文字的情绪标记
	 * 
	 * @param num_id
	 * @param emotion
	 */
	public void updateTextEmotion(long num_id, String emotion);
}
