package br.ufrj.del.geform.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.ufrj.del.geform.bean.Answer;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;

/**
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "geform.db";
	public static final int DATABASE_VERSION = 3;

	public static final String FOREIGN_KEY_ENABLE = "PRAGMA foreign_keys = ON;";

	public static final String SQLITE_BOOLEAN_TRUE = "1";
	public static final String SQLITE_BOOLEAN_FALSE = "0";

	public static final String VIEW = "viewer";
	public static final String VIEW_COLUMN_COUNT = "view_count";
	public static final String CREATE_VIEW = String.format( "create view if not exists %1$s as select f.%2$s, f.%3$s, count( distinct c.%4$s ) as %5$s from %6$s f left join %7$s c on f.%2$s = c.%8$s and c.%9$s = %10$s group by f.%2$s;",
														VIEW,
														FormsTable._ID,
														FormsTable.COLUMN_TITLE,
														CollectionsTable._COUNT,
														VIEW_COLUMN_COUNT,
														FormsTable.TABLE_FORMS,
														CollectionsTable.TABLE_COLLECTION,
														CollectionsTable.COLUMN_FORM_ID,
														CollectionsTable.COLUMN_UPDATED,
														SQLITE_BOOLEAN_FALSE );

	public static final String DROP_VIEW = String.format( "drop view if exists %s;", VIEW );

	private static DatabaseHelper m_instance;

	/**
	 * 
	 * @param context to use to open or create the database.
	 */
	private DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	/**
	 * Return the instance of a DatabaseHelper
	 * that can be used to access the application's database.
	 * @param context to use to open or create the database.
	 * @return the DatabaseHelper instance.
	 */
	public static DatabaseHelper getInstance( Context context) {
		if( m_instance == null ) {
			m_instance = new DatabaseHelper( context.getApplicationContext() );
		}
		return m_instance;
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate( SQLiteDatabase database ) {
		database.execSQL( FormsTable.CREATE );
		database.execSQL( CollectionsTable.CREATE );
		database.execSQL( CREATE_VIEW );
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen( SQLiteDatabase database ) {
		if( !database.isReadOnly() ) {
			database.execSQL( FOREIGN_KEY_ENABLE );
		}
		super.onOpen( database );
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
		Log.w( "database", "Upgrading database from version " + oldVersion	+ " to " + newVersion + ", which will destroy all old data" );
		database.execSQL( DROP_VIEW );
		database.execSQL( CollectionsTable.DROP );
		database.execSQL( FormsTable.DROP );
		onCreate( database );
	}

	/**
	 * Inserts a form into the application's database.
	 * @param title the form title.
	 * @return the ID of the newly inserted form, or -1 if an error occurred.
	 * @throws SQLException
	 */
	public long insertForm ( String title ) throws SQLException {
		ContentValues content = new ContentValues();
		content.putNull( FormsTable._ID );
		content.put( FormsTable.COLUMN_TITLE, title );

		SQLiteDatabase db = m_instance.getWritableDatabase();
		db.beginTransaction();
		long id = Form.NO_ID;
		try {
			id = db.insertOrThrow( FormsTable.TABLE_FORMS, null, content );
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return id; 
	}

	/**
	 * 
	 * @param collection
	 * @throws SQLException
	 */
	public void insertCollection ( Collection collection ) throws SQLException {
		final Form reference = collection.getReference();
		final Long formId = reference.id();
		final int count = getCollectionCount( formId );

		SQLiteDatabase db = m_instance.getWritableDatabase();
		db.beginTransaction();
		try {
			for( int item = 0; item < reference.size(); item++ ) {
				final Answer answer = collection.get( item );
				if( answer == null ) {

				}
				for( final String singleAnswer : answer ) {
					final ContentValues content = new ContentValues();
					content.putNull( CollectionsTable._ID );
					content.put( CollectionsTable.COLUMN_FORM_ID, formId );
					content.put( CollectionsTable._COUNT, count + 1 );
					content.put( CollectionsTable.COLUMN_ITEM, item );
					content.put( CollectionsTable.COLUMN_ANSWER, singleAnswer );

					db.insertOrThrow( CollectionsTable.TABLE_COLLECTION, null, content );
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 
	 * @param referenceId
	 * @return
	 */
	public int getCollectionCount( final Long referenceId ) {
		SQLiteDatabase db = m_instance.getReadableDatabase();
		final String columnMaxCollectionCount = String.format( "max(%s)", CollectionsTable._COUNT );
		Cursor cursor = db.query( CollectionsTable.TABLE_COLLECTION,
				new String[] { columnMaxCollectionCount },
				CollectionsTable.COLUMN_FORM_ID + " = ?",
				new String[] { String.valueOf( referenceId ) },
				null,
				null,
				null );
		cursor.moveToFirst();

		final int count = (cursor.getCount() != 0) ? cursor.getInt( cursor.getColumnIndexOrThrow( columnMaxCollectionCount ) ) : 0;
		return count;
	}

	/**
	 * 
	 * @return
	 */
	public Cursor getFormsTitleAndCounter() {
		SQLiteDatabase db = m_instance.getReadableDatabase();
		final String query = String.format( "select v.%1$s, v.%2$s, v.%3$s, c.%4$s from %5$s v left join %6$s c on v.%1$s = c.%7$s group by v.%1$s order by v.%2$s;",
				FormsTable._ID,
				FormsTable.COLUMN_TITLE,
				VIEW_COLUMN_COUNT,
				CollectionsTable._COUNT,
				VIEW,
				CollectionsTable.TABLE_COLLECTION,
				CollectionsTable.COLUMN_FORM_ID );
		Cursor cursor = db.rawQuery( query, null );
		cursor.moveToFirst();

		return cursor;
	}

	/**
	 * Gets the form title associated to specified ID.
	 * @param id the form ID.
	 * @return the form title.
	 */
	public String formTitle( Long id ) {
		SQLiteDatabase db = m_instance.getReadableDatabase();
		Cursor cursor = db.query( FormsTable.TABLE_FORMS, new String[] { FormsTable.COLUMN_TITLE }, FormsTable._ID + " = ?", new String[] { String.valueOf( id ) }, null, null, null );
		cursor.moveToFirst();

		return cursor.getString( cursor.getColumnIndexOrThrow( FormsTable.COLUMN_TITLE ) );
	}

	/**
	 * 
	 * @return
	 */
	public Cursor fetchAllForms() {
		SQLiteDatabase db = m_instance.getReadableDatabase();
		Cursor cursor = db.query( FormsTable.TABLE_FORMS, new String[] { FormsTable._ID, FormsTable.COLUMN_TITLE }, null, null, null, null, null, FormsTable.COLUMN_TITLE );
		cursor.moveToFirst();

		return cursor;
	}

	/**
	 * 
	 * @param formId
	 * @param filterUpdated
	 * @return
	 */
	public List<Collection> getCollectionsByForm( Long formId, boolean filterUpdated ) {
		SQLiteDatabase db = m_instance.getReadableDatabase();
		final String selection;
		final String[] selectionArgs;
		if( filterUpdated ) {
			selection = String.format( "%s = ? and %s = ?", CollectionsTable.COLUMN_FORM_ID, CollectionsTable.COLUMN_UPDATED );
			selectionArgs = new String[] { formId.toString(), SQLITE_BOOLEAN_FALSE };
		} else {
			selection = String.format( "%s = ?", CollectionsTable.COLUMN_FORM_ID );
			selectionArgs = new String[] { formId.toString() };
		}
		Cursor cursor = db.query(
				CollectionsTable.TABLE_COLLECTION,
				new String[]{ CollectionsTable._ID, CollectionsTable._COUNT, CollectionsTable.COLUMN_ITEM, CollectionsTable.COLUMN_ANSWER },
				selection,
				selectionArgs,
				null,
				null,
				CollectionsTable._COUNT,
				null );
		cursor.moveToFirst();
		final List<Collection> result = new ArrayList<Collection>();
		final int countColumnIndex = cursor.getColumnIndex( CollectionsTable._COUNT );
		final int itemColumnIndex = cursor.getColumnIndex( CollectionsTable.COLUMN_ITEM );
		final int answerColumnIndex = cursor.getColumnIndex( CollectionsTable.COLUMN_ANSWER );
		int count = 0;
		Collection newCollection = null;
		while( !cursor.isAfterLast() ) {
			final int cursorCount = cursor.getInt( countColumnIndex );
			final int cursorItem = cursor.getInt( itemColumnIndex );
			final String cursorAnswer = cursor.getString( answerColumnIndex );
			if( cursorCount != count ) {
				final Form reference = new Form();
				reference.setId( formId );
				newCollection = new Collection( reference );
				result.add( newCollection );
				count = cursorCount;
			}
			newCollection.add( cursorItem, cursorAnswer );
			cursor.moveToNext();
		}
		return result;
	}

}
