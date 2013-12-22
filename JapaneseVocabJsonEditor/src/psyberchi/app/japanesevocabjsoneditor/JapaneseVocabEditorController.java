/*
 *  JapaneseVocabEditorController.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.json.simple.parser.ParseException;
import static psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditor.APP_TITLE;

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
	private static final Preferences prefs = Preferences.userNodeForPackage(JapaneseVocabEditor.class);
	public static String PROP_CatSelectionChange = "PROP_CatSelectionChange";
	public static String PROP_VocabSelectionChange = "PROP_VocabSelectionChange";
	/**
	 * A container for all of the current set of vocabulary.
	 */
	private ArrayList<VocabItem> vocabulary = new ArrayList<VocabItem>();
	/**
	 * Whether or not the currently open file has been modified.
	 */
	private boolean fileModified = false;
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
	/**
	 * The currently selected lesson.
	 */
	private int currentLesson = -1;
	private int currentVocabIdx = -1;
	/**
	 * The currently open fileOpened.
	 */
	private File fileOpened = null;
	/**
	 * Handle to the main editor.
	 */
	private JapaneseVocabEditor vocabEditor;
	private JFileChooser jFileChooserSave = new JFileChooser();
	private JFileChooser jFileChooserOpen = new JFileChooser();
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
	 * Enumeration of preferences available for the editor application.
	 */
	public static enum EditorPrefs {
		SortMode,
		VocabDisplayMode,
		FontEnglish,
		FontRomaji,
		FontKana,
		FontKanji,
		RecentRememberMax, // max number of recently opened files to remember
		RecentOpen, // recently open file prefix
		RecentOpenNum // number of recently remembered files
		;

		@Override
		public String toString() {
			return "EditorPreferences." + name();
		}
	};

	/**
	 * ActionCommand list for the editor.
	 */
	public static enum EditorActions {
		FileNew,
		FileOpen,
		FileSave,
		FileSaveAs,
		FileClose,
		Exit,
		CatAdd,
		CatDelete,
		VocabAdd,
		VocabDelete,
		VocabMove,
		SortModeChange,
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
		prefs.addPreferenceChangeListener(this);
	}

	public void initComponents() {
		modelCategories = (DefaultListModel) vocabEditor.jListCategories.getModel();
		modelLessons = (DefaultListModel) vocabEditor.jListLessons.getModel();
		modelVocabulary = (DefaultListModel) vocabEditor.jListVocabulary.getModel();
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
				// Prompt user for JSON fileOpened
				int resp = jFileChooserOpen.showOpenDialog(null);
				if (JFileChooser.APPROVE_OPTION == resp) {
					openFile(jFileChooserOpen.getSelectedFile());
				}
				break;
			case FileSave:
				// Assuming the opened file is the one we want to save to
				saveFile(fileOpened);
				break;
			case FileSaveAs:
				saveAsFile();
				break;
			case FileClose:
				closeFile();
				break;
			case Exit:
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
						addVocabulary(vi);
					}
				}
				break;
			case CatDelete:
				break;
			case VocabAdd:
				break;
			case VocabDelete:
				break;
			case VocabMove:
				String chosenCat = ae.getActionCommand();
				int[] sels;

				// Show popup menu
				if (chosenCat.equals(EditorActions.VocabMove.name())) {
					// Make sure at least one vocabulary is selected
					sels = vocabEditor.jListVocabulary.getSelectedIndices();
					if (sels.length == 0) {
						return;
					}
					// Show popup menu with category options
					setupCategoryPopup(vocabEditor.jPopupMenuCategories, true);
					vocabEditor.jPopupMenuCategories.show(vocabEditor.jButtonVocabMove, 0, 0);
				}
				else {
					// Carry out menu action
					// @todo Trim prefix
					chosenCat = chosenCat.replaceAll("VocabMove_", "");
					sels = vocabEditor.jListVocabulary.getSelectedIndices();
					// Move each one individually
					for (int a = sels.length - 1; a >= 0; a--) {
						VocabItem vocab = vocabulary.get(sels[a]);
						moveVocabItem(vocab, currentCat, chosenCat);
					}
					updateVocabularyList(currentCat);
				}
				break;
			case SortModeChange:
				// todo - change the +/- actionCommand values
				SortMode pick = getSortMode();
				((CardLayout) vocabEditor.jPanelCategoryLesson.getLayout()).show(
						vocabEditor.jPanelCategoryLesson, pick.name().toLowerCase());
				updateCategoryLessonList();
				switch (pick) {
					case Categories:
						Object value = vocabEditor.jListCategories.getSelectedValue();
						if (value != null) {
							updateVocabularyList(value.toString());
						}
						else {
							modelVocabulary.clear();
						}
						setGUIEnabled(true);
						break;
					case Lessons:
						int sel = vocabEditor.jListLessons.getSelectedIndex();
						if (sel > -1) {
							updateVocabularyList(sel);
						}
						else {
							modelVocabulary.clear();
						}
						setGUIEnabled(false);
						break;
				}
				break;
			case VocabSelect:
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
		if (model != null && cat != null && !modelCategories.contains(currentCat)) {
			model.addCategory(cat);
			logger.log(Level.INFO, "Adding new category: {0}", cat);
			// Make sure it's sorted after add
			updateCategoryLessonList();
			// Highlight the new category
//			jListCategories.setSelectedValue(currentCat, true);
			firePropertyChange(null, PROP_CatSelectionChange, currentCat, cat);
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
				vocabEditor.jListVocabulary.setSelectedValue(resp, true);
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
	public boolean addVocabulary(VocabItem item) {
		if (model != null && !vocabulary.contains(item)) {
			if (model.addVocabItem(currentCat, item)) {
				updateVocabularyList(currentCat);
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
	public void closeFile() {
		clearGUI();
		fileOpened = null;
		model = null;
		vocabEditor.setTitle(APP_TITLE);
		setFileModified(false);
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
		vocabEditor.japaneseVocabEditorPanel.setEnabled(false);
		return false;
	}

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
			String ov = selectedVocabItem.getEnglish();
			String nv = field.getText();
			if (!ov.equals(nv)) {
				selectedVocabItem.setEnglish(nv);
//				firePropertyChange(PROP_MODIFIED_ENGLISH, ov, nv);
//				propertyChange(new PropertyChangeEvent(src, JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH, ov, nv));
				firePropertyChange(src, JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH, ov, nv);
			}
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

	public int getCurrentLesson() {
		return currentLesson;
	}

	public int getCurrentVocabIndex() {
		return currentVocabIdx;
	}

	/**
	 * Returns the currently selected SortMode.
	 *
	 * @return
	 */
	public SortMode getSortMode() {
		return ((SortMode) vocabEditor.jComboBoxSortMode.getSelectedItem());
	}

	public VocabItem getVocabulary(int idx) {
		return vocabulary.get(idx);
	}

	public boolean isFileModified() {
		return fileModified;
	}

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
		// TODO
		JComboBox src = (JComboBox) ie.getSource();

		if (src.equals(vocabEditor.jComboBoxVocabDisplay)) {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				updateVocabulary();
			}
		}
		else if (src.equals(vocabEditor.jComboBoxSortMode)) {

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
				setFileModified(true);
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
		if (isFileModified()) {
			// TODO
		}
		try {
			fileOpened = file;
			model = JsonVocabIO.readJsonFile(fileOpened);
			clearGUI();
			updateCategoryLessonList();
			setGUIEnabled(true);
//			jListVocabularyValueChanged(new ListSelectionEvent(this, 0, 0, false));
			valueChanged(new ListSelectionEvent(vocabEditor.jListVocabulary, 0, 0, false));
			vocabEditor.setTitle(APP_TITLE + " - " + fileOpened.getAbsolutePath());
			String status = String.format(
					"Opened %d categories and %d vocabulary from %s",
					model.getCategoryCount(),
					model.getVocabCount(),
					fileOpened.getName());
			setStatusText(status, 5000);
			setFileModified(false);
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

	@Override
	public void preferenceChange(PreferenceChangeEvent pce) {
		// TODO
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		// TODO

		// Listen for JapaneseEditorPanel items
		logger.log(Level.INFO, "PropChange: {0} = {1} / {2}", new Object[]{
			pce.getPropertyName(),
			pce.getOldValue(),
			pce.getNewValue()
		});

		if (pce.getPropertyName().equals("text")) {
		}


		// JapaneseVocabEditorPanel specific properties
		if (JapaneseVocabEditorPanel.PROP_MODIFIED_ENGLISH.equals(pce.getPropertyName())) {
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
//				currentCategory = nv.replace("#", "");
				setCurrentCategory(nv.replace("#", ""));
				updateCategoryLessonList();
//				jListCategories.setSelectedValue(currentCategory, true);
				vocabEditor.jListCategories.setSelectedValue(getCurrentCategory(), true);
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

			setFileModified(true);

			if (getCurrentVocabIndex() > -1) {
				// TODO check what display mode
//				vocabulary.get(selectedVocabIndex).setEnglish(nv);
				getVocabulary(getCurrentVocabIndex()).setEnglish(nv);
			}
			updateVocabularyList(getCurrentCategory()); // send null could assume current
			vocabEditor.jListVocabulary.setSelectedValue(nv, true);
		}
		else if (JapaneseVocabEditorPanel.PROP_MODIFIED_LESSON.equals(pce.getPropertyName())) {
			// lesson was modified
			setFileModified(true);
		}

	}

	/**
	 * Removes selected vocabulary from the list asking confirmation from the
	 * user if desired.
	 *
	 * @param confirm
	 * @return
	 */
	public boolean removeVocabItem(boolean confirm) {
		int[] sels = vocabEditor.jListVocabulary.getSelectedIndices();
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
				String currItem = vocabEditor.jListVocabulary.getModel().getElementAt(sels[a]).toString();
				if (currItem.startsWith("#")) {
					// No removing category label item
					JOptionPane.showMessageDialog(null, "Cannot delete category label item");
					continue;
				}
				else {
//					String itemString = vocabulary.get(sels[a]).toJSONString();
					String itemString = getVocabulary(sels[a]).toJSONString();
					if (model.removeVocabItem(currentCat, vocabulary.get(sels[a]))) {
						logger.log(Level.INFO, "Removing vocabulary: {0}", itemString);
						setFileModified(true);
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
	 * Prompts the user for a new file to save to.
	 */
	private boolean saveAsFile() {
		if (model == null) {
			return false;
		}
		if (fileOpened != null) {
			jFileChooserSave.setCurrentDirectory(fileOpened.getParentFile());
		}
		int resp = jFileChooserSave.showSaveDialog(null);
		if (JOptionPane.OK_OPTION == resp) {
			File file = jFileChooserSave.getSelectedFile();
			if (file.getParentFile().canWrite()) {
				fileOpened = file;
				return saveFile(fileOpened);
			}
			else {
				JOptionPane.showMessageDialog(null,
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
			setFileModified(false);
			return true;
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Cannot write to chosen directory.",
					"Cannot save",
					JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	public void setCurrentCategory(String cat) {
		if (cat != currentCat) {
			currentCat = cat;
			// fire current cat change?
		}
	}

	public void setCurrentLesson(int les) {
		if (les != currentLesson) {
			currentLesson = les;
		}
	}

	public void setCurrentVocabIndex(int idx) {
		if (idx != currentVocabIdx) {
			currentVocabIdx = idx;
		}
	}

	public void setFileModified(boolean mod) {
		if (mod != fileModified) {
			fileModified = mod;
			// fire property change somewhere
		}
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
	public void setGUIEnabled(boolean enable) {
		// Add/remove buttons only valid for category mode
//		boolean showing = getSortMode().equals(SortMode.Categories);
		boolean showing = getSortMode().equals(SortMode.Categories);
		vocabEditor.jComboBoxSortMode.setEnabled(enable);
		vocabEditor.jComboBoxVocabDisplay.setEnabled(enable);
		vocabEditor.jListCategories.setEnabled(enable);
		vocabEditor.jListLessons.setEnabled(enable);
		vocabEditor.jListVocabulary.setEnabled(enable);
		vocabEditor.japaneseVocabEditorPanel.setEnabled(enable);
		// Buttons
		vocabEditor.jButtonCategoryAdd.setEnabled(enable && showing);
		vocabEditor.jButtonCategoryDelete.setEnabled(enable && showing);
		vocabEditor.jButtonVocabAdd.setEnabled(enable && showing);
		vocabEditor.jButtonVocabDelete.setEnabled(enable && showing);
		vocabEditor.jButtonVocabMove.setEnabled(enable && showing);
		// Menu items
		vocabEditor.jMenuItemCatAdd.setEnabled(enable && showing);
		vocabEditor.jMenuItemCatDelete.setEnabled(enable && showing);
		vocabEditor.jMenuItemVocabAdd.setEnabled(enable && showing);
		vocabEditor.jMenuItemVocabDelete.setEnabled(enable && showing);
		vocabEditor.jMenuItemVocabMove.setEnabled(enable && showing);
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

	public void setTimedStatus(boolean status) {
		if (status != timedStatus) {
			timedStatus = status;
		}
	}

	/**
	 * Configures and sets up the JPopupMenu used to show the user a list of
	 * categories to be picked from.
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
//			item.addActionListener(categoryPopupListener);
			item.addActionListener(this);
			menu.add(item);
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
	}

	/**
	 * Updates the GUI, populating the category list from the model.
	 */
	public void updateCategoryLessonList() {
		if (model == null) {
			return;
		}
		// Update the category list
		List<String> cats = model.getCategories();
		Object selectedItem = vocabEditor.jListCategories.getSelectedValue();
		modelCategories.clear();
		Collections.sort(cats);
		for (String cat : cats) {
			if (!modelCategories.contains(cat)) {
				modelCategories.addElement(cat);
			}
		}
		if (selectedItem != null) {
			vocabEditor.jListCategories.setSelectedValue(selectedItem, true);
		}

		// Update the lesson list
		List<Integer> lessons = model.getLessons();
		Collections.sort(lessons);
		selectedItem = vocabEditor.jListLessons.getSelectedValue();
		modelLessons.clear();
		for (Integer lesson : lessons) {
			if (!modelLessons.contains(lessons)) {
				modelLessons.addElement(lesson);
			}
		}
		if (selectedItem != null) {
			vocabEditor.jListLessons.setSelectedValue(selectedItem, true);
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
		Object selectedItem = vocabEditor.jListVocabulary.getSelectedValue();
		// TODO get selected indices
		int selectedIndex = vocabEditor.jListVocabulary.getSelectedIndex();
		vocabulary = items;
		modelVocabulary.clear();
		// Sort vocabulary
		// TODO should I sort differently depending on display mode?
		Collections.sort(vocabulary, new EnglishComparator());
		// Populate list with the English words
		VocabDisplayMode mode = ((VocabDisplayMode) vocabEditor.jComboBoxVocabDisplay.getSelectedItem());
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
			vocabEditor.jListVocabulary.setSelectedValue(selectedItem, true);
			// Check if selection was successful
			if (!selectedItem.equals(vocabEditor.jListVocabulary.getSelectedValue())) {
				// If not, try using index
				// This is the case either when the item has been removed,
				// renamed(?), or switching between display modes.
				vocabEditor.jListVocabulary.setSelectedIndex(selectedIndex);
			}
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
		// Try to maintain selection
		Object selectedItem = vocabEditor.jListLessons.getSelectedValue();
		modelVocabulary.clear();
		vocabulary = model.getVocabItems(lesson);
		for (VocabItem item : vocabulary) {
			modelVocabulary.addElement(item.getEnglish());
		}
		if (selectedItem != null) {
			vocabEditor.jListLessons.setSelectedValue(selectedItem, true);
		}
		setFileModified(false);
	}

	/**
	 * Handle list selection changes.
	 *
	 * @param lse
	 */
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		// TODO
		JList src = (JList) evt.getSource();

		// category change
		if (src.equals(vocabEditor.jListCategories)) {
			if (!evt.getValueIsAdjusting() && src.getSelectedValue() != null) {
				// Switch between showing categories or lessons
				currentCat = src.getSelectedValue().toString();
				if (model == null || currentCat == null) {
					return;
				}
				updateVocabularyList(currentCat);
			}
		}
		// lesson change
		else if (src.equals(vocabEditor.jListLessons)) {
			if (!evt.getValueIsAdjusting()) {
				Object selObject = src.getSelectedValue();
				if (selObject == null) {
					currentLesson = -1;
					return;
				}
				currentLesson = Integer.parseInt(selObject.toString(), 10);
				// Show vocabulary from given lesson
				updateVocabularyList(currentLesson);
			}
		}
		else if (src.equals(vocabEditor.jListVocabulary)) {
			// vocab selection
			if (evt.getValueIsAdjusting()) {
				return;
			}
			int[] sel = src.getSelectedIndices();
			if (sel.length > 0) {
//				selectedVocabIndex = sel[0];
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
