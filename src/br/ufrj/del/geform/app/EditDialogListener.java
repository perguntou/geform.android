/**
 * 
 */
package br.ufrj.del.geform.app;

import android.support.v4.app.DialogFragment;


/**
 * 
 */
public interface EditDialogListener {

	/**
	 * 
	 * @param dialog
	 */
	public void onDialogPositiveClick( DialogFragment dialog );

	/**
	 * 
	 * @param dialog
	 */
	public void onDialogNegativeClick( DialogFragment dialog );

}

