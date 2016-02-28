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
import java.util.List;

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

	public static void main(String[] args) {
		NLP_PreProessing ot = new NLP_PreProessing();
		HashMap<Integer, TaggedMessage> records = null;
		ot.init();
		records = ot.loadRecordsFromFile("data/mark_records.txt");
		//去掉句子的多余成分(人称代词等)
		//识别句子中的否定词，否定词列表
		// 将数据写入ARFF格式
		ot.writeRecords2Arff(records, "data/training_data2.arff");
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
