package br.ufrj.del.geform.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.ufrj.del.geform.Constants;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.database.DatabaseHelper;
import br.ufrj.del.geform.net.NetworkHelper;
import br.ufrj.del.geform.xml.FormXmlPull;


public class FormsActivity extends ListActivity {

	private static final int COLLECT_DATA = 0;
	private static final int CREATE_FORM = 1;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_forms );

		final LayoutInflater layoutInflater = getLayoutInflater();
		final View header = layoutInflater.inflate( R.layout.header_forms, null );
		final ListView listView = getListView();
		listView.addHeaderView( header, null, false );

		final Context applicationContext = getApplicationContext();
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( applicationContext );

		final Context context = getBaseContext();
		final ListAdapter adapter = new FormAdapter( context, dbHelper.getFormsTitleAndCounter() );

		setListAdapter( adapter );
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick( ListView listView, View view, int position, long id ) {
		super.onListItemClick( listView, view, position, id );

		Form form = null;
		try {
			long identifier = listView.getItemIdAtPosition( position );
			final File directory = getDir( "forms", FragmentActivity.MODE_PRIVATE );
			final String path = String.format( "%s%s%s.%s", directory, File.separator, identifier, Constants.extension );
			final FileInputStream in = new FileInputStream( path );
			final FormXmlPull xmlHandler = FormXmlPull.getInstance();
			final List<Form> result = xmlHandler.parse( in );
			form = result.get(0);
			form.setId( identifier );
		} catch( FileNotFoundException e ) {
			Log.e( "FormParse", e.getMessage() );
			e.printStackTrace();
		} catch( XmlPullParserException e ) {
			Log.e( "FormParse", e.getMessage() );
			e.printStackTrace();
		} catch( IOException e ) {
			Log.e( "FormParse", e.getMessage() );
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		final Collection collection = new Collection( form );
		final Context context = getBaseContext();
		Intent intent = new Intent( context, FillFormActivity.class );
		intent.putExtra( "collection", collection );

		startActivityForResult( intent, COLLECT_DATA );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.menu_forms, menu );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		final Context context = getBaseContext();
		switch( item.getItemId() ) {
		case R.id.menu_form_add:
			Intent intent = new Intent( context, EditFormActivity.class );
			intent.putExtra( "form", (Parcelable) new Form() );
			startActivityForResult( intent, CREATE_FORM );
			break;
		case R.id.menu_form_download:
			//TODO get the URL to download the form from user input.
			final Form form = NetworkHelper.downloadForm( Form.NO_ID );
			if( form == null ) {
				Toast.makeText( context, getString( R.string.message_download_error ), Toast.LENGTH_LONG ).show();
				return false;
			}
			insertForm( form );
			updateAdapter();
			break;
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
			switch( requestCode ) {
			case COLLECT_DATA:
				updateAdapter();
				break;
			case CREATE_FORM:
				Form form = (Form) result.getParcelableExtra( "form" );
				insertForm( form );
				updateAdapter();
				break;
			default:
			}
		}
	}

	/**
	 * 
	 * @param form
	 */
	private void insertForm( Form form ) {
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( this.getApplicationContext() );
		final Long id = dbHelper.insertForm( form.title() );
		try {
			final File directory = getDir( "forms", FragmentActivity.MODE_PRIVATE );
			final String path = String.format( "%s%s%s.%s", directory, File.separator, id, Constants.extension );
			final FileOutputStream out = new FileOutputStream( path );
			final FormXmlPull xmlHandler = FormXmlPull.getInstance();
			xmlHandler.serialize( form , out );
		} catch (IllegalArgumentException e) {
			Log.e( "FillActivity", e.getMessage() );
		} catch (IllegalStateException e) {
			Log.e( "FillActivity", e.getMessage() );
		} catch (FileNotFoundException e) {
			Log.e( "FillActivity", e.getMessage() );
		} catch (XmlPullParserException e) {
			Log.e( "FillActivity", e.getMessage() );
		} catch (IOException e) {
			Log.e( "FillActivity", e.getMessage() );
		}
	}

	/**
	 * 
	 */
	private void updateAdapter() {
		final FormAdapter adapter = (FormAdapter) getListAdapter();
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( this );
		adapter.changeCursor( dbHelper.getFormsTitleAndCounter() );
	}

}
