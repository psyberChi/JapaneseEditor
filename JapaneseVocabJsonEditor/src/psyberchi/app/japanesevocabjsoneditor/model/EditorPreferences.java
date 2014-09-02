/*
 *  EditorPreferences.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.model;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
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
		RECENT_PREFIX_,
		WINDOW_POSITION,
		WINDOW_SIZE;

		public String getPrefName() {
			return "FieldName." + name();
		}

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

	public Preferences getPrefs() {
		return prefs;
	}

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
}
