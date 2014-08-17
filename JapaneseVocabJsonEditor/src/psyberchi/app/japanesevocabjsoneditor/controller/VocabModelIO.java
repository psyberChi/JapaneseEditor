/*
 *  VocabModelIO.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.controller;

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

	public VocabModelIO() {
	}

	/**
	 * Resets the open file object.
	 */
	public boolean closeFile() {
		// @todo check for modified file before closing
		setFileOpened(null);
		setFileModified(false);
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
	public VocabModel openFile() throws Exception {
		int resp = jFileChooserOpen.showOpenDialog(null);
		if (JFileChooser.APPROVE_OPTION == resp) {
			return openFile(jFileChooserOpen.getSelectedFile());
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
	public VocabModel openFile(File file) throws Exception {
		if (file == null || !file.canRead()) {
			return null;
		}
		VocabModel model = null;
		// Check if current file is modified
		if (isFileModified()) {
			// TODO
		}
		try {
			setFileOpened(file);
			model = JsonVocabIO.readJsonFile(getFileOpened());
			setFileModified(false);
			return model;
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
	 * Prompts the user for a new file to save to.
	 */
	public boolean saveAsFile(VocabModel model) {
		if (model == null) {
			return false;
		}
		if (getFileOpened() != null) {
			jFileChooserSave.setCurrentDirectory(getFileOpened().getParentFile());
		}
		int resp = jFileChooserSave.showSaveDialog(null);
		if (JOptionPane.OK_OPTION == resp) {
			File file = jFileChooserSave.getSelectedFile();
			if (file.getParentFile().canWrite()) {
				setFileOpened(file);
				return saveFile(getFileOpened(), model);
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
	public boolean saveFile(VocabModel model) {
		return saveFile(getFileOpened(), model);
	}

	/**
	 * Saves the vocabulary model to a given file.
	 *
	 * @param file
	 */
	public boolean saveFile(File file, VocabModel model) {
		if (file == null || model == null) {
			return false;
		}
		if (file.getParentFile().canWrite()) {
			JsonVocabIO.writeJsonFile(file, model);
//			setStatusText("File saved: " + file.getAbsolutePath(), 4000);
//			setFileModified(false);
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

	/**
	 * @return the fileModified
	 */
	public boolean isFileModified() {
		return fileModified;
	}

	/**
	 * @param fileModified the fileModified to set
	 */
	public void setFileModified(boolean fileModified) {
		this.fileModified = fileModified;
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
		this.fileOpened = fileOpened;
	}
}
