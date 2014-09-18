/*
 * EditorPrefPanel.java
 *
 * GNU GPL License.
 *
 * Change Log
 * Date        Author               Changes
 * 2014-09-01  Kendall Conrad       Created
 */
package psyberchi.app.japanesevocabjsoneditor.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import psyberchi.app.japanesevocabjsoneditor.model.EditorPreferences;
import psyberchi.app.japanesevocabjsoneditor.model.EditorPreferences.FieldName;

/**
 * The preference panel for the main editor GUI. Allows configuring the font of
 * the list and vocabulary editor panel fields.
 *
 * @author Kendall Conrad
 */
public final class EditorPrefPanel extends PrefPanel implements ChangeListener, ItemListener {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(EditorPrefPanel.class.getCanonicalName());
	/**
	 * Preferences object that will be used for loading and saving preferences
	 */
	private Preferences prefs = Preferences.userNodeForPackage(getClass());
	/**
	 * Helper object for the preferences
	 */
	private EditorPreferences editorPrefs;
	/**
	 * Capture the values of the fields when first loaded to have something to
	 * compare to when saving.
	 */
	private HashMap<FontItemPanel, Font> oldValues = new HashMap<>();
	/**
	 * Keeps a mapping between font item panel and preference name for it
	 */
	private HashMap<FontItemPanel, FieldName> fontFieldMap = new HashMap<>();

	/**
	 * Creates new form EditorPrefPanel
	 */
	public EditorPrefPanel() {
		initComponents();
		editorPrefs = new EditorPreferences(prefs);
		loadPreferences();
		configure();
	}

	/**
	 * Collects the initial values of the fields when the preference window
	 * opens, which will later be used to determine whether or not a preference
	 * has changed and if it needs saving or not.
	 */
	private void configure() {
		fontFieldMap.put(fontItemPanelListSortMode, FieldName.FONT_LIST_SORTMODE);
		fontFieldMap.put(fontItemPanelListEnglish, FieldName.FONT_LIST_ENGLISH);
		fontFieldMap.put(fontItemPanelListRomaji, FieldName.FONT_LIST_ROMAJI);
		fontFieldMap.put(fontItemPanelListKana, FieldName.FONT_LIST_KANA);
		fontFieldMap.put(fontItemPanelListKanji, FieldName.FONT_LIST_KANJI);
		fontFieldMap.put(fontItemPanelEditorEnglish, FieldName.FONT_EDITOR_ENGLISH);
		fontFieldMap.put(fontItemPanelEditorRomaji, FieldName.FONT_EDITOR_ROMAJI);
		fontFieldMap.put(fontItemPanelEditorKana, FieldName.FONT_EDITOR_KANA);
		fontFieldMap.put(fontItemPanelEditorKanji, FieldName.FONT_EDITOR_KANJI);

		// Add listeners an store starting values
		for (Map.Entry<FontItemPanel, FieldName> p : fontFieldMap.entrySet()) {
			p.getKey().jComboBoxFontFamily.addItemListener(this);
			p.getKey().jSpinnerFontSize.addChangeListener(this);
			oldValues.put(p.getKey(), p.getKey().getFontItem());
		}
	}

	@Override
	public boolean dataIsValid() {
		// TODO add any necessary validation
		return true;
	}

	/**
	 * Determines if the value of the FontItemPanel has changed.
	 *
	 * @param c FontItemPanel to check
	 * @return
	 */
	private boolean hasChanged(FontItemPanel c) {
		return !oldValues.get(c).equals(c.getFontItem());
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		if (ItemEvent.SELECTED != evt.getStateChange()) {
			return;
		}
		JComboBox src = (JComboBox) evt.getSource();
		if (src.getParent() instanceof FontItemPanel) {
			Font f = ((FontItemPanel) src.getParent()).getFontItem();
			jLabelFontLook.setFont(f);
		}
	}

	@Override
	public void loadPreferences() {
		logger.log(Level.INFO, "Loading preferences from Editor prefs");
		try {
			// load list fonts
			Font f;
			for (Map.Entry<FontItemPanel, FieldName> p : fontFieldMap.entrySet()) {
				f = editorPrefs.getFontPref(p.getValue());
				p.getKey().setFontItem(f);
				logger.log(Level.INFO, "{2}: {0}:{1}", new Object[]{
					f.getFamily(), f.getSize(), p.getValue().getPrefName()
				});
			}
		}
		catch (Exception ex) {
			logger.log(Level.INFO, "Exception while loading preferences: {0}", ex.getLocalizedMessage());
		}
	}

	@Override
	public void savePreferences() {
		logger.log(Level.INFO, "Saving Editor prefs");

		for (Map.Entry<FontItemPanel, FieldName> p : fontFieldMap.entrySet()) {
			// Only care about items that have actually been motified
			if (hasChanged(p.getKey())) {
				editorPrefs.saveFontPref(p.getValue(), p.getKey().getFontItem());
				logger.log(Level.INFO, "{2}: {0}:{1}", new Object[]{
					p.getKey().getFontItem().getFamily(),
					p.getKey().getFontItem().getSize(),
					p.getValue().getPrefName()
				});
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) e.getSource();
		if (spinner.getParent() instanceof FontItemPanel) {
			Font f = ((FontItemPanel) spinner.getParent()).getFontItem();
			jLabelFontLook.setFont(f);
		}
	}

	/**
	 * A testing main.
	 *
	 * @param a
	 */
	public static void main(String[] a) {
		JFrame dialog = new JFrame();
		BorderLayout layout = new BorderLayout();
		dialog.setLayout(layout);
		dialog.setSize(400, 400);

		EditorPrefPanel panel = new EditorPrefPanel();
		panel.setSize(300, 300);

		dialog.add(panel, BorderLayout.CENTER);

		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane = new javax.swing.JScrollPane();
        jPanelMain = new javax.swing.JPanel();
        jPanelListFonts = new javax.swing.JPanel();
        fontItemPanelListSortMode = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelListEnglish = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelListRomaji = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelListKana = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelListKanji = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        jPanelEditorFonts = new javax.swing.JPanel();
        fontItemPanelEditorEnglish = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelEditorRomaji = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelEditorKana = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        fontItemPanelEditorKanji = new psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel();
        jPanelFontLook = new javax.swing.JPanel();
        jLabelFontLook = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelListFonts.setBorder(javax.swing.BorderFactory.createTitledBorder("List Fonts"));
        jPanelListFonts.setLayout(new javax.swing.BoxLayout(jPanelListFonts, javax.swing.BoxLayout.PAGE_AXIS));

        fontItemPanelListSortMode.setFontLabel("Sort Mode:");
        jPanelListFonts.add(fontItemPanelListSortMode);

        fontItemPanelListEnglish.setFontLabel("English:");
        jPanelListFonts.add(fontItemPanelListEnglish);

        fontItemPanelListRomaji.setFontLabel("Romaji:");
        jPanelListFonts.add(fontItemPanelListRomaji);

        fontItemPanelListKana.setFontLabel("Kana:");
        jPanelListFonts.add(fontItemPanelListKana);

        fontItemPanelListKanji.setFontLabel("Kanji:");
        jPanelListFonts.add(fontItemPanelListKanji);

        jPanelMain.add(jPanelListFonts);

        jPanelEditorFonts.setBorder(javax.swing.BorderFactory.createTitledBorder("Vocab Editor Fonts"));
        jPanelEditorFonts.setLayout(new javax.swing.BoxLayout(jPanelEditorFonts, javax.swing.BoxLayout.PAGE_AXIS));

        fontItemPanelEditorEnglish.setFontLabel("English:");
        jPanelEditorFonts.add(fontItemPanelEditorEnglish);

        fontItemPanelEditorRomaji.setFontLabel("Romaji:");
        jPanelEditorFonts.add(fontItemPanelEditorRomaji);

        fontItemPanelEditorKana.setFontLabel("Kana:");
        jPanelEditorFonts.add(fontItemPanelEditorKana);

        fontItemPanelEditorKanji.setFontLabel("Kanji:");
        jPanelEditorFonts.add(fontItemPanelEditorKanji);

        jPanelMain.add(jPanelEditorFonts);

        jScrollPane.setViewportView(jPanelMain);

        add(jScrollPane, java.awt.BorderLayout.CENTER);

        jPanelFontLook.setLayout(new java.awt.BorderLayout());

        jLabelFontLook.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        jLabelFontLook.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFontLook.setText("English 日本語 ひらがな");
        jLabelFontLook.setMinimumSize(new java.awt.Dimension(168, 30));
        jLabelFontLook.setPreferredSize(new java.awt.Dimension(168, 50));
        jPanelFontLook.add(jLabelFontLook, java.awt.BorderLayout.CENTER);

        add(jPanelFontLook, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelEditorEnglish;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelEditorKana;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelEditorKanji;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelEditorRomaji;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelListEnglish;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelListKana;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelListKanji;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelListRomaji;
    private psyberchi.app.japanesevocabjsoneditor.ui.FontItemPanel fontItemPanelListSortMode;
    private javax.swing.JLabel jLabelFontLook;
    private javax.swing.JPanel jPanelEditorFonts;
    private javax.swing.JPanel jPanelFontLook;
    private javax.swing.JPanel jPanelListFonts;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables
}
