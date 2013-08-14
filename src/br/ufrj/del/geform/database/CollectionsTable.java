/**
 * 
 */
package br.ufrj.del.geform.database;

import android.provider.BaseColumns;

/**
 *
 */
public class CollectionsTable implements BaseColumns {

	public static final String TABLE_COLLECTION = "collections";
	public static final String COLUMN_FORM_ID = "form_id";
	public static final String COLUMN_ANSWER = "answer";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_UPDATED = "updated";

	public static final String CREATE = "create table if not exists " + TABLE_COLLECTION + " (" +
			_ID + " integer primary key, " +
			COLUMN_FORM_ID + " integer, " +
			_COUNT + " integer not null, " +
			COLUMN_ITEM + " integer not null, " +
			COLUMN_ANSWER + " text not null, " +
			COLUMN_UPDATED + " integer default 0, " +
			"foreign key(" + COLUMN_FORM_ID + ") references "+ FormsTable.TABLE_FORMS +"(" + FormsTable._ID + ") on delete cascade" +
			");";

	public static final String DROP = "drop table if exists " + TABLE_COLLECTION;

}
