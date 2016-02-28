package com.zhongli.NLPProgram.app;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.zhongli.NLPProgram.model.TaggedMessage;

import cmu.arktweetnlp.Tagger;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.core.Instances;

public class NLP_PreProessing {

	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;
	private Set<String> remove_POS_set;

	public static void main(String[] args) {
		NLP_PreProessing ot = new NLP_PreProessing();
		HashMap<Integer, TaggedMessage> records = null;
		ot.init();
		// 将原文分词，并且将分词与标记联合输出
		records = ot.loadRecordsFromFile("data/mark_records.txt");
		// 将数据写入ARFF格式
		ot.writeRecords2Arff(records,
				"data/training_data_combined_before_remove_useless_part.arff",
				true);
		// 去掉句子的多余成分(人称代词等)
		ot.removeUselessPart(records);
		ot.writeRecords2Arff(records,
				"data/training_data_combined_after_remove_useless_part.arff",
				false);
	}

	private void removeUselessPart(HashMap<Integer, TaggedMessage> records) {
		List<Integer> key_list = new ArrayList<Integer>();
		key_list.addAll(records.keySet());
		for (int i = 0; i < key_list.size(); i++) {
			motifyOneRecord(records.get(key_list.get(i)));
		}

	}

	private void motifyOneRecord(TaggedMessage taggedMessage) {
		ArrayList<String> newTokens = new ArrayList<String>();
		ArrayList<String> newTags = new ArrayList<String>();
		// 首先根据词性筛选
		for (int i = 0; i < taggedMessage.getTags().size(); i++) {
			String tag = taggedMessage.getTags().get(i);
			if (remove_POS_set.contains(tag)) {
				// skip
			} else {
				newTokens.add(taggedMessage.getTokens().get(i));
				newTags.add(taggedMessage.getTags().get(i));
			}
		}

		// 将新的列表写入对象
		taggedMessage.setTags(newTags);
		taggedMessage.setTokens(newTokens);
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
					firstRecord.addEmotion(nextLine[5]);
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
	 * 直接将被分词的数据写入ARFF
	 * 
	 * @param records
	 * @param filePath
	 */
	private void writeRecords2Arff(HashMap<Integer, TaggedMessage> records,
			String filePath, boolean isCombined) {
		List<TaggedMessage> records_list = new ArrayList<TaggedMessage>();
		Instances data = TaggedMessage.getWekaInstances(false);
		records_list.addAll(records.values());
		for (int i = 0; i < records_list.size(); i++) {
			// 如果为有效记录则写入文件
			if (records_list.get(i).isUseful()) {
				data.add(records_list.get(i).toWekaInstance(data, false,
						isCombined));
			}
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
		/**
		 * Alphabetical list of part-of-speech tags used in the Penn Treebank
		 * Project: https://www.ling.upenn.edu/courses/Fall_2003/ling001/
		 * penn_treebank_pos.html
		 */
		// 需要去掉的词性列表
		remove_POS_set = new HashSet<String>();
		// @username
		remove_POS_set.add("USR");
		// URL
		remove_POS_set.add("URL");
		// 名词
		// Noun, singular or mass
		remove_POS_set.add("NN");
		// 专有名词，单数
		// Proper noun, singular
		remove_POS_set.add("NNP");
		// 专有名词，复数
		// Proper noun, plural
		remove_POS_set.add("NNPS");
		// 名词，复数
		// Noun, plural
		remove_POS_set.add("NNS");
		// 数字
		// Cardinal number
		remove_POS_set.add("CD");
		// to
		remove_POS_set.add("TO");
		// 人称代词
		// Personal pronoun
		remove_POS_set.add("PRP");
		// 物主代词
		// Possessive pronoun
		remove_POS_set.add("PRP$");
		// 限定词
		// Determiner
		remove_POS_set.add("DT");
		// 介词或从属连词
		// Preposition or subordinating conjunction
		remove_POS_set.add("IN");
	}
}
