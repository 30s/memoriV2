package com.xtremeprog.memoriv2.adapters;

import java.security.acl.Owner;
import java.util.ArrayList;

import com.xtremeprog.memoriv2.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OwnerListAdapter extends BaseAdapter {

	private ArrayList<String> owners;

	public OwnerListAdapter(ArrayList<String> owner) {
		this.owners = owner;
	}
	
	@Override
	public int getCount() {
		return owners.size();
	}

	@Override
	public Object getItem(int position) {
		return owners.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_owner, null);
		}

		TextView txt_owner = (TextView) convertView.findViewById(R.id.txt_owner);
		txt_owner.setText((String) getItem(position));
		
		return convertView;
	}

}
