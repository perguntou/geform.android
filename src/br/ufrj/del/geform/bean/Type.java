/**
 * 
 */
package br.ufrj.del.geform.bean;


/**
 *
 */
public enum Type {
	TEXT( "text" ),
	SINGLE_CHOICE( "single" ),
	MULTIPLE_CHOICE( "multiple" );

	private final String type;

	Type( final String type ) {
		this.type = type;
	}

	public static Type fromValue( final String value ) {
		for( Type type : Type.values() ){
			if( type.toString().equals(value) ){
				return type;
			}
		}
		throw new IllegalArgumentException( String.format( "%s is not a valid Type", value ) );
	}

    @Override
	public String toString() {
		return type;
	}

}
