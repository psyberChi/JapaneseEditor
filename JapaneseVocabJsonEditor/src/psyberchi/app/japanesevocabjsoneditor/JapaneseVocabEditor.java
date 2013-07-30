/*
 *  JapaneseVocabEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabEditor extends javax.swing.JFrame implements PropertyChangeListener {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(JapaneseVocabEditor.class.getCanonicalName());
	/**
	 * The level to log at
	 */
	public static Level logLevel = Level.INFO;
	/**
	 * The main title of the application.
	 */
	public static final String APP_TITLE = "Japanese Vocabulary Editor";
	/**
	 * The ActionListener tied to each of the MenuItem on the JPopupMenu used to
	 * show the categories for the user to choose.
	 */
	private ActionListener categoryPopupListener = null;
	/**
	 * A container for all of the current set of vocabulary.
	 */
	private ArrayList<VocabItem> vocabulary = new ArrayList<VocabItem>();
	/**
	 * Sets whether or not we're using a timed status.
	 */
	private static boolean timedStatus = false;
	/**
	 * Whether the currently selected vocabulary item is modified.
	 */
	private boolean fileModified = false;
	/**
	 * The ListModel for the category JList.
	 */
	private DefaultListModel modelCategories = null;
	/**
	 * The ListModel for the lessons JList.
	 */
	private DefaultListModel modelLessons = null;
	/**
	 * The ListModel for the vocabulary JList.
	 */
	private DefaultListModel modelVocabulary = null;
	/**
	 * The currently open fileOpened.
	 */
	private File fileOpened = null;
	/**
	 * The current lesson selected
	 */
	private int currentLesson = -1;
	/**
	 * Index of selected vocabulary.
	 */
	private int selectedVocabIndex = -1;
	/**
	 * The current category chosen.
	 */
	private String currentCategory;
	/**
	 * The vocabulary model holding categories and vocabulary.
	 */
	private VocabModel model = null;

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
	 * Creates new form JapaneseVocabEditor
	 */
	public JapaneseVocabEditor() {
		initComponents();
		// Capture models
		modelCategories = (DefaultListModel) jListCategories.getModel();
		modelLessons = (DefaultListModel) jListLessons.getModel();
		modelVocabulary = (DefaultListModel) jListVocabulary.getModel();
		japaneseVocabEditorPanel.addPropertyChangeListener(this);
		clearGUI();
		setGUIEnabled(false);
		categoryPopupListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String chosenCat = ae.getActionCommand();
				int[] sels = jListVocabulary.getSelectedIndices();
				// Move each one individually
				for (int a = sels.length - 1; a >= 0; a--) {
					VocabItem vocab = vocabulary.get(sels[a]);
					moveVocabItem(vocab, currentCategory, chosenCat);
				}
				updateVocabularyList(currentCategory);
			}
		};
	}

	/**
	 * Adds a new category to the list if it doesn't already exist.
	 *
	 * @param cat
	 * @return
	 */
	private boolean addCategory(String cat) {
		if (model != null && !modelCategories.contains(cat)) {
			model.addCategory(cat);
			logger.log(Level.INFO, "Adding new category: {0}", cat);
			// Make sure it's sorted after add
			updateCategoryLessonList();
			// Highlight the new category
			jListCategories.setSelectedValue(cat, true);
			fileModified = true;
			return true;
		}
		return false;
	}

	/**
	 * Adds a new vocabulary item by prompting for an English word, which will
	 * then be used to populate the romaji list as long as the new item does not
	 * start with a # or already exists.
	 */
	private void addVocabulary() {
		String resp = JOptionPane.showInputDialog(this, "English entry:",
				"Add new vocabulary", JOptionPane.QUESTION_MESSAGE);
		if (resp != null) {
			if (modelVocabulary.contains(resp)) {
				JOptionPane.showMessageDialog(this, "That vocabulary item already exists.");
				return;
			}
			else if (resp.startsWith("#")) {
				JOptionPane.showMessageDialog(this, "Only the category label can start with #.");
				return;
			}
			if (addVocabulary(new VocabItem(resp, "", "", ""))) {
				jListVocabulary.setSelectedValue(resp, true);
				logger.log(Level.INFO, "Adding new vocabulary: {0}", resp);
			}
			else {
				setStatusText("Failed to add vocab " + resp, 4000);
			}
		}
	}

	/**
	 * Adds a new {@link VocabItem} to currently selected category.
	 *
	 * @param item
	 * @return
	 */
	private boolean addVocabulary(VocabItem item) {
		if (model != null && !vocabulary.contains(item)) {
			if (model.addVocabItem(currentCategory, item)) {
				updateVocabularyList(currentCategory);
				fileModified = true;
				logger.log(Level.INFO, "Adding new vocabulary: {0}", item.toJSONString());
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the GUI components to be ready for new data.
	 */
	private void clearGUI() {
		// Clear all of the JList
		modelCategories.clear();
		modelLessons.clear();
		modelVocabulary.clear();
		japaneseVocabEditorPanel.clearPanel();
		setStatusText(" ");
		currentCategory = null;
		selectedVocabIndex = -1;
	}

	/**
	 * Clears the GUI and resets the open file object.
	 */
	private void closeFile() {
		clearGUI();
		fileOpened = null;
		model = null;
		setTitle(APP_TITLE);
		fileModified = false;
	}

	/**
	 * Creates a new file by creating a new model and updating the GUI.
	 *
	 * @return
	 */
	private boolean createNewFile() {
		// Check if any currently open file is modified
		// TODO
		// Close off any existing file
		closeFile();
		// Create a new model
		model = new VocabModel();
		clearGUI();
		setGUIEnabled(true);
		japaneseVocabEditorPanel.setEnabled(false);
		return false;
	}

	/**
	 * Returns the currently selected SortMode.
	 *
	 * @return
	 */
	private SortMode getSortMode() {
		return ((SortMode) jComboBoxSortMode.getSelectedItem());
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
				updateVocabularyList(currentCategory);
				logger.log(Level.INFO, "Moving vocab from {0} to {1}: {2}",
						new Object[]{fromCat, toCat, item.toJSONString()});
				fileModified = true;
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
	 * Opens a given {@link File} and creates a {@link VocabModel} from it that
	 * is used to load in the categories.
	 *
	 * @param fileOpened
	 * @return
	 */
	private boolean openFile(File file) {
		if (file == null || !file.canRead()) {
			return false;
		}
		// Check if current file is modified
		if (fileModified) {
			// TODO
		}
		try {
			fileOpened = file;
			model = JsonVocabIO.readJsonFile(fileOpened);
			clearGUI();
			updateCategoryLessonList();
			setGUIEnabled(true);
			jListVocabularyValueChanged(new ListSelectionEvent(this, 0, 0, false));
			setTitle(APP_TITLE + " - " + fileOpened.getAbsolutePath());
			String status = String.format(
					"Opened %d categories and %d vocabulary from %s",
					model.getCategoryCount(),
					model.getVocabCount(),
					fileOpened.getName());
			setStatusText(status, 5000);
			fileModified = false;
			return true;
		}
		catch (FileNotFoundException ex) {
			logger.log(Level.SEVERE, null, ex);
			setStatusText("File not found", 3000);
		}
		catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			setStatusText("Could not read file", 3000);
		}
		catch (ParseException ex) {
			logger.log(Level.SEVERE, null, ex);
			setStatusText("Could not parse file", 4000);
		}
		return false;
	}

	/**
	 * Handle when changes are made to the vocabulary editor. Need to capture
	 * changes and update the JList appropriately based on what changed.
	 *
	 * @param pce
	 */
	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		if (JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH.equals(pce.getPropertyName())) {
			String ov = pce.getOldValue().toString().trim();
			String nv = pce.getNewValue().toString().trim();
			boolean renamed = false;

			// Check if trying to use blank value
			if (nv.isEmpty()) {
				// Put old value back in
				japaneseVocabEditorPanel.setEnglish(ov);
				setStatusText("English values cannot be blank.", 6000);
				return;
			}

			// Check if modifying the category
			if (ov.startsWith("#")) {
				// New value must start with #
				if (!nv.startsWith("#")) {
					nv = "#" + nv;
					japaneseVocabEditorPanel.setEnglish(nv);
					if (nv.equals(ov)) {
						JOptionPane.showMessageDialog(this, "Category items must start with #.");
						return;
					}
				}
				// Update category spelling
				model.renameCategory(ov.replace("#", ""), nv.replace("#", ""));
				currentCategory = nv.replace("#", "");
				updateCategoryLessonList();
				jListCategories.setSelectedValue(currentCategory, true);
				renamed = true;
			}
			// If making an item into a category label
			else if (nv.startsWith("#")) {
				// no adding a new one.
				JOptionPane.showMessageDialog(this, "Only category items can start with #.");
				// TODO Remove front # chars
				// Put the value back to the previous one
				japaneseVocabEditorPanel.setEnglish(ov);
				return;
			}

			// Make sure they aren't changing to an existing item
			if (modelVocabulary.contains(nv) && !renamed) {
				JOptionPane.showMessageDialog(this, "A vocabulary item with that English already exists.");
				// Put the value back to the previous one
				japaneseVocabEditorPanel.setEnglish(ov);
				return;
			}

			fileModified = true;

			if (selectedVocabIndex > -1) {
				// TODO check what display mode
				vocabulary.get(selectedVocabIndex).setEnglish(nv);
			}
			updateVocabularyList(currentCategory);
			jListVocabulary.setSelectedValue(nv, true);
		}
		else if (JapaneseVocabEditorPanel.PROP_MODIFIED_LESSON.equals(pce.getPropertyName())) {
			// lesson was modified
			fileModified = true;
		}
	}

	/**
	 * Removes selected vocabulary from the list asking confirmation from the
	 * user if desired.
	 *
	 * @param confirm
	 * @return
	 */
	private boolean removeVocabItem(boolean confirm) {
		int[] sels = jListVocabulary.getSelectedIndices();
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
				int resp = JOptionPane.showConfirmDialog(this, msg.toString());
				if (JOptionPane.YES_OPTION != resp) {
					return false;
				}
			}
			// Remove from end to start the selected items
			for (int a = sels.length - 1; a >= 0; a--) {
				String currItem = jListVocabulary.getModel().getElementAt(sels[a]).toString();
				if (currItem.startsWith("#")) {
					// No removing category label item
					JOptionPane.showMessageDialog(this, "Cannot delete category label item");
					continue;
				}
				else {
					String itemString = vocabulary.get(sels[a]).toJSONString();
					if (model.removeVocabItem(currentCategory, vocabulary.get(sels[a]))) {
						logger.log(Level.INFO, "Removing vocabulary: {0}", itemString);
						fileModified = true;
					}
				}
			}
			updateVocabularyList(currentCategory);
			setStatusText("Removed " + sels.length + " items", 3000);
			return true;
		}
		return false;
	}

	/**
	 * Prompts the user for a new file to save to.
	 */
	private boolean saveAsFile() {
		if (model == null) {
			return false;
		}
		if (fileOpened != null) {
			jFileChooserSave.setCurrentDirectory(fileOpened.getParentFile());
		}
		int resp = jFileChooserSave.showSaveDialog(this);
		if (JOptionPane.OK_OPTION == resp) {
			File file = jFileChooserSave.getSelectedFile();
			if (file.getParentFile().canWrite()) {
				fileOpened = file;
				return saveFile(fileOpened);
			}
			else {
				JOptionPane.showMessageDialog(this,
						"Cannot write to chosen directory.",
						"Cannot save",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	/**
	 * Saves the vocabulary model to a given file.
	 *
	 * @param file
	 */
	private boolean saveFile(File file) {
		if (file == null || model == null) {
			return false;
		}
		if (file.getParentFile().canWrite()) {
			JsonVocabIO.writeJsonFile(fileOpened, model);
			setStatusText("File saved: " + fileOpened.getAbsolutePath(), 4000);
			fileModified = false;
			return true;
		}
		else {
			JOptionPane.showMessageDialog(this,
					"Cannot write to chosen directory.",
					"Cannot save",
					JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	/**
	 * Sets the GUI in an enabled/disabled state based on whether a file is open
	 * or not so controls can't be used when not appropriate.
	 *
	 * TODO if I can establish a way to modify a category while in lesson sort
	 * mode then I can remove the boolean showing.
	 *
	 * @param enable
	 */
	private void setGUIEnabled(boolean enable) {
		// Add/remove buttons only valid for category mode
		boolean showing = getSortMode().equals(SortMode.Categories);
		jComboBoxSortMode.setEnabled(enable);
		jComboBoxVocabDisplay.setEnabled(enable);
		jListCategories.setEnabled(enable);
		jListLessons.setEnabled(enable);
		jListVocabulary.setEnabled(enable);
		japaneseVocabEditorPanel.setEnabled(enable);
		// Buttons
		jButtonCategoryAdd.setEnabled(enable && showing);
		jButtonCategoryDelete.setEnabled(enable && showing);
		jButtonRomajiAdd.setEnabled(enable && showing);
		jButtonRomajiDelete.setEnabled(enable && showing);
		jButtonRomajiMove.setEnabled(enable && showing);
		// Menu items
		jMenuItemCatAdd.setEnabled(enable && showing);
		jMenuItemCatDelete.setEnabled(enable && showing);
		jMenuItemVocabAdd.setEnabled(enable && showing);
		jMenuItemVocabDelete.setEnabled(enable && showing);
		jMenuItemVocabMove.setEnabled(enable && showing);
	}

	/**
	 * Sets the status text for a given delay.
	 *
	 * @param status
	 * @param ms
	 */
	private void setStatusText(String status, int ms) {
		setStatusText(status);
		timedStatus = true;
		// Clear status after ms milliseconds
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Only clear it if another status has not been set
				if (timedStatus) {
					setStatusText(" ");
				}
			}
		}, ms);
	}

	/**
	 * Configures and sets up the JPopupMenu used to show the user a list of
	 * categories to be picked from.
	 */
	private void setupCategoryPopup(boolean disableCurrent) {
		jPopupMenuCategories.removeAll();
		List<String> cats = model.getCategories();
		for (String cat : cats) {
			JMenuItem item = new JMenuItem(cat);
			item.setActionCommand(cat);
			// Disable current category so it can't be chosen
			if (cat.equals(currentCategory)) {
				item.setEnabled(false);
			}
			// Set the action
			item.addActionListener(categoryPopupListener);
			jPopupMenuCategories.add(item);
		}
	}

	/**
	 * Sets the text of the status bar.
	 *
	 * @param status
	 */
	private void setStatusText(String status) {
		timedStatus = false;
		jLabelStatus.setText(status);
	}

	/**
	 * Updates the GUI, populating the category list from the model.
	 */
	private void updateCategoryLessonList() {
		if (model == null) {
			return;
		}
		// Update the category list
		List<String> cats = model.getCategories();
		Object selectedItem = jListCategories.getSelectedValue();
		modelCategories.clear();
		Collections.sort(cats);
		for (String cat : cats) {
			if (!modelCategories.contains(cat)) {
				modelCategories.addElement(cat);
			}
		}
		if (selectedItem != null) {
			jListCategories.setSelectedValue(selectedItem, true);
		}

		// Update the lesson list
		List<Integer> lessons = model.getLessons();
		Collections.sort(lessons);
		selectedItem = jListLessons.getSelectedValue();
		modelLessons.clear();
		for (Integer lesson : lessons) {
			if (!modelLessons.contains(lessons)) {
				modelLessons.addElement(lesson);
			}
		}
		if (selectedItem != null) {
			jListLessons.setSelectedValue(selectedItem, true);
		}
	}

	/**
	 * Updates the vocabulary listing based on the currently set sort mode.
	 */
	private void updateVocabulary() {
		// Are we in category or lesson mode?
		SortMode mode = getSortMode();
		switch (mode) {
			case Categories:
				updateVocabularyList(currentCategory);
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
	private void updateVocabularyList(String category) {
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
		Object selectedItem = jListVocabulary.getSelectedValue();
		// TODO get selected indices
		int selectedIndex = jListVocabulary.getSelectedIndex();
		vocabulary = items;
		modelVocabulary.clear();
		// Sort vocabulary
		// TODO should I sort differently depending on display mode?
		Collections.sort(vocabulary, new EnglishComparator());
		// Populate list with the English words
		VocabDisplayMode mode = ((VocabDisplayMode) jComboBoxVocabDisplay.getSelectedItem());
		for (VocabItem item : vocabulary) {
			// Display value according to preference
			switch (mode) {
				case English:
					modelVocabulary.addElement(item.getEnglish());
					break;
				case Romaji:
					String romaji = item.getRomanji();
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
			jListVocabulary.setSelectedValue(selectedItem, true);
			// Check if selection was successful
			if (!selectedItem.equals(jListVocabulary.getSelectedValue())) {
				// If not, try using index
				// This is the case either when the item has been removed,
				// renamed(?), or switching between display modes.
				jListVocabulary.setSelectedIndex(selectedIndex);
			}
		}
	}

	/**
	 * Sets the vocabulary list with vocabulary from the given lesson. If the
	 * lesson is less than 0, the list will not be updated.
	 *
	 * @param lesson
	 */
	private void updateVocabularyList(int lesson) {
		if (model == null || lesson < 0) {
			return;
		}
		// Try to maintain selection
		Object selectedItem = jListLessons.getSelectedValue();
		modelVocabulary.clear();
		vocabulary = model.getVocabItems(lesson);
		for (VocabItem item : vocabulary) {
			modelVocabulary.addElement(item.getEnglish());
		}
		if (selectedItem != null) {
			jListLessons.setSelectedValue(selectedItem, true);
		}
		fileModified = false;
	}

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
        jPanelRomanji = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxVocabDisplay = new javax.swing.JComboBox();
        jScrollPaneRomaji = new javax.swing.JScrollPane();
        jListVocabulary = new javax.swing.JList();
        jPanelRomajiButtons = new javax.swing.JPanel();
        jButtonRomajiAdd = new javax.swing.JButton();
        jButtonRomajiDelete = new javax.swing.JButton();
        jButtonRomajiMove = new javax.swing.JButton();
        japaneseVocabEditorPanel = new psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorPanel();
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
        jFileChooserSave.setFileHidingEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Japanese Vocabulary Editor");
        setMinimumSize(new java.awt.Dimension(300, 300));
        setPreferredSize(new java.awt.Dimension(500, 360));

        jPanelMain.setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerLocation(220);
        jSplitPane.setDividerSize(8);
        jSplitPane.setResizeWeight(0.5);

        jPanelCategories.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jComboBoxSortMode.setModel(new DefaultComboBoxModel(SortMode.values()));
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
        jListCategories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListCategoriesValueChanged(evt);
            }
        });
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
        jButtonCategoryDelete.setIconTextGap(0);
        jButtonCategoryDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryDelete.setPreferredSize(new java.awt.Dimension(24, 20));
        jPanelCategoryButtons.add(jButtonCategoryDelete);

        jPanelCategories.add(jPanelCategoryButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setLeftComponent(jPanelCategories);

        jPanelRomanji.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jComboBoxVocabDisplay.setModel(new DefaultComboBoxModel(VocabDisplayMode.values()));
        jComboBoxVocabDisplay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxVocabDisplayItemStateChanged(evt);
            }
        });
        jPanel1.add(jComboBoxVocabDisplay, java.awt.BorderLayout.CENTER);

        jPanelRomanji.add(jPanel1, java.awt.BorderLayout.NORTH);

        jListVocabulary.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jListVocabulary.setModel(new DefaultListModel());
        jListVocabulary.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListVocabularyValueChanged(evt);
            }
        });
        jScrollPaneRomaji.setViewportView(jListVocabulary);

        jPanelRomanji.add(jScrollPaneRomaji, java.awt.BorderLayout.CENTER);

        jPanelRomajiButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonRomajiAdd.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonRomajiAdd.setText("+");
        jButtonRomajiAdd.setIconTextGap(0);
        jButtonRomajiAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRomajiAdd.setPreferredSize(new java.awt.Dimension(24, 20));
        jButtonRomajiAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRomajiAddActionPerformed(evt);
            }
        });
        jPanelRomajiButtons.add(jButtonRomajiAdd);

        jButtonRomajiDelete.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonRomajiDelete.setText("-");
        jButtonRomajiDelete.setIconTextGap(0);
        jButtonRomajiDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRomajiDelete.setPreferredSize(new java.awt.Dimension(24, 20));
        jButtonRomajiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRomajiDeleteActionPerformed(evt);
            }
        });
        jPanelRomajiButtons.add(jButtonRomajiDelete);

        jButtonRomajiMove.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonRomajiMove.setText("mv");
        jButtonRomajiMove.setIconTextGap(0);
        jButtonRomajiMove.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonRomajiMove.setPreferredSize(new java.awt.Dimension(30, 20));
        jButtonRomajiMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRomajiMoveActionPerformed(evt);
            }
        });
        jPanelRomajiButtons.add(jButtonRomajiMove);

        jPanelRomanji.add(jPanelRomajiButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setRightComponent(jPanelRomanji);

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
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemOpen.setText("Open File");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save as");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuItemClose.setText("Close File");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFileAction(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuItemExit.setText("Exit");
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuVocab.setText("Vocabulary");

        jMenuItemCatAdd.setText("Add Category");
        jMenuVocab.add(jMenuItemCatAdd);

        jMenuItemCatDelete.setText("Delete Category");
        jMenuVocab.add(jMenuItemCatDelete);
        jMenuVocab.add(jSeparator2);

        jMenuItemVocabAdd.setText("Add Vocabulary");
        jMenuVocab.add(jMenuItemVocabAdd);

        jMenuItemVocabDelete.setText("Delete Category");
        jMenuVocab.add(jMenuItemVocabDelete);

        jMenuItemVocabMove.setText("Move Vocabulary");
        jMenuVocab.add(jMenuItemVocabMove);

        jMenuBar.add(jMenuVocab);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListCategoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListCategoriesValueChanged
		if (!evt.getValueIsAdjusting() && jListCategories.getSelectedValue() != null) {
			// Switch between showing categories or lessons
			currentCategory = jListCategories.getSelectedValue().toString();
			if (model == null || currentCategory == null) {
				return;
			}
			updateVocabularyList(currentCategory);
		}
    }//GEN-LAST:event_jListCategoriesValueChanged

    private void jComboBoxSortModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSortModeActionPerformed
		SortMode pick = getSortMode();
		((CardLayout) jPanelCategoryLesson.getLayout()).show(
				jPanelCategoryLesson, pick.name().toLowerCase());
		updateCategoryLessonList();
		switch (pick) {
			case Categories:
				Object value = jListCategories.getSelectedValue();
				if (value != null) {
					updateVocabularyList(value.toString());
				}
				else {
					modelVocabulary.clear();
				}
				break;
			case Lessons:
				int sel = jListLessons.getSelectedIndex();
				if (sel > -1) {
					updateVocabularyList(sel);
				}
				else {
					modelVocabulary.clear();
				}
				break;
		}
    }//GEN-LAST:event_jComboBoxSortModeActionPerformed

    private void jListVocabularyValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListVocabularyValueChanged
		if (evt.getValueIsAdjusting()) {
			return;
		}
		int[] sel = jListVocabulary.getSelectedIndices();
		if (sel.length > 0) {
			selectedVocabIndex = sel[0];
			if (selectedVocabIndex > -1) {
				japaneseVocabEditorPanel.setVocabItem(vocabulary.get(selectedVocabIndex));
			}
			else {
				japaneseVocabEditorPanel.clearPanel();
			}
		}
		// Editor only enabled when one vocabulary is selected
		japaneseVocabEditorPanel.setEnabled(sel.length == 1);
    }//GEN-LAST:event_jListVocabularyValueChanged

    private void jButtonCategoryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCategoryAddActionPerformed
		if (model == null) {
			return;
		}
		String resp = JOptionPane.showInputDialog(this, "New Category:",
				"New Category", JOptionPane.QUESTION_MESSAGE);
		if (resp != null) {
			if (!addCategory(resp)) {
				JOptionPane.showMessageDialog(this, "That category already exists.");
			}
			else {
				// Add category label item
				VocabItem vi = new VocabItem("#" + resp, "", "", "");
				addVocabulary(vi);
			}
		}
    }//GEN-LAST:event_jButtonCategoryAddActionPerformed

    private void jButtonRomajiAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRomajiAddActionPerformed
		addVocabulary();
    }//GEN-LAST:event_jButtonRomajiAddActionPerformed

    private void jButtonRomajiDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRomajiDeleteActionPerformed
		removeVocabItem(true);
    }//GEN-LAST:event_jButtonRomajiDeleteActionPerformed

    private void jListLessonsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListLessonsValueChanged
		if (!evt.getValueIsAdjusting()) {
			Object selObject = jListLessons.getSelectedValue();
			if (selObject == null) {
				currentLesson = -1;
				return;
			}
			currentLesson = Integer.parseInt(selObject.toString(), 10);
			// Show vocabulary from given lesson
			updateVocabularyList(currentLesson);
		}
    }//GEN-LAST:event_jListLessonsValueChanged

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		// Prompt user for JSON fileOpened
		int resp = jFileChooserOpen.showOpenDialog(this);
		if (JFileChooser.APPROVE_OPTION == resp) {
			openFile(jFileChooserOpen.getSelectedFile());
		}
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		// Assuming the opened file is the one we want to save to
		saveFile(fileOpened);
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
		saveAsFile();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void closeFileAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFileAction
		closeFile();
    }//GEN-LAST:event_closeFileAction

    private void jButtonRomajiMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRomajiMoveActionPerformed
		// Make sure at least one vocabulary is selected
		int[] sels = jListVocabulary.getSelectedIndices();
		if (sels.length == 0) {
			return;
		}
		// Show popup menu with category options
		setupCategoryPopup(true);
		jButtonRomajiMove.setComponentPopupMenu(jPopupMenuCategories);
		jPopupMenuCategories.show(jButtonRomajiMove, 0, 0);
    }//GEN-LAST:event_jButtonRomajiMoveActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
		createNewFile();
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void jComboBoxVocabDisplayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxVocabDisplayItemStateChanged
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			updateVocabulary();
		}
    }//GEN-LAST:event_jComboBoxVocabDisplayItemStateChanged

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
    private javax.swing.JButton jButtonCategoryAdd;
    private javax.swing.JButton jButtonCategoryDelete;
    private javax.swing.JButton jButtonRomajiAdd;
    private javax.swing.JButton jButtonRomajiDelete;
    private javax.swing.JButton jButtonRomajiMove;
    private javax.swing.JComboBox jComboBoxSortMode;
    private javax.swing.JComboBox jComboBoxVocabDisplay;
    private javax.swing.JFileChooser jFileChooserOpen;
    private javax.swing.JFileChooser jFileChooserSave;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JList jListCategories;
    private javax.swing.JList jListLessons;
    private javax.swing.JList jListVocabulary;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemCatAdd;
    private javax.swing.JMenuItem jMenuItemCatDelete;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemVocabAdd;
    private javax.swing.JMenuItem jMenuItemVocabDelete;
    private javax.swing.JMenuItem jMenuItemVocabMove;
    private javax.swing.JMenu jMenuVocab;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCategories;
    private javax.swing.JPanel jPanelCategoryButtons;
    private javax.swing.JPanel jPanelCategoryLesson;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelRomajiButtons;
    private javax.swing.JPanel jPanelRomanji;
    private javax.swing.JPopupMenu jPopupMenuCategories;
    private javax.swing.JScrollPane jScrollPaneCategories;
    private javax.swing.JScrollPane jScrollPaneLessons;
    private javax.swing.JScrollPane jScrollPaneRomaji;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane;
    private psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorPanel japaneseVocabEditorPanel;
    // End of variables declaration//GEN-END:variables

}
