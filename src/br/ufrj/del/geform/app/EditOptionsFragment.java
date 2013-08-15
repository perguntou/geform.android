package br.ufrj.del.geform.app;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import br.ufrj.del.geform.R;
import br.ufrj.del.geform.bean.Item;

/**
 *
 */
public class EditOptionsFragment extends ListFragment {

	public static final String FRAGMENT_TAG = "edit_option";
	public static final String ARGUMENT_INDEX = "index";

	private List<String> m_options;
	private MenuItem m_menuItem;

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		final Item item = ((EditItemActivity) getActivity()).getItem();
		List<String> options = item.getOptions();
		m_options = (options != null) ? options : new ArrayList<String>();

		View view = inflater.inflate( R.layout.edit_options, container, false );

		setHasOptionsMenu( true );

		setListAdapter( new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_list_item_1, m_options ) );

		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
		m_menuItem = menu.add( R.string.menu_add_option );
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			m_menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
		}
		m_menuItem.setIcon( R.drawable.ic_menu_plus );
		super.onCreateOptionsMenu( menu, inflater );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		final int menuItemId = m_menuItem.getItemId();
		if( item.getItemId() == menuItemId ) {
			editOptionDialog( new String(), m_options.size() );
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick( ListView listView, View view, int position, long id ) {
		editOptionDialog( getListView().getItemAtPosition( position ).toString(), position );
	}

	/**
	 * Creates an edit dialog to change an option's value
	 * @param option the initial value
	 * @param position the index of the option
	 * @see EditDialog
	 */
	public void editOptionDialog( String option, int position ) {
		final EditDialog newFragment = new EditDialog() {
			@Override
			void onPositiveClick() {
				final String inputValue = getInputValue();
				final Bundle args = getArguments();
				final int position = args.getInt( ARGUMENT_INDEX );
				final int size = m_options.size();
				if( "".equals( inputValue ) ) {
					if( position < size ) {
						m_options.remove( position );
					}
				} else {
					if( position < size ) {
						m_options.set( position, inputValue );
					} else {
						m_options.add( inputValue );
					}
				}
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}
		};

		final Bundle args = new Bundle();
		final String title = getString( R.string.dialog_edit_option );
		args.putString( EditDialog.ARGUMENT_TITLE, title );
		args.putString( EditDialog.ARGUMENT_VALUE, option );
		args.putInt( ARGUMENT_INDEX, position );
		newFragment.setArguments( args );

		newFragment.show( getFragmentManager(), FRAGMENT_TAG );
	}

	/**
	 * Returns the edited options
	 * @return the options
	 */
	public List<String> getOptions() {
		return m_options;
	}

}
