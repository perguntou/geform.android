package br.ufrj.del.geform.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.ufrj.del.geform.Constants;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Item;
import br.ufrj.del.geform.bean.Type;

/**
 * 
 */
public class EditItemActivity extends FragmentActivity {

	private Item m_item;

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		m_item = getIntent().getParcelableExtra( "item" );

		setContentView( R.layout.activity_edit_item );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			getActionBar().setDisplayHomeAsUpEnabled( true );
		}

		final EditText questionEditText = ((EditText) findViewById( R.id.item_question ));
		final EditText positionEditText = ((EditText) findViewById( R.id.item_position ));
		final Spinner itemTypeSpinner = ((Spinner) findViewById( R.id.item_type )); 

		positionEditText.setText( String.valueOf( getIntent().getIntExtra( "requestPosition", 1 ) ) );
		questionEditText.setText( m_item.getQuestion() );
		final Type type = m_item.getType();

		final boolean isVisible = type != Type.TEXT;
		changeFragmentVisibility( isVisible );

		itemTypeSpinner.setSelection( type.ordinal() );

		itemTypeSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {
					/*
					 * (non-Javadoc)
					 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
					 */
					@Override
					public void onItemSelected( AdapterView<?> adapter, View view, int position, long id ) {
						final Type selectedType = Type.values()[position]; 
						final boolean isVisible = selectedType != Type.TEXT;

						changeFragmentVisibility( isVisible );
					}
					/*
					 * (non-Javadoc)
					 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
					 */
					@Override
					public void onNothingSelected( AdapterView<?> adapter ) {}
				}
		);

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.menu_edit_item, menu );
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
		case R.id.menu_item_accept:
			final EditText questionEditText = ((EditText) findViewById( R.id.item_question ));
			final EditText positionEditText = ((EditText) findViewById( R.id.item_position ));
			final EditOptionsFragment editOptionsFragment = (EditOptionsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_edit_options );
			final Spinner itemTypeSpinner = ((Spinner) findViewById( R.id.item_type )); 

			m_item.setQuestion( questionEditText.getText().toString().trim() );

			final String question = m_item.getQuestion();
			if( question.equals("") ) {
				Toast.makeText( getApplicationContext(),  R.string.message_question_missing, Toast.LENGTH_LONG ).show();
				return false;
			}
			List<String> options = editOptionsFragment.getOptions();
			final int typeOrdinal = itemTypeSpinner.getSelectedItemPosition();
			final Type type = Type.values()[typeOrdinal];
			m_item.setType( type );
			if( editOptionsFragment.isVisible() ) {
				if( options.size() < Constants.MIN_NUMBER_OPTIONS ) {
					final String format = getString( R.string.message_number_options_invalid );
					Toast.makeText( getApplicationContext(),  String.format( format, Constants.MIN_NUMBER_OPTIONS ), Toast.LENGTH_LONG ).show();
					return false;
				}
				m_item.setOptions( options );
			} else {
				m_item.getOptions().clear();
			}

			Intent intent = getIntent();
			intent.putExtra( "resultPosition", Integer.parseInt( positionEditText.getText().toString() ) );
			intent.putExtra( "item", m_item );
			setResult( Activity.RESULT_OK,  intent );
			finish();
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * Changes the visibility of options's insertion fragment.
	 * @param isVisible
	 */
	private void changeFragmentVisibility( boolean isVisible )
	{
		final EditOptionsFragment editOptionsFragment = (EditOptionsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_edit_options );

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction =  isVisible ? transaction.show( editOptionsFragment ) : transaction.hide( editOptionsFragment );
		transaction.commit();
	}

	/**
	 * Returns the edited item
	 * @return the item
	 */
	public Item getItem() {
		return m_item;
	}

}
