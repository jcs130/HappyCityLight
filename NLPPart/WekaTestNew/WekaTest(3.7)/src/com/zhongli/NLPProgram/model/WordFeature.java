package com.zhongli.NLPProgram.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import au.com.bytecode.opencsv.CSVReader;

/**
 * 只保留提取的特征的数据集
 * 
 * @author John ag,dg,fr,hp,sd,sp 情绪以及 turning_words, negative_words
 */
public class WordFeature {
	private static HashSet<String> angerSet;
	private static HashSet<String> disgustSet;
	private static HashSet<String> fearSet;
	private static HashSet<String> surpriseSet;
	private static HashSet<String> happySet;
	private static HashSet<String> sadnessSet;
	private static HashSet<String> turningSet;
	private static HashSet<String> notSet;
	private static HashSet<String> positiveSet;
	private static HashSet<String> negativeSet;

	private static int ag_num = 0;
	private static int dg_num = 0;
	private static int fr_num = 0;
	private static int hp_num = 0;
	private static int sd_num = 0;
	private static int sp_num = 0;
	private static int turn_num = 0;
	private static int no_num = 0;
	private static int positive_num = 0;
	private static int negative_num = 0;

	// public WordFeature(TaggedMessage msg) {
	// super();
	// init();
	// getNumbers(msg);
	// }
	static {
		if (angerSet == null) {
			// System.out.println("Loading words..");
			init();
			// System.out.println("Loading words..Done..");
		}
	}

	private static void getNumbers(TaggedMessage msg) {
		if (angerSet == null) {
			System.out.println("Loading words..");
			init();
			System.out.println("Loading words..Done..");
		}
		String filiteredSentens = "";
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

	public static Instance getWekaInstance(Instances data,
			TaggedMessage taggedMessage) {
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

	private static void init() {
		String positivewords = "data/featureWords/positive-words.txt";
		String negativewords = "data/featureWords/negative-words.txt";
		String turningwords = "data/featureWords/turning.txt";
		String notwords = "data/featureWords/negative.txt";
		String anger = "data/featureWords/ag.txt";
		String disgust = "data/featureWords/dg.txt";
		String fear = "data/featureWords/fr.txt";
		String happy = "data/featureWords/hp.txt";
		String sadness = "data/featureWords/sd.txt";
		String surprise = "data/featureWords/sp.txt";
		angerSet = readMotionCSVFile(anger);
		disgustSet = readMotionCSVFile(disgust);
		fearSet = readMotionCSVFile(fear);
		surpriseSet = readMotionCSVFile(surprise);
		happySet = readMotionCSVFile(happy);
		sadnessSet = readMotionCSVFile(sadness);
		turningSet = readMotionCSVFile(turningwords);
		notSet = readMotionCSVFile(notwords);
		positiveSet = readWordListFromTxtFile(positivewords);
		negativeSet = readWordListFromTxtFile(negativewords);
	}

	@SuppressWarnings("resource")
	private static HashSet<String> readMotionCSVFile(String motionFile) {
		HashSet<String> res = new HashSet<String>();
		CSVReader reader;
		String word = "";
		try {
			reader = new CSVReader(new FileReader(motionFile));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				word = nextLine[0].toLowerCase();
				if (!"".equals(word)) {
					res.add(word);
					// System.out.println("add: " + word);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("list size: " + res.size());
		return res;
	}

	private static HashSet<String> readWordListFromTxtFile(String path) {
		HashSet<String> res = new HashSet<String>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (line.equals("") || line.charAt(0) == ';') {
					// skip
				} else {
					res.add(line.trim().toLowerCase());
					// System.out.println(line.trim().toLowerCase());
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
