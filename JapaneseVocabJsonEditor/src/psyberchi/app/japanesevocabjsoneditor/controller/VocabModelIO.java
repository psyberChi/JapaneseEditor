/*
 *  VocabModelIO.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.json.simple.parser.ParseException;
import psyberchi.app.japanesevocabjsoneditor.model.JsonVocabIO;
import psyberchi.app.japanesevocabjsoneditor.model.VocabModel;

/**
 * @author Kendall Conrad
 */
public class VocabModelIO {
	private static final Logger logger = Logger.getLogger(VocabModelIO.class.getCanonicalName());
	public static final String PROP_FILE_MODIFIED_CHANGED = "PROP_FILE_MODIFIED_CHANGED";
	public static final String PROP_FILE_CHANGED = "PROP_FILE_CHANGED";
	/**
	 * Whether or not the currently open file has been modified.
	 */
	private boolean fileModified = false;
	/**
	 * The currently open fileOpened.
	 */
	private File fileOpened = null;
	/**
	 * The file chooser when saving documents.
	 */
	private JFileChooser jFileChooserSave = new JFileChooser();
	/**
	 * The file chooser when opening documents.
	 */
	private JFileChooser jFileChooserOpen = new JFileChooser();
	/**
	 * Property change support object.
	 */
	private PropertyChangeSupport pcs;

	public VocabModelIO() {
		pcs = new PropertyChangeSupport(this);
	}

	/**
	 * Adds a PropertyChangeListener to the class to receive property change
	 * events.
	 *
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Resets the open file object.
	 */
	public boolean closeFile(VocabModel model) {
		if (!handleModifiedFile(model)) {
			return false;
		}
		logger.log(Level.INFO, "Successfully closed file: {0}",
				(fileOpened == null ? "" : fileOpened.getAbsolutePath()));
		setFileOpened(null);
		setFileModified(false);
		return true;
	}

	/**
	 * Fires off a PropertyChangeEvent to all of the listeners.
	 *
	 * @param evt
	 */
	public void firePropertyChangeEvent(PropertyChangeEvent evt) {
		pcs.firePropertyChange(evt);
	}

	/**
	 * Checks if the currently open file has been modified. If so, it will
	 * prompt the user to save it. If the user says yes the file will be saved
	 * and return true. If the user says no, the file will not be saved and will
	 * return true. If the user says cancel, it will return false. If the user
	 * says yes to save, but the save fails it will return false.
	 *
	 * @return true if the user is OK with proceeding, false if further action
	 * should stop.
	 */
	private boolean handleModifiedFile(VocabModel model) {
		// Only care if the current file is modified
		if (isFileModified()) {
			// Ask the user what to do
			int resp = JOptionPane.showConfirmDialog(null,
					"File is modified. Would you like to save it?",
					"Modified File",
					JOptionPane.YES_NO_CANCEL_OPTION);
			// Handle the yes response
			if (resp == JOptionPane.YES_OPTION) {
				if (!saveFile(model)) {
					// Save had problems.
					return false;
				}
			}
			else if (resp == JOptionPane.CANCEL_OPTION) {
				// Abort
				return false;
			}
		}
		// Reach if not modified, successfully saved, or user did not want to save
		return true;
	}

	/**
	 * Returns whether or not a file is open currently.
	 *
	 * @return
	 */
	public boolean isFileOpen() {
		return fileOpened != null;
	}

	/**
	 * Opens a file from the user.
	 *
	 * @return
	 * @throws Exception
	 */
	public VocabModel openFile(VocabModel model) throws Exception {
		if (!handleModifiedFile(model)) {
			return null;
		}
		int resp = jFileChooserOpen.showOpenDialog(null);
		if (JFileChooser.APPROVE_OPTION == resp) {
			return openFile(jFileChooserOpen.getSelectedFile(), model);
		}
		return null;
	}

	/**
	 * Opens a given {@link File} and creates a {@link VocabModel} from it that
	 * is used to load in the categories.
	 *
	 * @param fileOpened
	 * @return
	 */
	public VocabModel openFile(File file, VocabModel model) throws Exception {
		if (file == null || !file.canRead()) {
			return null;
		}
		VocabModel newModel;
		// Check if current file is modified
		if (!handleModifiedFile(model)) {
			return null;
		}
		try {
			setFileOpened(file);
			newModel = JsonVocabIO.readJsonFile(getFileOpened());
			setFileModified(false);
			return newModel;
		}
		catch (FileNotFoundException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new Exception("File not found");
		}
		catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new Exception("Could not read file");
		}
		catch (ParseException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new Exception("Could not parse file");
		}
	}

	/**
	 * Removes a given PropertyChangeListener and it will not receive
	 * PropertyChangeEvent any longer.
	 *
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Prompts the user for a new file to save to.
	 */
	public boolean saveAsFile(VocabModel model) {
		if (model == null) {
			return false;
		}
		if (getFileOpened() != null) {
			jFileChooserSave.setCurrentDirectory(getFileOpened().getParentFile());
		}
		boolean ok = false;
		while (!ok) {
			int resp = jFileChooserSave.showSaveDialog(null);
			if (JFileChooser.APPROVE_OPTION == resp) {
				File file = jFileChooserSave.getSelectedFile();
				if (file != null) {
					setFileOpened(file);
					if (saveFile(getFileOpened(), model)) {
						return true;
					}
				}
			}
			else if (JFileChooser.CANCEL_OPTION == resp) {
				ok = true;
			}
		}
		return false;
	}

	/**
	 * Saves the vocabulary model to a given file.
	 *
	 * @param file
	 */
	public boolean saveFile(VocabModel model) {
		if (getFileOpened() == null) {
			return saveAsFile(model);
		}
		return saveFile(getFileOpened(), model);
	}

	/**
	 * Saves the vocabulary model to a given file.
	 *
	 * @param file
	 */
	public boolean saveFile(File file, VocabModel model) {
		if (model == null) {
			logger.log(Level.INFO, "saveFile: model null");
			return false;
		}
		if (file == null) {
			return saveAsFile(model);
		}
		if (file.getParentFile().canWrite()) {
			JsonVocabIO.writeJsonFile(file, model);
			setFileModified(false);
			return true;
		}
		JOptionPane.showMessageDialog(null,
				"Cannot write to chosen directory.",
				"Cannot save",
				JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * Returns whether the current file is modified.
	 *
	 * @return the fileModified
	 */
	public boolean isFileModified() {
		return fileModified;
	}

	/**
	 * @param fileModified the fileModified to set
	 */
	public void setFileModified(boolean fileModified) {
		if (this.fileModified != fileModified) {
			this.fileModified = fileModified;
			firePropertyChangeEvent(new PropertyChangeEvent(this,
					PROP_FILE_MODIFIED_CHANGED,
					!fileModified, fileModified));
		}
	}

	/**
	 * @return the fileOpened
	 */
	public File getFileOpened() {
		return fileOpened;
	}

	/**
	 * @param fileOpened the fileOpened to set
	 */
	public void setFileOpened(File fileOpened) {
		if (this.fileOpened != fileOpened) {
			File oldFile = this.fileOpened;
			this.fileOpened = fileOpened;
			firePropertyChangeEvent(new PropertyChangeEvent(this,
					PROP_FILE_CHANGED,
					oldFile, this.fileOpened));
		}
	}
}
