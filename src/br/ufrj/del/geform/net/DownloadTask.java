package br.ufrj.del.geform.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.xml.FormXmlPull;

/**
 * 
 */
public class DownloadTask extends AsyncTask<URL, Void, Form> {

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Form doInBackground( URL... urls ) {
		Form form = null;
		try {
			final URL url = urls[0];
			form = download( url );
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
	private Form download( URL url ) throws XmlPullParserException, IOException, ParseException {
		InputStream stream = null;
		Form form = null;

		try {
			stream = inputStreamFromURL( url );
			final FormXmlPull xmlHandler = FormXmlPull.getInstance();
			final List<Form> result = xmlHandler.parse( stream );
			form = result.get(0);
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
	private InputStream inputStreamFromURL( URL url ) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout( 10000 /* milliseconds */ );
		conn.setConnectTimeout( 15000 /* milliseconds */ );
		conn.setRequestMethod( "GET" );
		conn.setDoInput( true );
		conn.connect();

		return conn.getInputStream();
	}

}
