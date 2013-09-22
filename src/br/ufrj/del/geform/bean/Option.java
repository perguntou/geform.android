/**
 * 
 */
package br.ufrj.del.geform.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class Option extends IdentifiableBean implements Parcelable {

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
	 * @return the value
	 */
	public String getValue() {
		return m_value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue( final String value ) {
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
		super.writeToParcel( out, flags );
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
		public Option[] newArray( int size ) {
			return new Option[size];
		}
	};

	/**
	 * Constructs a new Option instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	public Option( Parcel in ) {
		super( in );
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
