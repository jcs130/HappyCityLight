package com.zhongli.NLPProgram.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import cmu.arktweetnlp.Tagger;

import com.zhongli.NLPProgram.model.TaggedMessage;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LoadModelTest {
	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;

	public static void main(String[] args) {
		LoadModelTest tm = new LoadModelTest();
		tm.init();
		tm.dosth();
	}

	private void dosth() {
		// load model
		String rootPath = "data/";
		String originalTrain_filename = "training_set(Befor_remove_stopwords).arff";
		String classifier_filename = "J48_1(Befor_remove_stopwords).model";
		Classifier cls;
		try {
			cls = (Classifier) weka.core.SerializationHelper.read(rootPath
					+ classifier_filename);
			// predict instance class values
			Instances originalTrain = new Instances(new BufferedReader(
					new FileReader(rootPath + originalTrain_filename)));

			// load or create Instances to predict
			originalTrain.setClassIndex(originalTrain.numAttributes() - 1);
			// which instance to predict class value
			// int s1 = 2600;
			// 新建Instance，转换为训练数据格式
			TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
					sentence_etector_en, tokenizer, 0, "Hello, Good Good Day~~");
			Instances data = TaggedMessage.getWekaInstances(false);
			Instance test_ins = test.toWekaInstance(data, false,false);
			
			double value = cls.classifyInstance(test_ins);

			System.out.println(originalTrain.instance(0));
			// get the name of the class value
			String prediction = originalTrain.classAttribute().value(
					(int) value);

			System.out.println("The predicted value of instance:" + prediction);
		} catch (Exception e) {
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
	}
}
