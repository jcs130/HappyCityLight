package com.citydigitalpulse.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.message.StructuredFullMessage;

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

	public ArrayList<StructuredFullMessage> getFilteredMessages(
			long time_start, long time_end, String place_name,
			List<LocArea> areas, List<String> lang, List<String> message_from,
			boolean is_true_location, List<String> keywords);
}
