package com.moe.x4jdm.widget;
import android.support.v7.widget.GridLayoutManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;

public class IndexGridLayoutManager extends GridLayoutManager
{
	//private int over=200,overoffset;
	public IndexGridLayoutManager(Context context, final RecyclerView.Adapter adaptet)
	{
		super(context, 3);
		setSpanSizeLookup(new SpanSizeLookup(){

				@Override
				public int getSpanSize(int p1)
				{
					switch (adaptet.getItemViewType(p1))
					{
						case 0:
						case 1://header
						case 2://tab
						case 3://header_title
						case 5://post_line
						case 8:
						case 7://load_more
						case 9:
						case 10:
						case 11://imagepreview
						case 12://commemnt
						case 13://listcomic
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

	/*@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		int offset=super.scrollVerticallyBy(dy, recycler, state);
		int remain=state.getRemainingScrollVertical();
		if (offset == 0)
		{
			if (dy > 0)
			{
				//上拉
				if(overoffset==over)return 0;
				dy *= 0.5f;
				if (overoffset +dy>= over)
				{
					dy =  over-overoffset;
				}
				overoffset += dy;
				offsetChildrenVertical(-dy);
				return dy;
			}
		}else{
			if(remain>0)
				overoffset=0;
		}
		return offset;
	}

	@Override
	public void onScrollStateChanged(int state)
	{
		if(state==0&&overoffset>0){
		}
	}

*/
}
