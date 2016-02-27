package com.zhongli.NLPProgram.model;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import emoji4j.Emoji;
import emoji4j.EmojiManager;
import emoji4j.EmojiUtils;

/**
 * 
 * @author John
 *
 */
public class TaggedMessage {

	private Tagger ARK_POStagger_en;
	private SentenceDetectorME sentence_etector_en;
	private Tokenizer tokenizer;

	private int msg_id;
	private String raw_message;
	private List<String> hashtags;
	private List<String> emotion;
	private List<String> emoji_list;
	private List<Double> emotion_value;
	private List<String> tokens;
	private List<String> tags;

	public TaggedMessage(Tagger ark_POStagger_en,
			SentenceDetectorME sentence_etector_en, Tokenizer tokenizer,
			int msg_id, String raw_message) {
		super();
		this.emoji_list = new ArrayList<String>();
		this.emotion = new ArrayList<String>();
		this.emotion_value = new ArrayList<Double>();
		this.tokens = new ArrayList<String>();
		this.tags = new ArrayList<String>();
		this.hashtags = new ArrayList<String>();
		this.ARK_POStagger_en = ark_POStagger_en;
		this.sentence_etector_en = sentence_etector_en;
		this.tokenizer = tokenizer;
		this.msg_id = msg_id;
		this.raw_message = raw_message;
		// 进行分词
		splitRawMessage();
	}

	public void addEmotion(String emotion) {
		this.emotion.add(emotion);
		if (emotion.toLowerCase().equals("positive")) {
			this.emotion_value.add(5.0);
		} else if (emotion.toLowerCase().equals("negative")) {
			this.emotion_value.add(-5.0);
		} else if (emotion.toLowerCase().equals("neutral")) {
			this.emotion_value.add(0.0);
		} else {
			this.emotion_value.add(0.0);
		}
	}

	private void splitRawMessage() {
		// 将字符表情转化为Emoji
		String full_tweet = addSpace(raw_message);
		// System.out.println(full_tweet);
		// 首先进行分句
		String[] sentences = sentence_etector_en.sentDetect(raw_message);
		for (int i = 0; i < sentences.length; i++) {
			// 对没有表情符号的句子进行分词
			List<TaggedToken> taggedTokens = ARK_POStagger_en
					.tokenizeAndTag(sentences[i]);
			for (TaggedToken tt : taggedTokens) {
				if (tt.tag.equals("URL")) {
					tokens.add("URL");
					tags.add("URL");
				} else if (tt.tag.equals("USR")) {
					tokens.add("USR");
					tags.add("USR");
				} else if (tt.tag.equals("HT")) {
					hashtags.add(tt.token);
					tokens.add(tt.token);
					tags.add("HT");
				} else if (tt.tag.equals(".")) {
					// 标点符号使用OPNLP来分词
					String[] simples = tokenizer.tokenize(tt.token);
					for (String s : simples) {
						tokens.add(s);
						tags.add(".");
					}
				}
				// else if (tt.tag.equals("''")) {
				// // 将引号转义
				// tokens.add("``");
				// tags.add("``");
				// }
				else if (tt.tag.equals("UH")) {
					// 将符号表情转化为Emoji表情
					// 将字符表情转化为Emoji
					String emoji_text = EmojiUtils.emojify(tt.token);
					// 如果为连续的表情则拆分,如果是Emoji表情则存入到Emoji列表中，并将表情转为文字存入到句子总体结构中
					String[] UHs = addSpace(emoji_text).trim().split(" ");
					for (String temp : UHs) {
						if (EmojiUtils.isEmoji(temp)) {
							tokens.add(textCodify(temp));
							emoji_list.add(temp);
							tags.add("EMO");
						} else {
							tokens.add(temp);
							tags.add(tt.tag);
						}
					}
				} else {
					tokens.add(tt.token);
					tags.add(tt.tag);
				}
			}
		}
	}

	private String textCodify(String text) {
		String emojifiedText = EmojiUtils.emojify(text);
		for (Emoji emoji : EmojiManager.data()) {
			StringBuilder shortCodeBuilder = new StringBuilder();
			shortCodeBuilder.append("").append(emoji.getAliases().get(0))
					.append("");

			emojifiedText = emojifiedText.replace(emoji.getEmoji(),
					shortCodeBuilder.toString());
		}
		return emojifiedText;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param tweet
	 * @return
	 */
	private String addSpace(String tweet) {
		for (Emoji emoji : EmojiManager.data()) {
			tweet = tweet.replace(emoji.getEmoji(), " " + emoji.getEmoji()
					+ " ");
		}
		return tweet;
	}

	/**
	 * @return the msg_id
	 */
	public int getMsg_id() {
		return msg_id;
	}

	/**
	 * @return the raw_message
	 */
	public String getRaw_message() {
		return raw_message;
	}

	/**
	 * @return the hashtags
	 */
	public List<String> getHashtags() {
		return hashtags;
	}

	/**
	 * @return the emotion
	 */
	public List<String> getEmotion() {
		return emotion;
	}

	/**
	 * @return the emoji_list
	 */
	public List<String> getEmoji_list() {
		return emoji_list;
	}

	/**
	 * @return the emotion_value
	 */
	public List<Double> getEmotion_value() {
		return emotion_value;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	public String[] toStringArray() {
		List<String> res = new ArrayList<String>();
		// 第一列为分词后的句子，第二列为句子的结构
		String text = "";
		String structure = "";
		String emojis = "";
		String score_str = "";
		for (int i = 0; i < this.tokens.size(); i++) {
			text += tokens.get(i).toLowerCase() + " ";
			structure += tags.get(i).toUpperCase() + " ";
		}
		res.add(text.trim());
		res.add(structure.trim());
		// 第三列为Emoji表情列表
		for (int i = 0; i < emoji_list.size(); i++) {
			emojis += emoji_list.get(i) + " ";
		}
		res.add(emojis.trim());
		double score = 0;
		// 第四列为情绪得分
		for (int i = 0; i < emotion_value.size(); i++) {
			score += emotion_value.get(i);
		}
		score = score / (double) emotion_value.size();
		res.add(score_str + score);
		// 第五列为最终情绪
		if (score > 1.67) {
			res.add("positive");
		} else if (score < -1.67) {
			res.add("negative");
		} else {
			res.add("neutral");
		}
		return res.toArray(new String[0]);
	}

	/**
	 * 将数据写入Weka Instances对象中
	 * 
	 * @param data
	 * @return
	 */
	public Instance toWekaInstances(Instances data, boolean numericClass) {
		if (data == null) {
			System.out.println("null");
			data = TaggedMessage.getWekaInstances(numericClass);
		}
		String[] line = this.toStringArray();
		double[] vals;
		// 3. fill with data
		vals = new double[data.numAttributes()];
		// - string
		vals[0] = data.attribute(0).addStringValue(line[0]);
		// - string
		vals[1] = data.attribute(1).addStringValue(line[1]);
		// - string
		vals[2] = data.attribute(2).addStringValue(line[2]);
		if (data.attribute("emotion").isNumeric()) {
			/**************** 1.输出计算后的情绪值 ***************/
			// - numeric
			vals[3] = Double.parseDouble(line[3]);
		} else {
			/**************** 2.输出计算后的情绪文字，向量表示 ******/
			// - nominal
			vals[3] = data.attribute("emotion").indexOfValue(line[4]);
		}
		return new Instance(1.0, vals);
	}

	public static Instances getWekaInstances(boolean numericClass) {
		FastVector atts;
		FastVector attVals;
		// 1. set up attributes
		atts = new FastVector();
		// - string
		atts.addElement(new Attribute("text", (FastVector) null));
		// - string
		atts.addElement(new Attribute("structure", (FastVector) null));
		// - string
		atts.addElement(new Attribute("emojis", (FastVector) null));
		if (numericClass) {
			/**************** 1.输出计算后的情绪值 ***************/
			// - numeric
			atts.addElement(new Attribute("emotion"));
		} else {
			/**************** 2.输出计算后的情绪文字，向量表示 ******/
			// - nominal
			attVals = new FastVector();
			attVals.addElement("positive");
			attVals.addElement("neutral");
			attVals.addElement("negative");
			atts.addElement(new Attribute("emotion", attVals));
		}

		// 2. create Instances object
		System.out.println("Create new datas");
		return new Instances("training_data", atts, 0);
	}
}
