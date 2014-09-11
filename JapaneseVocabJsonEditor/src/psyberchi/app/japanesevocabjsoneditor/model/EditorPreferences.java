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
import java.util.prefs.Preferences;

/**
 * A partial preference handler for the Japanese Editor application
 * that handles some of the interface to the preference file.
 *
 * @author Kendall Conrad
 */
public class EditorPreferences {
	private ArrayList<String> recentFiles;
	private Point windowLocation;
	private Dimension windowSize;
	private int maxRecent = 5;

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
		WINDOW_SIZE;

		/**
		 * Returns the preference name to be used for the field.
		 *
		 * @returns a string key value for the preference.
		 */
		public String getPrefName() {
			return "FieldName." + name();
		}

		/**
		 * Gets the default font size for a given field. It should
		 * only be used for fields that begin with "FONT" as the name.
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
		 * Gets the default font name for a given field. It should
		 * only be used for fields that begin with "FONT" as the name.
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
//		prefs = Preferences.userNodeForPackage(getClass());
		prefs = p;
	}

	private int getFontSize(FieldName p) {
		return prefs.getInt(p.getPrefName(), FieldName.getDefaultFontSize(p));
	}

	private String getFontName(FieldName p) {
		return prefs.get(p.getPrefName(), FieldName.getDefaultFontName(p));
	}

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
		int max = prefs.getInt(FieldName.RECENT_MAX.getPrefName(), 10);
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
				if (!recentFiles.contains(path) {
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
		Dimension dim = null;
		String strDim = prefs.get(FieldName.WINDOW_SIZE.getPrefName(), "300,340");
		String[] dims = strDim.split(",");
		try {
			return new Dimension(dims[0], dims[1]);
		}
		catch (Exception ex) {
			return new Dimension(300, 340);
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

	public void saveRecentFiles() {
		// TODO keep local aspect or accept a list?
	}

	/**
	 * Saves a given list of files as the most recent
	 */
	public void saveRecentFiles(ArrayList<File> files) {
		int max = getMaxNumberRecentFiles();
		int numFiles = Math.min(max, files.size());
		String prefix = FieldName.RECENT_ITEM_.getPrefName();
		// Store the file's absolute path with index value
		for (int x = 0; x < numFiles; x++) {
			prefs.put(prefix + x, files.get(x).getAbsolutePath());
		}
		// Save number of files
		prefs.put(FieldName.RECENT_ITEMS_NUM.getPrefName(), numFiles);
	}

	/**
	 * Saves away a point to represent the window position.
	 *
	 * @param pt the upperleft position of the window.
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
	 * Save the window size from a Dimension of it. When dim is null
	 * the preference item will be removed.
	 *
	 * @param dim the Dimension of the window to save.
	 */
	public void saveWindowSize(Dimension dim) {
		// Interpret null value as wanting to remove it
		if (dim == null) {
			prefs.remove(FieldName.WINDOW_SIZE.getPrefName());
			return;
		}
		String strDim = dim.getWidth() + "," + dim.getHeight();
		prefs.put(FieldName.WINDOW_SIZE.getPrefName(), strDim);
	}

}
