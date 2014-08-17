/*
 *  JapaneseVocabEditorPanel.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeListener;
import psyberchi.app.japanesevocabjsoneditor.model.VocabItem;

/**
 * A JPanel that shows the contents of a VocabItem and allows editing its
 * values. When fields are modified and lose focus they fire property change
 * events that give the old and new values.
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabEditorPanel extends javax.swing.JPanel {
	/**
	 * Property for when the English field is modified.
	 */
	public static final String PROP_MODIFIED_ENGLISH = "MODIFIED_ENGLISH";
	/**
	 * Property for when the romaji field is modified.
	 */
	public static final String PROP_MODIFIED_ROMAJI = "MODIFIED_ROMAJI";
	/**
	 * Property for when the kana field is modified.
	 */
	public static final String PROP_MODIFIED_KANA = "MODIFIED_KANA";
	/**
	 * Property for when the kanji field is modified.
	 */
	public static final String PROP_MODIFIED_KANJI = "MODIFIED_KANJI";
	/**
	 * Property for when the lesson field is modified.
	 */
	public static final String PROP_MODIFIED_LESSON = "MODIFIED_LESSON";
	/**
	 * The currently set VocabItem in the panel.
	 */
	private VocabItem vocabItem = null;
	private FocusListener focusListener;
	private ChangeListener changeListener;

	/**
	 * Constructor.
	 */
	public JapaneseVocabEditorPanel() {
		initComponents();
		focusListener = new FocusAdapter() {
		};
		addListeners(); // ???
	}

	/**
	 * Constructor that takes a FocusListener to be used as a MVC controller.
	 *
	 * @param focusHandler
	 */
	public JapaneseVocabEditorPanel(FocusListener focusHandler, ChangeListener changeHandler) {
		initComponents();
		focusListener = focusHandler;
		changeListener = changeHandler;
		addListeners();
	}

	private void addListeners() {
		jTextFieldEnglish.addFocusListener(focusListener);
		jTextFieldRomaji.addFocusListener(focusListener);
		jTextFieldKana.addFocusListener(focusListener);
		jTextFieldKanji.addFocusListener(focusListener);
		jSpinnerLesson.addChangeListener(null);
	}

	/**
	 * Resets all fields to empty or default values.
	 */
	public void clearPanel() {
		jTextFieldEnglish.setText("");
		jTextFieldRomaji.setText("");
		jTextFieldKana.setText("");
		jTextFieldKanji.setText("");
		jSpinnerLesson.setValue(0);
	}

	/**
	 * Returns the currently set VocabItem on the panel.
	 *
	 * @return currently set VocabItem.
	 */
	public VocabItem getVocabItem() {
		return vocabItem;
	}

	/**
	 * Sets the current VocabItem for the panel and fills in all the fields.
	 *
	 * @param item VocabItem to use to fill out fields.
	 * @return false if VocabItem null, true otherwise.
	 */
	public boolean setVocabItem(VocabItem item) {
		if (item == null) {
			return false;
		}
		vocabItem = item;
		jTextFieldEnglish.setText(item.getEnglish());
		jTextFieldRomaji.setText(item.getRomaji());
		jTextFieldKana.setText(item.getKana());
		jTextFieldKanji.setText(item.getKanji());
		jSpinnerLesson.getModel().setValue(item.getLesson());
		return true;
	}

	/**
	 * Enables/Disables each field individually.
	 *
	 * @param enable whether to enable or disable.
	 */
	@Override
	public void setEnabled(boolean enable) {
		// Assuming all controls have same enabled state
		// Only change enabled state if it is in fact changing
		if (jTextFieldEnglish.isEnabled() != enable) {
			jTextFieldEnglish.setEnabled(enable);
			jTextFieldRomaji.setEnabled(enable);
			jTextFieldKana.setEnabled(enable);
			jTextFieldKanji.setEnabled(enable);
			jSpinnerLesson.setEnabled(enable);
		}
	}

	/**
	 * Retrieve the text from the English field.
	 *
	 * @return String of the English text field.
	 */
	public String getEnglish() {
		return jTextFieldEnglish.getText();
	}

	/**
	 * Sets the text for the English field. A null clears the field.
	 *
	 * @param str String to use to fill the field.
	 */
	public void setEnglish(String str) {
		vocabItem.setEnglish(str);
		jTextFieldEnglish.setText(str);
	}

	/**
	 * Retrieve the text from the Romaji field.
	 *
	 * @return String of the Romaji text field.
	 */
	public String getRomaji() {
		return jTextFieldRomaji.getText();
	}

	/**
	 * Sets the text for the Romaji field. A null clears the field.
	 *
	 * @param str String to use to fill the field.
	 */
	public void setRomaji(String str) {
		vocabItem.setRomaji(str);
		jTextFieldRomaji.setText(str);
	}

	/**
	 * Retrieve the text from the Kana field.
	 *
	 * @return String of the Kana text field.
	 */
	public String getKana() {
		return jTextFieldKana.getText();
	}

	/**
	 * Sets the text for the Kana field. A null clears the field.
	 *
	 * @param str String to use to fill the field.
	 */
	public void setKana(String str) {
		vocabItem.setKana(str);
		jTextFieldKana.setText(str);
	}

	/**
	 * Retrieve the text from the Kanji field.
	 *
	 * @return String of the Kanji text field.
	 */
	public String getKanji() {
		return jTextFieldKanji.getText();
	}

	/**
	 * Sets the text for the Kanji field. A null clears the field.
	 *
	 * @param str String to use to fill the field.
	 */
	public void setKanji(String str) {
		vocabItem.setKanji(str);
		jTextFieldKanji.setText(str);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelEnglish = new javax.swing.JPanel();
        jTextFieldEnglish = new javax.swing.JTextField();
        jLabelEnglish = new javax.swing.JLabel();
        jPanelRomaji = new javax.swing.JPanel();
        jTextFieldRomaji = new javax.swing.JTextField();
        jLabelRomaji = new javax.swing.JLabel();
        jPanelKana = new javax.swing.JPanel();
        jTextFieldKana = new javax.swing.JTextField();
        jLabelKana = new javax.swing.JLabel();
        jPanelKanji = new javax.swing.JPanel();
        jTextFieldKanji = new javax.swing.JTextField();
        jLabelKanji = new javax.swing.JLabel();
        jPanelLesson = new javax.swing.JPanel();
        jLabelLesson = new javax.swing.JLabel();
        jPanelLessonSpinner = new javax.swing.JPanel();
        jSpinnerLesson = new javax.swing.JSpinner();

        setMinimumSize(new java.awt.Dimension(160, 155));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelEnglish.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        jPanelEnglish.setLayout(new java.awt.BorderLayout());

        jTextFieldEnglish.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextFieldEnglish.setText("english");
        jTextFieldEnglish.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldEnglishFocusLost(evt);
            }
        });
        jPanelEnglish.add(jTextFieldEnglish, java.awt.BorderLayout.CENTER);

        jLabelEnglish.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelEnglish.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelEnglish.setText("English:");
        jLabelEnglish.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanelEnglish.add(jLabelEnglish, java.awt.BorderLayout.WEST);

        add(jPanelEnglish);

        jPanelRomaji.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        jPanelRomaji.setLayout(new java.awt.BorderLayout());

        jTextFieldRomaji.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextFieldRomaji.setText("romaji");
        jTextFieldRomaji.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRomajiFocusLost(evt);
            }
        });
        jPanelRomaji.add(jTextFieldRomaji, java.awt.BorderLayout.CENTER);

        jLabelRomaji.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRomaji.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelRomaji.setText("Romaji:");
        jLabelRomaji.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanelRomaji.add(jLabelRomaji, java.awt.BorderLayout.WEST);

        add(jPanelRomaji);

        jPanelKana.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        jPanelKana.setLayout(new java.awt.BorderLayout());

        jTextFieldKana.setFont(new java.awt.Font("Serif", 0, 22)); // NOI18N
        jTextFieldKana.setText("かな");
        jTextFieldKana.setMargin(new java.awt.Insets(2, 0, 0, 0));
        jTextFieldKana.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldKanaFocusLost(evt);
            }
        });
        jPanelKana.add(jTextFieldKana, java.awt.BorderLayout.CENTER);

        jLabelKana.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelKana.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelKana.setText("Kana:");
        jLabelKana.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanelKana.add(jLabelKana, java.awt.BorderLayout.WEST);

        add(jPanelKana);

        jPanelKanji.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        jPanelKanji.setLayout(new java.awt.BorderLayout());

        jTextFieldKanji.setFont(new java.awt.Font("Serif", 0, 22)); // NOI18N
        jTextFieldKanji.setText("感じ");
        jTextFieldKanji.setMargin(new java.awt.Insets(2, 0, 0, 0));
        jTextFieldKanji.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldKanjiFocusLost(evt);
            }
        });
        jPanelKanji.add(jTextFieldKanji, java.awt.BorderLayout.CENTER);

        jLabelKanji.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelKanji.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelKanji.setText("Kanji:");
        jLabelKanji.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanelKanji.add(jLabelKanji, java.awt.BorderLayout.WEST);

        add(jPanelKanji);

        jPanelLesson.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        jPanelLesson.setLayout(new java.awt.BorderLayout());

        jLabelLesson.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelLesson.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelLesson.setText("Lesson:");
        jLabelLesson.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanelLesson.add(jLabelLesson, java.awt.BorderLayout.WEST);

        jPanelLessonSpinner.setLayout(new java.awt.BorderLayout());

        jSpinnerLesson.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jSpinnerLesson.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        jSpinnerLesson.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerLessonStateChanged(evt);
            }
        });
        jPanelLessonSpinner.add(jSpinnerLesson, java.awt.BorderLayout.WEST);

        jPanelLesson.add(jPanelLessonSpinner, java.awt.BorderLayout.CENTER);

        add(jPanelLesson);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEnglishFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldEnglishFocusLost
		if (vocabItem == null) {
			return;
		}
		String ov = vocabItem.getEnglish();
		String nv = jTextFieldEnglish.getText();
		if (!ov.equals(nv)) {
			vocabItem.setEnglish(nv);
			firePropertyChange(PROP_MODIFIED_ENGLISH, ov, nv);
		}
    }//GEN-LAST:event_jTextFieldEnglishFocusLost

    private void jTextFieldRomajiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRomajiFocusLost
		if (vocabItem == null) {
			return;
		}
		String ov = vocabItem.getRomaji();
		String nv = jTextFieldRomaji.getText();
		if (!ov.equals(nv)) {
			vocabItem.setRomaji(nv);
			firePropertyChange(PROP_MODIFIED_ROMAJI, ov, nv);
		}
    }//GEN-LAST:event_jTextFieldRomajiFocusLost

    private void jTextFieldKanaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldKanaFocusLost
		if (vocabItem == null) {
			return;
		}
		String ov = vocabItem.getKana();
		String nv = jTextFieldKana.getText();
		if (!ov.equals(nv)) {
			vocabItem.setKana(nv);
			firePropertyChange(PROP_MODIFIED_KANA, ov, nv);
		}
    }//GEN-LAST:event_jTextFieldKanaFocusLost

    private void jTextFieldKanjiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldKanjiFocusLost
		if (vocabItem == null) {
			return;
		}
		String ov = vocabItem.getKanji();
		String nv = jTextFieldKanji.getText();
		if (!ov.equals(nv)) {
			vocabItem.setKanji(nv);
			firePropertyChange(PROP_MODIFIED_KANJI, ov, nv);
		}
    }//GEN-LAST:event_jTextFieldKanjiFocusLost

    private void jSpinnerLessonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerLessonStateChanged
		if (vocabItem == null) {
			return;
		}
		int nv = ((javax.swing.SpinnerNumberModel) jSpinnerLesson.getModel()).getNumber().intValue();
		int ov = vocabItem.getLesson();
		if (ov != nv) {
			vocabItem.setLesson(nv);
			firePropertyChange(PROP_MODIFIED_LESSON, ov, nv);
		}
    }//GEN-LAST:event_jSpinnerLessonStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelEnglish;
    private javax.swing.JLabel jLabelKana;
    private javax.swing.JLabel jLabelKanji;
    private javax.swing.JLabel jLabelLesson;
    private javax.swing.JLabel jLabelRomaji;
    private javax.swing.JPanel jPanelEnglish;
    private javax.swing.JPanel jPanelKana;
    private javax.swing.JPanel jPanelKanji;
    private javax.swing.JPanel jPanelLesson;
    private javax.swing.JPanel jPanelLessonSpinner;
    private javax.swing.JPanel jPanelRomaji;
    public javax.swing.JSpinner jSpinnerLesson;
    public javax.swing.JTextField jTextFieldEnglish;
    public javax.swing.JTextField jTextFieldKana;
    public javax.swing.JTextField jTextFieldKanji;
    public javax.swing.JTextField jTextFieldRomaji;
    // End of variables declaration//GEN-END:variables
}
