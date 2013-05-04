package br.ufrj.softwaresmartphone.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import br.ufrj.softwaresmartphone.util.Options.ChoiceType;

/**
 * This class enables the conversion between a XML based stream
 * and a {@link Form} using Simple API for XML (SAX).
 * @see SAXParser
 * @see SAXParserFactory
 * @see SAXTransformerFactory
 * @see TransformerHandler
 */
public final class FormSAX {
	
	/**
	 * Processes the content of the given {@link InputStream} instance as XML
	 * into a {@link Form} data structure.
	 * @param in InputStream containing the content the be parsed.
	 * @return the resultant form from parse.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Form parse( InputStream in ) throws ParserConfigurationException, SAXException, IOException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();

			SAXParser parser = parserFactory.newSAXParser();
			FormHandler handler = new FormHandler();

//			Schema schema;
//			SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
//			schema = schemaFactory.newSchema( new File( "FormSchema.xsd" ) );
//			parserFactory.setSchema( schema );

			parser.parse( in, handler );
			return handler.getForm();
		} finally {
			in.close();
		}
	}

	/**
	 * Processes the content from the specified {@link Form} instance into the given
	 * {@link OutputStream} instance as XML.
	 * @param form the form to be serialized.
	 * @param out target output stream.
	 * @throws TransformerConfigurationException
	 * @throws SAXException
	 * @throws IOException 
	 */
	public static void serialize( Form form, OutputStream out ) throws TransformerConfigurationException, SAXException, IOException	{
		try {
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler handler = factory.newTransformerHandler();

			handler.setResult( new StreamResult( out ) );
			handler.getTransformer().setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
			handler.getTransformer().setOutputProperty( OutputKeys.INDENT, "yes" );
//			handler.getTransformer().setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
			
			handler.startDocument();
			handler.startElement( "", "", "form" , null );
				handler.startElement( "", "", "title", null );
					handler.characters( form.title().toCharArray(), 0, form.title().length() );
				handler.endElement( "", "", "title" );
				for( Item item : form ) {
					handler.startElement( "", "", "item" , null );
						handler.startElement( "", "", "question" , null );
							handler.characters( item.getQuestion().toCharArray(), 0, item.getQuestion().length() );
						handler.endElement( "", "", "question" );
						if( item.hasOptions() ) {
							AttributesImpl choiceAtt = new AttributesImpl();
							choiceAtt.addAttribute( "", "", "choice", "", String.valueOf( item.getOptions().getChoiceType() ) );
							handler.startElement( "", "", "options", choiceAtt );
							for( String option : item.getOptions() ) {
								handler.startElement( "", "", "option", choiceAtt );
								handler.characters( option.toCharArray(), 0, option.length() );
								handler.endElement( "", "", "option" );
							}
							handler.endElement( "", "", "options" );
						}
					handler.endElement( "", "", "item" );
				}
			handler.endElement( "", "", "form" );
			handler.endDocument();
		} finally {
			out.close();
		}
	}

	/**
	 * This class extends {@link DefaultHandler} to support parsing
	 * into a {@link Form} data structure.
	 */
	private static class FormHandler extends DefaultHandler {

		private Form m_form;
		private String m_builder;
		private Item m_currentItem;
		private Options m_currentOptions;

		/**
		 * Returns the handled {@link Form} instance.
		 * @return the handled form.
		 */
		public Form getForm() { return m_form; }

		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException {
			super.characters( ch, start, length );
			m_builder = String.copyValueOf( ch, start, length );
		}

		@Override
		public void endElement( String uri, String localName, String name )	throws SAXException {
			super.endElement( uri, localName, name );
			
			if( localName.equals( "form" ) ) {
			} else
			if( localName.equals( "title" ) ) {
				m_form.setTitle( m_builder.toString() );
			} else
			if( localName.equals( "item" ) ) {
				m_form.add( m_currentItem );
			} else
			if( localName.equals( "question" ) ) {
				m_currentItem.setQuestion( m_builder );
			} else
			if( localName.equals( "options" ) ) {
				m_currentItem.setOptions( m_currentOptions );
			} else
			if( localName.equals( "option" ) ) {
				m_currentOptions.add( m_builder );
			} else {
				throw new SAXException( "Invalid tag: " + localName );
			}
		}
		
		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			m_form = new Form();
			m_builder = new String("");
		}

		@Override
		public void startElement( String uri, String localName, String name, Attributes attributes ) throws SAXException {
			super.startElement( uri, localName, name, attributes );

			if( localName.equals( "form" ) ) {
			} else
			if( localName.equals( "title" ) ) {
			} else
			if( localName.equals( "item" ) ) {
				m_currentItem = new Item();
			} else
			if( localName.equals( "question" ) ) {
			} else
			if( localName.equals( "options" ) ) {
				if( attributes != null ) {
					String attValue = attributes.getValue( uri, "choice" ); 
					m_currentOptions = new Options();
					if( attValue.equals( "single" ) ) {
						m_currentOptions.setChoiceType( ChoiceType.single );
					} else
					if( attValue.equals( "multiple" ) ) {
						m_currentOptions.setChoiceType( ChoiceType.multiple );
					} else {
						throw new SAXException( "Invalid value for attribute 'choice'" );
					}	
				} else {
					throw new SAXException( "Missing required attribute 'choice'" );
				}
			} else
			if( localName.equals( "option" ) ) {
			} else {
				throw new SAXException( "Invalid tag: " + localName );
			}
		}

	}

}
