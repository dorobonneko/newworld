package com.moe.x4jdm.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.R;
import com.moe.x4jdm.adapter.IndexAdapter;
import com.moe.x4jdm.model.Index;
import com.moe.x4jdm.util.Space;
import com.moe.x4jdm.widget.IndexGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.graphics.Rect;

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private String url;
	private int page;
	private int count;
	private JSONArray post_data;
	private boolean loadMore,canloadmore=true;
	private IndexAdapter pa;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout=view.findViewById(R.id.swiperefreshlayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView=view.findViewById(R.id.recyclerview);
		mRecyclerView.setItemViewCacheSize(20);
		mRecyclerView.setDrawingCacheEnabled(true);
		mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		mRecyclerView.setHasFixedSize(true);
		pa=new IndexAdapter(post_data=new JSONArray());
		mRecyclerView.setLayoutManager(new IndexGridLayoutManager(getContext(),pa));
		
		//((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics())));
		mRecyclerView.setAdapter(pa);
		mRecyclerView.addOnScrollListener(new Scroll());
		final Toolbar bar=getActivity().findViewById(R.id.toolbar);
		if(bar!=null)
		bar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){

				@Override
				public boolean onPreDraw()
				{
					bar.getViewTreeObserver().removeOnPreDrawListener(this);
					Rect rect=new Rect();
					bar.getGlobalVisibleRect(rect);
					mRecyclerView.setPadding(0,rect.bottom,0,0);
					//view.setPadding(0,rect.bottom,0,0);
					int offset=mSwipeRefreshLayout.getProgressCircleDiameter();
					mSwipeRefreshLayout.setProgressViewOffset(false,-offset,rect.bottom+offset);

					return false;
				}
			});else{
				mRecyclerView.setFitsSystemWindows(true);
				mRecyclerView.requestApplyInsets();
			}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		Bundle b=getArguments();
		if(b!=null){
			url=b.getString("url");
			page=b.getInt("page",1);
		}
		mSwipeRefreshLayout.setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onRefresh()
	{
		page=1;
		canloadmore=true;
		loadMore();
	}
	private void loadMore(){
		loadMore=true;
		new Thread(){
			public void run(){
				try{
				final JSONObject jo=JSONObject.parseObject(Index.getModel(getContext()).getList(url.replace("%d",String.valueOf(page))));
				getView().post(new Runnable(){

						@Override
						public void run()
						{
							loadMore=false;
							mSwipeRefreshLayout.setRefreshing(false);
							if(jo==null)return;
							if(jo.size()==0){
								pa.setFoot(R.drawable.check,"加载失败",false);
								return;
							}else{
								page=jo.getIntValue("page")+1;
								count=jo.getIntValue("count");
								canloadmore=page<=count;
								pa.setFoot(R.drawable.check,canloadmore?"加载完成":"已到底",false);
								
							}
							JSONArray items=jo.getJSONArray("item");
							int count=post_data.size();
							if(page==2){
								post_data.clear();
								mRecyclerView.getAdapter().notifyItemRangeRemoved(0,count);
								count=0;
							}
							if(items!=null)
							post_data.addAll(items);
							//pa.notifyDataSetChanged();
							pa.notifyItemRangeInserted(count+1,post_data.size()-count);
							
							
						}
					});
					}catch(Exception e){
						mSwipeRefreshLayout.post(new Runnable(){

								@Override
								public void run()
								{
									loadMore=false;
									mSwipeRefreshLayout.setRefreshing(false);
									
								}
							});
					}
			}
		}.start();
	}
	class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			GridLayoutManager glm=(GridLayoutManager) recyclerView.getLayoutManager();
			if(canloadmore&&!mSwipeRefreshLayout.isRefreshing()&&!loadMore&&dy>=0&&glm.findLastVisibleItemPosition()>glm.getItemCount()-glm.getSpanCount()){
				loadMore();
				pa.setFoot(R.drawable.loading,"正在加载",true);
			}
		}
	
}
	
}
