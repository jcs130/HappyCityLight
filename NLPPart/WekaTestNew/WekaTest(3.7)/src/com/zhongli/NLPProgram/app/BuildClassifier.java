package com.zhongli.NLPProgram.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class BuildClassifier {

	public static void main(String[] args) {
		BuildClassifier bc = new BuildClassifier();
		String root_path = "data/";
		// 1. 读取数据集文件
		String training_dataset_file_path = "";
		Instances training_dataset = bc.loadInstancesFromFile(root_path
				+ training_dataset_file_path);
		// 2.构造分类器
		try {
			Classifier clf = bc.buildClassifier(training_dataset);
			//测试分类器
			bc.cross_validation();
			// 3.将分类器存储为文件
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void cross_validation() {
		// TODO Auto-generated method stub
		
	}

	public FilteredClassifier buildClassifier(Instances training_dataset)
			throws Exception {
		// filter
		StringToWordVector stv_filter = new StringToWordVector();
		stv_filter.setInputFormat(training_dataset.stringFreeStructure());
		// classifier
		SMO smo_classifier = new SMO();
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(stv_filter);
		fc.setClassifier(smo_classifier);
		// train and make predictions
		fc.buildClassifier(training_dataset);
		return fc;
	}

	private Instances loadInstancesFromFile(String fileFath) {
		try {
			return new Instances(new BufferedReader(new FileReader(fileFath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
