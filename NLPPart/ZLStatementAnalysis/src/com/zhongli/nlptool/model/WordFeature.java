package com.zhongli.nlptool.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 只保留提取的特征的数据集
 * 
 * @author John ag,dg,fr,hp,sd,sp 情绪以及 turning_words, negative_words
 */
public class WordFeature {
	private HashSet<String> angerSet;
	private HashSet<String> disgustSet;
	private HashSet<String> fearSet;
	private HashSet<String> surpriseSet;
	private HashSet<String> happySet;
	private HashSet<String> sadnessSet;
	private HashSet<String> turningSet;
	private HashSet<String> notSet;
	private HashSet<String> positiveSet;
	private HashSet<String> negativeSet;

	private int ag_num = 0;
	private int dg_num = 0;
	private int fr_num = 0;
	private int hp_num = 0;
	private int sd_num = 0;
	private int sp_num = 0;
	private int turn_num = 0;
	private int no_num = 0;
	private int positive_num = 0;
	private int negative_num = 0;

	/**
	 * 
	 */
	public WordFeature() {
		init();
	}

	private void getNumbers(TaggedMessage msg) {
		if (angerSet == null) {
			System.out.println("Loading words..");
			init();
			System.out.println("Loading words..Done..");
		}
		String temp_token = "";
		ag_num = 0;
		dg_num = 0;
		fr_num = 0;
		hp_num = 0;
		sd_num = 0;
		sp_num = 0;
		turn_num = 0;
		no_num = 0;
		positive_num = 0;
		negative_num = 0;
		// System.out.println(msg.getTokens().size());
		// System.out.println("no:" + notSet.size());
		// System.out.println(msg.getTokens());
		// 将数组重新组装成字符串,用于检测词组
		for (int i = 0; i < msg.getTokens().size(); i++) {
			temp_token = msg.getTokens().get(i).toLowerCase();
			// System.out.println(temp_token);
			// 循环每个token检查情绪
			if (angerSet.contains(temp_token)) {
				ag_num += 1;
			}
			if (disgustSet.contains(temp_token)) {
				dg_num += 1;
			}
			if (fearSet.contains(temp_token)) {
				fr_num += 1;
			}
			if (surpriseSet.contains(temp_token)) {
				sp_num += 1;
			}
			if (happySet.contains(temp_token)) {
				hp_num += 1;
			}
			if (sadnessSet.contains(temp_token)) {
				sd_num += 1;
			}
			if (turningSet.contains(temp_token)) {
				turn_num += 1;
			}
			if (notSet.contains(temp_token)) {
				no_num += 1;
			}
			if (positiveSet.contains(temp_token)) {
				positive_num += 1;
			}
			if (negativeSet.contains(temp_token)) {
				negative_num += 1;
			}
			// filiteredSentens += msg.getTokens().get(i) + " ";
		}
		// filiteredSentens = filiteredSentens.trim().toLowerCase();

	}

	public Instance getWekaInstance(Instances data, TaggedMessage taggedMessage) {
		if (data == null) {
			// System.out.println("null");
			data = getWekaInstances();
		}
		getNumbers(taggedMessage);
		double[] vals;
		// 3. fill with data
		vals = new double[data.numAttributes()];
		vals[0] = 0 + ag_num;
		vals[1] = 0 + dg_num;
		vals[2] = 0 + fr_num;
		vals[3] = 0 + hp_num;
		vals[4] = 0 + sd_num;
		vals[5] = 0 + sp_num;
		vals[6] = 0 + turn_num;
		vals[7] = 0 + no_num;
		vals[8] = 0 + positive_num;
		vals[9] = 0 + negative_num;
		vals[10] = data.attribute("emotion").indexOfValue(
				taggedMessage.getFinalEmotion());
		return new DenseInstance(1.0, vals);
	}

	public static Instances getWekaInstances() {
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		// 1. HashSet up attributes
		atts = new ArrayList<Attribute>();
		// - numeric
		atts.add(new Attribute("ag_num"));
		// - numeric
		atts.add(new Attribute("dg_num"));
		// - numeric
		atts.add(new Attribute("fr_num"));
		// - numeric
		atts.add(new Attribute("hp_num"));
		// - numeric
		atts.add(new Attribute("sd_num"));
		// - numeric
		atts.add(new Attribute("sp_num"));
		// - numeric
		atts.add(new Attribute("turn_num"));
		// - numeric
		atts.add(new Attribute("no_num"));
		// - numeric
		atts.add(new Attribute("positive_num"));
		// - numeric
		atts.add(new Attribute("negative_num"));
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("positive");
		attVals.add("neutral");
		attVals.add("negative");
		atts.add(new Attribute("emotion", attVals));
		// 2. create Instances object
		// System.out.println("Create new Frature datas");
		return new Instances("training_data", atts, 0);
	}

	private void init() {
		String positivewords = "featurewords/positive-words.txt";
		String negativewords = "featurewords/negative-words.txt";
		String turningwords = "featurewords/turning.txt";
		String notwords = "featurewords/negative.txt";
		String anger = "featurewords/ag.txt";
		String disgust = "featurewords/dg.txt";
		String fear = "featurewords/fr.txt";
		String happy = "featurewords/hp.txt";
		String sadness = "featurewords/sd.txt";
		String surprise = "featurewords/sp.txt";
		angerSet = readWordListFromTxtFile(anger);
		disgustSet = readWordListFromTxtFile(disgust);
		fearSet = readWordListFromTxtFile(fear);
		surpriseSet = readWordListFromTxtFile(surprise);
		happySet = readWordListFromTxtFile(happy);
		sadnessSet = readWordListFromTxtFile(sadness);
		turningSet = readWordListFromTxtFile(turningwords);
		notSet = readWordListFromTxtFile(notwords);
		positiveSet = readWordListFromTxtFile(positivewords);
		negativeSet = readWordListFromTxtFile(negativewords);
	}

	private HashSet<String> readWordListFromTxtFile(String path) {
		HashSet<String> res = new HashSet<String>();
		InputStream ins = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path);
		try {

			String line = "";
			int t = ins.read();
			while (t != -1) {
				if ((char) t == ';') {
					t = ins.read();
					continue;
				}
				if ((char) t == '\n') {
					String word = line.trim().toLowerCase().split(",")[0];
					res.add(word);
					line = "";
				} else {
					line += (char) t;
				}
				t = ins.read();
			}

			ins.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		// System.out.println(path + "....done");
		return res;
	}
}
