package com.zhongli.NLPProgram.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import weka.core.Attribute;
import weka.core.DenseInstance;
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
	private Set<String> remove_POS_set;
	private boolean hasPositive = false;
	private boolean hasNegative = false;
	private boolean hasNeutral = false;

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
		init();
		splitRawMessage();

	}

	/**
	 * Alphabetical list of part-of-speech tags used in the Penn Treebank
	 * Project: https://www.ling.upenn.edu/courses/Fall_2003/ling001/
	 * penn_treebank_pos.html
	 */
	private void init() {

		// 需要去掉的词性列表
		remove_POS_set = new HashSet<String>();
		// @username
		remove_POS_set.add("USR");
		// hashtag
		remove_POS_set.add("HT");
		remove_POS_set.add("-LRB-");
		remove_POS_set.add("-RRB-");
		// URL
		remove_POS_set.add("URL");
		// 符号
		remove_POS_set.add(".");
		// 名词
		// Noun, singular or mass
		remove_POS_set.add("NN");
		// 专有名词，单数
		// Proper noun, singular
		remove_POS_set.add("NNP");
		// 专有名词，复数
		// Proper noun, plural
		remove_POS_set.add("NNPS");
		// 名词，复数
		// Noun, plural
		remove_POS_set.add("NNS");
		// 数字
		// Cardinal number
		remove_POS_set.add("CD");
		// to
		remove_POS_set.add("TO");
		// 人称代词
		// Personal pronoun
		remove_POS_set.add("PRP");
		// 物主代词
		// Possessive pronoun
		remove_POS_set.add("PRP$");
		// 限定词
		// Determiner
		remove_POS_set.add("DT");
		// 介词或从属连词
		// Preposition or subordinating conjunction
		// remove_POS_set.add("IN");
	}

	public void addEmotion(String emotion) {
		this.emotion.add(emotion);
		if (emotion.toLowerCase().equals("positive")) {
			this.emotion_value.add(5.0);
			this.hasPositive = true;
		} else if (emotion.toLowerCase().equals("negative")) {
			this.emotion_value.add(-5.0);
			this.hasNegative = true;
		} else if (emotion.toLowerCase().equals("neutral")) {
			this.hasNeutral = true;
			this.emotion_value.add(0.0);
		} else {
			this.emotion_value.add(0.0);
		}
	}

	private void splitRawMessage() {
		String temp_text = raw_message;
		// 先将所有的URL转化为去掉，以免对转换表情产生影响
		List<TaggedToken> taggedTokens = ARK_POStagger_en
				.tokenizeAndTag(raw_message);
		for (TaggedToken tt : taggedTokens) {
			if (tt.tag.equals("URL")) {
				// System.out.println(tt.token);
				temp_text = temp_text.replace(tt.token, " ");
				// System.out.println(temp_text);
			}
			// 将所有的'll 展开为 will
			temp_text = temp_text.replaceAll("'ll ", " will ");
			// 将所有的否定词展开
			// 将所有的否定缩写提取
			temp_text = temp_text.replaceAll(" can't ", " can not ");
			temp_text = temp_text.replaceAll(" won't ", " will not ");
			temp_text = temp_text.replaceAll("n't ", " not ");
		}
		// // 将字符表情转化为Emoji並且加上空格
		// String full_tweet = addSpace(EmojiUtils.emojify(temp_text));
		String full_tweet = temp_text;
		// System.out.println(full_tweet);
		// 首先进行分句
		String[] sentences = sentence_etector_en.sentDetect(full_tweet);
		for (int i = 0; i < sentences.length; i++) {
			// 对没有表情符号的句子进行分词
			taggedTokens = ARK_POStagger_en.tokenizeAndTag(sentences[i]);
			for (TaggedToken tt : taggedTokens) {
				if (tt.tag.equals("USR")) {
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
				} else if (tt.tag.equals("UH")) {
					// 如果是Emoji表情，则将对英文字加入到原文中，并且将表情加入到表情列表
					if (EmojiUtils.isEmoji(tt.token)) {
						// tokens.add(textCodify(tt.token));
						tokens.add(tt.token);
						emoji_list.add(tt.token);
						tags.add("EMO");
					} else {
						tokens.add(tt.token);
						tags.add(tt.tag);
					}
				} else {
					String token = tt.token.toLowerCase();
					String tag = tt.tag.toUpperCase();
					// 如果以#开头，则去掉
					if (tt.token.indexOf("#") == 0) {
						hashtags.add(tt.token);
						token = tt.token.toLowerCase().substring(0);
					}

					// } else {
					tokens.add(token);
					tags.add(tag);
					// }
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
	 * @param tokens
	 *            the tokens to set
	 */
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
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

	public boolean isUseful() {
		// 如果同时存在正反中标记则本记录无效
		if (hasPositive == true && hasNegative == true && hasNeutral == true) {
			// System.out.println("skip:" + this.emotion);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 将文字与词性一起输出
	 * 
	 * @return
	 */
	public String[] toCombineStringArray() {
		List<String> res = new ArrayList<String>();
		// 第一列为分词后的句子和在句子中的成分
		String text = "";
		String emojis = "";
		String score_str = "";
		for (int i = 0; i < this.tokens.size(); i++) {
			text += tokens.get(i).toLowerCase() + "_"
					+ tags.get(i).toUpperCase() + " ";
		}
		res.add(text.trim());
		// 第二列为Emoji表情列表
		for (int i = 0; i < emoji_list.size(); i++) {
			emojis += emoji_list.get(i) + " ";
		}
		res.add(emojis.trim());
		double score = 0;
		// 第三列为情绪得分
		for (int i = 0; i < emotion_value.size(); i++) {
			score += emotion_value.get(i);
		}
		score = score / (double) emotion_value.size();
		res.add(score_str + score);
		// 第四列为最终情绪
		// 如果没有被标记，显示unknown
		if (emotion.size() == 0) {
			res.add("unknown");
		} else {
			if (score > 1.65) {
				res.add("positive");
			} else if (score < -1.65) {
				res.add("negative");
			} else {
				res.add("neutral");
			}
		}
		return res.toArray(new String[0]);
	}

	public String[] toNormalStringArray() {
		List<String> res = new ArrayList<String>();
		// 第一列为分词后的句子和在句子中的成分
		String text = "";
		String emojis = "";
		for (int i = 0; i < this.tokens.size(); i++) {
			text += tokens.get(i).toLowerCase() + " ";
		}
		res.add(text.trim());
		// 第二列为Emoji表情列表
		for (int i = 0; i < emoji_list.size(); i++) {
			emojis += emoji_list.get(i) + " ";
		}
		res.add(emojis.trim());
		// 第三列为情绪得分
		res.add("" + getScore());
		// 第四列为最终情绪
		// 如果没有被标记，显示unknown
		res.add(getFinalEmotion());
		return res.toArray(new String[0]);
	}

	public double getScore() {
		double res = 0;
		for (int i = 0; i < emotion_value.size(); i++) {
			res += emotion_value.get(i);
		}
		res = res / (double) emotion_value.size();
		return res;
	}

	public String getFinalEmotion() {
		String res = "";
		double score = getScore();
		if (emotion.size() == 0) {
			res = "unknown";
		} else {
			if (score > 1.65) {
				res = "positive";
			} else if (score < -1.65) {
				res = "negative";
			} else {
				res = "neutral";
			}
		}
		return res;
	}

	/**
	 * 将数据写入Weka Instances对象中
	 * 
	 * @param data
	 * @return
	 */
	public Instance toWekaInstance(Instances data, boolean numericClass,
			boolean isCombined) {
		if (data == null) {
			System.out.println("null");
			data = TaggedMessage.getWekaInstances(numericClass);
		}
		String[] line;
		if (isCombined) {
			line = this.toCombineStringArray();
		} else {
			line = this.toNormalStringArray();

		}
		double[] vals;
		// 3. fill with data
		vals = new double[data.numAttributes()];
		// - string
		vals[0] = data.attribute(0).addStringValue(line[0]);
		// // - string
		// vals[1] = data.attribute(1).addStringValue(line[1]);
		// - string
		vals[1] = data.attribute(1).addStringValue(line[1]);
		if (data.attribute("emotion").isNumeric()) {
			/**************** 1.输出计算后的情绪值 ***************/
			// - numeric
			vals[2] = Double.parseDouble(line[2]);
		} else {
			/**************** 2.输出计算后的情绪文字，向量表示 ******/
			// - nominal
			vals[2] = data.attribute("emotion").indexOfValue(line[3]);
		}
		return new DenseInstance(1.0, vals);
	}

	public static Instances getWekaInstances(boolean numericClass) {
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		// 1. set up attributes
		atts = new ArrayList<Attribute>();
		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));
		// - string
		// atts.add(new Attribute("structure", (ArrayList<String>) null));
		// - string
		atts.add(new Attribute("emojis", (ArrayList<String>) null));
		if (numericClass) {
			/**************** 1.输出计算后的情绪值 ***************/
			// - numeric
			atts.add(new Attribute("emotion"));
		} else {
			/**************** 2.输出计算后的情绪文字，向量表示 ******/
			// - nominal
			attVals = new ArrayList<String>();
			attVals.add("positive");
			attVals.add("neutral");
			attVals.add("negative");
			atts.add(new Attribute("emotion", attVals));
		}

		// 2. create Instances object
		// System.out.println("Create new datas");
		return new Instances("training_data", atts, 0);
	}

	public void motifyOneRecord() {
		ArrayList<String> newTokens = new ArrayList<String>();
		ArrayList<String> newTags = new ArrayList<String>();
		// 首先根据词性筛选
		for (int i = 0; i < this.tags.size(); i++) {
			String tag = this.tags.get(i);
			if (remove_POS_set.contains(tag)) {
				// skip
			} else {
				newTokens.add(this.tokens.get(i));
				newTags.add(this.tags.get(i));
			}
		}

		// 将新的列表写入对象
		this.tags = newTags;
		this.tokens = newTokens;
	}
}
