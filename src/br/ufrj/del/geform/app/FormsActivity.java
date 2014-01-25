package br.ufrj.del.geform.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import br.ufrj.del.geform.Constants;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.bean.IdentifiableBean;
import br.ufrj.del.geform.bean.Item;
import br.ufrj.del.geform.database.DatabaseHelper;
import br.ufrj.del.geform.net.NetworkHelper;
import br.ufrj.del.geform.xml.FormXmlPull;

/**
 * 
 */
public class FormsActivity extends ActionBarActivity {

	private static final int COLLECT_DATA = 0;
	private static final int CREATE_FORM = 1;

	private ListView m_listView;
	private TextView m_headerTextView;
	private ProgressBar m_progressBar;

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
				final ListView listView = getListView();
				final long identifier = listView.getItemIdAtPosition( position );
				final Form form = loadForm( identifier );
				startCollect( form );
			}
		} );
		final Context thisActivity = this;
		listView.setOnItemLongClickListener( new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
				final ListView listView = getListView();
				final long identifier = listView.getItemIdAtPosition( position );
				final Form form = loadForm( identifier );
				final Builder dialog = new AlertDialog.Builder( thisActivity );
				dialog.setTitle( R.string.dialog_form_info_title );

				final Date timestamp = form.getTimestamp();
				final String description = form.getDescription();
				final String descriptionToShow = description != null ? description : Constants.EMPTY_STRING;
				List<Item> items = form.getItems();

				final long formId = form.getId();
				final String message = getString( R.string.dialog_form_info_message,
				form.getTitle(),
				formId,
				form.getCreator(),
				DateUtils.formatDateTime( thisActivity, timestamp.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME ),
				items.size(),
				descriptionToShow );

				dialog.setMessage( message );
				dialog.setPositiveButton( android.R.string.ok, null );
				dialog.setNegativeButton( R.string.delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface dialog, int which ) {
						deleteForm( formId );
					}
				} );
				dialog.show();
				return true;
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

		final String userPreference = getUserIdentification();
		if( userPreference == null ) {
			final Builder dialog = new AlertDialog.Builder( this );
			dialog.setTitle( R.string.dialog_alert_user_undefined_title );
			dialog.setMessage( R.string.dialog_alert_user_undefined_message );
			dialog.setPositiveButton( R.string.title_activity_settings, new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which ) {
					editSettings( context );
				}
			} );
			dialog.setNegativeButton( android.R.string.cancel, null );
			dialog.show();
		}

		m_headerTextView = (TextView) header.findViewById( R.id.label_forms );
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		final String userPreference = getUserIdentification();
		final String user;
		if( userPreference == null ) {
			user = this.getString( R.string.default_user );
		} else {
			user = userPreference;
		}

		updateHeaderTextView( user );
	}

	/**
	 * 
	 * @return
	 */
	public String getUserIdentification() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
		final String userPreference = preferences.getString( SettingsActivity.KEY_USER, null );
		return userPreference;
	}

	private void updateHeaderTextView( final String user ) {
		final String text = this.getString( R.string.label_list_forms, user );
		m_headerTextView.setText( text );
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
		case R.id.menu_form_create:
		{
			final String creator = getUserIdentification();
			if( creator == null ) {
				final Builder dialog = new AlertDialog.Builder( this );
				dialog.setTitle( R.string.dialog_alert_user_undefined_title );
				dialog.setMessage( R.string.dialog_alert_form_create_user_message );
				dialog.setPositiveButton( android.R.string.ok, null );
				dialog.show();
				break;
			}
			Intent intent = new Intent( context, EditFormActivity.class );
			final Form newForm = new Form();
			newForm.setCreator( creator );
			intent.putExtra( "form", newForm );
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
				protected void onPostDownload( Form result ) {
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
							final DatabaseHelper dbHelper = DatabaseHelper.getInstance( context );
							if( dbHelper.checkIfFormExists( id ) ) {
								handleExistingForm( id );
								return;
							}
							network.downloadForm( id );
						} catch( NumberFormatException e ) {
							final String format = getString( R.string.message_id_invalid );
							final String msg = String.format( format, value );
							Toast toast = Toast.makeText( context, msg, Toast.LENGTH_LONG );
							toast.show();
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
		case R.id.menu_collection_upload:
		{
			final FormAdapter adapter = (FormAdapter) getListAdapter();
			final int count = adapter.getCount();
			if( count > 0 ) {
				final String collector = getUserIdentification();
				if( collector == null ) {
					final Builder dialog = new AlertDialog.Builder( this );
					dialog.setTitle( R.string.dialog_alert_user_undefined_title );
					dialog.setMessage( R.string.dialog_alert_form_upload_user_message );
					dialog.setPositiveButton( android.R.string.ok, null );
					dialog.show();
					break;
				}
				final DatabaseHelper dbHelper = DatabaseHelper.getInstance( context );
				final NetworkHelper network = new NetworkHelper() {
					@Override
					protected void onPreExecute() {
						item.setEnabled( false );
						m_progressBar.setVisibility( ProgressBar.VISIBLE );
					}
					@Override
					protected void onPostUpload( final Long result ) {
						item.setEnabled( true );
						m_progressBar.setVisibility( ProgressBar.INVISIBLE );
						if( result == Form.NO_ID ) {
							final String msg = getString( R.string.message_collections_upload_error );
							final Toast toast = Toast.makeText( context, msg, Toast.LENGTH_LONG );
							toast.show();
							return;
						}
						dbHelper.setCollectionsUpdated( result );
						updateAdapter();
					}
				};

				for( int position = 0; position < count; ++position ) {
					final Long id = adapter.getItemId( position );
					final List<Collection> collections = dbHelper.getCollectionsByForm( id, true );
					if( !collections.isEmpty() ) {
						for( Collection collection : collections ) {
							collection.setCollector( collector );
						}
						network.uploadCollections( collections, id );
					}
				}
			}
			break;
		}
		case R.id.menu_settings:
		{
			editSettings( context );
			break;
		}
		default:
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param formId
	 */
	protected void handleExistingForm( final Long formId ) {
		final Builder dialog = new AlertDialog.Builder( this );
		dialog.setTitle( R.string.dialog_alert_form_downloaded_already_title );
		dialog.setMessage( R.string.dialog_alert_form_downloaded_already_message );
		dialog.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				final Form form = loadForm( formId );
				startCollect( form );
			}
		} );
		dialog.setNegativeButton( android.R.string.cancel, null );
		dialog.show();
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
				final Long id = result.getLongExtra( "created_form_id", IdentifiableBean.NO_ID );
				final Context context = this.getBaseContext();
				final NetworkHelper network = new NetworkHelper() {
					@Override
					protected void onPreExecute() {
						m_progressBar.setVisibility( ProgressBar.VISIBLE );
					}
					@Override
					protected void onPostDownload( Form result ) {
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
				network.downloadForm( id );
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
		final String title = form.getTitle();
		final Long id = form.getId();
		dbHelper.insertForm( id, title );
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

	public void editSettings( final Context context ) {
		Intent intent = new Intent( context, SettingsActivity.class );
		startActivity( intent );
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 * @throws RuntimeException
	 */
	private Form loadForm( final long identifier ) throws RuntimeException {
		final Form form;
		try {
			final File directory = getDir( "forms", FragmentActivity.MODE_PRIVATE );
			final String path = String.format( "%s%s%s.%s", directory, File.separator, identifier, Constants.extension );
			final FileInputStream in = new FileInputStream( path );
			final FormXmlPull xmlHandler = FormXmlPull.getInstance();
			final List<Form> result = xmlHandler.parse( in );
			form = result.get(0);
			form.setId( identifier );
		} catch( Exception e ) {
			final String message = String.format( "Error while loading form (id=%s)", identifier );
			throw new RuntimeException( message, e );
		}
		return form;
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 */
	private boolean deleteForm( final long identifier )
	{
		final DatabaseHelper dbHelper = DatabaseHelper.getInstance( this );
		final int status = dbHelper.removeForm( identifier );
		if( status > 0 ) {
			final File directory = getDir( "forms", FragmentActivity.MODE_PRIVATE );
			final String filename = String.format( "%s.%s", identifier, Constants.extension );
			final File file = new File( directory, filename );
			final boolean deleted = file.delete();
			updateAdapter();
			return deleted;
		}
		return false;
	}

	/**
	 * @param form
	 */
	private void startCollect( final Form form ) {
		final Collection collection = new Collection( form );
		final Context context = getBaseContext();
		Intent intent = new Intent( context, FillFormActivity.class );
		intent.putExtra( "collection", collection );

		startActivityForResult( intent, COLLECT_DATA );
	}

}
