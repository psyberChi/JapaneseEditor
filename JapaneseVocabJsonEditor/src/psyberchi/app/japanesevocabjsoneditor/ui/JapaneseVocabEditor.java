/*
 *  JapaneseVocabEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.ui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.KeyStroke;
import psyberchi.app.japanesevocabjsoneditor.controller.JapaneseVocabEditorController;
import psyberchi.app.japanesevocabjsoneditor.controller.JapaneseVocabEditorController.EditorActions;
import psyberchi.app.japanesevocabjsoneditor.controller.JapaneseVocabEditorController.SortMode;
import psyberchi.app.japanesevocabjsoneditor.controller.JapaneseVocabEditorController.VocabDisplayMode;

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
	 * The main title of the application.
	 */
	public static final String APP_TITLE = "Japanese Vocabulary Editor";
	/**
	 * The MVC controller for the GUI.
	 */
	private JapaneseVocabEditorController controller;

	/**
	 * Creates new form JapaneseVocabEditor
	 */
	public JapaneseVocabEditor() {
		controller = new JapaneseVocabEditorController(this);
		initComponents();
		addListeners();
		controller.initComponents();
		japaneseVocabEditorPanel.addPropertyChangeListener(controller);
		controller.clearGUI();
		controller.setGUIEnabled(false);
	}

	/**
	 * Add all of the component listeners to the controller.
	 */
	private void addListeners() {
		// JList change listeners
		listSelectorCategoryLesson.getSelector().setActionCommand(EditorActions.SortModeChange.name());
		listSelectorCategoryLesson.getSelector().addItemListener(controller);
		listSelectorCategoryLesson.getSelector().addActionListener(controller);
		listSelectorCategoryLesson.getList().addListSelectionListener(controller);

		// @todo probably wrong action command
		listSelectorVocabulary.getSelector().setActionCommand(EditorActions.VocabSelect.name());
		listSelectorVocabulary.getSelector().addItemListener(controller);
		listSelectorVocabulary.getSelector().addActionListener(controller);
		listSelectorVocabulary.getList().addListSelectionListener(controller);

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
		jMenuItemPreferences.addActionListener(controller);
		jMenuItemClose.addActionListener(controller);
		jMenuItemExit.addActionListener(controller);
		// JMenuItem Vocabulary action listeners
		jMenuItemCatAdd.addActionListener(controller);
		jMenuItemCatDelete.addActionListener(controller);
		jMenuItemVocabAdd.addActionListener(controller);
		jMenuItemVocabDelete.addActionListener(controller);
		jMenuItemVocabMove.addActionListener(controller);

		// TODO Preferences change listener ???
	}

	/**
	 * Load user preferences.
	 */
	private void loadPreferences() {
//		try {
//			listSelectorCategoryLesson.getSelector().setSelectedIndex(
//					prefs.getInt(EditorPrefs.SortMode.toString(), 0));
//			listSelectorVocabulary.getSelector().setSelectedIndex(
//					prefs.getInt(EditorPrefs.VocabDisplayMode.toString(), 0));
//		}
//		catch (Exception ex) {
//			logger.log(Level.SEVERE, "Problem loading preference: {0}", ex.getLocalizedMessage());
//		}
	}

	/**
	 * Saves user settings.
	 */
	private void savePreferences() {
//		prefs.putInt(EditorPrefs.SortMode.toString(),
//				listSelectorCategoryLesson.getSelector().getSelectedIndex());
//		prefs.putInt(EditorPrefs.VocabDisplayMode.toString(),
//				listSelectorVocabulary.getSelector().getSelectedIndex());
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
        jButtonCategoryAdd = new javax.swing.JButton();
        jButtonCategoryDelete = new javax.swing.JButton();
        jButtonVocabAdd = new javax.swing.JButton();
        jButtonVocabDelete = new javax.swing.JButton();
        jButtonVocabMove = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        listSelectorCategoryLesson = new ListSelector(new DefaultComboBoxModel(SortMode.values()), new DefaultListModel());
        listSelectorVocabulary = new ListSelector(new DefaultComboBoxModel(VocabDisplayMode.values()), new DefaultListModel());
        ;
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
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
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

        jButtonCategoryAdd.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonCategoryAdd.setText("+");
        jButtonCategoryAdd.setActionCommand("CatAdd");
        jButtonCategoryAdd.setIconTextGap(0);
        jButtonCategoryAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryAdd.setPreferredSize(new java.awt.Dimension(24, 20));

        jButtonCategoryDelete.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonCategoryDelete.setText("-");
        jButtonCategoryDelete.setActionCommand("DeleteCategory");
        jButtonCategoryDelete.setIconTextGap(0);
        jButtonCategoryDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCategoryDelete.setPreferredSize(new java.awt.Dimension(24, 20));

        jButtonVocabAdd.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabAdd.setText("+");
        jButtonVocabAdd.setActionCommand(EditorActions.VocabAdd.name());
        jButtonVocabAdd.setIconTextGap(0);
        jButtonVocabAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonVocabAdd.setPreferredSize(new java.awt.Dimension(24, 20));

        jButtonVocabDelete.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabDelete.setText("-");
        jButtonVocabDelete.setActionCommand(EditorActions.VocabDelete.name());
        jButtonVocabDelete.setIconTextGap(0);
        jButtonVocabDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonVocabDelete.setPreferredSize(new java.awt.Dimension(24, 20));

        jButtonVocabMove.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonVocabMove.setText("mv");
        jButtonVocabMove.setActionCommand(EditorActions.VocabMove.name());
        jButtonVocabMove.setComponentPopupMenu(jPopupMenuCategories);
        jButtonVocabMove.setIconTextGap(0);
        jButtonVocabMove.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonVocabMove.setPreferredSize(new java.awt.Dimension(30, 20));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Japanese Vocabulary Editor");
        setMinimumSize(new java.awt.Dimension(300, 300));
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
        jSplitPane.setLeftComponent(listSelectorCategoryLesson);
        listSelectorCategoryLesson.addButton(jButtonCategoryAdd);
        listSelectorCategoryLesson.addButton(jButtonCategoryDelete);
        jSplitPane.setRightComponent(listSelectorVocabulary);
        listSelectorVocabulary.addButton(jButtonVocabAdd);
        listSelectorVocabulary.addButton(jButtonVocabDelete);
        listSelectorVocabulary.addButton(jButtonVocabMove);

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
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemOpen.setText("Open File");
        jMenuItemOpen.setActionCommand(EditorActions.FileOpen.name());
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItemSave.setText("Save");
        jMenuItemSave.setActionCommand(EditorActions.FileSave.name());
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save as");
        jMenuItemSaveAs.setActionCommand(EditorActions.FileSaveAs.name());
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuItemPreferences.setText("Preferences");
        jMenuFile.add(jMenuItemPreferences);
        jMenuFile.add(jSeparator3);

        jMenuItemClose.setText("Close File");
        jMenuItemClose.setActionCommand(EditorActions.FileClose.name());
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
		loadPreferences();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		savePreferences();
    }//GEN-LAST:event_formWindowClosing
	/**
	 * @param args the command line arguments
	 */
//	public static void main(String args[]) {
//		SingleLineLogFormatter formatter = new SingleLineLogFormatter();
//		Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
//		while (loggerNames.hasMoreElements()) {
//			String loggerName = loggerNames.nextElement();
//			Logger.getLogger(loggerName).setLevel(logLevel);
//			// Set level for each handler
//			for (Handler handler : Logger.getLogger(loggerName).getHandlers()) {
//				handler.setLevel(logLevel);
//				handler.setFormatter(formatter);
//			}
//		}
//		Logger.getAnonymousLogger().log(Level.INFO, "Starting");
//
//		/* Set the Nimbus look and feel */
//		//<editor-fold-null defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//			/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//		 */
//		try {
//			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//				if ("Metal".equals(info.getName())) {
//					javax.swing.UIManager.setLookAndFeel(info.getClassName());
//					break;
//				}
//			}
//		}
//		catch (ClassNotFoundException ex) {
//			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		}
//		catch (InstantiationException ex) {
//			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		}
//		catch (IllegalAccessException ex) {
//			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		}
//		catch (javax.swing.UnsupportedLookAndFeelException ex) {
//			java.util.logging.Logger.getLogger(JapaneseVocabEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		}
//		//</editor-fold-null>
//
//		/* Create and display the form */
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				new JapaneseVocabEditor().setVisible(true);
//			}
//		});
//	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButtonCategoryAdd;
    public javax.swing.JButton jButtonCategoryDelete;
    public javax.swing.JButton jButtonVocabAdd;
    public javax.swing.JButton jButtonVocabDelete;
    public javax.swing.JButton jButtonVocabMove;
    private javax.swing.JFileChooser jFileChooserOpen;
    private javax.swing.JFileChooser jFileChooserSave;
    public javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    public javax.swing.JMenuItem jMenuItemCatAdd;
    public javax.swing.JMenuItem jMenuItemCatDelete;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    public javax.swing.JMenuItem jMenuItemVocabAdd;
    public javax.swing.JMenuItem jMenuItemVocabDelete;
    public javax.swing.JMenuItem jMenuItemVocabMove;
    private javax.swing.JMenu jMenuVocab;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelMain;
    public javax.swing.JPopupMenu jPopupMenuCategories;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane;
    public psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditorPanel japaneseVocabEditorPanel;
    public psyberchi.app.japanesevocabjsoneditor.ui.ListSelector listSelectorCategoryLesson;
    public psyberchi.app.japanesevocabjsoneditor.ui.ListSelector listSelectorVocabulary;
    // End of variables declaration//GEN-END:variables
}
