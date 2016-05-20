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
package com.zhongli.nlptool.classifiter;

import java.io.IOException;
import java.io.InputStream;

import com.zhongli.nlptool.model.TaggedMessage;
import com.zhongli.nlptool.model.WordFeature;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.classifiers.Classifier;
import weka.core.Instances;
import cmu.arktweetnlp.Tagger;

/**
 * 提取特征词后的训练器
 * 
 * @author Zhongli Li
 *
 */
public class SentenceFeatureClassifier_en {
	private String model_file_path;
	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;
	private Classifier cls;
	private Instances unlabeled_feature;
	private WordFeature wf;

	/**
	 * 训练好的分类器的路径
	 * 
	 * @param model_file_path
	 * @throws Exception
	 *             训练器加载错误
	 */
	public SentenceFeatureClassifier_en(String model_file_path)
			throws Exception {
		this.model_file_path = model_file_path;
		this.init();
	}

	// 使用分类器得到预测结果
	public String getSentiment(String text) {
		String prediction = "";
		// 新建Instance，转换为训练数据格式
		TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
				sentence_etector_en, tokenizer, 0, text);
		unlabeled_feature.add(wf.getWekaInstance(unlabeled_feature, test));
		try {
			double clsLabel = cls.classifyInstance(unlabeled_feature
					.firstInstance());
			unlabeled_feature.clear();
			prediction = unlabeled_feature.classAttribute().value(
					(int) clsLabel);
		} catch (Exception e) {
			e.printStackTrace();
			new Throwable("Classifier model not match.");
		}
		return prediction;
	}

	public double getSentimentValue(String text) {
		double postive = 0, negitive = 0, neutral = 0;
		// 新建Instance，转换为训练数据格式
		TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
				sentence_etector_en, tokenizer, 0, text);
		unlabeled_feature.add(wf.getWekaInstance(unlabeled_feature, test));
		try {
			double[] values = cls.distributionForInstance(unlabeled_feature
					.firstInstance());
			unlabeled_feature.clear();
			postive = values[0];
			neutral = values[1];
			negitive = values[2];

		} catch (Exception e) {
			e.printStackTrace();
			new Throwable("Classifier model not match.");
		}
		if (postive > negitive && postive > neutral) {
			return (postive + neutral);
		} else if (negitive > postive && negitive > neutral) {
			return -(negitive + neutral);
		} else {
			return 0;
		}
	}

	/**
	 * @throws Exception
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void init() throws Exception {
		// String modelFileName = "model/model.20120919";
		ARK_POStagger_en = new Tagger();
		try {
			ARK_POStagger_en
					.loadModel("/model.ritter_ptb_alldata_fixed.20130723");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 加载工具所需文件
		InputStream modelIn = null;
		// 断句工具
		try {
			modelIn = TaggedMessage.class
					.getResourceAsStream("/opennlp/languagemodel/en/en-sent.bin");

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
			modelIn = TaggedMessage.class
					.getResourceAsStream("/opennlp/languagemodel/en/en-token.bin");
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
		// 载入分类器
		cls = (Classifier) weka.core.SerializationHelper.read(model_file_path);
		unlabeled_feature = WordFeature.getWekaInstances();
		// set class attribute
		unlabeled_feature.setClassIndex(unlabeled_feature.numAttributes() - 1);
		this.wf = new WordFeature();
	}
}
