package br.ufrj.del.geform.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.xml.FormXmlPull;

/**
 * 
 */
public class DownloadTask extends AsyncTask<String, Void, Form> {

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Form doInBackground( String... urls ) {
		Form form = null;
		try {
			final String urlString = urls[0];
			form = download( urlString );
		} catch( IOException e ) {
			Log.e( "DownloadTask", e.getMessage() );
		} catch( XmlPullParserException e ) {
			Log.e( "DownloadTask", e.getMessage() );
		} catch( ParseException e ) {
			e.printStackTrace();
		}

		return form;
	}

	/**
	 * 
	 * @param urlString
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException
	 */
	private Form download( String urlString ) throws XmlPullParserException, IOException, ParseException {
		InputStream stream = null;
		Form form = null;

		try {
			stream = inputStreamFromURL( urlString );
			form = FormXmlPull.parse( stream );
		} finally {
			if( stream != null ) {
				stream.close();
			}
		}

		return form;
	}

	/**
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	private InputStream inputStreamFromURL( String urlString ) throws IOException {
		URL url = new URL( urlString );
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout( 10000 /* milliseconds */ );
		conn.setConnectTimeout( 15000 /* milliseconds */ );
		conn.setRequestMethod( "GET" );
		conn.setDoInput( true );
		conn.connect();

		return conn.getInputStream();
	}

}
