package com.drivingevaluate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.drivingevaluate.R;
import com.drivingevaluate.model.Image;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class StatusGridImgsAdapter extends BaseAdapter {

	private Context context;
	private List<Image> datas;
	private ImageLoader imageLoader;

	public StatusGridImgsAdapter(Context context, List<Image> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Image getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_image, null);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.image_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		GridView gv = (GridView) parent;
		int horizontalSpacing = gv.getHorizontalSpacing();
		int numColumns = gv.getNumColumns();
		int itemWidth = (gv.getWidth() - (numColumns-1) * horizontalSpacing
				- gv.getPaddingLeft() - gv.getPaddingRight()) / numColumns;

		LayoutParams params = new LayoutParams(itemWidth, itemWidth);
		holder.iv_image.setLayoutParams(params);

		Image urls = getItem(position);
		imageLoader.displayImage(urls.getUrl(), holder.iv_image);
		
		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_image;
	}

}
