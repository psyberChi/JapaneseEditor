/*
 *  EnglishComparator.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.model;

import java.util.Comparator;

/**
 * @author Kendall Conrad
 */
public class EnglishComparator implements Comparator<VocabItem> {
	@Override
	public int compare(VocabItem item1, VocabItem item2) {
		VocabItem v1 = (VocabItem) item1;
		VocabItem v2 = (VocabItem) item2;
		return v1.getEnglish().compareTo(v2.getEnglish());
	}
}
