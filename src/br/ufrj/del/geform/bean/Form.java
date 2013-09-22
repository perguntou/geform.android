package br.ufrj.del.geform.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class extends {@link IdentifiableBean}.
 */
public class Form extends IdentifiableBean implements Parcelable {

	/*
	 * the form title.
	 */
	private String m_title;

	private String m_creator;

	private String m_description;

	private Date m_timestamp;

	private List<Item> m_items;

	/**
	 * Constructs a new Form instance with zero initial capacity
	 * and no title.
	 */
	public Form() {
		this( new String(), new ArrayList<Item>() );
	}

	/**
	 * Constructs a new Form instance with the specified title and
	 * containing the items of the specified collection.
	 * @param title the new form title.
	 * @param items the collection of items to add.
	 * @see Item
	 */
	public Form( String title, List<Item> items ) {
		this.setTitle( title );
		this.setItems( items );
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
	public String getTitle() { return m_title; }

	/**
	 *	Sets the form title.
	 *	@param	title the form title.
	 */
	public void setTitle( String title ) { m_title = title; }

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return m_creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator( String creator ) {
		m_creator = creator;
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
	 * @return the items
	 */
	public List<Item> getItems() {
		return m_items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems( List<Item> items ) {
		this.m_items = items;
	}

	/**
	 * 
	 * @param item
	 */
	public void add( final Item item ) {
		final List<Item> items = this.getItems();
		items.add( item );
	}

	/**
	 * 
	 * @param position
	 * @param item
	 */
	public void add( int position, final Item item ) {
		final List<Item> items = this.getItems();
		items.add( position, item );
	}

	/**
	 * Replaces the element at the specified position in the item's list with the specified item.
	 * This operation does not change the size of the items.
	 * @param position the index at which to put the specified item.
	 * @param item the item to insert.
	 * @return the previous item at the position.
	 */
	public Item set( int position, final Item item ) {
		final List<Item> items = this.getItems();
		final Item previousItem = items.set( position, item );
		return previousItem;
	}

	/**
	 * 
	 * @param position
	 */
	public Item get( int position ) {
		final List<Item> items = this.getItems();
		final Item item = items.get( position );
		return item;
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public Item remove( int position ) {
		final List<Item> items = this.getItems();
		final Item item = items.remove( position );
		return item;
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		final List<Item> items = this.getItems();
		final int size = items.size();
		return size;
	}

	/**
	 * Returns a new Form with the same elements, the same size,
	 * the same capacity as this Form but no ID.
	 * @return a shallow copy of this Form.
	 */
	@Override
	public Object clone() {
		Form cp = new Form();
		final String copyTitle = String.format( "%s_cp", this.getTitle() );
		cp.setTitle( copyTitle );
		cp.setDescription( this.getDescription() );
		cp.setItems( this.getItems() );
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
		super.writeToParcel( out, flags );
		out.writeString( this.getTitle() );
		out.writeString( this.getCreator() );
		out.writeString( this.getDescription() );
		final Date timestamp = this.getTimestamp();
		final Long time = timestamp == null ? null : timestamp.getTime();
		out.writeValue( time );
		out.writeTypedList( this.getItems() );
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
		super( in );
		final String title = in.readString();
		this.setTitle( title );
		final String creator = in.readString();
		this.setCreator( creator );
		final String description = in.readString();
		this.setDescription( description );
		final Long time = (Long) in.readValue( Long.class.getClassLoader() );
		if( time != null ) {
			final Date timestamp = new Date( time );
			this.setTimestamp( timestamp );
		}
		final List<Item> items = in.createTypedArrayList( Item.CREATOR );
		this.setItems( items );
	}

}
