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
package com.zhongli.NLPProgram.app;

import java.io.IOException;
import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import emoji4j.Emoji;
import emoji4j.EmojiManager;
import emoji4j.EmojiUtils;

/**
 * @author Zhongli Li
 *
 */
public class TestMain {
	private Tagger tagger;

	public static void main(String[] args) {
		TestMain tm = new TestMain();
		tm.init();
		tm.emojiTest();
		
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void emojiTest() {
		String tweet = "Wow political correctness is soooo shit like take a joke why don't you:-)??? ❤️❤️❤️:):-),:-):-]:-xP=*:*<3:P:p,=-). ☀ and But I have every right to make people feel bad it's so cOmedic chillll";
		// 将字符表情转化为Emoji
		String emoji_text = EmojiUtils.emojify(tweet);
		System.out.println(emoji_text);
		String full_tweet = addSpace(emoji_text);
		System.out.println(full_tweet);

		// 将表情换成对应的文字
		String tweet_with_emoji_text = EmojiUtils.shortCodify(tweet);
		System.out.println(tweet_with_emoji_text);

		// 将句子中的表情都去掉
		String tweet_withoutEmoji = EmojiUtils.removeAllEmojis(emoji_text);
		System.out.println(tweet_withoutEmoji);
		// 对没有表情符号的句子进行分词
		List<TaggedToken> taggedTokens = tagger
				.tokenizeAndTag(tweet_withoutEmoji);
		for (TaggedToken tt : taggedTokens) {
			// replace N
			System.out.print(tt.token + "(" + tt.tag + ") ");
		}
		System.out.println();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param tweet
	 * @return
	 */
	private String addSpace(String tweet) {
		for (Emoji emoji : EmojiManager.data()) {
			tweet = tweet.replace(emoji.getEmoji(), " " + emoji.getEmoji()
					+ " ");
		}
		return tweet;
	}

	private void init() {
		// String modelFileName = "model/model.20120919";
		String modelFileName = "model.ritter_ptb_alldata_fixed.20130723";
		tagger = new Tagger();
		try {
			tagger.loadModel(modelFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
