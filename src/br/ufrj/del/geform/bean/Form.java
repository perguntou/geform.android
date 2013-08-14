package br.ufrj.del.geform.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class extends ArrayList< {@link Item} >.
 * @see ArrayList
 */
public class Form extends ArrayList<Item> implements Parcelable {

	/*
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * initial form ID (not assigned)
	 */
	public static final long NO_ID = -1;

	/*
	 * the form title.
	 */
	private String m_title;

	/*
	 * the form's unique identifier
	 */
	private Long m_id = Long.valueOf( NO_ID );

	private String m_author;

	private String m_description;

	private Date m_timestamp;

	/**
	 * Constructs a new Form instance with zero initial capacity
	 * and no title.
	 */
	public Form() {
		this( new String("") );
	}

	/**
	 * Constructs a new Form instance with the specified title and
	 * containing the items of the specified collection.
	 * @param title the new form title.
	 * @param items the collection of items to add.
	 * @see Item
	 */
	public Form( String title, Collection<? extends Item> items ) {
		super( items );
		setTitle( title );
	}

	/**
	 * Constructs a new Form instance with the specified title
	 * and no items.
	 * @param title the form title.
	 */
	public Form( String title ) {
		super();
		setTitle( title );
	}

	/**
	 *	Returns the form title.
	 *	@return the form title.
	 */
	public String title() { return m_title; }

	/**
	 *	Sets the form title.
	 *	@param	title the form title.
	 */
	public void setTitle( String title ) { m_title = title; }

	/**
	 *	Returns the form identifier.
	 *	@return the form ID.
	 */
	public Long id() { return m_id; }

	/**
	 *	Sets the form's unique identifier.
	 *	@param	id the form ID.
	 */
	public void setId( Long id ) {
		//FIXME ID should be assigned only once
		//but the form's copy ID must be reset
		//when clone() is called.
//		Assert.assertTrue( , m_id == NO_ID );
		m_id = id;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return m_author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor( String author ) {
		m_author = author;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return m_description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription( String description ) {
		m_description = description;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return m_timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp( Date timestamp ) {
		m_timestamp = timestamp;
	}

	/**
	 * Returns a new Form with the same elements, the same size,
	 * the same capacity as this Form but no ID.
	 * @return a shallow copy of this Form.
	 */
	@Override
	public Object clone() {
		Form cp = (Form) super.clone();
		cp.setTitle( this.title() + "_cp" );
		cp.setId( NO_ID );
		return cp;
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
		out.writeString( this.title() );
		out.writeLong( this.m_id );
		out.writeTypedList( this );
	}

	public static final Parcelable.Creator<Form> CREATOR
	= new Parcelable.Creator<Form>() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		public Form createFromParcel( Parcel in ) {
			return new Form( in );
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Form[] newArray(int size) {
			return new Form[size];
		}
	};

	/**
 	 * Constructs a new Form instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	private Form( Parcel in ) {
		this.m_title = in.readString();
		this.m_id = in.readLong();
		in.readTypedList( this, Item.CREATOR );
	}

}
