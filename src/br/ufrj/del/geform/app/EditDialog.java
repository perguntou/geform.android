package br.ufrj.del.geform.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import br.ufrj.del.geform.R;

/**
 *
 */
public class EditDialog extends DialogFragment {

	public static final String ARGUMENT_TITLE = "title";
	public static final String ARGUMENT_VALUE = "value";

	private EditDialogListener m_listener;
	private EditText m_input;

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach( Activity activity ) {
		super.onAttach( activity );

		if( m_listener == null ) {
			final String msg = String.format( "Be sure that a listener is set for this %s. Use %s.setListener(...).",
												EditDialog.class.getSimpleName(),
												EditDialog.class.getName() );
			throw new NullPointerException( msg );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {

		final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		final CharSequence title;
		final CharSequence value;
		final Bundle args = getArguments();

		final CharSequence defaultTitle = getString( R.string.dialog_edit );
		final CharSequence defaultValue = "";

		if( args != null ) {
			final CharSequence candidateTitle = args.getString( ARGUMENT_TITLE );  
			title = candidateTitle == null ? defaultTitle :  candidateTitle;

			final CharSequence candidateValue = args.getString( ARGUMENT_VALUE );
			value = candidateValue == null ? defaultValue :  candidateValue;
		} else {
			title = defaultTitle;
			value = defaultValue;
		}

		builder.setTitle( title );

		m_input = new EditText( getActivity().getApplicationContext() );
		m_input.setText( value );
		final int textColor = getResources().getColor( android.R.color.primary_text_light );
		m_input.setTextColor( textColor );
		builder.setView( m_input );

		builder.setPositiveButton(
				android.R.string.ok,
				new DialogInterface.OnClickListener() {
					/*
					 * (non-Javadoc)
					 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
					 */
					@Override
					public void onClick( DialogInterface dialog, int which ) {
						m_listener.onDialogPositiveClick( EditDialog.this );
					}
				}
		);

		builder.setNegativeButton(
				android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					/*
					 * (non-Javadoc)
					 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
					 */
					@Override
					public void onClick( DialogInterface dialog, int which ) {
						m_listener.onDialogNegativeClick( EditDialog.this );
						dialog.dismiss();
					}
				}
		);

		return builder.create();
	}

	/**
	 * Returns the value entered
	 * @return the input value
	 */
	public String getInputValue() {
		return m_input.getText().toString().trim();
	}

	/**
	 * 
	 * @param listener the listener to set.
	 * @see EditDialogListener
	 */
	public void setListener( final Object listener ) {
		try {
			m_listener = (EditDialogListener) listener;
		} catch( ClassCastException e ) {
			throw new ClassCastException( listener + " must implement EditDialogListener" );
		}
	}

	/**
	 * 
	 * @return the listener.
	 * @see EditDialogListener
	 */
	public EditDialogListener getListener() {
		return m_listener;
	}

}
