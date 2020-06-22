package com.moe.x4jdm.widget;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.graphics.Rect;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView.State;
import java.util.ArrayList;
import java.util.List;

public class SpanLayoutManager extends RecyclerView.LayoutManager
{
	private SpanSizeLookup lookup;
	private int totalHeight,offset;
	
	public SpanLayoutManager(){
		this(new SpanSizeLookup(){

				@Override
				public Span getSpan(int position)
				{
					return new Span(1,1);
				}
			});
	}
	public SpanLayoutManager(SpanSizeLookup lookup){
		this.lookup=lookup;
		setAutoMeasureEnabled(true);
		}
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams()
	{
		return new RecyclerView.LayoutParams(-1,-2);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		fill(recycler,state);
	}
	
	public void fill(RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		if(getItemCount()==0){
			removeAndRecycleAllViews(recycler); return;}
		if(state.isPreLayout())return;
		int offsetx=getPaddingStart(),offsety=getPaddingTop()-offset,h=1,maxWidth=getWidth(),columnWidth=maxWidth,maxh=0;
		detachAndScrapAttachedViews(recycler);
		List<AnchorInfo> list=new ArrayList<>();
		for (int position=0;position < getItemCount();position++) {
			View child=recycler.getViewForPosition(position);
			Span span=lookup.getSpan(position);
			int usedw=span.h==span.v?0:(maxWidth/span.h*(span.h-span.v));
			measureChildWithMargins(child,usedw,0);addView(child);
			//calculateItemDecorationsForChild(child,rect);
			//int width=getDecoratedMeasuredWidth(child);
			int height=getDecoratedMeasuredHeight(child);
			if(h==span.h){
				//相同列数
				//检查空间是否够用
				if(offsetx+columnWidth*span.v>maxWidth){
					//另起一行
				}else{
					//设置控件位置
					Rect rect=new Rect(offsetx,offsety,(offsetx+=columnWidth*span.v),offsety+height);
					layoutDecoratedWithMargins(child,rect.left,rect.top,rect.right,rect.bottom);
					maxh=Math.max(maxh,height);
					if(offsety<getHeight()&&offsety+height>0)
					{
						AnchorInfo info=new AnchorInfo();
						info.v=child;
						info.rect=rect;
						list.add(info);
					}
					else
						removeAndRecycleView(child,recycler);
					continue;//进入下一个控件
				}
			}
			{
				//另起一行
				h=span.h;
				columnWidth=maxWidth/h;
				offsety+=maxh;
				offsetx=getPaddingStart();
				maxh=0;
				Rect rect=new Rect(offsetx,offsety,(offsetx+=columnWidth*span.v),offsety+height);
				layoutDecoratedWithMargins(child,rect.left,rect.top,rect.right,rect.bottom);
				maxh=Math.max(maxh,height);
				if(offsety<getHeight()&&offsety+height>0)
				{
					AnchorInfo info=new AnchorInfo();
					info.v=child;
					info.rect=rect;
					list.add(info);
				}
				else
					removeAndRecycleView(child,recycler);

			}


		}
		totalHeight=offsety+maxh-getPaddingTop()+offset;
		detachAndScrapAttachedViews(recycler);
		for(int i=0;i<list.size();i++){
			AnchorInfo info=list.get(i);
			addView(info.v);
			layoutDecoratedWithMargins(info.v,info.rect.left,info.rect.top,info.rect.right,info.rect.bottom);
		}
	}
	@Override
	public boolean canScrollVertically()
	{
		return true;
	}
	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		//列表向下滚动dy为正，列表向上滚动dy为负，这点与Android坐标系保持一致。
		//实际要滑动的距离
		int travel = dy;
		if(offset+dy>totalHeight-getHeight()+getPaddingTop()+getPaddingBottom()){
			travel=totalHeight-offset-getHeight()+getPaddingTop()+getPaddingBottom();
		}else if(offset+dy<0){
			travel=-offset;
		}
		offset+=travel;
		//offsetChildrenVertical(-travel);
		fill(recycler,state);
		return travel;
	}
	public static abstract class SpanSizeLookup{
		abstract Span getSpan(int position);
	}
	/*h总列数 v当前空间占据的列数*/
	public static class Span{
		public Span(int h,int v){
			this.v=v;
			this.h=h;
		}
		int v,h;
	}
	class AnchorInfo{
		View v;
		Rect rect;
	}
}
