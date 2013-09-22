package br.ufrj.del.geform.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 */
public class Item implements Parcelable {

	private String m_question;
	private Type m_type;
	private List<Option> m_options;

	/**
	 * Constructs a new Item instance with no question
	 * and no options.
	 */
	public Item() {
		this( new String() );
	}

	/**
	 * Constructs a new Item instance with the specified question
	 * and no options.
	 * @param question the item's question.
	 */
	public Item( String question ) {
		this( question, null, Type.TEXT );
	}

	/**
	 * Constructs a new Item instance with the specified title,
	 * options and type.
	 * @param question the item question
	 * @param options the item options
	 * @param type the item type
	 * @see Option
	 * @see Type
	 */
	public Item( final String question, final List<Option> options, final Type type ) {
		this.setQuestion( question );
		this.setOptions( options );
		this.setType( type );
	}

	/**
	 * Returns the Item's question.
	 * @return the question.
	 */
	public String getQuestion() { return m_question; }

	/**
	 * Sets the item's question.
	 * @param question the item's question.
	 */
	public void setQuestion( String question )	{ m_question = question; }

	/**
	 * @return the type
	 */
	public Type getType() {	return m_type; }

	/**
	 * @param type the type to set
	 */
	public void setType( Type type ) { m_type = type; }

	/**
	 * Returns the item's options.
	 * @return the item's options.
	 */
	public List<Option> getOptions() { return m_options; }

	/**
	 * Sets the item's options.
	 * @param options the item's options.
	 */
	public void setOptions( List<Option> options ) { m_options = options; }

	/**
	 * Returns if this Item has options.
	 * @return true if this Item has no options, false otherwise.
	 */
	public boolean hasOptions() {
		final List<Option> options = this.getOptions();
		final boolean hasOptions = options != null && !options.isEmpty(); 
		return hasOptions;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel( Parcel out, int flags ) {
		out.writeString( this.getQuestion() );
		out.writeString( this.getType().toString() );
		out.writeTypedList( this.getOptions() );
	}

	public static final Parcelable.Creator<Item> CREATOR
	= new Parcelable.Creator<Item>() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		public Item createFromParcel( Parcel in ) {
			return new Item( in );
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	/**
	 * Constructs a new Item instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	private Item( Parcel in ) {
		final String question = in.readString();
		this.setQuestion( question );
		final String typeString = in.readString();
		final Type type = Type.fromValue( typeString );
		this.setType( type );
		final List<Option> options = in.createTypedArrayList( Option.CREATOR );
		this.setOptions( options );
	}

}
