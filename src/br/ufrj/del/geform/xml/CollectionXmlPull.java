package br.ufrj.del.geform.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import br.ufrj.del.geform.bean.Answer;
import br.ufrj.del.geform.bean.Collection;
import br.ufrj.del.geform.bean.Form;
import br.ufrj.del.geform.xml.XmlElements.Tag;

/**
 * This class enables the conversion between a XML based stream
 * and a {@link Collection} using the XmlPull API.
 * @see XmlPullParser
 * @see XmlSerializer
 * @see XmlPullParserFactory
 * @see InputStream
 * @see OutpuStream
 */
public final class CollectionXmlPull extends AbstractXmlPull {

	private static CollectionXmlPull m_instance;

	/**
	 * 
	 * @return
	 */
	public static CollectionXmlPull getInstance() {
		if( m_instance == null ) {
			m_instance = new CollectionXmlPull();
		}
		return m_instance;
	}

	/**
	 * Processes the content from the {@link Collection} list into the given
	 * {@link OutputStream} instance as XML.
	 * @param collections the list of collection to be serialized.
	 * @param out target output stream.
	 * @throws XmlPullParserException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@Override
	public <T> void serialize( List<T> collections, OutputStream out ) throws XmlPullParserException, IllegalArgumentException, IllegalStateException, IOException {
		@SuppressWarnings("unchecked")
		final List<Collection> list = (List<Collection>) collections;
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		parserFactory.setNamespaceAware( true );
		XmlSerializer serializer = parserFactory.newSerializer();
		serializer.setOutput( out, encoding );
		serializer.setFeature( FEATURE_INDENT_OUTPUT, true );

		serializer.startDocument( encoding, null );
		serializer.startTag( namespace, Tag.COLLECTIONS.toString() );
		for( final Collection collection : list ) {
			writeCollection( collection, serializer );
		}
		serializer.endTag( namespace, Tag.COLLECTIONS.toString() );
		serializer.endDocument();
	}

	/*
	 * (non-Javadoc)
	 * @see br.ufrj.del.geform.xml.AbstractXmlPull#parse(java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> parse( InputStream in ) {
		return null;
	}

	/**
	 * Internal method that serializes the contents of a collection.
	 * @param collection the collection to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @see Collection
	 * @see XmlSerializer
	 */
	private static void writeCollection( Collection collection, XmlSerializer serializer ) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {
		serializer.startTag( namespace, Tag.COLLECTION.toString() );

		final Form form = collection.getReference();
		final Long formId = form.getId();
		serializeSimpleTextElement( formId.toString(), Tag.FORM, serializer );
		final String collector = collection.getCollector();
		if( collector != null ) {
			serializeSimpleTextElement( collector, Tag.COLLECTOR, serializer );
		}
		for( int position = 0; position < collection.size(); ++position ) {
			final Answer answer = collection.get( position );
			if( answer.isEmpty() ) {
				throw new IllegalArgumentException();
			}
			writeAnswer( answer, serializer );
		}

		serializer.endTag( namespace, Tag.COLLECTION.toString() );
	}

	/**
	 * Internal method that serializes the contents of a collection's answer.
	 * @param answers the answers to be serialized.
	 * @param serializer the responsible for serialize.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @see Answer
	 * @see XmlSerializer
	 */
	private static void writeAnswer( Answer answer, XmlSerializer serializer ) throws XmlPullParserException, IOException {
		serializer.startTag( namespace, Tag.ITEM.toString() );
		for( final String simpleAnswer : answer ) {
			serializeSimpleTextElement( simpleAnswer, Tag.ANSWER, serializer );
		}
		serializer.endTag( namespace, Tag.ITEM.toString() );
	}

}
