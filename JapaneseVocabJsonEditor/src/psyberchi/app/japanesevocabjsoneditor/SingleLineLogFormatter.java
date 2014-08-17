/*
 *  SingleLineLogFormatter.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Kendall Conrad
 */
public class SingleLineLogFormatter extends Formatter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String format(LogRecord lr) {
		String logname = lr.getLoggerName();
		if (logname != null) {
			logname = logname.replace("psyberchi.app.japanesevocabjsoneditor", "Editor");
		}
		String fs = String.format("[%-6s %s] %s: %s\n",
				lr.getLevel(),
				dateFormat.format(new Date(lr.getMillis())),
				logname == null ? "Global" : logname,
				formatMessage(lr));
		return fs;
	}
}
