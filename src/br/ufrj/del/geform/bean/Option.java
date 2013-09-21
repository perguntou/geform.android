/**
 * 
 */
package br.ufrj.del.geform.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class Option implements Parcelable {

	/*
	 * initial option ID (not assigned)
	 */
	public static final long NO_ID = -1;

	private Long m_id = NO_ID;
	private String m_value;

	public Option() {
		//do nothing
	}

	/**
	 * 
	 * @param value
	 */
	public Option( final String value ) {
		this.setValue( value );
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return m_id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( Long id ) {
		this.m_id = id;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return m_value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue( String value ) {
		this.m_value = value;
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
		out.writeString( this.getValue() );
	}

	public static final Parcelable.Creator<Option> CREATOR
	= new Parcelable.Creator<Option>() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		public Option createFromParcel( Parcel in ) {
			return new Option( in );
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Option[] newArray(int size) {
			return new Option[size];
		}
	};

	/**
	 * Constructs a new Option instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	public Option( Parcel in ) {
		final Long id = in.readLong();
		this.setId( id );
		final String value = in.readString();
		this.setValue( value );
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue();
	}

}
