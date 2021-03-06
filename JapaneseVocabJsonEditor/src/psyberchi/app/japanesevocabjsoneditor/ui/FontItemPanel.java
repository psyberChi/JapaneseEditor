/*
 *  FontItemPanel.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Kendall Conrad
 */
public class FontItemPanel extends javax.swing.JPanel {
	private boolean fontFamilyVisible = true;
	private boolean fontSizeVisible = true;
	private static Font[] availableFonts;
	private static ArrayList<String> availableFontFamilies = null;
	private static final Object fontLock = new Object();
	private String defaultFontFamily = "serif";
	private int defaultFontSize = 16;
	private String fontLabel = "Font";

	/**
	 * Creates new form FontItemPanel
	 */
	public FontItemPanel() {
		initComponents();
		loadFontFamily();
	}

	public FontItemPanel(String label) {
		initComponents();
		setFontLabel(label);
		loadFontFamily();
	}

	public FontItemPanel(String label, boolean hasFontFamily, boolean hasFontSize) {
		initComponents();
		setFontLabel(label);
		setFontFamilyVisible(hasFontFamily);
//		if (!(fontFamilyVisible = hasFontFamily)) {
//			jComboBoxFontFamily.setVisible(false);
//		}
		if (!(fontSizeVisible = hasFontSize)) {
			jSpinnerFontSize.setVisible(false);
		}
		loadFontFamily();
	}

	public Font getFontItem() {
		String fam = defaultFontFamily;
		int sz = defaultFontSize;
		if (isFontFamilyVisible()) {
			fam = jComboBoxFontFamily.getSelectedItem().toString();
		}
		if (isFontSizeVisible()) {
			sz = ((SpinnerNumberModel) jSpinnerFontSize.getModel()).getNumber().intValue();
		}
		return new Font(fam, Font.PLAIN, sz);
	}

	private void loadFontFamily() {
		if (!isFontFamilyVisible()) {
			return;
		}
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		synchronized (fontLock) {
			if (availableFontFamilies == null) {
				availableFonts = e.getAllFonts();
				availableFontFamilies = new ArrayList<>();
				int a = 0;
				for (Font f : availableFonts) {
					if (!availableFontFamilies.contains(f.getFamily())) {
						availableFontFamilies.add(f.getFamily());
					}
				}
			}
		}
		jComboBoxFontFamily.setModel(new DefaultComboBoxModel(availableFontFamilies.toArray()));
	}

	public void setFontItem(Font f) {
		if (isFontFamilyVisible()) {
			jComboBoxFontFamily.setSelectedItem(f.getFamily());
		}
		if (isFontSizeVisible()) {
			jSpinnerFontSize.setValue(f.getSize());
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

        jLabel = new javax.swing.JLabel();
        jComboBoxFontFamily = new javax.swing.JComboBox();
        jSpinnerFontSize = new javax.swing.JSpinner();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel.setText("Label:");
        jLabel.setPreferredSize(new java.awt.Dimension(100, 16));
        add(jLabel);

        jComboBoxFontFamily.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Font Family" }));
        add(jComboBoxFontFamily);

        jSpinnerFontSize.setModel(new javax.swing.SpinnerNumberModel(16, 8, 42, 1));
        add(jSpinnerFontSize);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox jComboBoxFontFamily;
    public javax.swing.JLabel jLabel;
    public javax.swing.JSpinner jSpinnerFontSize;
    // End of variables declaration//GEN-END:variables

	/**
	 * @return the fontLabel
	 */
	public String getFontLabel() {
		return fontLabel;
	}

	/**
	 * @param fontLabel the fontLabel to set
	 */
	public void setFontLabel(String fontLabel) {
		this.fontLabel = fontLabel;
		jLabel.setText(fontLabel);
	}

	/**
	 * @return the fontFamilyVisible
	 */
	public boolean isFontFamilyVisible() {
		return fontFamilyVisible;
	}

	/**
	 * @param fontFamilyVisible the fontFamilyVisible to set
	 */
	public void setFontFamilyVisible(boolean fontFamilyVisible) {
		this.fontFamilyVisible = fontFamilyVisible;
		jComboBoxFontFamily.setVisible(fontFamilyVisible);
	}

	/**
	 * @return the fontSizeVisible
	 */
	public boolean isFontSizeVisible() {
		return fontSizeVisible;
	}

	/**
	 * @param fontSizeVisible the fontSizeVisible to set
	 */
	public void setFontSizeVisible(boolean fontSizeVisible) {
		this.fontSizeVisible = fontSizeVisible;
		jSpinnerFontSize.setVisible(fontSizeVisible);
	}
}
