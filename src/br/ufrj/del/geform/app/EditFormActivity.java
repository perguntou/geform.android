package br.ufrj.del.geform.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.bean.Item;

/**
 *
 */
public class EditFormActivity extends ListActivity {
	final int ADD_ITEM_REQUEST_CODE = 0;
	final int EDIT_ITEM_REQUEST_CODE = 1;

	private Form m_form;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_edit_form );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			getActionBar().setDisplayHomeAsUpEnabled( true );
		}

		m_form = getIntent().getParcelableExtra( "form" );

		final String title = m_form.getTitle();
		((EditText) findViewById( R.id.form_name )).setText( title );

		final List<Item> items = m_form.getItems();
		setListAdapter( new ItemAdapter( this, android.R.layout.simple_list_item_1, items ) );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.menu_edit_form, menu );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick( ListView listView, View view, int position, long id ) {
		super.onListItemClick( listView, view, position, id );

		Intent intent = new Intent( EditFormActivity.this, EditItemActivity.class );
		intent.putExtra( "item", (Item) listView.getItemAtPosition( position ) );
		intent.putExtra( "requestPosition", position + 1 );
		startActivityForResult( intent, EDIT_ITEM_REQUEST_CODE );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.menu_item_add:
		{
			final Intent intent = new Intent( EditFormActivity.this, EditItemActivity.class );
			intent.putExtra( "item", new Item() );
			intent.putExtra( "requestPosition", m_form.size() + 1 );
			startActivityForResult( intent, ADD_ITEM_REQUEST_CODE );
			break;
		}
		case R.id.menu_item_accept:
		{
			final EditText view = (EditText) findViewById( R.id.form_name );

			final Editable text = view.getText();
			final String textAsString = text.toString();
			final String title = textAsString.trim();
			if( title.equals("") ) {
				final Toast toast = Toast.makeText( EditFormActivity.this, R.string.message_title_missing, Toast.LENGTH_LONG );
				toast.show();
				break;
			}
			m_form.setTitle( title );

			if( m_form.size() <= 0 ) {
				final Toast toast = Toast.makeText( EditFormActivity.this, R.string.message_number_items_invalid, Toast.LENGTH_LONG );
				toast.show();
				break;
			}

			final Builder dialog = new AlertDialog.Builder( this );
			dialog.setTitle( R.string.dialog_alert_title );
			dialog.setMessage( R.string.dialog_alert_message );
			dialog.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which ) {
					final Intent intent = getIntent();
					intent.putExtra( "form", (Parcelable) m_form );
					setResult( Activity.RESULT_OK,  intent );
					finish();
				}
			} );
			dialog.setNegativeButton( android.R.string.cancel, null );
			dialog.show();

			break;
		}
		default:
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent result ) {
		super.onActivityResult( requestCode, resultCode, result );

		if( resultCode == RESULT_OK ) {
			int resultPosition = checkBoundaries( result.getIntExtra( "resultPosition", m_form.size() ) - 1 ); 
			Item resultItem = (Item) result.getParcelableExtra( "item" );

			switch( requestCode ) {
			case ADD_ITEM_REQUEST_CODE:
				m_form.add( resultPosition, resultItem );
				break;
			case EDIT_ITEM_REQUEST_CODE:
				int requestPosition = result.getIntExtra( "requestPosition", m_form.size() ) - 1;
				if( requestPosition != resultPosition ) {
					if( resultPosition > requestPosition ) {
						resultPosition = Math.min( ++resultPosition, m_form.size() );
					} else {
						requestPosition = Math.min( ++requestPosition, m_form.size() );
					}
					m_form.add( resultPosition, resultItem );
					m_form.remove( requestPosition );
				} else {
					m_form.set( resultPosition, resultItem );
				}
				break;
			default:
				return;
			}
			((ItemAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	/**
	 * Validates an index in the form. Limits the position to
	 * the form's boundaries, if out returns the last valid position.
	 * @param position the index
	 * @return a valid position
	 */
	private int checkBoundaries( int position ) {
		if( position < 0 || position > m_form.size() ) {
			Toast.makeText( getApplicationContext(), R.string.message_index_invalid, Toast.LENGTH_LONG ).show();
			position = m_form.size();
		}
		return position;
	}

}
