/**
 * 
 */
package br.ufrj.del.geform.app;

import java.util.List;

import junit.framework.Assert;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Answer;
import br.ufrj.del.geform.bean.Item;
import br.ufrj.del.geform.bean.Type;

/**
 *
 */
public class ItemFragment extends ListFragment {
		public static final String ARG_POSITION = "position";
		public static final String ARG_ITEM = "item";
		public static final String ARG_ANSWER = "answer";

		public static final String FRAGMENT_TAG = "edit_text_answer";

		protected Answer m_answer;

		/**
		 * @return the answer
		 */
		public Answer getAnswer() {
			return m_answer;
		}

		/*
		 * (non-Javadoc)
		 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
			Bundle args = getArguments();
			Assert.assertTrue( !args.isEmpty() );

			Assert.assertTrue( args.containsKey( ItemFragment.ARG_ANSWER ) );
			m_answer = args.getParcelable( ItemFragment.ARG_ANSWER );

			Assert.assertTrue( args.containsKey( ItemFragment.ARG_ITEM ) );
			final Item item = args.getParcelable( ItemFragment.ARG_ITEM );

			View view = inflater.inflate( R.layout.fragment_item, container, false );

			final TextView questionView = (TextView) view.findViewById( R.id.text_item_question );
			questionView.setText( item.getQuestion() );

			final ListAdapter adapter;

			final Type type = item.getType();
			switch( type ) {
			case TEXT:
				Assert.assertTrue( m_answer.size() <= 1 );
				adapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_list_item_1, m_answer );
				final TextView empty = (TextView) view.findViewById( android.R.id.empty );
				empty.setOnClickListener(
						new View.OnClickListener() {
							/*
							 * (non-Javadoc)
							 * @see android.view.View.OnClickListener#onClick(android.view.View)
							 */
							@Override
							public void onClick( View v ) {
								editTextAnswerDialog( new String() );
							}
						}
				);
				break;
			case SINGLE_CHOICE:
				List<String> optionsSingle = item.getOptions();
				adapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_list_item_single_choice, optionsSingle );
				break;
			case MULTIPLE_CHOICE:
				List<String> optionsMultiple = item.getOptions();
				adapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_list_item_multiple_choice, optionsMultiple );
				break;
				default:
					throw new IllegalArgumentException( String.format("Invalid item Type (%s)",type) );
			}

			setListAdapter( adapter );
			return view;
		}

		/*
		 * (non-Javadoc)
		 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
		 */
		@Override
		public void onViewCreated( View view, Bundle savedInstanceState ) {
			super.onViewCreated( view, savedInstanceState );

			Bundle args = getArguments();
			Assert.assertTrue( !args.isEmpty() );

			Assert.assertTrue( args.containsKey( ItemFragment.ARG_ITEM ) );
			final Item item = args.getParcelable( ItemFragment.ARG_ITEM );

			final ListView listView = getListView();

			final Type type = item.getType();
			switch( type ) {
			case TEXT:
				listView.setChoiceMode( ListView.CHOICE_MODE_NONE );
				break;
			case SINGLE_CHOICE:
				listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
				for( int i = 0; i < m_answer.size(); i++ ) {
					listView.setItemChecked( Integer.valueOf( m_answer.get( i ) ), true );
				}
				break;
			case MULTIPLE_CHOICE:
				listView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
				for( int i = 0; i < m_answer.size(); i++ ) {
					listView.setItemChecked( Integer.valueOf( m_answer.get( i ) ), true );
				}
				break;
				default:
					throw new IllegalArgumentException( String.format("Invalid item Type (%s)",type) );
			}

		}

		/*
		 * (non-Javadoc)
		 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
		 */
		@Override
		public void onListItemClick( ListView listView, View view, int position, long id ) {
			super.onListItemClick( listView, view, position, id );

			Bundle args = getArguments();
			final Item item = args.getParcelable( ItemFragment.ARG_ITEM );

			final Type type = item.getType();
			switch( type ) {
			case TEXT:
				Assert.assertTrue( m_answer.size() <= 1 );
				final String itemClicked = (String) listView.getItemAtPosition(position); 
				editTextAnswerDialog( itemClicked );
				break;
			case SINGLE_CHOICE:
			case MULTIPLE_CHOICE:
				m_answer.clear();
				SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
				for( int i = 0; i < checkedItems.size(); i++ ) {
					if( checkedItems.valueAt(i) ) {
						m_answer.add( String.valueOf( checkedItems.keyAt( i ) ) );
					}
				}
				break;
			default:
				throw new IllegalArgumentException( String.format("Invalid item Type (%s)",type) );
			}
		}

		/**
		 * Creates an edit dialog to change a text answer
		 * @param text the initial value
		 * @see EditDialog
		 */
		public void editTextAnswerDialog( String text ) {
			final EditDialog newFragment = new EditDialog() {
				@Override
				void onPositiveClick() {
					String inputValue = getInputValue();
					updateAnswer( inputValue );
				}
			};

			final Bundle args = new Bundle();
			args.putString( EditDialog.ARGUMENT_VALUE, text );
			newFragment.setArguments( args );

			newFragment.show( getFragmentManager(), FRAGMENT_TAG );
		}

		/**
		 * @param inputValue
		 */
		public void updateAnswer(String inputValue) {
			if( "".equals( inputValue ) ) {
				m_answer.clear();
			} else {
				if( m_answer.isEmpty() ) {
					m_answer.add( 0, inputValue );
				} else {
					m_answer.set( 0, inputValue );
				}
			}
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}

}
