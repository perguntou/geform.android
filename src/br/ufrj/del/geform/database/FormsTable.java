/**
 * 
 */
package br.ufrj.del.geform.database;

import android.provider.BaseColumns;

/**
 *
 */
public class FormsTable implements BaseColumns {

	public static final String TABLE_FORMS = "forms";
	public static final String COLUMN_TITLE = "title";

	public static final String CREATE = "create table if not exists " + TABLE_FORMS + " (" +
			_ID + " integer primary key, " +
			COLUMN_TITLE + " text not null" +
			");";

	public static final String DROP = "drop table if exists " + TABLE_FORMS;

}
