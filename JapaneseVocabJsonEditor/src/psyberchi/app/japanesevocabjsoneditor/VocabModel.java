/*
 *  VocabModel.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	 *
	 * @param cat the category to add.
	 * @return true if able to add the new category, false if it already exists.
	 */
	public boolean addCategory(String cat) {
		if (!categories.containsKey(cat)) {
			categories.put(cat, new ArrayList<VocabItem>());
			logger.log(Level.FINE, "Adding category: {0}", cat);
			return true;
		}
		return false;
	}

	/**
	 * Add a vocabulary item to a category.
	 *
	 * @param cat the category to add to, which must exist already.
	 * @param item the vocabulary item to add.
	 * @return true if successfully added, false if the category does not exist
	 * or the vocabulary item already exist in the category.
	 */
	public boolean addVocabItem(String cat, VocabItem item) {
		if (categories.containsKey(cat)) {
			if (!categories.get(cat).contains(item)) {
				categories.get(cat).add(item);
				logger.log(Level.FINE, "Adding vocab to category {0}: {1}",
						new Object[]{cat, item.toJSONString()});
				return true;
			}
			logger.log(Level.WARNING, "Vocabulary already exist: {0}", item.getEnglish());
			return false;
		}
		logger.log(Level.WARNING, "Category doesn''t exist: {0}", new Object[]{cat});
		return false;
	}

	/**
	 * Gets the names of the categories.
	 *
	 * @return
	 */
	public List<String> getCategories() {
		Set<String> keyset = categories.keySet();
		Iterator<String> iter = keyset.iterator();
		List<String> cats = new ArrayList<String>();
		int a = 0;
		while (iter.hasNext()) {
			cats.add(iter.next());
		}
		Collections.sort(cats);
		return cats;
	}

	/**
	 * Returns the number of categories in the model.
	 *
	 * @return
	 */
	public int getCategoryCount() {
		return categories.size();
	}

	/**
	 * Returns and array of all lesson numbers present in the model.
	 *
	 * @return
	 */
	public List<Integer> getLessons() {
		ArrayList<Integer> lessons = new ArrayList<Integer>();
		for (Map.Entry<String, ArrayList<VocabItem>> set : categories.entrySet()) {
			for (VocabItem item : set.getValue()) {
				if (!lessons.contains(item.getLesson())) {
					lessons.add(item.getLesson());
				}
			}
		}
		return lessons;
	}

	/**
	 * Returns the total number of vocabulary in the model.
	 *
	 * @return
	 */
	public int getVocabCount() {
		int total = 0;
		for (ArrayList<VocabItem> set : categories.values()) {
			total += set.size();
		}
		return total;
	}

	/**
	 * Get the list of vocabulary for the given category.
	 *
	 * @param category the category to retrieve the vocabulary from.
	 * @return the list of vocabulary of the category, null if the category
	 * doesn't exist.
	 */
	public ArrayList<VocabItem> getVocabItems(String category) {
		return categories.get(category);
	}

	/**
	 * Retrieves all VocabItem that have a lesson value matching the given
	 * lesson passed in.
	 *
	 * @param lesson
	 * @return
	 */
	public ArrayList<VocabItem> getVocabItems(int lesson) {
		ArrayList<VocabItem> items = new ArrayList<VocabItem>();
		for (Map.Entry<String, ArrayList<VocabItem>> set : categories.entrySet()) {
			for (VocabItem item : set.getValue()) {
				// If the lesson number matches and it's not a category label
				if (item.getLesson() == lesson && !item.getEnglish().startsWith("#")) {
					items.add(item);
				}
			}
		}
		Collections.sort(items, new EnglishComparator());
		return items;
	}

	/**
	 * Returns whether or not a category exists.
	 *
	 * @param category
	 * @return
	 */
	public boolean hasCategory(String category) {
		return categories.containsKey(category);
	}

	/**
	 * Removes a given VocabItem from a given category if both exist.
	 *
	 * @param category
	 * @param item
	 * @return
	 */
	public boolean removeVocabItem(String category, VocabItem item) {
		if (item != null) {
			ArrayList<VocabItem> items = categories.get(category);
			if (items != null && items.contains(item)) {
				logger.log(Level.INFO, "Removing vocab from category {0}: {1}",
						new Object[]{category, item.toJSONString()});
				return items.remove(item);
			}
		}
		return false;
	}

	/**
	 * Renames a category and copies over the vocabular items. The old name must
	 * exist and the new name must not yet exist.
	 *
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean renameCategory(String oldName, String newName) {
		if (categories.containsKey(oldName) && !categories.containsKey(newName)) {
			categories.put(newName, categories.remove(oldName));
			logger.log(Level.INFO, "Renaming category: {0} to {1}", new Object[]{oldName, newName});
			return true;
		}
		return false;
	}
}
