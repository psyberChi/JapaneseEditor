/*
 *  VocabModel.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Kendall Conrad
 */
public class VocabModel {

	private HashMap<String, ArrayList<VocabItem>> categories = new HashMap<String, ArrayList<VocabItem>>();

	public VocabModel() {
		//
	}

	/**
	 * Add a new category to our collection.
	 * @param cat the category to add.
	 * @return true if able to add the new category, false if it already exists.
	 */
	public boolean addCategory(String cat) {
		if (!categories.containsKey(cat)) {
			categories.put(cat, new ArrayList<VocabItem>());
			return true;
		}
		return false;
	}

	/**
	 * Add a vocabulary item to a category.
	 * @param cat the category to add to, which must exist already.
	 * @param item the vocabulary item to add.
	 * @return true if successfully added, false if the category does not exist
	 * or the vocabulary item already exist in the category.
	 */
	public boolean addVocabItem(String cat, VocabItem item) {
		if (categories.containsKey(cat)) {
			if (!categories.get(cat).contains(item)) {
				categories.get(cat).add(item);
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the list of vocabulary for the given category.
	 * @param category the category to retrieve the vocabulary from.
	 * @return the list of vocabulary of the category, null if the category
	 * doesn't exist.
	 */
	public ArrayList<VocabItem> getVocabItems(String category) {
		return categories.get(category);
	}

}
