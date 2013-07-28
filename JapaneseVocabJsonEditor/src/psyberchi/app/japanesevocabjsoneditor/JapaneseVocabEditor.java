/*
 *  JapaneseVocabEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
	 * A container for all of the current set of vocabulary.
	 */
	private ArrayList<VocabItem> vocabulary = new ArrayList<VocabItem>();
	/**
	 * Whether the currently selected vocabulary item is modified.
	 */
	private boolean vocabModified = false;
	/**
	 * The ListModel for the category JList.
	 */
	private DefaultListModel modelCategories = null;
	/**
	 * The ListModel for the lessons JList.
	 */
	private DefaultListModel modelLessons = null;
	/**
	 * The ListModel for the romaji JList.
	 */
	private DefaultListModel modelRomaji = null;
	/**
	 * The currently open fileOpened.
	 */
	private File fileOpened = null;
	/**
	 * Index of selected vocabulary.
	 */
	private int selectedIndex = -1;
	/**
	 * The current category chosen.
	 */
	private String currentCategory;
	/**
	 * The vocabulary model holding categories and vocabulary.
	 */
	private VocabModel model = null;

	/**
	 * Creates new form JapaneseVocabEditor
	 */
	public JapaneseVocabEditor() {
		initComponents();
		// Capture models
		modelCategories = (DefaultListModel) jListCategories.getModel();
		modelLessons = (DefaultListModel) jListLessons.getModel();
		modelRomaji = (DefaultListModel) jListRomaji.getModel();
		japaneseVocabEditorPanel.addPropertyChangeListener(this);
		clearGUI();
		setGUIEnabled(false);
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
			// Make sure it's sorted after add
			updateGUI();
			// Highlight the new category
			jListCategories.setSelectedValue(cat, true);
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
			if (modelRomaji.contains(resp)) {
				JOptionPane.showMessageDialog(this, "That vocabulary item already exists.");
				return;
			}
			else if (resp.startsWith("#")) {
				JOptionPane.showMessageDialog(this, "Only the category label can start with #.");
				return;
			}
			if (addVocabulary(new VocabItem(resp, "", "", ""))) {
				jListRomaji.setSelectedValue(resp, true);
			}
		}
	}

	/**
	 * Adds a new {@link VocabItem} to currently selected category.
	 * @param item
	 * @return
	 */
	private boolean addVocabulary(VocabItem item) {
		if (model == null) {
			return false;
		}
		if (!vocabulary.contains(item)) {
			if (model.addVocabItem(currentCategory, item)) {
				setVocabulary(currentCategory);
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
		modelRomaji.clear();
		japaneseVocabEditorPanel.clearPanel();
		currentCategory = null;
		selectedIndex = -1;
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
		try {
			fileOpened = file;
			model = JsonVocabIO.readJsonFile(fileOpened);
			clearGUI();
			updateGUI();
			setGUIEnabled(true);
			return true;
		}
		catch (FileNotFoundException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		catch (ParseException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Sets the vocabulary list with vocabulary from the given category. If the
	 * category is null or does not exist, the list will not be updated.
	 *
	 * @param category
	 */
	private void setVocabulary(String category) {
		// If no model or category, clear out vocabulary list
		if (model == null || category == null) {
			modelRomaji.clear();
			return;
		}
		ArrayList<VocabItem> items = model.getVocabItems(category);
		if (items == null) {
			return;
		}
		vocabulary = items;
		modelRomaji.clear();
		// Sort vocabulary
		Collections.sort(vocabulary, new EnglishComparator());
		// Populate list with the English words
		for (VocabItem item : vocabulary) {
			modelRomaji.addElement(item.getEnglish());
		}
		vocabModified = false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		if (JapaneseVocabEditorPanel.PROP_MODIFIED.equals(pce.getPropertyName())) {
			String ov = pce.getOldValue().toString();
			String nv = pce.getNewValue().toString();
			boolean renamed = false;

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
				updateGUI();
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
			if (modelRomaji.contains(nv) && !renamed) {
				JOptionPane.showMessageDialog(this, "A vocabulary item with that English already exists.");
				// Put the value back to the previous one
				japaneseVocabEditorPanel.setEnglish(ov);
				return;
			}

			vocabModified = true;

			if (selectedIndex > -1) {
				vocabulary.get(selectedIndex).setEnglish(nv);
			}
			setVocabulary(currentCategory);
			jListRomaji.setSelectedValue(nv, true);
		}
	}

	private void setGUIEnabled(boolean enable) {
		jComboBoxCategoryLesson.setEnabled(enable);
		jListCategories.setEnabled(enable);
		jListLessons.setEnabled(enable);
		jListRomaji.setEnabled(enable);
		jButtonCategoryAdd.setEnabled(enable);
		jButtonCategoryDelete.setEnabled(enable);
		jButtonRomajiAdd.setEnabled(enable);
		jButtonRomajiDelete.setEnabled(enable);
		jButtonRomajiMove.setEnabled(enable);
		japaneseVocabEditorPanel.setEnabled(enable);
	}

	/**
	 * Updates the GUI, populating the category list from the model.
	 */
	private void updateGUI() {
		if (model == null) {
			return;
		}
		// Update the category list
		String[] cats = model.getCategories();
		modelCategories.clear();
		Arrays.sort(cats);
		for (String cat : cats) {
			if (!modelCategories.contains(cat)) {
				modelCategories.addElement(cat);
			}
		}

		// Update the lesson list
		List<Integer> lessons = model.getLessons();
		Collections.sort(lessons);
		modelLessons.clear();
		for (Integer lesson : lessons) {
			if (!modelLessons.contains(lessons)) {
				modelLessons.addElement(lesson);
			}
		}
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
        jToolBar = new javax.swing.JToolBar();
        jButtonToolbarOpen = new javax.swing.JButton();
        jButtonToolbarSave = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        jPanelCategories = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBoxCategoryLesson = new javax.swing.JComboBox();
        jPanelCategoryLesson = new javax.swing.JPanel();
        jScrollPaneCategories = new javax.swing.JScrollPane();
        jListCategories = new javax.swing.JList();
        jScrollPaneLessons = new javax.swing.JScrollPane();
        jListLessons = new javax.swing.JList();
        jPanelCategoryButtons = new javax.swing.JPanel();
        jButtonCategoryAdd = new javax.swing.JButton();
        jButtonCategoryDelete = new javax.swing.JButton();
        jPanelRomanji = new javax.swing.JPanel();
        jScrollPaneRomaji = new javax.swing.JScrollPane();
        jListRomaji = new javax.swing.JList();
        jPanelRomajiButtons = new javax.swing.JPanel();
        jButtonRomajiAdd = new javax.swing.JButton();
        jButtonRomajiDelete = new javax.swing.JButton();
        jButtonRomajiMove = new javax.swing.JButton();
        japaneseVocabEditorPanel = new psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorPanel();
        jPanelButtons = new javax.swing.JPanel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        jFileChooserOpen.setDialogTitle("Select JSON file");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 500));

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonToolbarOpen.setText("Open");
        jButtonToolbarOpen.setFocusable(false);
        jButtonToolbarOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolbarOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolbarOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonToolbarOpenActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonToolbarOpen);

        jButtonToolbarSave.setText("Save");
        jButtonToolbarSave.setFocusable(false);
        jButtonToolbarSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolbarSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonToolbarSave);

        getContentPane().add(jToolBar, java.awt.BorderLayout.NORTH);

        jPanelMain.setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerLocation(220);
        jSplitPane.setDividerSize(8);
        jSplitPane.setResizeWeight(0.5);

        jPanelCategories.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jComboBoxCategoryLesson.setModel(new DefaultComboBoxModel(new String[]{"Categories", "Lessons"}));
        jComboBoxCategoryLesson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCategoryLessonActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBoxCategoryLesson, java.awt.BorderLayout.CENTER);

        jPanelCategories.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanelCategoryLesson.setLayout(new java.awt.CardLayout());

        jListCategories.setModel(new DefaultListModel());
        jListCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCategories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListCategoriesValueChanged(evt);
            }
        });
        jScrollPaneCategories.setViewportView(jListCategories);

        jPanelCategoryLesson.add(jScrollPaneCategories, "categories");

        jListLessons.setModel(new DefaultListModel());
        jListLessons.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneLessons.setViewportView(jListLessons);

        jPanelCategoryLesson.add(jScrollPaneLessons, "lessons");

        jPanelCategories.add(jPanelCategoryLesson, java.awt.BorderLayout.CENTER);

        jPanelCategoryButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonCategoryAdd.setText("+");
        jButtonCategoryAdd.setIconTextGap(0);
        jButtonCategoryAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCategoryAddActionPerformed(evt);
            }
        });
        jPanelCategoryButtons.add(jButtonCategoryAdd);

        jButtonCategoryDelete.setText("-");
        jButtonCategoryDelete.setIconTextGap(0);
        jButtonCategoryDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanelCategoryButtons.add(jButtonCategoryDelete);

        jPanelCategories.add(jPanelCategoryButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setLeftComponent(jPanelCategories);

        jPanelRomanji.setLayout(new java.awt.BorderLayout());

        jListRomaji.setModel(new DefaultListModel());
        jListRomaji.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListRomajiValueChanged(evt);
            }
        });
        jScrollPaneRomaji.setViewportView(jListRomaji);

        jPanelRomanji.add(jScrollPaneRomaji, java.awt.BorderLayout.CENTER);

        jPanelRomajiButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonRomajiAdd.setText("+");
        jButtonRomajiAdd.setIconTextGap(0);
        jButtonRomajiAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRomajiAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRomajiAddActionPerformed(evt);
            }
        });
        jPanelRomajiButtons.add(jButtonRomajiAdd);

        jButtonRomajiDelete.setText("-");
        jButtonRomajiDelete.setIconTextGap(0);
        jButtonRomajiDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRomajiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRomajiDeleteActionPerformed(evt);
            }
        });
        jPanelRomajiButtons.add(jButtonRomajiDelete);

        jButtonRomajiMove.setText("mv");
        jPanelRomajiButtons.add(jButtonRomajiMove);

        jPanelRomanji.add(jPanelRomajiButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane.setRightComponent(jPanelRomanji);

        jPanelMain.add(jSplitPane, java.awt.BorderLayout.CENTER);

        japaneseVocabEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Vocabulary Editor"));
        jPanelMain.add(japaneseVocabEditorPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);
        getContentPane().add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("File");
        jMenuBar.add(jMenu1);

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
			setVocabulary(currentCategory);
		}
    }//GEN-LAST:event_jListCategoriesValueChanged

    private void jComboBoxCategoryLessonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCategoryLessonActionPerformed
		String pick = jComboBoxCategoryLesson.getSelectedItem().toString();
		((CardLayout) jPanelCategoryLesson.getLayout()).show(jPanelCategoryLesson, pick.toLowerCase());
		updateGUI();
    }//GEN-LAST:event_jComboBoxCategoryLessonActionPerformed

    private void jListRomajiValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListRomajiValueChanged
		if (evt.getValueIsAdjusting()) {
			return;
		}
		int sel = jListRomaji.getSelectedIndex();
		if (vocabModified) {
			// warn about modification
//			JOptionPane.showMessageDialog(this, "Vocabuary item modified. Save changes.");
//			return;
		}
		selectedIndex = sel;
		if (selectedIndex > -1) {
			japaneseVocabEditorPanel.setVocabItem(vocabulary.get(sel));
		}
		else {
			japaneseVocabEditorPanel.clearPanel();
		}
		vocabModified = false;
    }//GEN-LAST:event_jListRomajiValueChanged

    private void jButtonToolbarOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonToolbarOpenActionPerformed
		// Prompt user for JSON fileOpened
		int resp = jFileChooserOpen.showOpenDialog(this);
		if (JFileChooser.APPROVE_OPTION == resp) {
			openFile(jFileChooserOpen.getSelectedFile());
		}
    }//GEN-LAST:event_jButtonToolbarOpenActionPerformed

    private void jButtonCategoryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCategoryAddActionPerformed
		if (model == null) {
			return;
		}
		String resp = JOptionPane.showInputDialog(this, "New Category:", "New Category", JOptionPane.QUESTION_MESSAGE);
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
        int[] sels = jListRomaji.getSelectedIndices();
		if (sels.length > 0) {
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
				return;
			}
			// Remove from end to start the selected items
			for (int a = sels.length - 1; a >= 0; a--) {
				String currItem = jListRomaji.getModel().getElementAt(sels[a]).toString();
				if (currItem.startsWith("#")) {
					// No removing category label item
					JOptionPane.showMessageDialog(this, "Cannot delete category label item");
					continue;
				}
				else {
					model.removeVocabItem(currentCategory, vocabulary.get(sels[a]));
				}
			}
			setVocabulary(currentCategory);
		}
    }//GEN-LAST:event_jButtonRomajiDeleteActionPerformed

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
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
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
		//</editor-fold>

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
    private javax.swing.JButton jButtonToolbarOpen;
    private javax.swing.JButton jButtonToolbarSave;
    private javax.swing.JComboBox jComboBoxCategoryLesson;
    private javax.swing.JFileChooser jFileChooserOpen;
    private javax.swing.JList jListCategories;
    private javax.swing.JList jListLessons;
    private javax.swing.JList jListRomaji;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCategories;
    private javax.swing.JPanel jPanelCategoryButtons;
    private javax.swing.JPanel jPanelCategoryLesson;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelRomajiButtons;
    private javax.swing.JPanel jPanelRomanji;
    private javax.swing.JScrollPane jScrollPaneCategories;
    private javax.swing.JScrollPane jScrollPaneLessons;
    private javax.swing.JScrollPane jScrollPaneRomaji;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JToolBar jToolBar;
    private psyberchi.app.japanesevocabjsoneditor.JapaneseVocabEditorPanel japaneseVocabEditorPanel;
    // End of variables declaration//GEN-END:variables

}
