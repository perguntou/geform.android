package br.ufrj.del.geform.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.bean.Item;
import br.ufrj.del.geform.bean.Type;
import br.ufrj.del.geform.xml.XmlElements.Attribute;
import br.ufrj.del.geform.xml.XmlElements.Tag;

/**
 * This class enables the conversion between a XML based stream
 * and a {@link Form} using the XmlPull API.
 * @see XmlPullParser
 * @see XmlSerializer
 * @see XmlPullParserFactory
 * @see InputStream
 * @see OutpuStream
 */
public final class FormXmlPull {

	private static final String FEATURE_INDENT_OUTPUT = "http://xmlpull.org/v1/doc/features.html#indent-output";

	private static final String ns = XmlPullParser.NO_NAMESPACE;
	private static final String encoding = "utf-8";

	/**
	 * Processes the content of the given {@link InputStream} instance as XML
	 * into a {@link Form} data structure.
	 * @param in InputStream containing the content the be parsed.
	 * @return the resultant form from parsing.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static Form parse( InputStream in ) throws XmlPullParserException, IOException, ParseException  {
		try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
			parser.setInput( in, encoding );
			parser.nextTag();

			final Form form = readForm( parser );
			return form;
		} finally {
			in.close();
		}
	}

	/**
	 * Processes the content from the specified {@link Form} instance into the given
	 * {@link OutputStream} instance as XML.
	 * @param form the form to be serialized.
	 * @param out target output stream.
	 * @throws XmlPullParserException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static void serialize( Form form, OutputStream out ) throws XmlPullParserException, IllegalArgumentException, IllegalStateException, IOException {
		try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			parserFactory.setNamespaceAware( true );
			XmlSerializer serializer = parserFactory.newSerializer();
			serializer.setOutput( out, encoding );
			serializer.setFeature( FEATURE_INDENT_OUTPUT, true );

			serializer.startDocument( encoding, null );
			writeForm( form, serializer );
			serializer.endDocument();
		} finally {
			out.close();
		}
	}

	/**
	 * Internal method that parses the contents of a form.
	 * @param parser the responsible for parsing.
	 * @return the resultant form from parse.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException 
	 * @see XmlPullParser
	 * @see Form
	 */
	private static Form readForm( XmlPullParser parser ) throws XmlPullParserException, IOException, ParseException {
		Form form = new Form();

		parser.require( XmlPullParser.START_TAG, ns, Tag.FORM.toString() );
		while( parser.next() != XmlPullParser.END_TAG ) {
			final int eventType = parser.getEventType();
			if( eventType != XmlPullParser.START_TAG ) {
				continue;
			}
			final String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case TITLE:
				final String textTitle = readText( parser );
				form.setTitle( textTitle );
				break;
			case AUTHOR:
				final String textAuthor = readText( parser );
				form.setAuthor( textAuthor );
				break;
			case DESCRIPTION:
				final String textDescription = readText( parser );
				form.setDescription( textDescription );
				break;
			case TIMESTAMP:
				final String textTimestamp = readText( parser );
				final SimpleDateFormat dateFormat = new SimpleDateFormat( XmlElements.DATE_FORMAT, Locale.US );
				final Date timestamp = dateFormat.parse( textTimestamp );
				form.setTimestamp( timestamp );
				break;
			case ITEM:
				final Item item = readItem( parser );
				form.add( item );
				break;
			default:
				final String logTag = String.format( "%s.%s", FormXmlPull.class.getName(), FormXmlPull.class.getEnclosingMethod().getName() );
				if( Log.isLoggable( logTag, Log.WARN ) ) {
					final String message = String.format( "Case %s not handled in this switch.", tag );
					Log.w( logTag, message );
				}
			}
		}
		return form;
	}

	/**
	 * Internal method that serializes the contents of a form.
	 * @param form the form to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @see Form
	 * @see XmlSerializer
	 */
	private static void writeForm( Form form, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {
		serializer.startTag( ns, Tag.FORM.toString() );

		final Date timestamp = form.getTimestamp();
		if( timestamp != null ) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat( XmlElements.DATE_FORMAT, Locale.US );
			final String stringTs = dateFormat.format( timestamp );
			serializeSimpleTextElement( stringTs, Tag.TIMESTAMP, serializer );
		}
		final String author = form.getAuthor();
		if( author != null ) {
			serializeSimpleTextElement( author, Tag.AUTHOR, serializer );
		}
		final String title = form.title();
		serializeSimpleTextElement( title, Tag.TITLE, serializer );
		final String description = form.getDescription();
		if( description != null ) {
			serializeSimpleTextElement( description, Tag.DESCRIPTION, serializer );
		}
		for( Item item : form ) {
			writeItem( item, serializer );
		}

		serializer.endTag( ns, Tag.FORM.toString() );
	}

	/**
	 * Internal method that parses the contents of an form's item.
	 * @param parser the responsible for parsing.
	 * @return the resultant item from parse.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see XmlPullParser
	 * @see Item
	 */
	private static Item readItem( XmlPullParser parser ) throws XmlPullParserException, IOException {
		Item item = new Item();

		parser.require( XmlPullParser.START_TAG, ns, Tag.ITEM.toString() );
		final String att = parser.getAttributeValue( ns, Attribute.TYPE.toString() );
		final Type type = Type.fromValue( att );
		item.setType( type );
		while( parser.next() != XmlPullParser.END_TAG ) {
			if( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case QUESTION:
				final String textQuestion = readText( parser );
				item.setQuestion( textQuestion );
				break;
			case OPTIONS:
				final List<String> options = readOptions( parser );
				item.setOptions( options );
				break;
			default:
				final String logTag = String.format( "%s.%s", FormXmlPull.class.getName(), FormXmlPull.class.getEnclosingMethod().getName() );
				if( Log.isLoggable( logTag, Log.WARN ) ) {
					final String message = String.format( "Case %s not handled in this switch.", tag );
					Log.w( logTag, message );
				}
			}
		}

		return item;
	}

	/**
	 * Internal method that serializes the contents of an form's item.
	 * @param item the item to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see Item
	 * @see XmlSerializer
	 */
	private static void writeItem( Item item, XmlSerializer serializer ) throws XmlPullParserException, IOException {
		serializer.startTag( ns, Tag.ITEM.toString() );
		final Type type = item.getType();
		serializer.attribute( ns, Attribute.TYPE.toString(), type.toString() );
		serializeSimpleTextElement( item.getQuestion(), Tag.QUESTION, serializer );
		if( item.hasOptions() ) {
			writeOptions( item.getOptions(), serializer );
		}
		serializer.endTag( ns, Tag.ITEM.toString() );
	}

	/**
	 * Internal method that parses the contents of an item's options.
	 * @param parser the responsible for parsing.
	 * @return the resultant options from parse.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see XmlPullParser
	 */
	private static List<String> readOptions( XmlPullParser parser ) throws XmlPullParserException, IOException {
		List<String> options = new ArrayList<String>();

		parser.require( XmlPullParser.START_TAG, ns, Tag.OPTIONS.toString() );
		while( parser.next() != XmlPullParser.END_TAG ) {
			if( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case OPTION:
				final String option = readOption(parser);
				options.add( option );
				break;
			default:
				final String logTag = String.format( "%s.%s", FormXmlPull.class.getName(), FormXmlPull.class.getEnclosingMethod().getName() );
				if( Log.isLoggable( logTag, Log.WARN ) ) {
					final String message = String.format( "Case %s not handled in this switch.", tag );
					Log.w( logTag, message );
				}
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, Tag.OPTIONS.toString() );

		return options;
	}

	/**
	 * Internal method that serializes the contents of an item's options.
	 * @param options the options to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @see XmlSerializer
	 */
	private static void writeOptions( List<String> options, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag( ns, Tag.OPTIONS.toString() );
		Iterator<String> it = options.iterator();
		while( it.hasNext() ) {
			final String option = it.next();
			writeOption( option, serializer );
		}
		serializer.endTag( ns, Tag.OPTIONS.toString() );
	}

	/**
	 * Internal method that parses a single option.
	 * @param parser the responsible for parsing.
	 * @return the resultant options from parse.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static String readOption( XmlPullParser parser ) throws XmlPullParserException, IOException
	{
		parser.require( XmlPullParser.START_TAG, ns, Tag.OPTION.toString() );
		final String value = readText( parser );
		final String option = value.trim();
		parser.require( XmlPullParser.END_TAG, ns, Tag.OPTION.toString() );

		return option;
	}

	/**
	 * Internal method that serializes a single option.
	 * @param option the option to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private static void writeOption( String option, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializeSimpleTextElement( option, Tag.OPTION, serializer );
	}
	
	/**
	 * Internal method that extracts text values.
	 * @param parser the responsible for parsing.
	 * @return the text extracted.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see XmlPullParser
	 */
	private static String readText( XmlPullParser parser ) throws XmlPullParserException, IOException {
		String text = new String();
		if( parser.next() == XmlPullParser.TEXT ) {
			text = parser.getText();
			parser.nextTag();
		}
		return text;
	}

	/**
	 * Internal method that serializes a simple text element.
	 * @param value the value associated to the element.
	 * @param tag the tag element.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private static void serializeSimpleTextElement( String value, Tag tag, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag( ns, tag.toString() );
			serializer.text( value );
		serializer.endTag( ns, tag.toString() );
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
