/*
 *  JapaneseVocabEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.KeyStroke;
import psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorController.EditorActions;
import psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorController.EditorPrefs;
import psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorController.SortMode;
import psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorController.VocabDisplayMode;

/**
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabEditor extends javax.swing.JFrame {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(JapaneseVocabEditor.class.getCanonicalName());
	/**
	 * The level to log at
	 */
	public static Level logLevel = Level.INFO;
	/**
	 * Preference object.
	 */
	private static final Preferences prefs = Preferences.userNodeForPackage(JapaneseVocabEditor.class);
	/**
	 * The main title of the application.
	 */
	public static final String APP_TITLE = "Japanese Vocabulary Editor";
	/**
	 * The ActionListener tied to each of the MenuItem on the JPopupMenu used to
	 * show the categories for the user to choose.
	 */
//	private ActionListener categoryPopupListener = null;
	/**
	 * A container for all of the current set of vocabulary.
	 */
//	private ArrayList<VocabItem> vocabulary = new ArrayList<VocabItem>();
	/**
	 * Sets whether or not we're using a timed status.
	 */
//	private static boolean timedStatus = false;
	/**
	 * Whether the currently selected vocabulary item is modified.
	 */
//	private boolean fileModified = false;
//	/**
//	 * The ListModel for the category JList.
//	 */
//	public DefaultListModel modelCategories = null;
//	/**
//	 * The ListModel for the lessons JList.
//	 */
//	public DefaultListModel modelLessons = null;
//	/**
//	 * The ListModel for the vocabulary JList.
//	 */
//	public DefaultListModel modelVocabulary = null;
	/**
	 * The currently open fileOpened.
	 */
//	private File fileOpened = null;
	/**
	 * The current lesson selected
	 */
//	private int currentLesson = -1;
	/**
	 * Index of selected vocabulary.
	 */
//	private int selectedVocabIndex = -1;
	/**
	 * The MVC controller for the GUI.
	 */
	private JapaneseVocabEditorController controller;
	/**
	 * The current category chosen.
	 */
//	private String currentCategory;
	/**
	 * The vocabulary model holding categories and vocabulary.
	 */
	private VocabModel model = null;

	/**
	 * Creates new form JapaneseVocabEditor
	 */
	public JapaneseVocabEditor() {
		controller = new JapaneseVocabEditorController(this);
		initComponents();
		addListeners();
		controller.initComponents();
		// Capture models
//		modelCategories = (DefaultListModel) jListCategories.getModel();
//		modelLessons = (DefaultListModel) jListLessons.getModel();
//		modelVocabulary = (DefaultListModel) jListVocabulary.getModel();
		japaneseVocabEditorPanel.addPropertyChangeListener(controller);
		controller.clearGUI();
		controller.setGUIEnabled(false);
//		categoryPopupListener = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent ae) {
//				String chosenCat = ae.getActionCommand();
//				int[] sels = jListVocabulary.getSelectedIndices();
//				// Move each one individually
//				for (int a = sels.length - 1; a >= 0; a--) {
//					VocabItem vocab = vocabulary.get(sels[a]);
//					moveVocabItem(vocab, currentCategory, chosenCat);
//				}
//				updateVocabularyList(currentCategory);
//			}
//		};
	}

	/**
	 * Adds a new category to the list if it doesn't already exist.
	 *
	 * @param cat
	 * @return
	 */
//	private boolean addCategory(String cat) {
//		if (model != null && !modelCategories.contains(cat)) {
//			model.addCategory(cat);
//			logger.log(Level.INFO, "Adding new category: {0}", cat);
//			// Make sure it's sorted after add
//			updateCategoryLessonList();
//			// Highlight the new category
//			jListCategories.setSelectedValue(cat, true);
//			fileModified = true;
//			return true;
//		}
//		return false;
//	}

	/**
	 * Add all of the component listeners to the controller.
	 */
	private void addListeners() {
		// JList change listeners
		jListCategories.addListSelectionListener(controller);
		jListLessons.addListSelectionListener(controller);
		jListVocabulary.addListSelectionListener(controller);
		// JButton action performed listeners
		jButtonCategoryAdd.addActionListener(controller);
		jButtonCategoryDelete.addActionListener(controller);
		jButtonVocabAdd.addActionListener(controller);
		jButtonVocabDelete.addActionListener(controller);
		jButtonVocabMove.addActionListener(controller);
		// JMenuItem File action listeners
		jMenuItemNew.addActionListener(controller);
		jMenuItemOpen.addActionListener(controller);
		jMenuItemSave.addActionListener(controller);
		jMenuItemSaveAs.addActionListener(controller);
		jMenuItemClose.addActionListener(controller);
		jMenuItemExit.addActionListener(controller);
		// JMenuItem Vocabulary action listeners
		jMenuItemCatAdd.addActionListener(controller);
		jMenuItemCatDelete.addActionListener(controller);
		jMenuItemVocabAdd.addActionListener(controller);
		jMenuItemVocabDelete.addActionListener(controller);
		jMenuItemVocabMove.addActionListener(controller);
		// JComboBox listeners
		jComboBoxSortMode.addItemListener(controller);
		jComboBoxSortMode.addActionListener(controller);
		jComboBoxVocabDisplay.addItemListener(controller);
		jComboBoxVocabDisplay.addActionListener(controller);
		// Preferences change listener ???
	}

//	/**
//	 * Adds a new vocabulary item by prompting for an English word, which will
//	 * then be used to populate the romaji list as long as the new item does not
//	 * start with a # or already exists.
//	 */
//	private void addVocabulary() {
//		String resp = JOptionPane.showInputDialog(this, "English entry:",
//				"Add new vocabulary", JOptionPane.QUESTION_MESSAGE);
//		if (resp != null) {
//			if (modelVocabulary.contains(resp)) {
//				JOptionPane.showMessageDialog(this, "That vocabulary item already exists.");
//				return;
//			}
//			else if (resp.startsWith("#")) {
//				JOptionPane.showMessageDialog(this, "Only the category label can start with #.");
//				return;
//			}
//			if (addVocabulary(new VocabItem(resp, "", "", ""))) {
//				jListVocabulary.setSelectedValue(resp, true);
//				logger.log(Level.INFO, "Adding new vocabulary: {0}", resp);
//			}
//			else {
//				setStatusText("Failed to add vocab " + resp, 4000);
//			}
//		}
//	}

//	/**
//	 * Adds a new {@link VocabItem} to currently selected category.
//	 *
//	 * @param item
//	 * @return
//	 */
//	private boolean addVocabulary(VocabItem item) {
//		if (model != null && !vocabulary.contains(item)) {
//			if (model.addVocabItem(currentCategory, item)) {
//				updateVocabularyList(currentCategory);
//				fileModified = true;
//				logger.log(Level.INFO, "Adding new vocabulary: {0}", item.toJSONString());
//				return true;
//			}
//		}
//		return false;
//	}

//	/**
//	 * Clears the GUI components to be ready for new data.
//	 */
//	private void clearGUI() {
//		// Clear all of the JList
//		controller.modelCategories.clear();
//		controller.modelLessons.clear();
//		controller.modelVocabulary.clear();
//		japaneseVocabEditorPanel.clearPanel();
//		controller.setStatusText(" ");
//		controller.setCurrentCategory(null);
////		selectedVocabIndex = -1;
//		controller.setCurrentVocabIndex(-1);
//	}

//	/**
//	 * Clears the GUI and resets the open file object.
//	 */
//	private void closeFile() {
//		clearGUI();
//		fileOpened = null;
//		model = null;
//		setTitle(APP_TITLE);
//		controller.setFileModified(false);
//	}

//	/**
//	 * Creates a new file by creating a new model and updating the GUI.
//	 *
//	 * @return
//	 */
//	private boolean createNewFile() {
//		// Check if any currently open file is modified
//		// TODO
//		// Close off any existing file
//		closeFile();
//		// Create a new model
//		model = new VocabModel();
//		clearGUI();
//		setGUIEnabled(true);
//		japaneseVocabEditorPanel.setEnabled(false);
//		return false;
//	}

	// unused?
//	public JTextField getEditorEnglish() {
//		return japaneseVocabEditorPanel.jTextFieldEnglish;
//	}

//	/**
//	 * Returns the currently selected SortMode.
//	 *
//	 * @return
//	 */
//	private SortMode getSortMode() {
//		return ((SortMode) jComboBoxSortMode.getSelectedItem());
//	}

	/**
	 * Load user preferences.
	 */
	private void loadPreferences() {
		try {
			jComboBoxSortMode.setSelectedIndex(prefs.getInt(EditorPrefs.SortMode.toString(), 0));
			jComboBoxVocabDisplay.setSelectedIndex(prefs.getInt(EditorPrefs.VocabDisplayMode.toString(), 0));
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Problem loading preference: {0}", ex.getLocalizedMessage());
		}
	}

//	/**
//	 * Move a VocabItem from one category to another.
//	 *
//	 * @param item
//	 * @param fromCat
//	 * @param toCat
//	 * @return
//	 */
//	private boolean moveVocabItem(VocabItem item, String fromCat, String toCat) {
//		if (model == null || item == null) {
//			return false;
//		}
//		// Check if both categories exist
//		if (model.hasCategory(fromCat) && model.hasCategory(toCat)) {
//			// Check if vocab exists in from category
//			ArrayList<VocabItem> fromItems = model.getVocabItems(fromCat);
//			ArrayList<VocabItem> toItems = model.getVocabItems(toCat);
//			if (fromItems.contains(item)) {
//				// Remove it from category
//				model.removeVocabItem(fromCat, item);
//				// Add it to the other
//				if (!toItems.contains(item)) {
//					model.addVocabItem(toCat, item);
//				}
//				else {
//					setStatusText("Category " + toCat
//							+ " already already has that vocabulary.", 4000);
//				}
//				// Update the GUI
//				updateVocabularyList(currentCategory);
//				logger.log(Level.INFO, "Moving vocab from {0} to {1}: {2}",
//						new Object[]{fromCat, toCat, item.toJSONString()});
//				controller.setFileModified(true);
//				return true;
//			}
//			else {
//				logger.log(Level.WARNING,
//						"Cannot move vocabulary. Category {0} does not have vocabulary {1}",
//						new Object[]{fromCat, item.getEnglish()});
//			}
//		}
//		return false;
//	}

//	/**
//	 * Opens a given {@link File} and creates a {@link VocabModel} from it that
//	 * is used to load in the categories.
//	 *
//	 * @param fileOpened
//	 * @return
//	 */
//	private boolean openFile(File file) {
//		if (file == null || !file.canRead()) {
//			return false;
//		}
//		// Check if current file is modified
//		if (controller.isFileModified()) {
//			// TODO
//		}
//		try {
//			fileOpened = file;
//			model = JsonVocabIO.readJsonFile(fileOpened);
//			clearGUI();
//			controller.updateCategoryLessonList();
//			setGUIEnabled(true);
//			jListVocabularyValueChanged(new ListSelectionEvent(this, 0, 0, false));
//			setTitle(APP_TITLE + " - " + fileOpened.getAbsolutePath());
//			String status = String.format(
//					"Opened %d categories and %d vocabulary from %s",
//					model.getCategoryCount(),
//					model.getVocabCount(),
//					fileOpened.getName());
//			setStatusText(status, 5000);
//			controller.setFileModified(false);
//			return true;
//		}
//		catch (FileNotFoundException ex) {
//			logger.log(Level.SEVERE, null, ex);
//			setStatusText("File not found", 3000);
//		}
//		catch (IOException ex) {
//			logger.log(Level.SEVERE, null, ex);
//			setStatusText("Could not read file", 3000);
//		}
//		catch (ParseException ex) {
//			logger.log(Level.SEVERE, null, ex);
//			setStatusText("Could not parse file", 4000);
//		}
//		return false;
//	}

	/**
	 * Handle when changes are made to the vocabulary editor. Need to capture
	 * changes and update the JList appropriately based on what changed.
	 *
	 * @param pce
	 */
//	@Override
//	public void propertyChange(PropertyChangeEvent pce) {
//		if (JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH.equals(pce.getPropertyName())) {
//			String ov = pce.getOldValue().toString().trim();
//			String nv = pce.getNewValue().toString().trim();
//			boolean renamed = false;
//
//			// Check if trying to use blank value
//			if (nv.isEmpty()) {
//				// Put old value back in
//				japaneseVocabEditorPanel.setEnglish(ov);
//				controller.setStatusText("English values cannot be blank.", 6000);
//				return;
//			}
//
//			// Check if modifying the category
//			if (ov.startsWith("#")) {
//				// New value must start with #
//				if (!nv.startsWith("#")) {
//					nv = "#" + nv;
//					japaneseVocabEditorPanel.setEnglish(nv);
//					if (nv.equals(ov)) {
//						JOptionPane.showMessageDialog(this, "Category items must start with #.");
//						return;
//					}
//				}
//				// Update category spelling
//				model.renameCategory(ov.replace("#", ""), nv.replace("#", ""));
////				currentCategory = nv.replace("#", "");
//				controller.setCurrentCategory(nv.replace("#", ""));
//				controller.updateCategoryLessonList();
////				jListCategories.setSelectedValue(currentCategory, true);
//				jListCategories.setSelectedValue(controller.getCurrentCategory(), true);
//				renamed = true;
//			}
//			// If making an item into a category label
//			else if (nv.startsWith("#")) {
//				// no adding a new one.
//				JOptionPane.showMessageDialog(this, "Only category items can start with #.");
//				// TODO Remove front # chars
//				// Put the value back to the previous one
//				japaneseVocabEditorPanel.setEnglish(ov);
//				return;
//			}
//
//			// Make sure they aren't changing to an existing item
//			if (controller.modelVocabulary.contains(nv) && !renamed) {
//				JOptionPane.showMessageDialog(this, "A vocabulary item with that English already exists.");
//				// Put the value back to the previous one
//				japaneseVocabEditorPanel.setEnglish(ov);
//				return;
//			}
//
//			controller.setFileModified(true);
//
//			if (controller.getCurrentVocabIndex() > -1) {
//				// TODO check what display mode
////				vocabulary.get(selectedVocabIndex).setEnglish(nv);
//				controller.getVocabulary(controller.getCurrentVocabIndex()).setEnglish(nv);
//			}
//			controller.updateVocabularyList(controller.getCurrentCategory()); // send null could assume current
//			jListVocabulary.setSelectedValue(nv, true);
//		}
//		else if (JapaneseVocabEditorPanel.PROP_MODIFIED_LESSON.equals(pce.getPropertyName())) {
//			// lesson was modified
//			controller.setFileModified(true);
//		}
//	}

//	/**
//	 * Removes selected vocabulary from the list asking confirmation from the
//	 * user if desired.
//	 *
//	 * @param confirm
//	 * @return
//	 */
//	public boolean removeVocabItem(boolean confirm) {
//		int[] sels = jListVocabulary.getSelectedIndices();
//		if (sels.length > 0) {
//			if (confirm) {
//				StringBuilder msg = new StringBuilder();
//				msg.append("Are you sure you want to delete ");
//				if (sels.length == 1) {
//					msg.append("this items?");
//				}
//				else {
//					msg.append("these ").append(sels.length).append(" items?");
//				}
//				int resp = JOptionPane.showConfirmDialog(this, msg.toString());
//				if (JOptionPane.YES_OPTION != resp) {
//					return false;
//				}
//			}
//			// Remove from end to start the selected items
//			for (int a = sels.length - 1; a >= 0; a--) {
//				String currItem = jListVocabulary.getModel().getElementAt(sels[a]).toString();
//				if (currItem.startsWith("#")) {
//					// No removing category label item
//					JOptionPane.showMessageDialog(this, "Cannot delete category label item");
//					continue;
//				}
//				else {
////					String itemString = vocabulary.get(sels[a]).toJSONString();
//					String itemString = controller.getVocabulary(sels[a]).toJSONString();
//					if (model.removeVocabItem(currentCategory, vocabulary.get(sels[a]))) {
//						logger.log(Level.INFO, "Removing vocabulary: {0}", itemString);
//						controller.setFileModified(true);
//					}
//				}
//			}
//			updateVocabularyList(currentCategory);
//			setStatusText("Removed " + sels.length + " items", 3000);
//			return true;
//		}
//		return false;
//	}

//	/**
//	 * Prompts the user for a new file to save to.
//	 */
//	private boolean saveAsFile() {
//		if (model == null) {
//			return false;
//		}
//		if (fileOpened != null) {
//			jFileChooserSave.setCurrentDirectory(fileOpened.getParentFile());
//		}
//		int resp = jFileChooserSave.showSaveDialog(this);
//		if (JOptionPane.OK_OPTION == resp) {
//			File file = jFileChooserSave.getSelectedFile();
//			if (file.getParentFile().canWrite()) {
//				fileOpened = file;
//				return saveFile(fileOpened);
//			}
//			else {
//				JOptionPane.showMessageDialog(this,
//						"Cannot write to chosen directory.",
//						"Cannot save",
//						JOptionPane.ERROR_MESSAGE);
//			}
//		}
//		return false;
//	}

//	/**
//	 * Saves the vocabulary model to a given file.
//	 *
//	 * @param file
//	 */
//	private boolean saveFile(File file) {
//		if (file == null || model == null) {
//			return false;
//		}
//		if (file.getParentFile().canWrite()) {
//			JsonVocabIO.writeJsonFile(fileOpened, model);
//			setStatusText("File saved: " + fileOpened.getAbsolutePath(), 4000);
//			controller.setFileModified(false);
//			return true;
//		}
//		else {
//			JOptionPane.showMessageDialog(this,
//					"Cannot write to chosen directory.",
//					"Cannot save",
//					JOptionPane.ERROR_MESSAGE);
//		}
//		return false;
//	}

	/**
	 * Saves user settings.
	 */
	private void savePreferences() {
		prefs.putInt(EditorPrefs.SortMode.toString(), jComboBoxSortMode.getSelectedIndex());
		prefs.putInt(EditorPrefs.VocabDisplayMode.toString(), jComboBoxVocabDisplay.getSelectedIndex());
	}

//	/**
//	 * Sets the GUI in an enabled/disabled state based on whether a file is open
//	 * or not so controls can't be used when not appropriate.
//	 *
//	 * TODO if I can establish a way to modify a category while in lesson sort
//	 * mode then I can remove the boolean showing.
//	 *
//	 * @param enable
//	 */
//	private void setGUIEnabled(boolean enable) {
//		// Add/remove buttons only valid for category mode
////		boolean showing = getSortMode().equals(SortMode.Categories);
//		boolean showing = controller.getSortMode().equals(SortMode.Categories);
//		jComboBoxSortMode.setEnabled(enable);
//		jComboBoxVocabDisplay.setEnabled(enable);
//		jListCategories.setEnabled(enable);
//		jListLessons.setEnabled(enable);
//		jListVocabulary.setEnabled(enable);
//		japaneseVocabEditorPanel.setEnabled(enable);
//		// Buttons
//		jButtonCategoryAdd.setEnabled(enable && showing);
//		jButtonCategoryDelete.setEnabled(enable && showing);
//		jButtonVocabAdd.setEnabled(enable && showing);
//		jButtonVocabDelete.setEnabled(enable && showing);
//		jButtonVocabMove.setEnabled(enable && showing);
//		// Menu items
//		jMenuItemCatAdd.setEnabled(enable && showing);
//		jMenuItemCatDelete.setEnabled(enable && showing);
//		jMenuItemVocabAdd.setEnabled(enable && showing);
//		jMenuItemVocabDelete.setEnabled(enable && showing);
//		jMenuItemVocabMove.setEnabled(enable && showing);
//	}

//	/**
//	 * Sets the status text for a given delay.
//	 *
//	 * @param status
//	 * @param ms
//	 */
//	public void setStatusText(String status, int ms) {
//		controller.setStatusText(status);
//		controller.setTimedStatus(true);
//		// Clear status after ms milliseconds
//		java.util.Timer timer = new java.util.Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				// Only clear it if another status has not been set
//				if (controller.isTimedStatus()) {
//					controller.setStatusText(" ");
//				}
//			}
//		}, ms);
//	}

//	/**
//	 * Configures and sets up the JPopupMenu used to show the user a list of
//	 * categories to be picked from.
//	 */
//	private void setupCategoryPopup(JComponent menu, boolean disableCurrent) {
//		// Only allowing popup menu and menu
//		if (!(menu instanceof JPopupMenu) && !(menu instanceof JMenu)) {
//			return;
//		}
//		menu.removeAll();
//		List<String> cats = model.getCategories();
//		for (String cat : cats) {
//			JMenuItem item = new JMenuItem(cat);
//			item.setActionCommand("VocabMove_" + cat);
//			// Disable current category so it can't be chosen
//			if (cat.equals(controller.getCurrentCategory()) && disableCurrent) {
//				item.setEnabled(false);
//			}
//			// Set the action
////			item.addActionListener(categoryPopupListener);
//			item.addActionListener(controller);
//			menu.add(item);
//		}
//	}

//	/**
//	 * Sets the text of the status bar.
//	 *
//	 * @param status
//	 */
//	public void setStatusText(String status) {
//		timedStatus = false;
//		jLabelStatus.setText(status);
//	}

//	/**
//	 * Updates the GUI, populating the category list from the model.
//	 */
//	private void updateCategoryLessonList() {
//		if (model == null) {
//			return;
//		}
//		// Update the category list
//		List<String> cats = model.getCategories();
//		Object selectedItem = jListCategories.getSelectedValue();
//		modelCategories.clear();
//		Collections.sort(cats);
//		for (String cat : cats) {
//			if (!modelCategories.contains(cat)) {
//				modelCategories.addElement(cat);
//			}
//		}
//		if (selectedItem != null) {
//			jListCategories.setSelectedValue(selectedItem, true);
//		}
//
//		// Update the lesson list
//		List<Integer> lessons = model.getLessons();
//		Collections.sort(lessons);
//		selectedItem = jListLessons.getSelectedValue();
//		modelLessons.clear();
//		for (Integer lesson : lessons) {
//			if (!modelLessons.contains(lessons)) {
//				modelLessons.addElement(lesson);
//			}
//		}
//		if (selectedItem != null) {
//			jListLessons.setSelectedValue(selectedItem, true);
//		}
//	}

//	/**
//	 * Updates the vocabulary listing based on the currently set sort mode.
//	 */
//	private void updateVocabulary() {
//		// Are we in category or lesson mode?
//		SortMode mode = getSortMode();
//		switch (mode) {
//			case Categories:
//				updateVocabularyList(currentCategory);
//				break;
//			case Lessons:
//				updateVocabularyList(currentLesson);
//				break;
//		}
//	}
//
//	/**
//	 * Sets the vocabulary list with vocabulary from the given category. If the
//	 * category is null or does not exist, the list will not be updated.
//	 *
//	 * @param category
//	 */
//	private void updateVocabularyList(String category) {
//		// If no model or category, clear out vocabulary list
//		if (model == null || category == null) {
//			modelVocabulary.clear();
//			return;
//		}
//		ArrayList<VocabItem> items = model.getVocabItems(category);
//		if (items == null) {
//			return;
//		}
//		// Maintain selection
//		Object selectedItem = jListVocabulary.getSelectedValue();
//		// TODO get selected indices
//		int selectedIndex = jListVocabulary.getSelectedIndex();
//		vocabulary = items;
//		modelVocabulary.clear();
//		// Sort vocabulary
//		// TODO should I sort differently depending on display mode?
//		Collections.sort(vocabulary, new EnglishComparator());
//		// Populate list with the English words
//		VocabDisplayMode mode = ((VocabDisplayMode) jComboBoxVocabDisplay.getSelectedItem());
//		for (VocabItem item : vocabulary) {
//			// Display value according to preference
//			switch (mode) {
//				case English:
//					modelVocabulary.addElement(item.getEnglish());
//					break;
//				case Romaji:
//					String romaji = item.getRomaji();
//					if (romaji.isEmpty()) {
//						romaji = "<" + item.getEnglish() + ">";
//					}
//					modelVocabulary.addElement(romaji);
//					break;
//				case Kana:
//					String kana = item.getKana();
//					// Substitue missing kana
//					if (kana.isEmpty()) {
//						kana = "<" + item.getEnglish() + ">";
//					}
//					modelVocabulary.addElement(kana);
//					break;
//				case Kanji:
//					String kanji = item.getKanji();
//					// Don't want to show empty field for missing kanji
//					if (kanji.isEmpty()) {
//						kanji = "<" + item.getEnglish() + ">";
//					}
//					modelVocabulary.addElement(kanji);
//					break;
//			}
//		}
//		if (selectedItem != null) {
//			jListVocabulary.setSelectedValue(selectedItem, true);
//			// Check if selection was successful
//			if (!selectedItem.equals(jListVocabulary.getSelectedValue())) {
//				// If not, try using index
//				// This is the case either when the item has been removed,
//				// renamed(?), or switching between display modes.
//				jListVocabulary.setSelectedIndex(selectedIndex);
//			}
//		}
//	}
//
//	/**
//	 * Sets the vocabulary list with vocabulary from the given lesson. If the
//	 * lesson is less than 0, the list will not be updated.
//	 *
//	 * @param lesson
//	 */
//	private void updateVocabularyList(int lesson) {
//		if (model == null || lesson < 0) {
//			return;
//		}
//		// Try to maintain selection
//		Object selectedItem = jListLessons.getSelectedValue();
//		modelVocabulary.clear();
//		vocabulary = model.getVocabItems(lesson);
//		for (VocabItem item : vocabulary) {
//			modelVocabulary.addElement(item.getEnglish());
//		}
//		if (selectedItem != null) {
//			jListLessons.setSelectedValue(selectedItem, true);
//		}
//		controller.setFileModified(false);
//	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserOpen = new javax.swing.JFileChooser();
        jFileChooserSave = new javax.swing.JFileChooser();
        jPopupMenuCategories = new javax.swing.JPopupMenu();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        jPanelCategories = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBoxSortMode = new javax.swing.JComboBox();
        jPanelCategoryLesson = new javax.swing.JPanel();
        jScrollPaneCategories = new javax.swing.JScrollPane();
        jListCategories = new javax.swing.JList();
        jScrollPaneLessons = new javax.swing.JScrollPane();
        jListLessons = new javax.swing.JList();
        jPanelCategoryButtons = new javax.swing.JPanel();
        jButtonCategoryAdd = new javax.swing.JButton();
        jButtonCategoryDelete = new javax.swing.JButton();
        jPanelVocab = new javax.swing.JPanel();
        jPanelVocabComboBox = new javax.swing.JPanel();
        jComboBoxVocabDisplay = new javax.swing.JComboBox();
        jScrollPaneVocab = new javax.swing.JScrollPane();
        jListVocabulary = new javax.swing.JList();
        jPanelVocabButtons = new javax.swing.JPanel();
        jButtonVocabAdd = new javax.swing.JButton();
        jButtonVocabDelete = new javax.swing.JButton();
        jButtonVocabMove = new javax.swing.JButton();
        japaneseVocabEditorPanel = new JapaneseVocabEditorPanel(controller, controller);
        jPanelButtons = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuVocab = new javax.swing.JMenu();
        jMenuItemCatAdd = new javax.swing.JMenuItem();
        jMenuItemCatDelete = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemVocabAdd = new javax.swing.JMenuItem();
        jMenuItemVocabDelete = new javax.swing.JMenuItem();
        jMenuItemVocabMove = new javax.swing.JMenuItem();

        jFileChooserOpen.setDialogTitle("Select JSON file");

        jFileChooserSave.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Japanese Vocabulary Editor");
        setMinimumSize(new java.awt.Dimension(300, 300));
        setPreferredSize(new java.awt.Dimension(500, 360));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelMain.setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerLocation(220);
        jSplitPane.setDividerSize(8);
        jSplitPane.setResizeWeight(0.5);

        jPanelCategories.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jComboBoxSortMode.setModel(new DefaultComboBoxModel(SortMode.values()));
        jComboBoxSortMode.setActionCommand("SortModeChange");
        jComboBoxSortMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSortModeActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBoxSortMode, java.awt.BorderLayout.CENTER);

        jPanelCategories.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanelCategoryLesson.setLayout(new java.awt.CardLayout());

        jListCategories.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jListCategories.setModel(new DefaultListModel());
        jListCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCategories.addListSelectionListener(controller);
        jScrollPaneCategories.setViewportView(jListCategories);

        jPanelCategoryLesson.add(jScrollPaneCategories, "categories");

        jListLessons.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jListLessons.setModel(new DefaultListModel());
        jListLessons.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListLessons.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListLessonsValueChanged(evt);
            }
        });
        jScrollPaneLessons.setViewportView(jListLessons);

        jPanelCategoryLesson.add(jScrollPaneLessons, "lessons");

        jPanelCategories.add(jPanelCategoryLesson, java.awt.BorderLayout.CENTER);

        jPanelCategoryButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonCategoryAdd.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonCategoryAdd.setText("+");
        jButtonCategoryAdd.setActionCommand("CatAdd");
        jButtonCategoryAdd.setIconTextGap(0);
        jButtonCategoryAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryAdd.setPreferredSize(new java.awt.Dimension(24, 20));
        jButtonCategoryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCategoryAddActionPerformed(evt);
            }
        });
        jPanelCategoryButtons.add(jButtonCategoryAdd);

        jButtonCategoryDelete.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonCategoryDelete.setText("-");
        jButtonCategoryDelete.setActionCommand("DeleteCategory");
        jButtonCategoryDelete.setIconTextGap(0);
        jButtonCategoryDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryDelete.setPreferredSize(new java.awt.Dimension(24, 20));
        jPanelCategoryButtons.add(jButtonCategoryDelete);

        jPanelCategories.add(jPanelCategoryButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setLeftComponent(jPanelCategories);

        jPanelVocab.setLayout(new java.awt.BorderLayout());

        jPanelVocabComboBox.setLayout(new java.awt.BorderLayout());

        jComboBoxVocabDisplay.setModel(new DefaultComboBoxModel(VocabDisplayMode.values()));
        jComboBoxVocabDisplay.setActionCommand("VocabSelect");
        jComboBoxVocabDisplay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxVocabDisplayItemStateChanged(evt);
            }
        });
        jPanelVocabComboBox.add(jComboBoxVocabDisplay, java.awt.BorderLayout.CENTER);

        jPanelVocab.add(jPanelVocabComboBox, java.awt.BorderLayout.NORTH);

        jListVocabulary.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jListVocabulary.setModel(new DefaultListModel());
        jListVocabulary.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListVocabularyValueChanged(evt);
            }
        });
        jScrollPaneVocab.setViewportView(jListVocabulary);

        jPanelVocab.add(jScrollPaneVocab, java.awt.BorderLayout.CENTER);

        jPanelVocabButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonVocabAdd.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabAdd.setText("+");
        jButtonVocabAdd.setActionCommand(EditorActions.VocabAdd.name());
        jButtonVocabAdd.setIconTextGap(0);
        jButtonVocabAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonVocabAdd.setPreferredSize(new java.awt.Dimension(24, 20));
        jButtonVocabAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVocabAddActionPerformed(evt);
            }
        });
        jPanelVocabButtons.add(jButtonVocabAdd);

        jButtonVocabDelete.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabDelete.setText("-");
        jButtonVocabDelete.setActionCommand(EditorActions.VocabDelete.name());
        jButtonVocabDelete.setIconTextGap(0);
        jButtonVocabDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonVocabDelete.setPreferredSize(new java.awt.Dimension(24, 20));
        jButtonVocabDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVocabDeleteActionPerformed(evt);
            }
        });
        jPanelVocabButtons.add(jButtonVocabDelete);

        jButtonVocabMove.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabMove.setText("mv");
        jButtonVocabMove.setActionCommand(EditorActions.VocabMove.name());
        jButtonVocabMove.setComponentPopupMenu(jPopupMenuCategories);
        jButtonVocabMove.setIconTextGap(0);
        jButtonVocabMove.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonVocabMove.setPreferredSize(new java.awt.Dimension(30, 20));
        jButtonVocabMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVocabMoveActionPerformed(evt);
            }
        });
        jPanelVocabButtons.add(jButtonVocabMove);

        jPanelVocab.add(jPanelVocabButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setRightComponent(jPanelVocab);

        jPanelMain.add(jSplitPane, java.awt.BorderLayout.CENTER);

        japaneseVocabEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Vocabulary Editor"));
        jPanelMain.add(japaneseVocabEditorPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        jLabelStatus.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabelStatus.setText("Status");
        jPanelButtons.add(jLabelStatus);

        getContentPane().add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText("File");

        jMenuItemNew.setText("New File");
        jMenuItemNew.setActionCommand(EditorActions.FileNew.name());
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemOpen.setText("Open File");
        jMenuItemOpen.setActionCommand(EditorActions.FileOpen.name());
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemSave.setText("Save");
        jMenuItemSave.setActionCommand(EditorActions.FileSave.name());
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save as");
        jMenuItemSaveAs.setActionCommand(EditorActions.FileSaveAs.name());
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuItemClose.setText("Close File");
        jMenuItemClose.setActionCommand(EditorActions.FileClose.name());
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFileAction(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.setActionCommand(EditorActions.Exit.name());
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuVocab.setText("Vocabulary");

        jMenuItemCatAdd.setText("Add Category");
        jMenuItemCatAdd.setActionCommand(EditorActions.CatAdd.name());
        jMenuVocab.add(jMenuItemCatAdd);

        jMenuItemCatDelete.setText("Delete Category");
        jMenuItemCatDelete.setActionCommand(EditorActions.CatDelete.name());
        jMenuVocab.add(jMenuItemCatDelete);
        jMenuVocab.add(jSeparator2);

        jMenuItemVocabAdd.setText("Add Vocabulary");
        jMenuItemVocabAdd.setActionCommand(EditorActions.VocabAdd.name());
        jMenuVocab.add(jMenuItemVocabAdd);

        jMenuItemVocabDelete.setText("Delete Vocabulary");
        jMenuItemVocabDelete.setActionCommand(EditorActions.VocabDelete.name());
        jMenuVocab.add(jMenuItemVocabDelete);

        jMenuItemVocabMove.setText("Move Vocabulary");
        jMenuItemVocabMove.setActionCommand(EditorActions.VocabMove.name());
        jMenuVocab.add(jMenuItemVocabMove);

        jMenuBar.add(jMenuVocab);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxSortModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSortModeActionPerformed
//		SortMode pick = controller.getSortMode();
//		((CardLayout) jPanelCategoryLesson.getLayout()).show(
//				jPanelCategoryLesson, pick.name().toLowerCase());
//		controller.updateCategoryLessonList();
//		switch (pick) {
//			case Categories:
//				Object value = jListCategories.getSelectedValue();
//				if (value != null) {
//					updateVocabularyList(value.toString());
//				}
//				else {
//					modelVocabulary.clear();
//				}
//				break;
//			case Lessons:
//				int sel = jListLessons.getSelectedIndex();
//				if (sel > -1) {
//					updateVocabularyList(sel);
//				}
//				else {
//					modelVocabulary.clear();
//				}
//				break;
//		}
    }//GEN-LAST:event_jComboBoxSortModeActionPerformed

    private void jListVocabularyValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListVocabularyValueChanged
//		if (evt.getValueIsAdjusting()) {
//			return;
//		}
//		int[] sel = jListVocabulary.getSelectedIndices();
//		if (sel.length > 0) {
//			selectedVocabIndex = sel[0];
//			if (selectedVocabIndex > -1) {
//				japaneseVocabEditorPanel.setVocabItem(vocabulary.get(selectedVocabIndex));
//			}
//			else {
//				japaneseVocabEditorPanel.clearPanel();
//			}
//		}
//		// Editor only enabled when one vocabulary is selected
//		japaneseVocabEditorPanel.setEnabled(sel.length == 1);
    }//GEN-LAST:event_jListVocabularyValueChanged

    private void jButtonCategoryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCategoryAddActionPerformed
//		if (model == null) {
//			return;
//		}
//		String resp = JOptionPane.showInputDialog(this, "New Category:",
//				"New Category", JOptionPane.QUESTION_MESSAGE);
//		if (resp != null) {
//			if (!addCategory(resp)) {
//				JOptionPane.showMessageDialog(this, "That category already exists.");
//			}
//			else {
//				// Add category label item
//				VocabItem vi = new VocabItem("#" + resp, "", "", "");
//				addVocabulary(vi);
//			}
//		}
    }//GEN-LAST:event_jButtonCategoryAddActionPerformed

    private void jButtonVocabAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVocabAddActionPerformed
//		controller.addVocabulary();
    }//GEN-LAST:event_jButtonVocabAddActionPerformed

    private void jButtonVocabDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVocabDeleteActionPerformed
//		removeVocabItem(true);
    }//GEN-LAST:event_jButtonVocabDeleteActionPerformed

    private void jListLessonsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListLessonsValueChanged
//		if (!evt.getValueIsAdjusting()) {
//			Object selObject = jListLessons.getSelectedValue();
//			if (selObject == null) {
//				currentLesson = -1;
//				return;
//			}
//			currentLesson = Integer.parseInt(selObject.toString(), 10);
//			// Show vocabulary from given lesson
//			updateVocabularyList(currentLesson);
//		}
    }//GEN-LAST:event_jListLessonsValueChanged

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
//		// Prompt user for JSON fileOpened
//		int resp = jFileChooserOpen.showOpenDialog(this);
//		if (JFileChooser.APPROVE_OPTION == resp) {
//			openFile(jFileChooserOpen.getSelectedFile());
//		}
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
//		// Assuming the opened file is the one we want to save to
//		saveFile(fileOpened);
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
//		saveAsFile();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void closeFileAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFileAction
//		closeFile();
    }//GEN-LAST:event_closeFileAction

    private void jButtonVocabMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVocabMoveActionPerformed
//		// Make sure at least one vocabulary is selected
//		int[] sels = jListVocabulary.getSelectedIndices();
//		if (sels.length == 0) {
//			return;
//		}
//		// Show popup menu with category options
//		setupCategoryPopup(jPopupMenuCategories, true);
//		jPopupMenuCategories.show(jButtonVocabMove, 0, 0);
    }//GEN-LAST:event_jButtonVocabMoveActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
//		createNewFile();
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void jComboBoxVocabDisplayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxVocabDisplayItemStateChanged
//		if (evt.getStateChange() == ItemEvent.SELECTED) {
//			updateVocabulary();
//		}
    }//GEN-LAST:event_jComboBoxVocabDisplayItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
		loadPreferences();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		savePreferences();
    }//GEN-LAST:event_formWindowClosing

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		SingleLineLogFormatter formatter = new SingleLineLogFormatter();
		Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
		while (loggerNames.hasMoreElements()) {
			String loggerName = loggerNames.nextElement();
			Logger.getLogger(loggerName).setLevel(logLevel);
			// Set level for each handler
			for (Handler handler : Logger.getLogger(loggerName).getHandlers()) {
				handler.setLevel(logLevel);
				handler.setFormatter(formatter);
			}
		}
		Logger.getAnonymousLogger().log(Level.INFO, "Starting");

		/* Set the Nimbus look and feel */
		//<editor-fold-null defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
			/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Metal".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold-null>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JapaneseVocabEditor().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButtonCategoryAdd;
    public javax.swing.JButton jButtonCategoryDelete;
    public javax.swing.JButton jButtonVocabAdd;
    public javax.swing.JButton jButtonVocabDelete;
    public javax.swing.JButton jButtonVocabMove;
    public javax.swing.JComboBox jComboBoxSortMode;
    public javax.swing.JComboBox jComboBoxVocabDisplay;
    private javax.swing.JFileChooser jFileChooserOpen;
    private javax.swing.JFileChooser jFileChooserSave;
    public javax.swing.JLabel jLabelStatus;
    public javax.swing.JList jListCategories;
    public javax.swing.JList jListLessons;
    public javax.swing.JList jListVocabulary;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    public javax.swing.JMenuItem jMenuItemCatAdd;
    public javax.swing.JMenuItem jMenuItemCatDelete;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    public javax.swing.JMenuItem jMenuItemVocabAdd;
    public javax.swing.JMenuItem jMenuItemVocabDelete;
    public javax.swing.JMenuItem jMenuItemVocabMove;
    private javax.swing.JMenu jMenuVocab;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCategories;
    private javax.swing.JPanel jPanelCategoryButtons;
    public javax.swing.JPanel jPanelCategoryLesson;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelVocab;
    private javax.swing.JPanel jPanelVocabButtons;
    private javax.swing.JPanel jPanelVocabComboBox;
    public javax.swing.JPopupMenu jPopupMenuCategories;
    private javax.swing.JScrollPane jScrollPaneCategories;
    private javax.swing.JScrollPane jScrollPaneLessons;
    private javax.swing.JScrollPane jScrollPaneVocab;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane;
    public psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorPanel japaneseVocabEditorPanel;
    // End of variables declaration//GEN-END:variables
}
