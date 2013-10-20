/**
 * 
 */
package br.ufrj.del.geform.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.os.AsyncTask;
import android.util.Pair;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.xml.AbstractXmlPull;
import br.ufrj.del.geform.xml.CollectionXmlPull;
import br.ufrj.del.geform.xml.FormXmlPull;

/**
 *
 */
public class UploadTask<T> extends AsyncTask<Object, Void, Pair<Integer,String>> {

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Pair<Integer,String> doInBackground( Object... parameters ) {
		final int size = parameters.length;
		if( size != 2 ) {
			throw new IllegalArgumentException();
		}
		try {
			@SuppressWarnings("unchecked")
			final List<T> objects = (List<T>) parameters[0];
			final URL url = (URL) parameters[1];
			final Pair<Integer,String> response = upload( objects, url );
			return response;
		} catch( ClassCastException e ) {
			throw new IllegalArgumentException(e);
		} catch( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param objects
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private Pair<Integer,String> upload( List<T> objects, final URL url ) throws IOException {
		final T sample = objects.get(0);
		final AbstractXmlPull xmlHandler;
		if( sample instanceof Collection ) {
			 xmlHandler = CollectionXmlPull.getInstance();
		} else if ( sample instanceof Form ) {
			xmlHandler = FormXmlPull.getInstance();
		} else {
			throw new IllegalArgumentException();
		}
		final Pair<Integer,String> serverResponse = uploadXml( objects, url, xmlHandler );
		return serverResponse;
	}

	/**
	 * 
	 * @param object
	 * @param url
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	private Pair<Integer,String> uploadXml( List<T> object, final URL url, final AbstractXmlPull handler ) throws IOException {
		Pair<Integer,String> serverResponse = null;
		OutputStream stream = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setConnectTimeout( 15000 /* milliseconds */ );
			conn.setChunkedStreamingMode( 0 );
			conn.setDoInput( true ); 
			conn.setDoOutput( true );
			conn.setUseCaches(false);
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "content-type", "application/xml; charset=utf-8" );
			final OutputStream outputStream = conn.getOutputStream();
			stream = new BufferedOutputStream( outputStream );
			handler.serialize( object, stream );

			final int serverResponseCode = conn.getResponseCode();
			final InputStream inputStream = conn.getInputStream();
			InputStream in = new BufferedInputStream( inputStream );
			final String serverResponseMessage = readResponse( in );
			serverResponse = new Pair<Integer,String>( serverResponseCode, serverResponseMessage );
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			if( stream != null ) {
				stream.close();
			}
			conn.disconnect();
		}
		return serverResponse;
	}

	/**
	 * 
	 * @param in
	 * @return
	 */
	private final String readResponse( InputStream in ) {
		final InputStreamReader inputStreamrReader = new InputStreamReader( in );
		BufferedReader reader = new BufferedReader( inputStreamrReader );
		StringBuilder response = new StringBuilder();
		String line;
		try {
			while( (line = reader.readLine()) != null ) {
				response.append(line);
			}
		} catch( IOException e ) {
			e.printStackTrace();
		}
		return response.toString();
	}

}
