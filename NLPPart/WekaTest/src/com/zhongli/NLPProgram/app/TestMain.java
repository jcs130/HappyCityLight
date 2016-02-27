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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zhongli.NLPProgram.model.TaggedMessage;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
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
	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;
	private POSTaggerME POS_tagger_en;

	public static void main(String[] args) {
		TestMain tm = new TestMain();
		HashMap<Integer, TaggedMessage> records = null;
		tm.init();
		// tm.emojiTest();
		// 一行一行的提取信息，进行分词后存入内存
		records = tm.loadRecordsFromFile("data/mark_records.txt");
		/***************************/
		// 为每句话增加其他的标记，例如转折词，否定词数量等

		// 将数据写入CSV文件
		// tm.writeRecords2CSV(records, "data/training_data.csv");
		// 将数据写入ARFF格式
		tm.writeRecords2Arff(records, "data/training_data2.arff");
	}

	private void writeRecords2Arff(HashMap<Integer, TaggedMessage> records,
			String filePath) {
		List<TaggedMessage> records_list = new ArrayList<TaggedMessage>();
		Instances data = TaggedMessage.getWekaInstances(false);
		records_list.addAll(records.values());
		for (int i = 0; i < records_list.size(); i++) {
			data.add(records_list.get(i).toWekaInstances(data, false));
		}

		// Write to the Arff file
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), Charset.forName("utf-8")));
			bw.write(data.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeRecords2CSV(HashMap<Integer, TaggedMessage> records,
			String filePath) {
		CSVWriter writer;
		List<TaggedMessage> records_list = new ArrayList<TaggedMessage>();
		try {
			writer = new CSVWriter(new FileWriter(filePath));
			// 写入第一行
			writer.writeNext("text,structure,emojis,emotion".split(","));
			// feed in your array (or convert your data to an array)
			// String[] entries = "first#second#third".split("#");
			records_list.addAll(records.values());
			for (int i = 0; i < records_list.size(); i++) {
				writer.writeNext(records_list.get(i).toStringArray());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private HashMap<Integer, TaggedMessage> loadRecordsFromFile(String filePaht) {
		// 1.从数据库中读取训练数据到文件
		HashMap<Integer, TaggedMessage> records = new HashMap<Integer, TaggedMessage>();
		CSVReader reader;
		int msg_id = 0;
		try {
			reader = new CSVReader(new FileReader(filePaht), '\t');
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				// System.out.println(nextLine[1] + "\t" + nextLine[4] + "\t"
				// + nextLine[5]);
				if (nextLine[1].equals("msg_id")) {
					continue;
				}
				msg_id = Integer.parseInt(nextLine[1]);
				if (records.containsKey(msg_id)) {
					records.get(msg_id).addEmotion(nextLine[5]);
				} else {
					TaggedMessage firstRecord = new TaggedMessage(
							ARK_POStagger_en, sentence_etector_en, tokenizer,
							msg_id, nextLine[4]);
					records.put(msg_id, firstRecord);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return records;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void emojiTest() {
		// String tweet =
		// "Wow political correctness is soooo shit like take a joke why don't you:-)??? ❤️❤️❤️:):-),:-):-]:-xP=*:*<3:P:p,=-). ☀ and But I have every right to make people feel bad it's so cOmedic chillll";
		String tweet = "\"Ask fascinating questions and find the answers\" TED Fellow Andrew Pelling:) shares the inspiration behind @pellinglab #HUBTalk loooooooooool :):) ????????????!!!!!!!!!!!!!";
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
		List<TaggedToken> taggedTokens = ARK_POStagger_en.tokenizeAndTag(tweet);
		for (TaggedToken tt : taggedTokens) {
			// replace N
			System.out.print(tt.token + "(" + tt.tag + ") ");

		}
		System.out.println();

		// Use the OpenNLP
		String[] sentents = sentence_etector_en.sentDetect(tweet);
		List<TaggedToken> temp = ARK_POStagger_en.tokenizeAndTag(tweet);
		List<String> tokens = new ArrayList<String>();
		for (int i = 0; i < temp.size(); i++) {
			tokens.add(temp.get(i).token);
		}

		String[] POS_tags = POS_tagger_en.tag(tokens.toArray(new String[0]));
		for (int j = 0; j < tokens.size(); j++) {
			System.out.print(tokens.get(j) + "(" + POS_tags[j] + ") ");
		}

		System.out.println();

		for (int i = 0; i < sentents.length; i++) {
			String words[] = tokenizer.tokenize(sentents[i]);
			POS_tags = POS_tagger_en.tag(words);
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
		ARK_POStagger_en = new Tagger();
		try {
			ARK_POStagger_en.loadModel(modelFileName);
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
