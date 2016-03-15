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

import java.io.BufferedReader;
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
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.core.Instances;
import cmu.arktweetnlp.Tagger;

import com.zhongli.NLPProgram.model.TaggedMessage;
import com.zhongli.NLPProgram.model.WordFeatureShiai;

/**
 * 将文件拼装成arff格式
 * 
 * @author Zhongli Li
 *
 */
public class BuildArff {
	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;

	public static void main(String[] args) {
		String root_dir = "data/shiaidb/";
		BuildArff bf = new BuildArff();
		bf.init();
		bf.work(root_dir);

	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
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

	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param root_dir
	 */
	private void work(String root_dir) {
		// 逐行扫描文本文件，根据第一列的id得到标记以及特征
		HashMap<Long, String> emotionMap = loadEmotionFromFile(root_dir
				+ "final_annotation_train.txt");
		// 得到bow的数据
		HashMap<Long, TaggedMessage> records = loadRecordsFromFile(root_dir
				+ "text_all_train.txt", emotionMap);
		// 得到提取特征之后的数据
		HashMap<Long, WordFeatureShiai> features_records = loadWordFeaturesFromForder(
				root_dir + "feasot/", emotionMap);
		// 将得到的数据写入相应的arff格式的文件
		writeRecords2Arff(records, root_dir
				+ "training_data_normal_before_remove_useless_part.arff", false);
		writeFeatures2Arff(features_records, root_dir
				+ "training_data_features_before_remove_useless_part.arff");
	}

	/**
	 * 读取ID对应的标注
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param filePaht
	 * @return
	 */
	private HashMap<Long, String> loadEmotionFromFile(String filePaht) {
		HashMap<Long, String> res = new HashMap<Long, String>();
		try {
			FileReader fr = new FileReader(filePaht);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] attr = null;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					attr = line.split("\t");
					res.put(Long.parseLong(attr[0]), attr[1]);
				}
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}

	private HashMap<Long, WordFeatureShiai> loadWordFeaturesFromForder(
			String forderPaht, Map<Long, String> emotions) {
		HashMap<Long, WordFeatureShiai> res = new HashMap<Long, WordFeatureShiai>();
		// 循环情绪列表并从文件中读取特征值
		List<Long> keys = new ArrayList<Long>();
		keys.addAll(emotions.keySet());
		for (int i = 0; i < keys.size(); i++) {
			// 根据key读取文件
			try {
				FileReader fr = new FileReader(forderPaht + keys.get(i)
						+ ".txt");
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				double[] features = new double[12];
				int f_num = 0;
				while ((line = br.readLine()) != null) {
					if (!line.equals("")) {
						features[f_num] = Double.parseDouble(line.trim());
						f_num++;
					}
				}
				if (f_num != 12) {
					System.out.println("feature not right");
				}
				br.close();
				fr.close();
				// 新建feature对象
				WordFeatureShiai record = new WordFeatureShiai(features,
						emotions.get(keys.get(i)));
				res.put(keys.get(i), record);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}

	private HashMap<Long, TaggedMessage> loadRecordsFromFile(String filePaht,
			Map<Long, String> emotions) {
		HashMap<Long, TaggedMessage> res = new HashMap<Long, TaggedMessage>();
		try {
			FileReader fr = new FileReader(filePaht);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String ID_str = "";
			String text = "";
			int split_num = 0;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					split_num = line.indexOf(":");
					ID_str = line.substring(0, split_num);
					// ID_str = line.substring(0, 18);
					text = line.substring(split_num + 1);
					// text = line.substring(19);
					TaggedMessage firstRecord = new TaggedMessage(
							ARK_POStagger_en, sentence_etector_en, tokenizer,
							0, text);
					firstRecord
							.addEmotion(emotions.get(Long.parseLong(ID_str)));
					res.put(Long.parseLong(ID_str), firstRecord);
				}
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 直接将被分词的数据写入ARFF
	 * 
	 * @param records
	 * @param filePath
	 */
	private void writeRecords2Arff(HashMap<Long, TaggedMessage> records,
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

	/**
	 * 直接将提取特征后的数据写入ARFF
	 * 
	 * @param records
	 * @param filePath
	 */
	private void writeFeatures2Arff(HashMap<Long, WordFeatureShiai> records,
			String filePath) {
		List<WordFeatureShiai> records_list = new ArrayList<WordFeatureShiai>();
		Instances data = WordFeatureShiai.getWekaInstances();
		records_list.addAll(records.values());
		for (int i = 0; i < records_list.size(); i++) {
			data.add(records_list.get(i).getWekaInstance(data));
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
}
