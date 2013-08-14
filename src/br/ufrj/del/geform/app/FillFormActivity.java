package br.ufrj.del.geform.app;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.database.DatabaseHelper;

/**
 *
 */
public class FillFormActivity extends ListActivity {

	public static final int COLLECT_REQUEST_CODE = 0;

	private Collection m_collection;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_fill_form );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			getActionBar().setDisplayHomeAsUpEnabled( true );
		}

		m_collection = getIntent().getParcelableExtra( "collection" );

		final Form form = m_collection.getReference();
		((TextView) findViewById( R.id.text_form_name )).setText( form.title() );

		setListAdapter( new ItemAdapter( this, android.R.layout.simple_list_item_1, form ) );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.menu_fill_form, menu );
		return true;
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
		case R.id.menu_commit:
			final boolean hasAnswersMissing = !m_collection.isAllAnswered();
			if( hasAnswersMissing ) {
				final Context context = this.getBaseContext();
				final String message = getString( R.string.message_answers_missing );
				final Toast toast = Toast.makeText( context, message, Toast.LENGTH_LONG );
				toast.show();
				return false;
			}
			final DatabaseHelper db = DatabaseHelper.getInstance( this.getBaseContext() );
			db.insertCollection( m_collection );
			setResult( RESULT_OK );
			finish();
			break;
		default:
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick( ListView listView, View view, int position, long id ) {
		super.onListItemClick( listView, view, position, id );

		Intent intent = new Intent( FillFormActivity.this, FormPagerActivity.class );
		intent.putExtra( "collection", m_collection );
		intent.putExtra( "position", position );
		startActivityForResult( intent, COLLECT_REQUEST_CODE );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent result ) {
		super.onActivityResult( requestCode, resultCode, result );
		if( resultCode == RESULT_OK && requestCode == COLLECT_REQUEST_CODE ) {
			m_collection = result.getParcelableExtra( "collection" );
		}
	}

}
