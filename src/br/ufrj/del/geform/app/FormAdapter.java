/**
 * 
 */
package br.ufrj.del.geform.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.database.CollectionsTable;
import br.ufrj.del.geform.database.DatabaseHelper;
import br.ufrj.del.geform.database.FormsTable;

/**
 *
 */
public class FormAdapter extends CursorAdapter {

	/**
	 * 
	 * @param context
	 * @param cursor
	 */
	public FormAdapter( Context context, Cursor cursor ) {
		super( context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parent ) {
		LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
		View retView = inflater.inflate( R.layout.single_row_item, parent, false);

		return retView;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView( View view, Context context, Cursor cursor ) {
		final TextView textViewFormTitle = (TextView) view.findViewById( android.R.id.text1 );
		final int formTitleColumnIndex = cursor.getColumnIndexOrThrow( FormsTable.COLUMN_TITLE );
		String stringTitle = cursor.getString( formTitleColumnIndex );
		textViewFormTitle.setText( stringTitle );

		final TextView textViewCounterCollections = (TextView) view.findViewById( android.R.id.text2 );
		final int counterCollectionsColumnIndex = cursor.getColumnIndex( CollectionsTable._COUNT );
		final int toUpdateColumnIndex = cursor.getColumnIndex( DatabaseHelper.VIEW_COLUMN_COUNT );
		final int counter = cursor.getInt( counterCollectionsColumnIndex );
		final int toUpdate = cursor.getInt( toUpdateColumnIndex );
		String stringCounter = context.getString( R.string.counter_collections, counter, toUpdate );
		textViewCounterCollections.setText( stringCounter );
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#getItemId(int)
	 */
	@Override
	public long getItemId( int position ) {
		final Cursor cursor = (Cursor) this.getItem( position );
		final int formIdColumnIndex = cursor.getColumnIndexOrThrow( FormsTable._ID );
		return cursor.getLong( formIdColumnIndex );
	}

}
