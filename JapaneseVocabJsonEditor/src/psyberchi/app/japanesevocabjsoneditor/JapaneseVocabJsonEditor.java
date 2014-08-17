/*
 *  JapaneseVocabJsonEditor.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditor;
import static psyberchi.app.japanesevocabjsoneditor.ui.JapaneseVocabEditor.logLevel;

/**
 *
 * @author Kendall Conrad
 */
public class JapaneseVocabJsonEditor {
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

		/* Set the Nimbus look and feel */
		//<editor-fold-null defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
			/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Metal".equals(info.getName())) {
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
		//</editor-fold-null>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JapaneseVocabEditor().setVisible(true);
			}
		});
	}
}
