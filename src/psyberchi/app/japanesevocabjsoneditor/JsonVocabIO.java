/*
 *  JsonVocabIO.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Kendall Conrad
 */
public class JsonVocabIO {

	private static final Logger logger = Logger.getLogger(JsonVocabIO.class.getCanonicalName());

	public static VocabModel readJsonFile(File jsonFile) throws FileNotFoundException, IOException, ParseException {
		if (!jsonFile.exists()) {
			return null;
		}
		JSONParser parser = new JSONParser();
		Object wholeFile = parser.parse(new FileReader(jsonFile));
		JSONObject jsonObject = (JSONObject) wholeFile;
		VocabModel model = new VocabModel();

		// Go through keys, which are the categories
		for (Object key : jsonObject.keySet()) {
			if (key instanceof String) {
				// Add the category
				String cat = (String) key;
				model.addCategory(cat);

				Object arrayPiece = jsonObject.get(key);
				if (!(arrayPiece instanceof JSONArray)) {
					logger.log(Level.INFO, "Type: {0}", jsonObject.get(key).getClass().toString());
					continue;
				}
				// Process the vocabulary for this category
				JSONArray objArray = (JSONArray) arrayPiece;
				for (Object vocabItem : objArray) {
					try {
						JSONObject vocabObject = (JSONObject) vocabItem;
						VocabItem item = new VocabItem(
								vocabObject.get("en").toString(),
								vocabObject.get("ro").toString(),
								vocabObject.get("kn").toString(),
								vocabObject.get("kj").toString(),
								Integer.parseInt(vocabObject.get("ln").toString(), 10));
						if (item.getEnglish().equals("_")) {
							// Category item, skip it
//							continue;
						}
						if (model.addVocabItem(cat, item)) {
							String s = String.format("%s: %s, %s, %s, %s, %d",
									cat, item.getEnglish(), item.getRomanji(),
									item.getKana(), item.getKanji(), item.getLesson());
							logger.log(Level.INFO, "Added new vocab: {0}", s);
						}
					}
					catch (Exception ex) {
						logger.log(Level.SEVERE, "VocabItem had problems: {0}",
								ex.getLocalizedMessage());
					}
				}
			}
		}
		return model;
	}

	/**
	 * Writes out the vocabulary model into a JSON format.
	 *
	 * @param file
	 * @param model
	 * @return
	 */
	public static boolean writeJsonFile(File file, VocabModel model) {
		// TODO
		return false;
	}

}
