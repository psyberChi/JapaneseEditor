/*
 *  VocabItem.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.model;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * @author Kendall Conrad
 */
public class VocabItem implements JSONAware {

	private int lesson;
	private String english;
	private String romaji;
	private String kana;
	private String kanji;

	/**
	 * Constructor for creating a new vocabulary item.
	 * @param en the English form.
	 * @param ro the romaji form.
	 * @param kn the kana form.
	 * @param kj the kanji form.
	 */
	public VocabItem(String en, String ro, String kn, String kj) {
		english = en;
		romaji = ro;
		kana = kn;
		kanji = kj;
		lesson = 0;
	}

	/**
	 * Constructor for creating a new vocabulary item.
	 * @param en the English form.
	 * @param ro the romaji form.
	 * @param kn the kana form.
	 * @param kj the kanji form.
	 * @param les the lesson the vocabulary is from.
	 */
	public VocabItem(String en, String ro, String kn, String kj, int les) {
		english = en;
		romaji = ro;
		kana = kn;
		kanji = kj;
		lesson = les;
	}

	/**
	 * Returns a JSON formatted String representing the vocabulary item.
	 * @return
	 */
	@Override
	public String toJSONString() {
		String ss = String.format("{\"en\":\"%s\","
				+ " \"ro\":\"%s\", \"kn\":\"%s\","
				+ " \"kj\":\"%s\", \"ln\":%d}",
				JSONObject.escape(getEnglish()),
				JSONObject.escape(getRomaji()),
				JSONObject.escape(getKana()),
				JSONObject.escape(getKanji()),
				getLesson());
		return ss;
	}

	/**
	 * @return the english
	 */
	public String getEnglish() {
		return english;
	}

	/**
	 * @param english the english to set
	 */
	public void setEnglish(String english) {
		this.english = english;
	}

	/**
	 * @return the romaji
	 */
	public String getRomaji() {
		return romaji;
	}

	/**
	 * @param romaji the romaji to set
	 */
	public void setRomaji(String romaji) {
		this.romaji = romaji;
	}

	/**
	 * @return the kana
	 */
	public String getKana() {
		return kana;
	}

	/**
	 * @param kana the kana to set
	 */
	public void setKana(String kana) {
		this.kana = kana;
	}

	/**
	 * @return the kanji
	 */
	public String getKanji() {
		return kanji;
	}

	/**
	 * @param kanji the kanji to set
	 */
	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	/**
	 * @return the lesson
	 */
	public int getLesson() {
		return lesson;
	}

	/**
	 * @param lesson the lesson to set
	 */
	public void setLesson(int lesson) {
		this.lesson = lesson;
	}

}
