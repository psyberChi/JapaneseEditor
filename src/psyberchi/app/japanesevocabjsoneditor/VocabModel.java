/*
 *  VocabModel.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONAware;

/**
 * @author Kendall Conrad
 */
public class VocabModel implements JSONAware {

	private static final Logger logger = Logger.getLogger(VocabModel.class.getCanonicalName());
	private HashMap<String, ArrayList<VocabItem>> categories = new HashMap<String, ArrayList<VocabItem>>();

	public VocabModel() {
		//
	}

	@Override
	public String toJSONString() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		int catCount = 0;
		int catSize = getCategoryCount();

		for (String cat : getCategories()) {
			json.append("\t\"").append(cat).append("\": [\n");
			ArrayList<VocabItem> items = getVocabItems(cat);
			for (int a = 0; a < items.size(); a++) {
				json.append("\t\t").append(items.get(a).toJSONString());
				if (a + 1 < items.size()) {
					json.append(",");
				}
				json.append("\n");
			}
			json.append((catCount + 1 < catSize) ? "\t],\n" : "\t]\n");
			catCount++;
		}
		json.append("}\n");
		return json.toString();
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
			logger.log(Level.WARNING, "Vocabulary already exist: {0}", item.getEnglish());
			return false;
		}
		logger.log(Level.WARNING, "Category doesn't exist: {0}", cat);
		return false;
	}

	/**
	 * Gets the names of the categories.
	 * @return
	 */
	public String[] getCategories() {
		Set<String> keyset = categories.keySet();
		Iterator<String> iter = keyset.iterator();
		String[] cats = new String[keyset.size()];
		int a = 0;
		while (iter.hasNext()) {
			cats[a++] = iter.next();
		}
		Arrays.sort(cats);
		return cats;
	}

	/**
	 * Returns the number of categories in the model.
	 * @return
	 */
	public int getCategoryCount() {
		return categories.size();
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
