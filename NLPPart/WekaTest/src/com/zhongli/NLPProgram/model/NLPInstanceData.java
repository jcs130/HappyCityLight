package com.zhongli.NLPProgram.model;

import java.util.List;

/**
 * One Line In model
 * 
 * @author John
 *
 */
public class NLPInstanceData {
	private List<String> text;
	private List<String> structure;
	private Double emotion;

	/**
	 * @return the text
	 */
	public List<String> getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(List<String> text) {
		this.text = text;
	}

	/**
	 * @return the structure
	 */
	public List<String> getStructure() {
		return structure;
	}

	/**
	 * @param structure
	 *            the structure to set
	 */
	public void setStructure(List<String> structure) {
		this.structure = structure;
	}

	/**
	 * @return the emotion
	 */
	public Double getEmotion() {
		return emotion;
	}

	/**
	 * @param emotion
	 *            the emotion to set
	 */
	public void setEmotion(Double emotion) {
		this.emotion = emotion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NLPInstanceData [text=" + text + ", structure=" + structure
				+ ", emotion=" + emotion + "]";
	}
}
