package br.ufrj.del.geform.bean;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Collection implements Parcelable {

	private Form m_reference;
	private SparseArray<Answer> m_collection;
	private String m_collector;

	/**
	 * 
	 * @param reference
	 */
	public Collection( Form reference ) {
		setReference( reference );
		setCollection( new SparseArray<Answer>() );
	}

	/**
	 * @return the reference
	 */
	public Form getReference() {
		return m_reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference( Form reference ) {
		this.m_reference = reference;
	}

	/**
	 * @return the collector
	 */
	public String getCollector() {
		return m_collector;
	}

	/**
	 * @param collector the collector to set
	 */
	public void setCollector( String collector ) {
		this.m_collector = collector;
	}

	/**
	 * 
	 * @param collection the collection to set
	 */
	private void setCollection( SparseArray<Answer> collection ) {
		m_collection = collection;
	}

	/**
	 * 
	 * @return the collection
	 */
	public SparseArray<Answer> getCollection() {
		return m_collection;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Answer get( int key ) {
		return m_collection.get( key, new Answer() );
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put( int key, Answer value ) {
		m_collection.put( key, value );
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return m_collection.size();
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void add( int key, String value ) {
		final Answer answer = get( key );
		answer.add( value );
		m_collection.put( key, answer );
	}

	/**
	 * 
	 * @param key
	 */
	public void delete( int key ) {
		m_collection.delete( key );
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAllAnswered() {
		final int numItems = m_reference.size();
		final int numAnswers = size();
		final boolean hasSameSize = numItems == numAnswers;
		return hasSameSize;
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
		out.writeParcelable( this.getReference(), flags );
		out.writeString( this.getCollector() );
		final Bundle bundle = new Bundle( Answer.class.getClassLoader() );
		bundle.putSparseParcelableArray( "collection", this.getCollection() );
		out.writeBundle( bundle );
	}

	public static final Parcelable.Creator<Collection> CREATOR
	= new Parcelable.Creator<Collection>() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		public Collection createFromParcel( Parcel in ) {
			return new Collection( in );
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Collection[] newArray( int size ) {
			return new Collection[ size ];
		}
	};

	/**
 	 * Constructs a new Collection instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	private Collection( Parcel in ) {
		final Form reference = in.readParcelable( Form.class.getClassLoader() );
		this.setReference( reference );
		final String collector = in.readString();
		this.setCollector( collector );
		final Bundle bundle = in.readBundle( Answer.class.getClassLoader() );
		final SparseArray<Answer> collection = bundle.getSparseParcelableArray( "collection" );
		this.setCollection( collection );
	}

}
