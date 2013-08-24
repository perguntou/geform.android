/**
 * 
 */
package br.ufrj.del.geform.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import br.ufrj.del.geform.bean.Form;

/**
 *
 */
public class NetworkHelper {

	public static final String SERVER_URL = "http://dl.dropbox.com/u/50275577/sample.gef";

	/**
	 * 
	 * @param formId
	 * @return
	 */
	public static Form downloadForm( long formId ) {
		final DownloadTask downloadTask = new DownloadTask();
		//TODO get the form from the right path and using the input formId
		final String path = String.format( SERVER_URL );
		try {
			final URL url = new URL( path );
			final AsyncTask<URL, Void, Form> task = downloadTask.execute( url );
			final Form form = task.get();
			return form;
		} catch( InterruptedException e ) {
			e.printStackTrace();
		} catch( ExecutionException e ) {
			e.printStackTrace();
		} catch( MalformedURLException e ) {
			e.printStackTrace();
		}
		return null;
	}

}
