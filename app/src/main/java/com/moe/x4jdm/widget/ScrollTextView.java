package com.moe.x4jdm.widget;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;


public class ScrollTextView extends android.support.v7.widget.AppCompatTextView {
    public ScrollTextView(Context context) {
        super(context);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    float lastScrollY = 0;
	boolean start;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
		if(isNestedScrollingEnabled())
        switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				lastScrollY=ev.getRawY();
				start=startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
				return true;
			case MotionEvent.ACTION_MOVE:
				int diff=(int)(lastScrollY-ev.getRawY());
				lastScrollY=ev.getRawY();
				int[] con=new int[2],win=new int[2];
				if(dispatchNestedPreScroll(0,(int)diff,con,win)){
					if(con[1]<diff){
						diff=diff-con[1];
					}else{
						return true;
					}
				}
				int unUse=0,oldDiff=diff;
				
				if(diff>0){
					//上拉
					int preHeight=getScrollY()+getMeasuredHeight()+diff;
					int height=getLineCount()*getLineHeight();
					if(preHeight>height){
						diff=height-getScrollY()-getMeasuredHeight();
					}
				}else if(diff<0){
					if(getScrollY()+diff<0){
						diff=-getScrollY();
					}
				}
				
				scrollBy(0,(int)diff);
				unUse=oldDiff-diff;
				dispatchNestedScroll(0,diff,0,unUse,new int[]{0,-unUse});
				return true;
			case MotionEvent.ACTION_UP:
				
				break;
		}
        return super.onTouchEvent(ev);
    }

	@Override
	public boolean isNestedScrollingEnabled()
	{
		return true;
	}

	
	
}
