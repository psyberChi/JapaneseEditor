/*
 *  JapaneseVocabJsonEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabEditorTest {

	public static Level logLevel = Level.INFO;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
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

//		File file = new File("/Volumes/ANGELWATT/angelwatt.git/angelwatt.com/japanese/vocab_helper.json");
//		File file = new File("/Volumes/Micro32/stuff/programming/java/apps/JapaneseVocabJsonEditor/vocab_helper.json");
		File file = new File("test_in.json");
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		int resp = chooser.showOpenDialog(null);
//		if (JFileChooser.APPROVE_OPTION == resp) {
//			file = chooser.getSelectedFile();
//		}
		try {
			VocabModel model = JsonVocabIO.readJsonFile(file);
			JsonVocabIO.writeJsonFile(new File("test_out.json"), model);
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
