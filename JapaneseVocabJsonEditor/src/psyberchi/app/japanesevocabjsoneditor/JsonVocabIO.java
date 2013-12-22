/*
 *  JsonVocabIO.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Kendall Conrad
 */
public class JsonVocabIO {

	private static final Logger logger = Logger.getLogger(JsonVocabIO.class.getCanonicalName());

	/**
	 * Checks the validity of a VocabItem object.
	 *
	 * @param vocabItem the JSONObject to check against a VocabItem structure.
	 * @return true if the object has the properties en, ro, kn, and kj; false
	 * otherwise.
	 */
	public static boolean isValidVocabItem(JSONObject vocabItem) {
		if (vocabItem.get("en") != null
				&& vocabItem.get("ro") != null
				&& vocabItem.get("kn") != null
				&& vocabItem.get("kj") != null) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param jsonFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static VocabModel readJsonFile(File jsonFile)
			throws FileNotFoundException, IOException, ParseException {
		// Make sure we have something real to work with
		if (!jsonFile.exists() || !jsonFile.canRead()) {
			logger.log(Level.WARNING,
					"The file ''{0}'' either does not exist or cannot be read.",
					jsonFile);
			return null;
		}
		JSONParser parser = new JSONParser();
		Object wholeFile = parser.parse(new FileReader(jsonFile));
		JSONObject jsonObject = (JSONObject) wholeFile;
		VocabModel model = new VocabModel();
		logger.log(Level.INFO, "JSON file ''{0}'' successfuly read in.",
				jsonFile.getCanonicalPath());

		int catCount = 0;
		int vocabCount = 0;

		// Go through keys, which are the categories
		for (Object key : jsonObject.keySet()) {
			if (key instanceof String) {
				// Add the category
				String cat = (String) key;
				if (model.addCategory(cat)) {
					catCount++;
				}

				Object arrayPiece = jsonObject.get(key);
				if (!(arrayPiece instanceof JSONArray)) {
					logger.log(Level.INFO, "Type not recognized: {0}",
							jsonObject.get(key).getClass().toString());
					continue;
				}
				// Process the vocabulary for this category
				JSONArray objArray = (JSONArray) arrayPiece;
				for (Object vocabItem : objArray) {
					try {
						JSONObject vocabObject = (JSONObject) vocabItem;
						// Make sure it's valid before trying to add it
						if (!isValidVocabItem(vocabObject)) {
							logger.log(Level.WARNING,
									"Invalid VocabItem found. Skipping it: {0}",
									vocabObject.toJSONString());
							continue;
						}

						// Get lesson
						Object lessonObject = vocabObject.get("ln");
						int lesson = 0;
						// Parse it out if it exists
						if (lessonObject != null) {
							lesson = Integer.parseInt(lessonObject.toString(), 10);
						}
						// Adjust category marker
						if (vocabObject.get("en").equals("_")) {
							vocabObject.put("en", "#" + cat);
						}
						// Create the VocabItem from the JSON
						VocabItem item = new VocabItem(
								vocabObject.get("en").toString(),
								vocabObject.get("ro").toString(),
								vocabObject.get("kn").toString(),
								vocabObject.get("kj").toString(),
								lesson);
						if (model.addVocabItem(cat, item)) {
							vocabCount++;
							if (logger.isLoggable(Level.FINE)) {
								String s = String.format("%s: %s, %s, %s, %s, %d",
										cat, item.getEnglish(), item.getRomaji(),
										item.getKana(), item.getKanji(), item.getLesson());
								logger.log(Level.FINE, "Added new vocab: {0}", s);
							}
						}
					}
					catch (Exception ex) {
						logger.log(Level.SEVERE, "VocabItem had problems: {0}",
								ex.getLocalizedMessage());
					}
				}
			}
		}
		logger.log(Level.INFO, "Read in {0} categories and {1} vocabulary items.",
				new Object[]{catCount, vocabCount});
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
		if (file == null || model == null) {
			return false;
		}
		try {
			String json = JSONValue.toJSONString(model);
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.close();
			logger.log(Level.INFO, "Successfully wrote JSON file to ''{0}''", file.getCanonicalPath());
			// TODO use writer directly to avoid String creation; maybe
//			StringWriter out = new StringWriter();
//			model.w
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Problem creating JSON: {0}", ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

}
