/*
 *  JapaneseVocabJsonEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabEditorTest {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		File file = new File("/Volumes/ANGELWATT/angelwatt.git/angelwatt.com/japanese/vocab_helper.json");
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		int resp = chooser.showOpenDialog(null);
//		if (JFileChooser.APPROVE_OPTION == resp) {
//			file = chooser.getSelectedFile();
//		}
		try {
			JsonVocabIO.readJsonFile(file);
		}
		catch (FileNotFoundException ex) {
			Logger.getLogger(JapaneseVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IOException ex) {
			Logger.getLogger(JapaneseVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (ParseException ex) {
			Logger.getLogger(JapaneseVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
