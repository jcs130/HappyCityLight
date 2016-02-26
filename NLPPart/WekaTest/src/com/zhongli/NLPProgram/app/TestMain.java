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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import au.com.bytecode.opencsv.CSVReader;
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
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;
	private POSTaggerME POS_tagger_en;

	public static void main(String[] args) {
		TestMain tm = new TestMain();
		tm.init();
		tm.emojiTest();

		/***************************/
		// 1.从数据库中读取训练数据到文件
		// CSVReader reader;
		// int i = 0;
		// try {
		// reader = new CSVReader(new FileReader("data/mark_records.txt"),
		// '\t');
		// String[] nextLine;
		// while ((nextLine = reader.readNext()) != null) {
		// // nextLine[] is an array of values from the line
		// System.out.println(nextLine[1] + "\t" + nextLine[4] + "\t"
		// + nextLine[5]);
		// i++;
		// }
		// System.out.println(i);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// 2.一行一行的提取信息，

	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void emojiTest() {
		// String tweet =
		// "Wow political correctness is soooo shit like take a joke why don't you:-)??? ❤️❤️❤️:):-),:-):-]:-xP=*:*<3:P:p,=-). ☀ and But I have every right to make people feel bad it's so cOmedic chillll";
		String tweet = "\"Ask fascinating questions and find the answers\" TED Fellow Andrew Pelling shares the inspiration behind @pellinglab #HUBTalk";
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
				.tokenizeAndTag(tweet);
		for (TaggedToken tt : taggedTokens) {
			// replace N
			System.out.print(tt.token + "(" + tt.tag + ") ");

		}
		System.out.println();

		// Use the OpenNLP
		String[] sentents = sentence_etector_en.sentDetect(tweet);
		for (int i = 0; i < sentents.length; i++) {
			String words[] = tokenizer.tokenize(sentents[i]);
			String[] POS_tags = POS_tagger_en.tag(words);
			for (int j = 0; j < words.length; j++) {
				System.out.print(words[j] + "(" + POS_tags[j] + ") ");
			}

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
		String modelFileName = "data/model.ritter_ptb_alldata_fixed.20130723";
		tagger = new Tagger();
		try {
			tagger.loadModel(modelFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 加载工具所需文件
		InputStream modelIn = null;
		// 断句工具
		try {
			modelIn = new FileInputStream(
					"opennlp/languagemodel/en/en-sent.bin");

			SentenceModel model = new SentenceModel(modelIn);
			sentence_etector_en = new SentenceDetectorME(model);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		// 断词工具
		try {
			modelIn = new FileInputStream(
					"opennlp/languagemodel/en/en-token.bin");
			TokenizerModel model = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(model);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		// 词性判断工具
		try {
			modelIn = new FileInputStream(
					"opennlp/languagemodel/en/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			POS_tagger_en = new POSTaggerME(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != modelIn) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
