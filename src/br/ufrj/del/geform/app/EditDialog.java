package br.ufrj.del.geform.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.widget.EditText;
import br.ufrj.del.geform.R;

/**
 *
 */
public class EditDialog extends DialogFragment {

	public static final String ARGUMENT_TITLE = "title";
	public static final String ARGUMENT_VALUE = "value";
	public static final String ARGUMENT_INPUT_TYPE = "inputType";


	private EditText m_input;

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {

		final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		final CharSequence title;
		final CharSequence value;
		final Integer inputType;
		final Bundle args = getArguments();

		final CharSequence defaultTitle = getString( R.string.dialog_edit );
		final CharSequence defaultValue = "";

		if( args != null ) {
			final CharSequence candidateTitle = args.getString( ARGUMENT_TITLE );  
			title = candidateTitle == null ? defaultTitle :  candidateTitle;

			final CharSequence candidateValue = args.getString( ARGUMENT_VALUE );
			value = candidateValue == null ? defaultValue :  candidateValue;

			inputType = args.getInt( ARGUMENT_INPUT_TYPE );
		} else {
			title = defaultTitle;
			value = defaultValue;
			inputType = null;
		}

		builder.setTitle( title );

		m_input = new EditText( getActivity().getApplicationContext() );
		if( inputType != null ) {
			m_input.setInputType( inputType );
		}
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
						onPositiveClick();
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
						onNegativeClick();
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
		final Editable editable = m_input.getText();
		final String rawText = editable.toString();
		return rawText.trim();
	}

	/**
	 * 
	 */
	void onPositiveClick() {}

	/**
	 * 
	 */
	void onNegativeClick() {};

}
