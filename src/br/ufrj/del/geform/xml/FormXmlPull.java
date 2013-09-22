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
import java.util.ListIterator;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.bean.Item;
import br.ufrj.del.geform.bean.Option;
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
public final class FormXmlPull extends AbstractXmlPull {

	private static FormXmlPull m_instance;

	public static FormXmlPull getInstance() {
		if( m_instance == null ) {
			m_instance = new FormXmlPull();
		}
		return m_instance;
	}

	/**
	 * Processes the content of the given {@link InputStream} instance as XML
	 * into a {@link Form} data structure.
	 * @param in InputStream containing the content the be parsed.
	 * @return the resultant form from parsing.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Form> parse( InputStream in ) throws XmlPullParserException, IOException, ParseException  {
		XmlPullParserFactory parserFactory = m_instance.getFactory();
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
		parser.setInput( in, encoding );
		parser.nextTag();

		final List<Form> result = new ArrayList<Form>(1);
		final Form form = readForm( parser );
		result.add( form );
		return result;
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
	@Override
	public <T> void serialize( final List<T> forms, final OutputStream out ) throws XmlPullParserException, IllegalArgumentException, IllegalStateException, IOException {
		@SuppressWarnings("unchecked")
		final List<Form> list = (List<Form>) forms;
		XmlPullParserFactory parserFactory = m_instance.getFactory();
		parserFactory.setNamespaceAware( true );
		XmlSerializer serializer = parserFactory.newSerializer();
		serializer.setOutput( out, encoding );
		serializer.setFeature( FEATURE_INDENT_OUTPUT, true );

		serializer.startDocument( encoding, null );
		for( ListIterator<Form> iterator = list.listIterator(); iterator.hasNext(); ) {
			final Form form = iterator.next();
			writeForm( form, serializer );
		}
		serializer.endDocument();
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

		parser.require( XmlPullParser.START_TAG, namespace, Tag.FORM.toString() );
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
			case CREATOR:
				final String creator = readText( parser );
				form.setCreator( creator );
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
		serializer.startTag( namespace, Tag.FORM.toString() );

		final Date timestamp = form.getTimestamp();
		if( timestamp != null ) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat( XmlElements.DATE_FORMAT, Locale.US );
			final String stringTs = dateFormat.format( timestamp );
			serializeSimpleTextElement( stringTs, Tag.TIMESTAMP, serializer );
		}
		final String creator = form.getCreator();
		if( creator != null ) {
			serializeSimpleTextElement( creator, Tag.CREATOR, serializer );
		}
		final String title = form.getTitle();
		serializeSimpleTextElement( title, Tag.TITLE, serializer );
		final String description = form.getDescription();
		if( description != null ) {
			serializeSimpleTextElement( description, Tag.DESCRIPTION, serializer );
		}
		final List<Item> items = form.getItems();
		for( Item item : items ) {
			writeItem( item, serializer );
		}

		serializer.endTag( namespace, Tag.FORM.toString() );
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

		parser.require( XmlPullParser.START_TAG, namespace, Tag.ITEM.toString() );
		final String att = parser.getAttributeValue( namespace, Attribute.TYPE.toString() );
		final Type type = Type.fromValue( att );
		item.setType( type );
		while( parser.next() != XmlPullParser.END_TAG ) {
			if( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case ID:
				final String textId = readText( parser );
				final long id = Long.getLong( textId, Item.NO_ID ); 
				item.setId( id );
				break;
			case QUESTION:
				final String textQuestion = readText( parser );
				item.setQuestion( textQuestion );
				break;
			case OPTIONS:
				final List<Option> options = readOptions( parser );
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
		serializer.startTag( namespace, Tag.ITEM.toString() );
		final Type type = item.getType();
		serializer.attribute( namespace, Attribute.TYPE.toString(), type.toString() );
		final long id = item.getId();
		if( id != Item.NO_ID ) {
			serializeSimpleTextElement( String.valueOf( id ), Tag.ID, serializer );
		}
		serializeSimpleTextElement( item.getQuestion(), Tag.QUESTION, serializer );
		if( item.hasOptions() ) {
			writeOptions( item.getOptions(), serializer );
		}
		serializer.endTag( namespace, Tag.ITEM.toString() );
	}

	/**
	 * Internal method that parses the contents of an item's options.
	 * @param parser the responsible for parsing.
	 * @return the resultant options from parse.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see XmlPullParser
	 */
	private static List<Option> readOptions( XmlPullParser parser ) throws XmlPullParserException, IOException {
		List<Option> options = new ArrayList<Option>();

		parser.require( XmlPullParser.START_TAG, namespace, Tag.OPTIONS.toString() );
		while( parser.next() != XmlPullParser.END_TAG ) {
			if( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case OPTION:
				final Option option = readOption(parser);
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
		parser.require(XmlPullParser.END_TAG, namespace, Tag.OPTIONS.toString() );

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
	private static void writeOptions( List<Option> options, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag( namespace, Tag.OPTIONS.toString() );
		Iterator<Option> it = options.iterator();
		while( it.hasNext() ) {
			final Option option = it.next();
			writeOption( option, serializer );
		}
		serializer.endTag( namespace, Tag.OPTIONS.toString() );
	}

	/**
	 * Internal method that parses a single option.
	 * @param parser the responsible for parsing.
	 * @return the resultant options from parse.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static Option readOption( XmlPullParser parser ) throws XmlPullParserException, IOException
	{
		parser.require( XmlPullParser.START_TAG, namespace, Tag.OPTION.toString() );

		final Option option = new Option();
		while( parser.next() != XmlPullParser.END_TAG ) {
			if( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			String name = parser.getName();
			final Tag tag = Tag.fromString( name );
			switch( tag ) {
			case ID:
			{
				final String textId = readText( parser );
				final long id = Long.getLong( textId, Option.NO_ID );
				option.setId( id );
				break;
			}
			case VALUE:
			{
				final String text = readText( parser );
				final String value = text.trim();
				option.setValue( value );
				break;
			}
			default:
				final String logTag = String.format( "%s.%s", FormXmlPull.class.getName(), FormXmlPull.class.getEnclosingMethod().getName() );
				if( Log.isLoggable( logTag, Log.WARN ) ) {
					final String message = String.format( "Case %s not handled in this switch.", tag );
					Log.w( logTag, message );
				}
			}
		}
		parser.require( XmlPullParser.END_TAG, namespace, Tag.OPTION.toString() );

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
	private static void writeOption( Option option, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag( namespace, Tag.OPTION.toString() );

		final long id = option.getId();
		if( id != Option.NO_ID ) {
			serializeSimpleTextElement( String.valueOf( id ), Tag.ID, serializer );
		}
		final String value = option.getValue();
		serializeSimpleTextElement( value, Tag.VALUE, serializer );

		serializer.endTag( namespace, Tag.OPTION.toString() );
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

}
