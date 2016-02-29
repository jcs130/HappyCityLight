package com.zhongli.NLPProgram.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import cmu.arktweetnlp.Tagger;

import com.zhongli.NLPProgram.model.TaggedMessage;
import com.zhongli.NLPProgram.model.WordFeature;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class LoadModelTest {
	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;

	public static void main(String[] args) {
		LoadModelTest tm = new LoadModelTest();
		tm.init();
		tm.dosth();
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
	}

	private void dosth() {
		// load model
		String rootPath = "data/";
		String classifier_text_img_filename = "training_data_normal_before_remove_useless_part.model";
		String classifier_text_only_filename = "Saimadata/SMO_training_data_normal_before_remove_useless_part.model";
		String classifier_text_img_feature_only_filename = "training_data_fratures_before_remove_useless_part.model";
		String classifier_text_only_feature_only_filename = "Saimadata/training_data_fratures_before_remove_useless_part.model";
		// String classifier_text_img_filename =
		// "training_data_normal_after_remove_useless_part.model";
		// String classifier_text_only_filename =
		// "Saimadata/SMO_training_data_normal_after_remove_useless_part.model";
		Classifier cls_full;
		Classifier cls_feature;
		String test_text = "Congratulations @LeoDiCaprio YOU DESERVE IT #Oscar ";
		// String test_text =
		// "'Shots fired' at Carleton University actually just balloons popping #ottnews http://ow.ly/YRgXC  ";
		System.out.println("Text: " + test_text);
		try {
			// 载入分类器
			cls_full = (Classifier) weka.core.SerializationHelper.read(rootPath
					+ classifier_text_img_filename);
			cls_feature = (Classifier) weka.core.SerializationHelper
					.read(rootPath + classifier_text_img_feature_only_filename);
			// 新建要被标记的数据集，可以静态创建
			Instances unlabeled_full = TaggedMessage.getWekaInstances(false);
			Instances unlabeled_feature = WordFeature.getWekaInstances();
			// set class attribute
			unlabeled_full.setClassIndex(unlabeled_full.numAttributes() - 1);
			unlabeled_feature
					.setClassIndex(unlabeled_feature.numAttributes() - 1);
			// 新建Instance，转换为训练数据格式
			TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
					sentence_etector_en, tokenizer, 0, test_text);
			// test.motifyOneRecord();
			// System.out.println(test.getTokens());
			// System.out.println(test.getTags());
			System.out.println("Big training set:");
			unlabeled_full.add(test
					.toWekaInstance(unlabeled_full, false, false));
			unlabeled_feature.add(WordFeature.getWekaInstance(
					unlabeled_feature, test));
			// create copy
			Instances labeled_full = new Instances(unlabeled_full);
			Instances labeled_feature = new Instances(unlabeled_feature);
			// label instances
			for (int i = 0; i < unlabeled_full.numInstances(); i++) {
				double clsLabel = cls_full.classifyInstance(unlabeled_full
						.instance(i));
				labeled_full.instance(i).setClassValue(clsLabel);
				// System.out.println(unlabeled.instance(i));
				// get the name of the class value
				String prediction = unlabeled_full.classAttribute().value(
						(int) clsLabel);
				System.out.println("Classifier 1" + " -- " + prediction);
			}
			for (int i = 0; i < unlabeled_feature.numInstances(); i++) {
				double clsLabel = cls_feature
						.classifyInstance(unlabeled_feature.instance(i));
				labeled_feature.instance(i).setClassValue(clsLabel);
				// System.out.println(unlabeled.instance(i));
				// get the name of the class value
				String prediction = unlabeled_feature.classAttribute().value(
						(int) clsLabel);
				System.out.println("Classifier 2" + " -- " + prediction);
			}
			System.out.println("Small training set:");
			// System.out.println(labeled.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/********* 小规模数据库 ***************/
		try {
			// 载入分类器
			cls_full = (Classifier) weka.core.SerializationHelper.read(rootPath
					+ classifier_text_only_filename);
			cls_feature = (Classifier) weka.core.SerializationHelper
					.read(rootPath + classifier_text_only_feature_only_filename);
			// 新建要被标记的数据集，可以静态创建
			Instances unlabeled_full = TaggedMessage.getWekaInstances(false);
			Instances unlabeled_feature = WordFeature.getWekaInstances();
			// set class attribute
			unlabeled_full.setClassIndex(unlabeled_full.numAttributes() - 1);
			unlabeled_feature
					.setClassIndex(unlabeled_feature.numAttributes() - 1);
			// 新建Instance，转换为训练数据格式
			TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
					sentence_etector_en, tokenizer, 0, test_text);
			// test.motifyOneRecord();
			// System.out.println(test.getTokens());
			// System.out.println(test.getTags());
			unlabeled_full.add(test
					.toWekaInstance(unlabeled_full, false, false));
			unlabeled_feature.add(WordFeature.getWekaInstance(
					unlabeled_feature, test));
			// create copy
			Instances labeled_full = new Instances(unlabeled_full);
			Instances labeled_feature = new Instances(unlabeled_feature);
			// label instances
			for (int i = 0; i < unlabeled_full.numInstances(); i++) {
				double clsLabel = cls_full.classifyInstance(unlabeled_full
						.instance(i));
				labeled_full.instance(i).setClassValue(clsLabel);
				// System.out.println(unlabeled.instance(i));
				// get the name of the class value
				String prediction = unlabeled_full.classAttribute().value(
						(int) clsLabel);
				System.out.println("Classifier 1" + " -- " + prediction);
			}
			for (int i = 0; i < unlabeled_feature.numInstances(); i++) {
				double clsLabel = cls_feature
						.classifyInstance(unlabeled_feature.instance(i));
				labeled_feature.instance(i).setClassValue(clsLabel);
				// System.out.println(unlabeled.instance(i));
				// get the name of the class value
				String prediction = unlabeled_feature.classAttribute().value(
						(int) clsLabel);
				System.out.println("Classifier 2" + " -- " + prediction);
			}
			// System.out.println(labeled.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
