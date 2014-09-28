/*
 *  EditorPreferences.java
 *
 *  GNU GPL License.
 *
 * Change Log
 * Date        Author            Changes
 * 2014-09-06  Kendall Conrad    Adding recent file load/save
 */
package psyberchi.app.japanesevocabjsoneditor.model;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * A partial preference handler for the Japanese Editor application that handles
 * some of the interface to the preference file.
 *
 * @author Kendall Conrad
 */
public class EditorPreferences {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(EditorPreferences.class.getCanonicalName());
	private ArrayList<String> recentFiles = new ArrayList<>();

	public static enum FieldName {
		FONT_LIST_SORTMODE,
		FONT_LIST_ENGLISH,
		FONT_LIST_ROMAJI,
		FONT_LIST_KANA,
		FONT_LIST_KANJI,
		FONT_EDITOR_ENGLISH,
		FONT_EDITOR_ROMAJI,
		FONT_EDITOR_KANA,
		FONT_EDITOR_KANJI,
		RECENT_MAX,
		RECENT_ITEMS_NUM,
		RECENT_ITEM_,
		WINDOW_POSITION,
		WINDOW_SIZE,
		WINDOW_SEPARATOR_POS;

		/**
		 * Returns the preference name to be used for the field.
		 *
		 * @returns a string key value for the preference.
		 */
		public String getPrefName() {
			return "FieldName." + name();
		}

		/**
		 * Gets the default font size for a given field. It should only be used
		 * for fields that begin with "FONT" as the name.
		 */
		public static int getDefaultFontSize(FieldName f) {
			switch (f) {
				case FONT_LIST_KANA:
				case FONT_LIST_KANJI:
					return 20;
				case FONT_EDITOR_KANA:
				case FONT_EDITOR_KANJI:
					return 22;
				case FONT_EDITOR_ENGLISH:
					return 16;
				case FONT_LIST_SORTMODE:
				case FONT_LIST_ENGLISH:
				case FONT_LIST_ROMAJI:
				case FONT_EDITOR_ROMAJI:
					return 16;
				default:
					return 16;
			}
		}

		/**
		 * Gets the default font name for a given field. It should only be used
		 * for fields that begin with "FONT" as the name.
		 */
		public static String getDefaultFontName(FieldName f) {
			switch (f) {
				case FONT_LIST_SORTMODE:
				case FONT_LIST_ENGLISH:
				case FONT_LIST_ROMAJI:
				case FONT_EDITOR_ENGLISH:
				case FONT_EDITOR_ROMAJI:
					return "Verdana";
				case FONT_LIST_KANA:
				case FONT_LIST_KANJI:
				case FONT_EDITOR_KANA:
				case FONT_EDITOR_KANJI:
				default:
					return "serif";
			}
		}
	};

	private Preferences prefs;

	public EditorPreferences(Preferences p) {
		prefs = p;
	}

	/**
	 * Adds a file to the recent list of files.
	 *
	 * @param path the file path
	 * @return true if the file was added to the list, false otherwise
	 */
	public boolean addRecentFile(String path) {
		if (path == null) {
			return false;
		}
		// If it is already in the list remove it first so it can be added to the
		// start of the list to make it more recent
		if (recentFiles.contains(path)) {
			recentFiles.remove(path);
		}
		recentFiles.add(0, path);
		saveRecentFiles();
		return true;
	}

	/**
	 * Retrieves the font size for a given FieldName.
	 *
	 * @param p the field of the font size to retrieve
	 * @return
	 */
	private int getFontSize(FieldName p) {
		return prefs.getInt(p.getPrefName(), FieldName.getDefaultFontSize(p));
	}

	/**
	 * Retrieves the font name for a given FieldName.
	 *
	 * @param p the field of the font name to retrieve
	 * @return
	 */
	private String getFontName(FieldName p) {
		return prefs.get(p.getPrefName(), FieldName.getDefaultFontName(p));
	}

	/**
	 * Retrieves the font for a given FieldName.
	 *
	 * @param p the field of the font to retrieve
	 * @return
	 */
	public Font getFontPref(FieldName p) {
		String defaultName = getFontName(p);
		int defaultSize = getFontSize(p);
		String fon = prefs.get(p.getPrefName(), defaultName + ":" + defaultSize);
		String[] bits = fon.split(":");
		Font font = new Font(bits[0], Font.PLAIN, Integer.valueOf(bits[1]));
		return font;
	}

	/**
	 * Gets the maximum number of recent files to remember.
	 */
	public int getMaxNumberRecentFiles() {
		int max = prefs.getInt(FieldName.RECENT_MAX.getPrefName(), 6);
		return max;
	}

	/**
	 * Returns an accessor for the preference object.
	 */
	public Preferences getPrefs() {
		return prefs;
	}

	/**
	 * Get a list of file paths for the recently opened files
	 */
	public ArrayList<String> getRecentFiles() {
		recentFiles.clear();
		// Get number of recent files stored
		int num = prefs.getInt(FieldName.RECENT_ITEMS_NUM.getPrefName(), 0);
		String path;
		for (int x = 0; x < num; x++) {
			path = prefs.get(FieldName.RECENT_ITEM_.getPrefName() + x, null);
			if (path != null) {
				if (!recentFiles.contains(path)) {
					recentFiles.add(path);
				}
			}
		}
		return recentFiles;
	}

	/**
	 * Returns the point position the window was and should be opened at.
	 *
	 * @returns Point for the upper-left position of the window.
	 */
	public Point getWindowPosition() {
		Point pt = new Point();
		String strPt = prefs.get(FieldName.WINDOW_POSITION.getPrefName(), "0,0");
		String[] pts = strPt.split(",");
		try {
			pt.x = Integer.parseInt(pts[0]);
			pt.y = Integer.parseInt(pts[1]);
		}
		catch (Exception ex) {
			pt.x = 0;
			pt.y = 0;
		}
		return pt;
	}

	/**
	 * Gets the window size from the preferences.
	 */
	public Dimension getWindowSize() {
		String strDim = prefs.get(FieldName.WINDOW_SIZE.getPrefName(), "480,380");
		String[] dims = strDim.split(",");
		try {
			Dimension dim = new Dimension(
					Double.valueOf(dims[0]).intValue(),
					Double.valueOf(dims[1]).intValue());
			return dim;
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Could not read window size: {0}", ex.getLocalizedMessage());
			return new Dimension(480, 380);
		}
	}

	/**
	 * Saves a font for a preference field.
	 */
	public boolean saveFontPref(FieldName p, Font font) {
		try {
			String pref = font.getFamily() + ":" + font.getSize();
			prefs.put(p.getPrefName(), pref);
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Saves the current known list of file paths that it knows about
	 */
	public void saveRecentFiles() {
		saveRecentFiles(recentFiles);
	}

	/**
	 * Saves a given list of files as the most recent
	 */
	public void saveRecentFiles(ArrayList<String> files) {
		int max = getMaxNumberRecentFiles();
		int numFiles = Math.min(max, files.size());
		String prefix = FieldName.RECENT_ITEM_.getPrefName();

		// Trim the list to ensure it's within limit
		while (files.size() > numFiles) {
			// Oldest files are considered and get dropped
			files.remove(files.size() - 1);
		}
		// Store the file's absolute path with index value
		for (int x = 0; x < numFiles; x++) {
			prefs.put(prefix + x, files.get(x));
		}
		// Save number of files
		prefs.putInt(FieldName.RECENT_ITEMS_NUM.getPrefName(), numFiles);
	}

	/**
	 * Saves away a point to represent the window position.
	 *
	 * @param pt the upper-left position of the window.
	 */
	public void saveWindowPosition(Point pt) {
		// Interpret null value as wanting to remove it
		if (pt == null) {
			prefs.remove(FieldName.WINDOW_POSITION.getPrefName());
			return;
		}
		String strPt = pt.x + "," + pt.y;
		prefs.put(FieldName.WINDOW_POSITION.getPrefName(), strPt);
	}

	/**
	 * Save the window size from a Dimension of it. When dim is null the
	 * preference item will be removed.
	 *
	 * @param dim the Dimension of the window to save.
	 */
	public void saveWindowSize(Dimension dim) {
		// Interpret null value as wanting to remove it
		if (dim == null) {
			prefs.remove(FieldName.WINDOW_SIZE.getPrefName());
			return;
		}
		String strDim = dim.width + "," + dim.height;
		prefs.put(FieldName.WINDOW_SIZE.getPrefName(), strDim);
	}
}
