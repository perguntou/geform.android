/**
 * 
 */
package br.ufrj.del.geform.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class Answer extends ArrayList<String> implements Parcelable {

	private static final long serialVersionUID = 1L;

	public Answer() {
		super();
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
		out.writeStringList( this );
	}

	public static final Parcelable.Creator<Answer> CREATOR
	= new Parcelable.Creator<Answer>() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		public Answer createFromParcel( Parcel in ) {
			return new Answer( in );
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Answer[] newArray( int size ) {
			return new Answer[ size ];
		}
	};

	/**
 	 * Constructs a new Answer instance from a {@link Parcel}.
	 * @param in the Parcel
	 */
	private Answer( Parcel in ) {
		in.readStringList( this );
	}

}
