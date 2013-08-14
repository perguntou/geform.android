/**
 * 
 */
package br.ufrj.del.geform.net;

import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import br.ufrj.del.geform.Constants;
import br.ufrj.del.geform.bean.Form;

/**
 *
 */
public class NetworkHelper {

	public static Form downloadForm( long formId ) {
		final DownloadTask downloadTask = new DownloadTask();
		final AsyncTask<String, Void, Form> task = downloadTask.execute( Constants.SERVER_URL );
		try {
			final Form form = task.get();
			return form;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
