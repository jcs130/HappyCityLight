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
		String classifier_filename = "SMO_FiliteredClassifier_test.model";
		Classifier cls;
		String test_text = "The hard work will be pay off~ I finally get the weka for java worked~ lol ~~";
		try {
			// 载入分类器
			cls = (Classifier) weka.core.SerializationHelper.read(rootPath
					+ classifier_filename);
			// 新建要被标记的数据集，可以静态创建
			Instances unlabeled = TaggedMessage.getWekaInstances(false);
			// set class attribute
			unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
			// 新建Instance，转换为训练数据格式
			TaggedMessage test = new TaggedMessage(ARK_POStagger_en,
					sentence_etector_en, tokenizer, 0, test_text);
			unlabeled.add(test.toWekaInstances(unlabeled, false));

			// create copy
			Instances labeled = new Instances(unlabeled);
			// label instances
			for (int i = 0; i < unlabeled.numInstances(); i++) {
				double clsLabel = cls.classifyInstance(unlabeled.instance(i));
				labeled.instance(i).setClassValue(clsLabel);
				// System.out.println(unlabeled.instance(i));
				// get the name of the class value
				String prediction = unlabeled.classAttribute().value(
						(int) clsLabel);
				System.out.println(test_text + " -- " + prediction);
			}
			System.out.println();
			System.out.println(labeled.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
