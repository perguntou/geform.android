/**
 * 
 */
package br.ufrj.del.geform.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import android.util.Pair;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.bean.IdentifiableBean;

/**
 *
 */
public class NetworkHelper {

	public static final String SERVER_URL = "http://10.0.2.2:8080/GeForm/rest/forms";

	/**
	 * 
	 * @param formId
	 */
	public void downloadForm( long formId ) {
		if( formId == IdentifiableBean.NO_ID ) {
			final String message = String.format( "Invalid id (%s)", formId );
			throw new IllegalArgumentException( message );
		}
		final String path = String.format( "%s/%s", SERVER_URL, formId );
		URL url;
		try {
			url = new URL( path );
			downloadForm( url );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 */
	public void downloadForm( final URL url ) {
		final DownloadTask downloadTask = new DownloadTask() {
			@Override
			protected void onPreExecute() {
				NetworkHelper.this.onPreExecute();
			}
			@Override
			protected void onPostExecute( Form result ) {
				NetworkHelper.this.onPostDownload( result );
			}
		};
		downloadTask.execute( url );
	}

	/**
	 * 
	 * @param form
	 * @return
	 */
	public void uploadForm( final Form form ) {
		final UploadTask<Form> uploadTask = new UploadTask<Form>() {
			@Override
			protected void onPreExecute() {
				NetworkHelper.this.onPreExecute();
			}
			@Override
			protected void onPostExecute( Pair<Integer,String> result ) {
				final String stringValue = (result!= null) ? result.second : null;
				Long value = null;
				try {
					value = Long.parseLong( stringValue );
				} catch( NumberFormatException e ) {
				}
				NetworkHelper.this.onPostUpload( value );
			}
		};
		try {
			final URL url = new URL( SERVER_URL );
			uploadTask.execute( Arrays.asList(form), url );
		} catch( MalformedURLException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param form
	 * @return
	 */
	public void uploadCollections( final List<Collection> collections, final Long formId ) {
		final UploadTask<Collection> uploadTask = new UploadTask<Collection>() {
			@Override
			protected void onPreExecute() {
				NetworkHelper.this.onPreExecute();
			}
			@Override
			protected void onPostExecute( Pair<Integer,String> result ) {
				final String stringValue = (result!= null) ? result.second : null;
				final Long value = Long.valueOf( stringValue );
				NetworkHelper.this.onPostUpload( value );
			}
		};
		final String path = String.format( "%s/%s", SERVER_URL, formId );
		try {
			final URL url = new URL( path );
			uploadTask.execute( collections, url );
		} catch( MalformedURLException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	protected void onPreExecute() {
		//do nothing
	}

	/**
	 * 
	 * @param result
	 */
	protected void onPostUpload( Long result ) {
		//do nothing
	}

	/**
	 * 
	 * @param result
	 */
	protected void onPostDownload( Form result ) {
		//do nothing
	}

}
