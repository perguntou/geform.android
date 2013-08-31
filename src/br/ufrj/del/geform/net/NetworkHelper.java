/**
 * 
 */
package br.ufrj.del.geform.net;

import java.net.MalformedURLException;
import java.net.URL;

import br.ufrj.del.geform.bean.Form;

/**
 *
 */
public class NetworkHelper {

	public static final String SERVER_URL = "http://10.0.2.2:8080/GeFormWS/rest/form";

	/**
	 * 
	 * @param formId
	 * @return
	 */
	public void downloadForm( long formId ) {
		final DownloadTask downloadTask = new DownloadTask() {
			@Override
			protected void onPreExecute() {
				NetworkHelper.this.onPreExecute();
			}
			@Override
			protected void onPostExecute( Form result ) {
				NetworkHelper.this.onPostExecute( result );
			}
		};
		final String path = String.format( "%s/%s", SERVER_URL, formId );
		try {
			final URL url = new URL( path );
			downloadTask.execute( url );
		} catch( MalformedURLException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	protected void onPreExecute() {}

	/**
	 * 
	 * @param result
	 */
	protected void onPostExecute( Form result ) {}

}
