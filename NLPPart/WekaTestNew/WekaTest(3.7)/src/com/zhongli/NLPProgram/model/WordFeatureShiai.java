package com.zhongli.NLPProgram.model;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 
 * @author Zhongli Li
 *
 */
public class WordFeatureShiai {
	private double[] features;
	private String emotion;

	/**
	 * 
	 */
	public WordFeatureShiai(double[] features, String emotion) {
		this.features = features;
		this.emotion = emotion;
	}

	public Instance getWekaInstance(Instances data) {
		if (data == null) {
			// System.out.println("null");
			data = getWekaInstances();
		}
		double[] vals;
		// 3. fill with data
		vals = new double[data.numAttributes()];
		vals[0] = features[0];
		vals[1] = features[1];
		vals[2] = features[2];
		vals[3] = features[3];
		vals[4] = features[4];
		vals[5] = features[5];
		vals[6] = features[6];
		vals[7] = features[7];
		vals[8] = features[8];
		vals[9] = features[9];
		vals[10] = features[10];
		vals[11] = features[11];
		vals[12] = data.attribute("emotion").indexOfValue(emotion);
		return new DenseInstance(1.0, vals);
	}

	public static Instances getWekaInstances() {
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		// 1. HashSet up attributes
		atts = new ArrayList<Attribute>();
		// - numeric
		atts.add(new Attribute("f1"));
		// - numeric
		atts.add(new Attribute("f2"));
		// - numeric
		atts.add(new Attribute("f3"));
		// - numeric
		atts.add(new Attribute("f4"));
		// - numeric
		atts.add(new Attribute("f5"));
		// - numeric
		atts.add(new Attribute("f6"));
		// - numeric
		atts.add(new Attribute("f7"));
		// - numeric
		atts.add(new Attribute("f8"));
		// - numeric
		atts.add(new Attribute("f9"));
		// - numeric
		atts.add(new Attribute("f10"));
		atts.add(new Attribute("f11"));
		atts.add(new Attribute("f12"));
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
}
