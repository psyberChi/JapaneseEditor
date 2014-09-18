/*
 *  JapaneseVocabEditorController.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.controller;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import psyberchi.app.japanesevocabjsoneditor.model.EditorPreferences;
import psyberchi.app.japanesevocabjsoneditor.model.EditorPreferences.FieldName;
import psyberchi.app.japanesevocabjsoneditor.model.EnglishComparator;
import psyberchi.app.japanesevocabjsoneditor.model.VocabItem;
import psyberchi.app.japanesevocabjsoneditor.model.VocabModel;
import psyberchi.app.japanesevocabjsoneditor.ui.EditorPrefPanel;
import psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditor;
import static psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditor.APP_TITLE;
import psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditorPanel;
import psyberchi.app.japanesevocabjsoneditor.ui.PreferenceDialog;

/**
 * @author Kendall Conrad
 */
public class JapaneseVocabEditorController implements ActionListener, ChangeListener, FocusListener, ItemListener, ListSelectionListener, PreferenceChangeListener, PropertyChangeListener {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(JapaneseVocabEditor.class.getCanonicalName());
	/**
	 * Preference object.
	 */
	private EditorPreferences prefs;
	/**
	 * Property that fires when the selected category or lesson has changed.
	 */
	public static String PROP_CatSelectionChange = "PROP_CatSelectionChange";
	/**
	 * Property that fires when the selected vocabulary has changed.
	 */
	public static String PROP_VocabSelectionChange = "PROP_VocabSelectionChange";
	/**
	 * A container for all of the current set of vocabulary.
	 */
	private ArrayList<VocabItem> vocabulary = new ArrayList<>();
	/**
	 * Whether the status message is a timed one.
	 */
	private boolean timedStatus = false;
	/**
	 * The ListModel for the category JList.
	 */
	public DefaultListModel modelCategories = null;
	/**
	 * The ListModel for the lessons JList.
	 */
	public DefaultListModel modelLessons = null;
	/**
	 * The ListModel for the vocabulary JList.
	 */
	public DefaultListModel modelVocabulary = null;
	private static final String defaultFontNameEn = "Verdana";
	private static final String defaultFontNameJp = "serif";
	private Font defaultFont = new Font(defaultFontNameEn, Font.PLAIN, 16);
	private HashMap<FieldName, Font> fontMap = new HashMap<>();
	/**
	 * Handle for doing file IO capabilities.
	 */
	private VocabModelIO vocabIo = new VocabModelIO();
	/**
	 * The currently selected lesson.
	 */
	private int currentLesson = -1;
	/**
	 * The currently selected index of the vocabulary item.
	 */
	private int currentVocabIdx = -1;
	/**
	 * Handle to the main editor.
	 */
	private JapaneseVocabEditor vocabEditor;
	/**
	 * The currently selected category
	 */
	private String currentCat = null;
	/**
	 * The currently selected VocabItem.
	 */
	private VocabItem selectedVocabItem;
	/**
	 * The vocabulary model holding categories and vocabulary.
	 */
	private VocabModel model = null;

	/**
	 * ActionCommand list for the editor.
	 */
	public static enum EditorActions {
		/**
		 * Create a new file
		 */
		FileNew,
		/**
		 * Open a file
		 */
		FileOpen,
		/**
		 * Opens a recent file
		 */
		FileRecent,
		/**
		 * Save open file
		 */
		FileSave,
		/**
		 * Save open file as a new file
		 */
		FileSaveAs,
		/**
		 * Opens the preferences
		 */
		Preferences,
		/**
		 * Close the currently open file
		 */
		FileClose,
		/**
		 * Exit the program
		 */
		Exit,
		/**
		 * Add a new category
		 */
		CatAdd,
		/**
		 * Delete a new category
		 */
		CatDelete,
		/**
		 * Add new vocabulary
		 */
		VocabAdd,
		/**
		 * Delete vocabulary
		 */
		VocabDelete,
		/**
		 * Move a currently selected vocabulary
		 */
		VocabMove,
		/**
		 * The sort mode has changed
		 */
		SortModeChange,
		/**
		 * The vocabulary item selected has changed?
		 */
		VocabSelect;

		/**
		 * Gets a EditorActions object from a String. Returns null if not found.
		 *
		 * @param str
		 * @return
		 */
		public static EditorActions getEditorAction(String str) {
			for (EditorActions action : EditorActions.values()) {
				if (action.name().equals(str)) {
					return action;
				}
			}
			if (str.startsWith("VocabMove_")) {
				return VocabMove;
			}
			return null;
		}
	};

	/**
	 * Enumeration of vocabulary display modes.
	 */
	public enum VocabDisplayMode {
		English,
		Romaji,
		Kana,
		Kanji;
	};

	/**
	 * Enumeration of the sort modes for organizing the vocabulary.
	 */
	public enum SortMode {
		Categories,
		Lessons;
	};

	/**
	 * Constructor
	 *
	 * @param editor
	 */
	public JapaneseVocabEditorController(JapaneseVocabEditor editor) {
		vocabEditor = editor;
		prefs = new EditorPreferences(Preferences.userNodeForPackage(EditorPrefPanel.class));
		prefs.getPrefs().addPreferenceChangeListener(this);
		vocabIo.addPropertyChangeListener(this);
	}

	/**
	 * Initialize some of the components that need it.
	 */
	public void initComponents() {
		modelCategories = new DefaultListModel();
		modelLessons = new DefaultListModel();
		modelVocabulary = new DefaultListModel();
		// Set the list selector panels with the new models
		vocabEditor.listSelectorCategoryLesson.setListModel(modelCategories);
		vocabEditor.listSelectorVocabulary.setListModel(modelVocabulary);
		readPreferences();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		EditorActions action = EditorActions.getEditorAction(ae.getActionCommand());
		if (action == null) {
			return;
		}
		switch (action) {
			case FileNew:
				createNewFile();
				break;
			case FileOpen:
				openFile();
				break;
			case FileRecent:
				openRecentFile(ae);
				break;
			case FileSave:
				vocabIo.saveFile(model);
				break;
			case FileSaveAs:
				vocabIo.saveAsFile(model);
				break;
			case Preferences:
				PreferenceDialog dialog = new PreferenceDialog(vocabEditor, true);
				dialog.setVisible(true);
				break;
			case FileClose:
				closeFile();
				break;
			case Exit:
				savePreferences();
				if (vocabIo.closeFile(model)) {
					logger.log(Level.INFO, "Exiting program");
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							vocabEditor.dispose();
							System.exit(0);
						}
					});
				}
				break;
			case CatAdd:
				if (model == null) {
					return;
				}
				String newCat = JOptionPane.showInputDialog(null, "New Category:",
						"New Category", JOptionPane.QUESTION_MESSAGE);
				if (newCat != null) {
					if (!addCategory(newCat)) {
						JOptionPane.showMessageDialog(null, "That category already exists.");
					}
					else {
						// Add category label item
						VocabItem vi = new VocabItem("#" + newCat, "", "", "");
						addVocabulary(vi, newCat);
					}
				}
				break;
			case CatDelete:
				// TODO How to handle vocab that exist in category?
				// Could block removing category while vocab exist, or
				// could move them to uncategorized category, or
				// could prompt user if all vocab should be deleted as well.
				// Forcing user to move them first makes sense.
				break;
			case VocabAdd:
				addVocabulary();
				break;
			case VocabDelete:
				removeVocabItem(true);
				break;
			case VocabMove:
				moveVocabulary(ae.getActionCommand());
				break;
			case SortModeChange:
				updateCategoryLessonList();
				switch (getSortMode()) {
					case Categories:
						vocabEditor.listSelectorCategoryLesson.setListModel(modelCategories);
						Object value = vocabEditor.listSelectorCategoryLesson.getList().getSelectedValue();
						if (value != null) {
							updateVocabularyList(value.toString());
							setVocabControlStates(true);
						}
						else {
							modelVocabulary.clear();
							setVocabControlStates(false);
						}
						break;
					case Lessons:
						vocabEditor.listSelectorCategoryLesson.setListModel(modelLessons);
						int sel = vocabEditor.listSelectorCategoryLesson.getList().getSelectedIndex();
						if (sel > -1) {
							updateVocabularyList(sel);
							setVocabControlStates(true);
						}
						else {
							modelVocabulary.clear();
							setVocabControlStates(false);
						}
						break;
				}
				break;
			case VocabSelect:
				// TODO may not need this event
				break;
		}
	}

	/**
	 * Adds a new category to the list if it doesn't already exist.
	 *
	 * @param cat
	 * @return
	 */
	private boolean addCategory(String cat) {
		if (model != null && cat != null && !modelCategories.contains(cat)) {
			model.addCategory(cat);
			logger.log(Level.INFO, "Adding new category: {0}", cat);
			// Make sure it's sorted after add
			updateCategoryLessonList();
			// Highlight the new category
			firePropertyChange(vocabEditor.jButtonCategoryAdd, PROP_CatSelectionChange, currentCat, cat);
			vocabIo.setFileModified(true);
			return true;
		}
		return false;
	}

	/**
	 * Adds a new vocabulary item by prompting for an English word, which will
	 * then be used to populate the romaji list as long as the new item does not
	 * start with a # or already exists.
	 */
	public void addVocabulary() {
		String resp = JOptionPane.showInputDialog(null, "English entry:",
				"Add new vocabulary", JOptionPane.QUESTION_MESSAGE);
		if (resp != null) {
			if (modelVocabulary.contains(resp)) {
				JOptionPane.showMessageDialog(null, "That vocabulary item already exists.");
				return;
			}
			else if (resp.startsWith("#")) {
				JOptionPane.showMessageDialog(null, "Only the category label can start with #.");
				return;
			}
			if (addVocabulary(new VocabItem(resp, "", "", ""))) {
				vocabEditor.listSelectorVocabulary.getList().setSelectedValue(resp, true);
				logger.log(Level.INFO, "Adding new vocabulary: {0}", resp);
			}
			else {
				setStatusText("Failed to add vocab " + resp, 4000);
			}
		}
	}

	/**
	 * Adds a VocabItem to the currently selected category.
	 *
	 * @param item a VocabItem to be added, which must not be null.
	 * @return true if successfully added, false otherwise.
	 */
	public boolean addVocabulary(VocabItem item) {
		return addVocabulary(item, currentCat);
	}

	/**
	 * Adds a new {@link VocabItem} to the given category.
	 *
	 * @param item the VocabItem to add.
	 * @return true if successfully added, false otherwise.
	 */
	public boolean addVocabulary(VocabItem item, String category) {
		if (model != null && item != null && !vocabulary.contains(item)) {
			if (model.addVocabItem(category, item)) {
				updateVocabularyList(category);
				vocabIo.setFileModified(true);
				logger.log(Level.INFO, "Adding new vocabulary: {0}", item.toJSONString());
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the GUI components to be ready for new data.
	 */
	public void clearGUI() {
		// Clear all of the JList
		modelCategories.clear();
		modelLessons.clear();
		modelVocabulary.clear();
		vocabEditor.japaneseVocabEditorPanel.clearPanel();
		setStatusText(" ");
		setCurrentCategory(null);
		setCurrentVocabIndex(-1);
	}

	/**
	 * Clears the GUI and resets the open file object.
	 */
	public boolean closeFile() {
		if (vocabIo.closeFile(model)) {
			clearGUI();
			model = null;
			return true;
		}
		return false;
	}

	/**
	 * Creates a new file by creating a new model and updating the GUI.
	 *
	 * @return
	 */
	private boolean createNewFile() {
		// Close off any existing file
		if (closeFile()) {
			// Create a new model
			model = new VocabModel();
			clearGUI();
			vocabEditor.listSelectorCategoryLesson.getSelector().setEnabled(true);
			// Trigger event so add/remove category buttons get enabled
			itemStateChanged(new ItemEvent(
					vocabEditor.listSelectorCategoryLesson.getSelector(),
					ItemEvent.ITEM_STATE_CHANGED, this, ItemEvent.SELECTED));
			vocabEditor.japaneseVocabEditorPanel.setEnabled(false);
			vocabIo.setFileModified(true);
			setStatusText("Created new file");
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param src
	 * @param prop
	 * @param oldValue
	 * @param newValue
	 */
	private void firePropertyChange(Object src, String prop, Object oldValue, Object newValue) {
		propertyChange(new PropertyChangeEvent(src, prop, oldValue, newValue));
	}

	@Override
	public void focusGained(FocusEvent fe) {
		// nothing
	}

	@Override
	public void focusLost(FocusEvent fe) {
		// TODO
		if (selectedVocabItem == null) {
			return;
		}
		Object src = fe.getSource();
		if (src instanceof JTextField) {
			JTextField field = (JTextField) src;
			// handle english change
			String ov = selectedVocabItem.getEnglish();
			String nv = field.getText();
			if (!ov.equals(nv)) {
				selectedVocabItem.setEnglish(nv);
//				firePropertyChange(PROP_MODIFIED_ENGLISH, ov, nv);
//				propertyChange(new PropertyChangeEvent(src, JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH, ov, nv));
				firePropertyChange(src, JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH, ov, nv);
			}
			// @todo handle romaji change

			// @todo handle kana change

			// @todo handle kanji change
		}
	}

	/**
	 * Gets the currently selected category.
	 *
	 * @return
	 */
	public String getCurrentCategory() {
		return currentCat;
	}

	/**
	 * Gets the currently selected lesson.
	 *
	 * @return selected lesson
	 */
	public int getCurrentLesson() {
		return currentLesson;
	}

	/**
	 * Gets the currently selected vocabulary index.
	 *
	 * @return vocabulary selected index
	 */
	public int getCurrentVocabIndex() {
		return currentVocabIdx;
	}

	/**
	 * Returns the currently selected SortMode.
	 *
	 * @return
	 */
	public SortMode getSortMode() {
		return ((SortMode) vocabEditor.listSelectorCategoryLesson.getSelector().getSelectedItem());
	}

	/**
	 * Gets a VocabItem by the index in the list.
	 *
	 * @param idx the index the vocabulary item is at.
	 * @return VocabItem at given index.
	 */
	public VocabItem getVocabulary(int idx) {
		return vocabulary.get(idx);
	}

	/**
	 * Returns whether or not using a timed status.
	 *
	 * @return
	 */
	public boolean isTimedStatus() {
		return timedStatus;
	}

	/**
	 * Handle JComboBox selection changes.
	 *
	 * @param ie
	 */
	@Override
	public void itemStateChanged(ItemEvent ie) {
		JComboBox src = (JComboBox) ie.getSource();

		// Vocabulary item sort order selection change
		if (src.equals(vocabEditor.listSelectorVocabulary.getSelector())) {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				updateVocabulary();
				// set font for type
				VocabDisplayMode mode = (VocabDisplayMode) src.getSelectedItem();
				Font font = defaultFont;
				switch (mode) {
					case English:
						font = fontMap.get(FieldName.FONT_LIST_ENGLISH);
						break;
					case Romaji:
						font = fontMap.get(FieldName.FONT_LIST_ROMAJI);
						break;
					case Kana:
						font = fontMap.get(FieldName.FONT_LIST_KANA);
						break;
					case Kanji:
						font = fontMap.get(FieldName.FONT_LIST_KANJI);
						break;
				}
				vocabEditor.listSelectorVocabulary.getList().setFont(font);
			}
		}
		else if (src.equals(vocabEditor.listSelectorCategoryLesson.getSelector())) {
			boolean isSelected = false;
			switch (getSortMode()) {
				case Categories:
					isSelected = !vocabEditor.listSelectorCategoryLesson.getList().isSelectionEmpty();
					vocabEditor.jButtonCategoryAdd.setEnabled(true);
					vocabEditor.jButtonCategoryDelete.setEnabled(true);
					vocabEditor.jMenuItemCatAdd.setEnabled(true);
					vocabEditor.jMenuItemCatDelete.setEnabled(true);
					break;
				case Lessons:
					isSelected = !vocabEditor.listSelectorCategoryLesson.getList().isSelectionEmpty();
					vocabEditor.jButtonCategoryAdd.setEnabled(false);
					vocabEditor.jButtonCategoryDelete.setEnabled(false);
					vocabEditor.jMenuItemCatAdd.setEnabled(false);
					vocabEditor.jMenuItemCatDelete.setEnabled(false);
					break;
			}
			setVocabControlStates(isSelected);
		}
	}

	/**
	 * Move a VocabItem from one category to another.
	 *
	 * @param item
	 * @param fromCat
	 * @param toCat
	 * @return
	 */
	private boolean moveVocabItem(VocabItem item, String fromCat, String toCat) {
		if (model == null || item == null) {
			return false;
		}
		// Check if both categories exist
		if (model.hasCategory(fromCat) && model.hasCategory(toCat)) {
			// Check if vocab exists in from category
			ArrayList<VocabItem> fromItems = model.getVocabItems(fromCat);
			ArrayList<VocabItem> toItems = model.getVocabItems(toCat);
			if (fromItems.contains(item)) {
				// Remove it from category
				model.removeVocabItem(fromCat, item);
				// Add it to the other
				if (!toItems.contains(item)) {
					model.addVocabItem(toCat, item);
				}
				else {
					setStatusText("Category " + toCat
							+ " already already has that vocabulary.", 4000);
				}
				// Update the GUI
				updateVocabularyList(currentCat);
				logger.log(Level.INFO, "Moving vocab from {0} to {1}: {2}",
						new Object[]{fromCat, toCat, item.toJSONString()});
				vocabIo.setFileModified(true);
				return true;
			}
			else {
				logger.log(Level.WARNING,
						"Cannot move vocabulary. Category {0} does not have vocabulary {1}",
						new Object[]{fromCat, item.getEnglish()});
			}
		}
		return false;
	}

	/**
	 * Handles the VocabMove event with given action command string.
	 *
	 * @param chosenCat
	 * @return
	 */
	private boolean moveVocabulary(String chosenCat) {
		int[] sels;
		// Show popup menu
		if (chosenCat.equals(EditorActions.VocabMove.name())) {
			// Make sure at least one vocabulary is selected
			sels = vocabEditor.listSelectorVocabulary.getList().getSelectedIndices();
			if (sels.length == 0) {
				return true;
			}
			// Show popup menu with category options
			setupCategoryPopup(vocabEditor.jPopupMenuCategories, true);
			vocabEditor.jPopupMenuCategories.show(vocabEditor.jButtonVocabMove, 0, 0);
		}
		else {
			// Carry out menu action
			// @todo Trim prefix
			chosenCat = chosenCat.replaceAll("VocabMove_", "");
			sels = vocabEditor.listSelectorVocabulary.getList().getSelectedIndices();
			// Move each one individually
			for (int a = sels.length - 1; a >= 0; a--) {
				VocabItem vocab = vocabulary.get(sels[a]);
				moveVocabItem(vocab, currentCat, chosenCat);
			}
			updateVocabularyList(currentCat);
		}
		return false;
	}

	/**
	 * Handles the open file event, asking the user for a file and setting the
	 * VocabModel appropriately based on a successful open.
	 */
	private void openFile() {
		try {
			model = vocabIo.openFile(model);
			openFilePost();
		}
		catch (Exception ex) {
			setStatusText(ex.getLocalizedMessage(), 3000);
		}
	}

	/**
	 * Runs the necessary steps after a new file has been opened.
	 */
	private void openFilePost() {
		if (model != null) {
			clearGUI();
			updateCategoryLessonList();
			vocabEditor.listSelectorCategoryLesson.getSelector().setEnabled(true);
			valueChanged(new ListSelectionEvent(vocabEditor.listSelectorVocabulary.getList(), 0, 0, false));
			vocabEditor.setTitle(APP_TITLE + " - " + vocabIo.getFileOpened().getAbsolutePath());
			String status = String.format(
					"Opened %d categories and %d vocabulary from %s",
					model.getCategoryCount(),
					model.getVocabCount(),
					vocabIo.getFileOpened().getName());
			setStatusText(status, 5000);
		}
	}

	/**
	 * Opens a recent file based on an ActionEvent that originates from a
	 * JMenuItem component, which will have the file path.
	 *
	 * @param ae the ActionEvent that triggered the open action
	 * @return true if opened the recent file, false otherwise
	 */
	private boolean openRecentFile(ActionEvent ae) {
		// Open the recent file clicked on
		Object obj = ae.getSource();
		if (!(obj instanceof JMenuItem)) {
			return false;
		}
		// Check if file exist and is readable
		JMenuItem menuItem = (JMenuItem) ae.getSource();
		File recentFile = new File(menuItem.getText());
		if (!recentFile.exists()) {
			// notify user of bad recent file
			logger.log(Level.WARNING, "Recent file does not exist: ''{0}''",
					recentFile.getAbsolutePath());
			JOptionPane.showMessageDialog(vocabEditor,
					"The file does not appear to exist",
					"File does not exist",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (!recentFile.canRead()) {
			// notify user it cannot be opened due to read permission
			logger.log(Level.WARNING, "Recent file missing read attribute: ''{0}''",
					recentFile.getAbsolutePath());
			JOptionPane.showMessageDialog(vocabEditor,
					"The file is missing read permissions",
					"Cannot read file",
					JOptionPane.ERROR_MESSAGE);
		}
		if (!recentFile.canWrite()) {
			// notify user it's read-only
			logger.log(Level.WARNING, "Recent file cannot be written to: ''{0}''",
					recentFile.getAbsolutePath());
			int resp = JOptionPane.showConfirmDialog(vocabEditor,
					"The file is read-only. You will not be able to save.",
					"Read-only file",
					JOptionPane.INFORMATION_MESSAGE);
			// If they answer no, bail. Don't try to open file
			if (JOptionPane.NO_OPTION == resp) {
				logger.log(Level.INFO, "User opted to not open read-only file");
				return false;
			}
		}
		// Open and try to load file
		try {
			model = vocabIo.openFile(recentFile, model);
			if (model == null) {
				// Something went wrong
				logger.log(Level.INFO, "Opening recent file gave a null VocabModel");
				return false;
			}
			openFilePost();
			logger.log(Level.INFO, "Opened recent file");
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Problem while opening recent file {0}: {1}",
					new Object[]{recentFile.getAbsolutePath(), ex.getLocalizedMessage()});
			return false;
		}
		return true;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent pce) {
		/**
		 * TODO catch the different properties and update GUI Need to adjust
		 * height of text fields as size changes.
		 */
		String key = pce.getKey();
		logger.log(Level.INFO, "Pref Change: {0}, value = {1}", new Object[]{key, pce.getNewValue()});
		Font f;

		if (FieldName.FONT_LIST_SORTMODE.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_LIST_SORTMODE);
			fontMap.put(FieldName.FONT_LIST_SORTMODE, f);
			vocabEditor.listSelectorCategoryLesson.getList().setFont(f);
		}
		// The vocab list
		// TODO only change list font if it is showing right mode
		else if (FieldName.FONT_LIST_ENGLISH.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_LIST_ENGLISH);
			fontMap.put(FieldName.FONT_LIST_ENGLISH, f);
			vocabEditor.listSelectorVocabulary.getList().setFont(f);
		}
		else if (FieldName.FONT_LIST_ROMAJI.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_LIST_ROMAJI);
			fontMap.put(FieldName.FONT_LIST_ROMAJI, f);
			vocabEditor.listSelectorVocabulary.getList().setFont(f);
		}
		else if (FieldName.FONT_LIST_KANA.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_LIST_KANA);
			fontMap.put(FieldName.FONT_LIST_KANA, f);
			vocabEditor.listSelectorVocabulary.getList().setFont(f);
		}
		else if (FieldName.FONT_LIST_KANJI.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_LIST_KANJI);
			fontMap.put(FieldName.FONT_LIST_KANJI, f);
			vocabEditor.listSelectorVocabulary.getList().setFont(f);
		}
		// The editor fields
		else if (FieldName.FONT_EDITOR_ENGLISH.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_EDITOR_ENGLISH);
			fontMap.put(FieldName.FONT_EDITOR_ENGLISH, f);
			vocabEditor.japaneseVocabEditorPanel.jTextFieldEnglish.setFont(f);
		}
		else if (FieldName.FONT_EDITOR_ROMAJI.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_EDITOR_ROMAJI);
			fontMap.put(FieldName.FONT_EDITOR_ROMAJI, f);
			vocabEditor.japaneseVocabEditorPanel.jTextFieldRomaji.setFont(f);
		}
		else if (FieldName.FONT_EDITOR_KANA.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_EDITOR_KANA);
			fontMap.put(FieldName.FONT_EDITOR_KANA, f);
			vocabEditor.japaneseVocabEditorPanel.jTextFieldKana.setFont(f);
		}
		else if (FieldName.FONT_EDITOR_KANJI.getPrefName().equals(key)) {
			f = prefs.getFontPref(FieldName.FONT_EDITOR_KANJI);
			fontMap.put(FieldName.FONT_EDITOR_KANJI, f);
			vocabEditor.japaneseVocabEditorPanel.jTextFieldKanji.setFont(f);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {

		// Listen for JapaneseEditorPanel items
//		logger.log(Level.INFO, "PropChange: {0} = {1} / {2}", new Object[]{
//			pce.getPropertyName(),
//			pce.getOldValue(),
//			pce.getNewValue()
//		});

		// JapaneseVocabEditorPanel specific properties
		switch (pce.getPropertyName()) {
			case JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH:
				String ov = pce.getOldValue().toString().trim();
				String nv = pce.getNewValue().toString().trim();
				boolean renamed = false;
				// Check if trying to use blank value
				if (nv.isEmpty()) {
					// Put old value back in
					vocabEditor.japaneseVocabEditorPanel.setEnglish(ov);
					setStatusText("English values cannot be blank.", 6000);
					return;
				}
				// Check if modifying the category
				if (ov.startsWith("#")) {
					// New value must start with #
					if (!nv.startsWith("#")) {
						nv = "#" + nv;
						vocabEditor.japaneseVocabEditorPanel.setEnglish(nv);
						if (nv.equals(ov)) {
							JOptionPane.showMessageDialog(null, "Category items must start with #.");
							return;
						}
					}
					// Update category spelling
					model.renameCategory(ov.replace("#", ""), nv.replace("#", ""));
					setCurrentCategory(nv.replace("#", ""));
					updateCategoryLessonList();
					vocabEditor.listSelectorCategoryLesson.getList().setSelectedValue(getCurrentCategory(), true);
					renamed = true;
				}
				// If making an item into a category label
				else if (nv.startsWith("#")) {
					// no adding a new one.
					JOptionPane.showMessageDialog(null, "Only category items can start with #.");
					// TODO Remove front # chars
					// Put the value back to the previous one
					vocabEditor.japaneseVocabEditorPanel.setEnglish(ov);
					return;
				}
				// Make sure they aren't changing to an existing item
				if (modelVocabulary.contains(nv) && !renamed) {
					JOptionPane.showMessageDialog(null, "A vocabulary item with that English already exists.");
					// Put the value back to the previous one
					vocabEditor.japaneseVocabEditorPanel.setEnglish(ov);
					return;
				}
				vocabIo.setFileModified(true);
				if (getCurrentVocabIndex() > -1) {
					// TODO check what display mode
					getVocabulary(getCurrentVocabIndex()).setEnglish(nv);
				}
				updateVocabularyList(getCurrentCategory()); // send null could assume current
				vocabEditor.listSelectorVocabulary.getList().setSelectedValue(nv, true);
				break;
			case JapaneseVocabEditorPanel.PROP_MODIFIED_LESSON:
				// lesson was modified
				vocabIo.setFileModified(true);
				break;
			case VocabModelIO.PROP_FILE_CHANGED:
				// Add file to recent file list
				if (pce.getNewValue() != null && pce.getNewValue() instanceof File) {
					prefs.addRecentFile(((File) pce.getNewValue()).getAbsolutePath());
					updateRecentFileMenu();
				}
				break;
			case VocabModelIO.PROP_FILE_MODIFIED_CHANGED:
				// Update application title
				StringBuilder title = new StringBuilder();
				if ((boolean) pce.getNewValue()) {
					title.append("*");
				}
				if (vocabIo.getFileOpened() != null) {
					title.append(vocabIo.getFileOpened().getName())
							.append(" ").append(vocabIo.getFileOpened().getPath());
				}
				else if (vocabIo.isFileModified()) {
					title.append("Untitled");
				}
				setApplicationTitle(title.toString());
				break;
		}
	}

	/**
	 * Read the user preferences and set fields appropriately.
	 */
	private void readPreferences() {
		logger.log(Level.INFO, "Reading in preferences");
		Font font;
		FieldName[] fontPrefs = new FieldName[]{
			FieldName.FONT_LIST_SORTMODE,
			FieldName.FONT_LIST_ENGLISH,
			FieldName.FONT_LIST_ROMAJI,
			FieldName.FONT_LIST_KANA,
			FieldName.FONT_LIST_KANJI,
			FieldName.FONT_EDITOR_ENGLISH,
			FieldName.FONT_EDITOR_ROMAJI,
			FieldName.FONT_EDITOR_KANA,
			FieldName.FONT_EDITOR_KANJI
		};
		for (FieldName field : fontPrefs) {
			font = prefs.getFontPref(field);
			fontMap.put(field, font);
			logger.log(Level.INFO, "Pref {2} = {0} {1}", new Object[]{
				font.getFamily(), font.getSize(), field.name()});
		}
		vocabEditor.listSelectorCategoryLesson.getList().setFont(
				fontMap.get(FieldName.FONT_LIST_SORTMODE));

		// English is the default selection so font will start there
		vocabEditor.listSelectorVocabulary.getList().setFont(
				fontMap.get(FieldName.FONT_LIST_ENGLISH));

		// Ensure vocab editor panel fields are appropriately sized
		vocabEditor.japaneseVocabEditorPanel.jTextFieldEnglish.setFont(
				fontMap.get(FieldName.FONT_EDITOR_ENGLISH));
		vocabEditor.japaneseVocabEditorPanel.jTextFieldRomaji.setFont(
				fontMap.get(FieldName.FONT_EDITOR_ROMAJI));
		vocabEditor.japaneseVocabEditorPanel.jTextFieldKana.setFont(
				fontMap.get(FieldName.FONT_EDITOR_KANA));
		vocabEditor.japaneseVocabEditorPanel.jTextFieldKanji.setFont(
				fontMap.get(FieldName.FONT_EDITOR_KANJI));

		// TODO set window position and size. Test
		Dimension winSize = prefs.getWindowSize();
		logger.log(Level.INFO, "Window Size: {0}, {1}", new Object[]{winSize.width, winSize.height});
		vocabEditor.setPreferredSize(winSize);
		vocabEditor.setSize(winSize);
		Point winPos = prefs.getWindowPosition();
		logger.log(Level.INFO, "Window Position: {0}, {1}", new Object[]{winPos.x, winPos.y});
		vocabEditor.setLocation(winPos);

		updateRecentFileMenu();
	}

	/**
	 * Removes selected vocabulary from the list asking confirmation from the
	 * user if desired.
	 *
	 * @param confirm
	 * @return
	 */
	public boolean removeVocabItem(boolean confirm) {
		int[] sels = vocabEditor.listSelectorVocabulary.getList().getSelectedIndices();
		if (sels.length > 0) {
			if (confirm) {
				StringBuilder msg = new StringBuilder();
				msg.append("Are you sure you want to delete ");
				if (sels.length == 1) {
					msg.append("this items?");
				}
				else {
					msg.append("these ").append(sels.length).append(" items?");
				}
				int resp = JOptionPane.showConfirmDialog(null, msg.toString());
				if (JOptionPane.YES_OPTION != resp) {
					return false;
				}
			}
			// Remove from end to start the selected items
			for (int a = sels.length - 1; a >= 0; a--) {
				String currItem = vocabEditor.listSelectorVocabulary.getList().getModel().getElementAt(sels[a]).toString();
				if (currItem.startsWith("#")) {
					// No removing category label item
					JOptionPane.showMessageDialog(null, "Cannot delete category label item");
					continue;
				}
				else {
					String itemString = getVocabulary(sels[a]).toJSONString();
					if (model.removeVocabItem(currentCat, vocabulary.get(sels[a]))) {
						logger.log(Level.INFO, "Removing vocabulary: {0}", itemString);
						vocabIo.setFileModified(true);
					}
				}
			}
			updateVocabularyList(currentCat);
			setStatusText("Removed " + sels.length + " items", 3000);
			return true;
		}
		return false;
	}

	/**
	 * Saves current preferences
	 */
	private void savePreferences() {
		logger.log(Level.INFO, "Saving preferences");
		// Save window size and position
		prefs.saveWindowPosition(vocabEditor.getLocationOnScreen());
		prefs.saveWindowSize(vocabEditor.getSize());
	}

	/**
	 * Sets the application title bar string to the app name plus the given
	 * name.
	 *
	 * @param name
	 */
	public void setApplicationTitle(String name) {
		vocabEditor.setTitle(APP_TITLE + " - " + name);
	}

	/**
	 * Sets the current selected category.
	 *
	 * @param cat The new selected category.
	 */
	public void setCurrentCategory(String cat) {
		if (cat != null && !cat.equals(currentCat)) {
			currentCat = cat;
			// fire current cat change?
		}
		else {
			currentCat = cat;
		}
	}

	/**
	 * Sets the currently selected lesson value.
	 *
	 * @param les
	 */
	public void setCurrentLesson(int les) {
		if (les != currentLesson) {
			currentLesson = les;
		}
	}

	/**
	 * Sets the currently selected vocabulary index.
	 *
	 * @param idx
	 */
	public void setCurrentVocabIndex(int idx) {
		if (idx != currentVocabIdx) {
			currentVocabIdx = idx;
		}
	}

	/**
	 * Sets the GUI in an enabled/disabled state based on whether a file is open
	 * or not so controls can't be used when not appropriate.
	 *
	 * TODO if I can establish a way to modify a category while in lesson sort
	 * mode then I can remove the boolean sortedByCategory.
	 *
	 * @param enable
	 */
	public void setGUIEnabled(boolean enable) {
		enable = enable && vocabIo.isFileOpen();

		// Add/remove buttons only valid for category mode
		vocabEditor.listSelectorCategoryLesson.getSelector().setEnabled(enable);
		vocabEditor.listSelectorVocabulary.getSelector().setEnabled(enable);
		vocabEditor.japaneseVocabEditorPanel.setEnabled(enable /*&& vocabSelected*/);
		// Buttons
		vocabEditor.jButtonCategoryAdd.setEnabled(enable);
		vocabEditor.jButtonCategoryDelete.setEnabled(enable);
		vocabEditor.jButtonVocabAdd.setEnabled(enable);
		vocabEditor.jButtonVocabDelete.setEnabled(enable);
		vocabEditor.jButtonVocabMove.setEnabled(enable);
		// Menu items
		vocabEditor.jMenuItemCatAdd.setEnabled(enable);
		vocabEditor.jMenuItemCatDelete.setEnabled(enable);
		vocabEditor.jMenuItemVocabAdd.setEnabled(enable);
		vocabEditor.jMenuItemVocabDelete.setEnabled(enable);
		vocabEditor.jMenuItemVocabMove.setEnabled(enable);
	}

	/**
	 * Sets the text of the status bar.
	 *
	 * @param status
	 */
	public void setStatusText(String status) {
		timedStatus = false;
		vocabEditor.jLabelStatus.setText(status);
	}

	/**
	 * Sets the status text for a given delay.
	 *
	 * @param status
	 * @param ms
	 */
	public void setStatusText(String status, int ms) {
		setStatusText(status);
		setTimedStatus(true);
		// Clear status after ms milliseconds
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Only clear it if another status has not been set
				if (isTimedStatus()) {
					setStatusText(" ");
				}
			}
		}, ms);
	}

	/**
	 * Sets whether or not going to use a timed status message.
	 *
	 * @param status
	 */
	public void setTimedStatus(boolean status) {
		if (status != timedStatus) {
			timedStatus = status;
		}
	}

	/**
	 * Configures and sets up the JPopupMenu used to show the user a list of
	 * categories to be picked from.
	 *
	 * @param menu
	 * @param disableCurrent
	 */
	public void setupCategoryPopup(JComponent menu, boolean disableCurrent) {
		// Only allowing popup menu and menu
		if (!(menu instanceof JPopupMenu) && !(menu instanceof JMenu)) {
			return;
		}
		menu.removeAll();
		List<String> cats = model.getCategories();
		for (String cat : cats) {
			JMenuItem item = new JMenuItem(cat);
			item.setActionCommand("VocabMove_" + cat);
			// Disable current category so it can't be chosen
			if (cat.equals(getCurrentCategory()) && disableCurrent) {
				item.setEnabled(false);
			}
			// Set the action
			item.addActionListener(this);
			menu.add(item);
		}
	}

	/**
	 * Sets the GUI controls related to whether a vocabulary item is currently
	 * selected or not.
	 *
	 * @param enable
	 */
	private void setVocabControlStates(boolean enable) {
		vocabEditor.listSelectorVocabulary.getSelector().setEnabled(enable);
		// Buttons
		vocabEditor.jButtonVocabAdd.setEnabled(enable);
		vocabEditor.jButtonVocabDelete.setEnabled(enable);
		vocabEditor.jButtonVocabMove.setEnabled(enable);
		// Menu items
		vocabEditor.jMenuItemVocabAdd.setEnabled(enable);
		vocabEditor.jMenuItemVocabDelete.setEnabled(enable);
		vocabEditor.jMenuItemVocabMove.setEnabled(enable);
		// JList
		vocabEditor.listSelectorVocabulary.getList().setEnabled(enable);
		// Enable editor panel as appropriate
		vocabEditor.japaneseVocabEditorPanel.setEnabled(enable);
		if (vocabEditor.listSelectorVocabulary.getList().getSelectedIndex() < 0) {
			vocabEditor.japaneseVocabEditorPanel.clearPanel();
		}
	}

	/**
	 * Handle JSpinner changes.
	 *
	 * @param ce
	 */
	@Override
	public void stateChanged(ChangeEvent ce) {
		// TODO
		// Get currently selected VocabItem, update its lessons value.
	}

	/**
	 * Updates the GUI, populating the category list from the model.
	 */
	public void updateCategoryLessonList() {
		if (model == null) {
			return;
		}
		SortMode pick = getSortMode();
		// Update the category list
		List<String> cats = model.getCategories();
		Object selectedItem = vocabEditor.listSelectorCategoryLesson.getList().getSelectedValue();
		modelCategories.clear();
		Collections.sort(cats);
		for (String cat : cats) {
			if (!modelCategories.contains(cat)) {
				modelCategories.addElement(cat);
			}
		}
		if (selectedItem != null && SortMode.Categories == pick) {
			vocabEditor.listSelectorCategoryLesson.getList().setSelectedValue(selectedItem, true);
		}

		// Update the lesson list
		List<Integer> lessons = model.getLessons();
		Collections.sort(lessons);
		selectedItem = vocabEditor.listSelectorCategoryLesson.getList().getSelectedValue();
		modelLessons.clear();
		for (Integer lesson : lessons) {
			if (!modelLessons.contains(lessons)) {
				modelLessons.addElement(lesson);
			}
		}
		if (selectedItem != null && SortMode.Lessons == pick) {
			vocabEditor.listSelectorCategoryLesson.getList().setSelectedValue(selectedItem, true);
		}
	}

	/**
	 * Updates the recent file menu with the known recent files.
	 */
	private void updateRecentFileMenu() {
		// TODO test
		ArrayList<String> filePaths = prefs.getRecentFiles();
		// Clear menu to ensure we won't have duplicates
		vocabEditor.jMenuRecentFiles.removeAll();
		for (String path : filePaths) {
			JMenuItem item = new JMenuItem(path);
			// Add action listener
			item.addActionListener(this);
			item.setActionCommand(EditorActions.FileRecent.toString());
			vocabEditor.jMenuRecentFiles.add(item);
		}
	}

	/**
	 * Updates the vocabulary listing based on the currently set sort mode.
	 */
	public void updateVocabulary() {
		// Are we in category or lesson mode?
		SortMode mode = getSortMode();
		switch (mode) {
			case Categories:
				updateVocabularyList(currentCat);
				break;
			case Lessons:
				updateVocabularyList(currentLesson);
				break;
		}
	}

	/**
	 * Sets the vocabulary list with vocabulary from the given category. If the
	 * category is null or does not exist, the list will not be updated.
	 *
	 * @param category
	 */
	public void updateVocabularyList(String category) {
		// If no model or category, clear out vocabulary list
		if (model == null || category == null) {
			modelVocabulary.clear();
			return;
		}
		ArrayList<VocabItem> items = model.getVocabItems(category);
		if (items == null) {
			return;
		}
		// Maintain selection
		Object selectedItem = vocabEditor.listSelectorVocabulary.getList().getSelectedValue();
		// TODO get selected indices
		int selectedIndex = vocabEditor.listSelectorVocabulary.getList().getSelectedIndex();
		vocabulary = items;
		modelVocabulary.clear();
		// Sort vocabulary
		// TODO should I sort differently depending on display mode?
		Collections.sort(vocabulary, new EnglishComparator());
		// Populate list with the English words
		VocabDisplayMode mode = ((VocabDisplayMode) vocabEditor.listSelectorVocabulary.getSelector().getSelectedItem());
		for (VocabItem item : vocabulary) {
			// Display value according to preference
			switch (mode) {
				case English:
					modelVocabulary.addElement(item.getEnglish());
					break;
				case Romaji:
					String romaji = item.getRomaji();
					if (romaji.isEmpty()) {
						romaji = "<" + item.getEnglish() + ">";
					}
					modelVocabulary.addElement(romaji);
					break;
				case Kana:
					String kana = item.getKana();
					// Substitue missing kana
					if (kana.isEmpty()) {
						kana = "<" + item.getEnglish() + ">";
					}
					modelVocabulary.addElement(kana);
					break;
				case Kanji:
					String kanji = item.getKanji();
					// Don't want to show empty field for missing kanji
					if (kanji.isEmpty()) {
						kanji = "<" + item.getEnglish() + ">";
					}
					modelVocabulary.addElement(kanji);
					break;
			}
		}
		if (selectedItem != null) {
			vocabEditor.listSelectorVocabulary.getList().setSelectedValue(selectedItem, true);
			// Check if selection was successful
			if (!selectedItem.equals(vocabEditor.listSelectorVocabulary.getList().getSelectedValue())) {
				// If not, try using index
				// This is the case either when the item has been removed,
				// renamed(?), or switching between display modes.
				vocabEditor.listSelectorVocabulary.getList().setSelectedIndex(selectedIndex);
			}
		}
		// If resulting update leaves no selected category, then disable vocabulary controls.
		if (vocabEditor.listSelectorCategoryLesson.getList().isSelectionEmpty()) {
			setVocabControlStates(false);
		}
	}

	/**
	 * Sets the vocabulary list with vocabulary from the given lesson. If the
	 * lesson is less than 0, the list will not be updated.
	 *
	 * @param lesson
	 */
	public void updateVocabularyList(int lesson) {
		if (model == null || lesson < 0) {
			return;
		}
		final JList list = vocabEditor.listSelectorCategoryLesson.getList();
		// Try to maintain selection
		Object selectedItem = list.getSelectedValue();
		modelVocabulary.clear();
		vocabulary = model.getVocabItems(lesson);
		for (VocabItem item : vocabulary) {
			modelVocabulary.addElement(item.getEnglish());
		}
		if (selectedItem != null) {
			list.setSelectedValue(selectedItem, true);
		}
		// If resulting update leaves no selected lesson, then disable vocabulary controls.
		if (list.isSelectionEmpty()) {
			setVocabControlStates(false);
		}
	}

	/**
	 * Handle list selection changes.
	 *
	 * @param lse
	 */
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		JList src = (JList) evt.getSource();
		if (src == null) {
			logger.log(Level.FINEST, "Null source on value changed event.");
			return;
		}

		// Category/Lesson change
		if (src.equals(vocabEditor.listSelectorCategoryLesson.getList())) {
			SortMode mode = getSortMode();
			switch (mode) {
				case Categories:
					if (src.getSelectedValue() == null) {
						setVocabControlStates(false);
					}
					else if (!evt.getValueIsAdjusting()) {
						// Switch between sortedByCategory categories or lessons
						currentCat = src.getSelectedValue().toString();
						updateVocabularyList(currentCat);
						if (model == null || currentCat == null) {
							setVocabControlStates(false);
						}
						else {
							setVocabControlStates(true);
						}
					}
					break;
				case Lessons:
					if (!evt.getValueIsAdjusting()) {
						Object selObject = src.getSelectedValue();
						if (selObject == null) {
							currentLesson = -1;
							setVocabControlStates(false);
							return;
						}
						currentLesson = Integer.parseInt(selObject.toString(), 10);
						// Show vocabulary from given lesson
						updateVocabularyList(currentLesson);
						setVocabControlStates(true);
					}
					break;
			}
		}
		// Vocabulary item selection changed
		else if (src.equals(vocabEditor.listSelectorVocabulary.getList())) {
			// vocab selection
			if (evt.getValueIsAdjusting()) {
				return;
			}
			int[] sel = src.getSelectedIndices();
			if (sel.length > 0) {
				setCurrentVocabIndex(sel[0]);
				if (getCurrentVocabIndex() > -1) {
					vocabEditor.japaneseVocabEditorPanel.setVocabItem(vocabulary.get(getCurrentVocabIndex()));
				}
				else {
					vocabEditor.japaneseVocabEditorPanel.clearPanel();
				}
			}
			// Editor only enabled when one vocabulary is selected
			vocabEditor.japaneseVocabEditorPanel.setEnabled(sel.length == 1);
		}

	}
}
