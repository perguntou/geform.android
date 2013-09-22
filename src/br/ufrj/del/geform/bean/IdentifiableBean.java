/**
 * 
 */
package br.ufrj.del.geform.bean;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
@SuppressLint("ParcelCreator")
public abstract class IdentifiableBean implements Parcelable {

	/*
	 * initial ID (not assigned)
	 */
	public static final long NO_ID = -1;

	private long m_id;

	protected IdentifiableBean() {
		this.setId( NO_ID );
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return m_id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( final long id ) {
		this.m_id = id;
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
		out.writeLong( this.getId() );
	}

	/**
	 * Constructs a new Option instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	public IdentifiableBean( Parcel in ) {
		final long id = in.readLong();
		this.setId( id );
	}

}
