/*
 *  ChangeablePreferences.java
 *
 *  GNU GPL License.
 */
package psyberchi.app.japanesevocabjsoneditor.controller;

/**
 *
 * @author Kendall Conrad
 */
public interface ChangeablePreferences {

	public void loadPreferences();

	public void savePreferences();

	public boolean dataIsValid();

}
