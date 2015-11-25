package com.example.dailyselfie;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private final Context context;
	private final ArrayList<Uri> fileUri;
	
	public ImageAdapter(Context context, ArrayList<Uri> fileUri) {
		super();
		this.context = context;
		this.fileUri = fileUri;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileUri.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView;
		
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.activity_selfie_list, parent,false);
		}
		else
		{
			rowView = convertView;
		}
		
		TextView textView = (TextView) rowView.findViewById(R.id.name);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.thumbnail);
		String filename = new File(fileUri.get(position).toString()).getName();
		textView.setText(filename);
		
		imageView.setImageURI(fileUri.get(position));
		
		return rowView;
	}
}
