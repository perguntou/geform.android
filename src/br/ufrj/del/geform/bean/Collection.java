package br.ufrj.del.geform.bean;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Collection implements Parcelable {

	private Form m_reference;
	private SparseArray<Answer> m_collection;

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
		out.writeParcelable( this.m_reference, flags );
		final Bundle bundle = new Bundle( Answer.class.getClassLoader() );
		bundle.putSparseParcelableArray( "collection", this.m_collection );
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
		m_reference = in.readParcelable( Form.class.getClassLoader() );
		final Bundle bundle = in.readBundle();
		bundle.setClassLoader( Answer.class.getClassLoader() );
		m_collection = bundle.getSparseParcelableArray( "collection" );
	}

}
