package com.ing.eatwhat.gridview;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ing.eatwhat.R;
import com.ing.eatwhat.gridview.MyImageView.OnMeasureListener;
import com.ing.eatwhat.gridview.NativeImageLoader.NativeImageCallBack;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

public class StickyGridAdapter extends BaseAdapter{

	private List<GridItem> list;
	private LayoutInflater mInflater;
	private GridView mGridView;
	private int count;
	private Point mPoint = new Point(0, 0);			//用来封装ImageView的宽和高的对象 

	public StickyGridAdapter(Context context, List<GridItem> list, GridView mGridView) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
		count = list.size();
	}

	@Override
	public int getCount() {
		if(count == 0){
			return 1;
		} else{
			return count + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.test_gridview_item, parent, false);
			mViewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.iv_frag_food_menu_item_pic);
			mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_frag_food_menu_item_name);
			convertView.setTag(mViewHolder);
			
			 //用来监听ImageView的宽和高  
			mViewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {          
                @Override  
                public void onMeasureSize(int width, int height) {  
                    mPoint.set(width, height);  
                }  
            }); 		
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		if(position == getCount()-1){
			mViewHolder.mImageView.setImageResource(R.drawable.add);
			mViewHolder.mTextView.setText("添加");
		} else {			
			String path = list.get(position).getpath();
			String name = list.get(position).getname();
			mViewHolder.mImageView.setTag(path);
			mViewHolder.mTextView.setText(name);

			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint,
					new NativeImageCallBack() {
						@Override
						public void onImageLoader(Bitmap bitmap, String path) {
							ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
							if (bitmap != null && mImageView != null) {
								mImageView.setImageBitmap(bitmap);
							}
						}
					});

			if (bitmap != null) {
				mViewHolder.mImageView.setImageBitmap(bitmap);
			} else {
				mViewHolder.mImageView.setImageResource(R.drawable.add);
			}
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		public MyImageView mImageView;
		public TextView mTextView;
	}

	public static class HeaderViewHolder {
		public TextView mTextView;
	}

}
