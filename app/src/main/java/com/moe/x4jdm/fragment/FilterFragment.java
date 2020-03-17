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
import com.moe.x4jdm.adapter.FilterAdapter;
import android.os.Handler;
import android.os.Message;

public class FilterFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,FilterAdapter.OnChangeListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private int page;
	private int count;
	private JSONArray post_data;
	private boolean loadMore,canloadmore=true;
	private IndexAdapter pa;
	private FilterAdapter fa;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.filter_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout=view.findViewById(R.id.swiperefreshlayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		mRecyclerView=view.findViewById(R.id.recyclerview);
		fa=new FilterAdapter(JSONArray.parseArray(Index.getModel(getContext()).getFilter()));
		mRecyclerView.setLayoutManager(new IndexGridLayoutManager(getContext(),fa));
		//((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.setAdapter(fa);
		mRecyclerView.setNestedScrollingEnabled(false);
		fa.setOnChangeListener(this);
		mRecyclerView=view.findViewById(R.id.recyclerview2);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemViewCacheSize(20);
		mRecyclerView.setDrawingCacheEnabled(true);
		mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		mRecyclerView.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics())));
		mRecyclerView.addOnScrollListener(new Scroll());
		this.pa=new IndexAdapter(post_data=new JSONArray());
		mRecyclerView.setLayoutManager(new IndexGridLayoutManager(getContext(),pa));
		//((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.setAdapter(pa);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		Bundle b=getArguments();
		if(b!=null){
			page=b.getInt("page",1);
		}
		handler.sendEmptyMessageDelayed(0,300);
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
					Index index=Index.getModel(getContext());
					final JSONObject jo=JSONObject.parseObject(index.getList(String.format(index.makeFilter(fa.getKey()),page)));
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
								if(page==2){
									int i=pa.getItemCount();
									post_data.clear();
									pa.notifyItemRangeRemoved(pa.getItemCount(),i-pa.getItemCount());
									count=0;
								}
								int count=pa.getItemCount();
								
								if(items!=null)
									post_data.addAll(items);
								//pa.notifyDataSetChanged();
								pa.notifyItemRangeInserted(count,pa.getItemCount()-count);


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

	@Override
	public void onChange()
	{
		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0,300);
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			mSwipeRefreshLayout.setRefreshing(true);
			onRefresh();
		}
		
	};
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
