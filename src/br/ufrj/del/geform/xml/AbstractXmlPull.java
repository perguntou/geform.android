/**
 * 
 */
package br.ufrj.del.geform.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import br.ufrj.del.geform.xml.XmlElements.Tag;

/**
 *
 */
public abstract class AbstractXmlPull {

	protected static final String FEATURE_INDENT_OUTPUT = "http://xmlpull.org/v1/doc/features.html#indent-output";

	protected static final String namespace = XmlPullParser.NO_NAMESPACE;
	protected static final String encoding = "utf-8";

	protected XmlPullParserFactory m_factory;

	AbstractXmlPull() {
		try {
			m_factory = XmlPullParserFactory.newInstance();
		} catch( XmlPullParserException e ) {
			e.printStackTrace();
		}
	}

	protected XmlPullParserFactory getFactory() {
		return m_factory;
	}

	public abstract <T> void serialize( final List<T> toSerialize, final OutputStream out ) throws Exception;

	public abstract <T> List<T> parse( final InputStream in ) throws Exception;

	/**
	 * Internal method that serializes a simple text element.
	 * @param value the value associated to the element.
	 * @param tag the tag element.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	protected static void serializeSimpleTextElement( String value, Tag tag, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag( namespace, tag.toString() );
			serializer.text( value );
		serializer.endTag( namespace, tag.toString() );
	}

	/**
	 * Internal method that extracts text values.
	 * @param parser the responsible for parsing.
	 * @return the text extracted.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see XmlPullParser
	 */
	protected String readText( final XmlPullParser parser ) throws XmlPullParserException, IOException {
		String text = new String();
		if( parser.next() == XmlPullParser.TEXT ) {
			text = parser.getText();
			parser.nextTag();
		}
		return text;
	}

	/**
	 * @param tag
	 * @param parser
	 * @param method
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	protected void unhandledTag( final Tag tag, final XmlPullParser parser, final String method ) throws XmlPullParserException, IOException {
		final String logTag = getClass().getSimpleName();
		if( Log.isLoggable( logTag, Log.WARN ) ) {
			final String message = String.format( "%s: tag '%s' not handled.", method, tag );
			Log.w( logTag, message );
		}
		readText( parser );
	}

//	/**
//	 * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
//	 * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
//	 * finds the matching END_TAG (as indicated by the value of "depth" being 0).
//	 * @param parser XmlPullParser instance
//	 * @throws XmlPullParserException
//	 * @throws IOException
//	 */
//	private static void skip( XmlPullParser parser ) throws XmlPullParserException, IOException {
//		if( parser.getEventType() != XmlPullParser.START_TAG ) {
//			throw new IllegalStateException();
//		}
//		int depth = 1;
//		while( depth != 0 ) {
//			switch( parser.next() ) {
//			case XmlPullParser.END_TAG:
//					depth--;
//					break;
//			case XmlPullParser.START_TAG:
//					depth++;
//					break;
//			}
//		}
//	}

}
