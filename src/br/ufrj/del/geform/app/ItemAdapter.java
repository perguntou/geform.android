package br.ufrj.del.geform.app;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.ufrj.del.geform.bean.Item;

/**
 *
 */
public class ItemAdapter extends ArrayAdapter<Item> {

	private ArrayList<Item> itemsData;
	private Activity context;

	/**
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param itemsData
	 */
	public ItemAdapter( Activity context, int textViewResourceId, ArrayList<Item> itemsData ) {
		super(context, textViewResourceId, itemsData);
		this.context = context;
		this.itemsData = itemsData;
	}

	/**
	 * 
	 */
	private class ViewHolder {
		TextView question;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		ViewHolder holder = null;
		if( convertView == null ) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = vi.inflate( android.R.layout.simple_list_item_1, null );

			holder = new ViewHolder();
			holder.question = (TextView) convertView.findViewById( android.R.id.text1 );
			holder.question.setEllipsize( TruncateAt.END );

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Item item = itemsData.get( position );
		holder.question.setText( item.getQuestion() );

		return convertView;
	}

}
