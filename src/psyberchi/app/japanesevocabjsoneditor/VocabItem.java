/*
 *  VocabItem.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

/**
 * @author Kendall Conrad
 */
public class VocabItem {

	private int lesson;
	private String english;
	private String romanji;
	private String kana;
	private String kanji;

	/**
	 * Constructor for creating a new vocabulary item.
	 * @param en the English form.
	 * @param ro the romanji form.
	 * @param kn the kana form.
	 * @param kj the kanji form.
	 */
	public VocabItem(String en, String ro, String kn, String kj) {
		english = en;
		romanji = ro;
		kana = kn;
		kanji = kj;
		lesson = 0;
	}

	/**
	 * Constructor for creating a new vocabulary item.
	 * @param en the English form.
	 * @param ro the romanji form.
	 * @param kn the kana form.
	 * @param kj the kanji form.
	 * @param les the lesson the vocabulary is from.
	 */
	public VocabItem(String en, String ro, String kn, String kj, int les) {
		english = en;
		romanji = ro;
		kana = kn;
		kanji = kj;
		lesson = les;
	}

	/**
	 * Returns a JSON formatted String representing the vocabulary item.
	 * @return
	 */
	public String toJsonString() {
		String ss = String.format("{\"en\": \"%s\","
				+ " \"ro\": \"%s\", \"kn\": \"%s\","
				+ " \"kj\": \"%s\", \"ln\": %d}",
				getEnglish(), getRomanji(), getKana(), getKanji(), getLesson());
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
	 * @return the romanji
	 */
	public String getRomanji() {
		return romanji;
	}

	/**
	 * @param romanji the romanji to set
	 */
	public void setRomanji(String romanji) {
		this.romanji = romanji;
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
