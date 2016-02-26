package com.zhongli.NLPProgram.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPTest {

	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;
	private POSTaggerME tagger_en;

	public static void main(String[] args) {
		OpenNLPTest ot = new OpenNLPTest();
		ot.test();
	}

	private void init() {
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
			tagger_en = new POSTaggerME(model);
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

	private void test() {
		init();
		
	}
}
