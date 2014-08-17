/*
 *  ListSelector.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.ui;

import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 *
 * @author Kendall Conrad
 */
public class ListSelector extends javax.swing.JPanel {
	private DefaultComboBoxModel selectorModel = new DefaultComboBoxModel();
	private DefaultListModel listModel = new DefaultListModel();
	private HashMap<String, JButton> buttonHash = new HashMap<>();

	/**
	 * Constructor.
	 */
	public ListSelector() {
		initComponents();
		postInit();
	}

	/**
	 * Constructor
	 *
	 * @param selectorModel
	 * @param listModel
	 */
	public ListSelector(DefaultComboBoxModel selectorModel, DefaultListModel listModel) {
		initComponents();
		this.selectorModel = selectorModel;
		this.listModel = listModel;
		postInit();
	}

	private void postInit() {
		jList.setModel(listModel);
		jComboBoxSelector.setModel(selectorModel);
	}

	/**
	 * Adds a button to the button area. The button must have an action command.
	 * If the panel already has a button with the same action command it will
	 * not be added.
	 *
	 * @param button
	 * @return true if successfully added button, false otherwise.
	 */
	public boolean addButton(JButton button) {
		if (button != null && !button.getActionCommand().isEmpty()) {
			if (buttonHash.containsKey(button.getActionCommand())) {
				return false;
			}
			buttonHash.put(button.getActionCommand(), button);
			jPanelButtons.add(button);
			return true;
		}
		return false;
	}

	public JList getList() {
		return jList;
	}

	public JComboBox getSelector() {
		return jComboBoxSelector;
	}

	/**
	 * Removes a button identified by its action command.
	 *
	 * @param actionCommand
	 * @return
	 */
	public boolean removeButton(String actionCommand) {
		if (actionCommand != null && buttonHash.containsKey(actionCommand)) {
			jPanelButtons.remove(buttonHash.get(actionCommand));
			buttonHash.remove(actionCommand);
			return true;
		}
		return false;
	}

	public void setListModel(DefaultListModel model) {
		// @todo handle null?
		listModel = model;
		jList.setModel(model);
	}

	public void setListItems(List<Object> list) {
		// @todo handle null?
		listModel.clear();
		for (Object o : list) {
			listModel.addElement(o);
		}
	}

	/**
	 * Sets the contents of the JComboBox.
	 *
	 * @param items a list of items to use for the listed items.
	 */
	public void setSelectorItems(Object[] items) {
		// TODO should we try to maintain any current selection?
		selectorModel.removeAllElements();
		for (Object o : items) {
			selectorModel.addElement(o);
		}
	}

	public void setSelectorModel(DefaultComboBoxModel model) {
		selectorModel = model;
		jComboBoxSelector.setModel(selectorModel);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelComboBox = new javax.swing.JPanel();
        jComboBoxSelector = new javax.swing.JComboBox();
        jPanelList = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();
        jPanelButtons = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanelComboBox.setLayout(new java.awt.BorderLayout());

        jPanelComboBox.add(jComboBoxSelector, java.awt.BorderLayout.CENTER);

        add(jPanelComboBox, java.awt.BorderLayout.NORTH);

        jPanelList.setLayout(new java.awt.BorderLayout());

        jScrollPane.setViewportView(jList);

        jPanelList.add(jScrollPane, java.awt.BorderLayout.CENTER);

        add(jPanelList, java.awt.BorderLayout.CENTER);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        add(jPanelButtons, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxSelector;
    private javax.swing.JList jList;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelComboBox;
    private javax.swing.JPanel jPanelList;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables
}
