package br.ufrj.del.geform.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import br.ufrj.del.geform.Constants;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.database.DatabaseHelper;
import br.ufrj.del.geform.net.NetworkHelper;
import br.ufrj.del.geform.xml.FormXmlPull;


public class FormsActivity extends FragmentActivity {

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
		final ListView listView = (ListView) findViewById( android.R.id.list );
		m_progressBar = (ProgressBar) findViewById( android.R.id.progress );
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				Form form = null;
				try {
					final ListView listView = getListView();
					long identifier = listView.getItemIdAtPosition( position );
					final File directory = getDir( "forms", FragmentActivity.MODE_PRIVATE );
					final String path = String.format( "%s%s%s.%s", directory, File.separator, identifier, Constants.extension );
					final FileInputStream in = new FileInputStream( path );
					final FormXmlPull xmlHandler = FormXmlPull.getInstance();
					final List<Form> result = xmlHandler.parse( in );
					form = result.get(0);
					form.setId( identifier );
				} catch( FileNotFoundException e ) {
					e.printStackTrace();
				} catch( XmlPullParserException e ) {
					e.printStackTrace();
				} catch( IOException e ) {
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
		} );
		listView.addHeaderView( header, null, false );
		setListView( listView );

		final Context applicationContext = getApplicationContext();
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( applicationContext );

		final Context context = getBaseContext();
		Cursor cursor = dbHelper.getFormsTitleAndCounter();
		final ListAdapter adapter = new FormAdapter( context, cursor );

		setListAdapter( adapter );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate( R.menu.menu_forms, menu );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( final MenuItem item ) {
		final Context context = getBaseContext();
		switch( item.getItemId() ) {
		case R.id.menu_form_add:
		{
			Intent intent = new Intent( context, EditFormActivity.class );
			intent.putExtra( "form", (Parcelable) new Form() );
			startActivityForResult( intent, CREATE_FORM );
			break;
		}
		case R.id.menu_form_download:
		{
			final NetworkHelper network = new NetworkHelper() {
				@Override
				protected void onPreExecute() {
					item.setEnabled( false );
					m_progressBar.setVisibility( ProgressBar.VISIBLE );
				}
				@Override
				protected void onPostExecute( Form result ) {
					item.setEnabled( true );
					m_progressBar.setVisibility( ProgressBar.INVISIBLE );
					if( result == null ) {
						final String msg = getString( R.string.message_download_error );
						final Toast toast = Toast.makeText( context, msg, Toast.LENGTH_LONG );
						toast.show();
						return;
					}
					insertForm( result );
					updateAdapter();
				}
			};
			final EditDialog dialog = new EditDialog() {
				@Override
				void onPositiveClick() {
					final String value = this.getInputValue();
					if( !"".equals( value ) ) {
						try {
							final long id = Long.parseLong( value );
							network.downloadForm( id );
						} catch( NumberFormatException e ) {
							final String format = getString( R.string.message_id_invalid );
							final String msg = String.format( format, value );
							Toast.makeText( context, msg, Toast.LENGTH_LONG ).show();
						}
					}
				}
			};
			final Bundle args = new Bundle();
			final String title = getString( R.string.dialog_form_download );
			args.putString( EditDialog.ARGUMENT_TITLE, title );
			args.putInt( EditDialog.ARGUMENT_INPUT_TYPE, InputType.TYPE_CLASS_NUMBER );
			dialog.setArguments( args );
			dialog.show( this.getSupportFragmentManager(), "" );
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
			xmlHandler.serialize( Arrays.asList(form) , out );
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void updateAdapter() {
		final FormAdapter adapter = (FormAdapter) getListAdapter();
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( this );
		Cursor cursor = dbHelper.getFormsTitleAndCounter();
		adapter.changeCursor( cursor );
	}

	/**
	 * @return the listView
	 */
	public ListView getListView() {
		return m_listView;
	}

	/**
	 * @param listView the listView to set
	 */
	public void setListView( ListView listView) {
		this.m_listView = listView;
	}

	/**
	 * 
	 * @param adapter
	 */
	private void setListAdapter( ListAdapter adapter ) {
		m_listView.setAdapter( adapter );
	}

	/**
	 * 
	 * @return
	 */
	private ListAdapter getListAdapter() {
		final HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) m_listView.getAdapter();
		final ListAdapter adapter = listAdapter.getWrappedAdapter();
		return adapter;
	}

	private ListView m_listView;
	private ProgressBar m_progressBar;

}
