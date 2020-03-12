package com.moe.x4jdm.widget;
import android.support.v7.widget.GridLayoutManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class IndexGridLayoutManager extends GridLayoutManager
{
	public IndexGridLayoutManager(Context context,final RecyclerView.Adapter adaptet){
		super(context,3);
		setSpanSizeLookup(new SpanSizeLookup(){

				@Override
				public int getSpanSize(int p1)
				{
					switch(adaptet.getItemViewType(p1)){
						case 1://header
						case 2://tab
						case 3://header_title
						case 5://post_line
						case 8:
						case 7://load_more
						case 100:
						return 3;
						case 4:
						case 6:
							return 1;
					}
					return 1;
				}
			});
	}
}
