package com.moe.x4jdm;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.pussy.Pussy;
import com.moe.pussy.transformer.CropTransformer;
import com.moe.pussy.transformer.RoundTransformer;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.adapter.PlayViewPagerAdapter;
import com.moe.x4jdm.model.Database;
import com.moe.x4jdm.model.Index;
import java.util.Map;
import com.moe.pussy.Anim;
import com.moe.pussy.transformer.BlurTransformer;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.ViewGroup;
import android.support.design.widget.HeaderScrollingViewBehavior;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.x4jdm.adapter.PlayAdapter;
import android.support.design.widget.BottomSheetDialog;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import com.moe.pussy.Listener;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.view.ViewTreeObserver;
import android.text.Spanned;
import android.text.Layout;
import com.moe.pussy.Target;

public class PostViewActivity extends AppCompatActivity implements View.OnApplyWindowInsetsListener,PlayViewPagerAdapter.OnClickListener,View.OnClickListener,Listener,ViewTreeObserver.OnPreDrawListener
{
	private ImageView icon,backicon;
	private TextView title,summary,profile;
	private String url,key;
	private Toolbar toolbar;
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private JSONArray play_data;
	private Button retry;
	private JSONObject jo;
	private PlayAdapter pa;
	private RecyclerView recyclerview;
	private BottomSheetDialog sheet;
	private View progress;
	private AppBarLayout.LayoutParams params;
	private Spanned profileText;
	private int lineCount;
	private CollapsingToolbarLayout ctl;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		View v= getWindow().getDecorView();
		v.setSystemUiVisibility(v.getSystemUiVisibility()|v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		super.onCreate(savedInstanceState);
		progress=new ProgressBar(this);
		FrameLayout.LayoutParams fl=new FrameLayout.LayoutParams(-2,-2);
		fl.gravity=Gravity.CENTER;
		progress.setLayoutParams(fl);
		ViewGroup content=((ViewGroup)findViewById(android.R.id.content));
		content.addView(progress);
		new AsyncLayoutInflater(this).inflate(R.layout.post_view, content, new AsyncLayoutInflater.OnInflateFinishedListener(){

				@Override
				public void onInflateFinished(View p1, int p2, ViewGroup p3)
				{
					setSupportActionBar(toolbar = (Toolbar)p1.findViewById(R.id.toolbar));
					url = getIntent().getStringExtra("url");
					key=getIntent().getStringExtra("key");
					icon = p1.findViewById(R.id.icon);
					title = p1.findViewById(R.id.title);
					summary =p1. findViewById(R.id.summary);
					summary.setMovementMethod(LinkMovementMethod.getInstance());
					profile = p1.findViewById(R.id.profile);
					//profile.setMovementMethod(ScrollingMovementMethod.getInstance());
					backicon = p1.findViewById(R.id.backicon);
					retry=p1.findViewById(R.id.retry);
					retry.setOnClickListener(PostViewActivity.this);
					ctl=p1.findViewById(R.id.collapsing);
					ctl.setTitleEnabled(false);
					params=(AppBarLayout.LayoutParams) ctl.getLayoutParams();
					params.setScrollFlags(0);
					getSupportActionBar().setTitle(null);
					//AppBarLayout abl=findViewById(R.id.appbarlayout);
					//abl.setFitsSystemWindows(true);
					//abl.setOnApplyWindowInsetsListener(this);
					//abl.requestFitSystemWindows()
					mTabLayout =p1. findViewById(R.id.tablayout);
					mViewPager = p1.findViewById(R.id.viewpager);
					mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
					mTabLayout.setupWithViewPager(mViewPager, true);
					mViewPager.setAdapter(new PlayViewPagerAdapter(play_data = new JSONArray()));
					((PlayViewPagerAdapter)mViewPager.getAdapter()).setOnClickListener(PostViewActivity.this);
					icon.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
					sheet=new BottomSheetDialog(p1.getContext());
					sheet.setContentView(recyclerview=new RecyclerView(p1.getContext()),new ViewGroup.LayoutParams(-1,-2));
					recyclerview.setLayoutManager(new LinearLayoutManager(p1.getContext()));
					recyclerview.setAdapter(pa=new PlayAdapter());
					load();
					p3.addView(p1);
					//setContentView(p1);
				}
			});
		
	}

	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		toolbar.setPadding(0, p2.getSystemWindowInsetTop(), 0, 0);
		return p2;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.favorite,menu);
		ImageView view=(ImageView) menu.findItem(R.id.favorite).getActionView();
		TypedArray ta=obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
		view.setBackground(ta.getDrawable(0));
		ta.recycle();
		view.setImageResource(Database.getInstance(getApplicationContext()).query(url)?R.drawable.heart:R.drawable.heart_outline);
		view.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					ImageView view=(ImageView)p1;
					if(Database.getInstance(getApplicationContext()).query(url))
						Database.getInstance(getApplicationContext()).delete(url);
						else
					Database.getInstance(getApplicationContext()).insert(url,key);
					view.setImageResource(Database.getInstance(getApplicationContext()).query(url)?R.drawable.heart:R.drawable.heart_outline);
					if(jo!=null){
						Database.getInstance(getApplicationContext()).update(url,jo.getString("src"),jo.getString("title"),jo.getString("desc"));
					}
				}
			});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.favorite:
				
				break;
		}
		return true;
	}


	private void load()
	{
		new Thread(){
			public void run()
			{
				try
				{
					final JSONObject jo=JSONObject.parseObject(Index.getModel(getApplicationContext(),key).getPost(url));
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								progress.setVisibility(View.INVISIBLE);
								if (jo == null || jo.isEmpty()){
									retry.setVisibility(View.VISIBLE);
									return;
								}
								
								PostViewActivity.this.jo=jo;
								title.setText(jo.getString("title"));
								String desc=jo.getString("desc");
								if (desc != null)
									summary.setText(com.moe.x4jdm.util.Html.getClickableHtml(Html.fromHtml(desc), new com.moe.x4jdm.util.Html.OnClickListener(){

															@Override
															public void OnClick(String url)
															{
															}
														}));
								String profile=jo.getString("profile");
								if(profile!=null){
								TextView profiledesc=PostViewActivity.this.profile;
								profiledesc.setText(profileText=Html.fromHtml(profile.replaceAll("\n","<br/>")));
								profiledesc.getViewTreeObserver().addOnPreDrawListener(PostViewActivity.this);
								}
								if(jo.getString("src")!=null){
								Pussy.$(PostViewActivity.this).load(jo.getString("src")).execute().transformer(new CropTransformer(Gravity.CENTER), new RoundTransformer(getResources().getDisplayMetrics(),5)).anime(Anim.cicle(300)).into(icon);
								Pussy.$(PostViewActivity.this).load(jo.getString("src")).execute().transformer(new CropTransformer(Gravity.CENTER), new BlurTransformer(75)).listener(PostViewActivity.this).anime(Anim.fade(150)).into(backicon);
								}play_data.clear();
								JSONArray data=jo.getJSONArray("video");
								if (data != null)
									play_data.addAll(data);
								mViewPager.getAdapter().notifyDataSetChanged();
								Database.getInstance(getApplicationContext()).update(url,jo.getString("src"),jo.getString("title"),jo.getString("desc"));
							}
						});
				}
				catch (Exception e)
				{
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								progress.setVisibility(View.INVISIBLE);
								retry.setVisibility(View.VISIBLE);
							}
						});
				}
			}
		}.start();
	}

	@Override
	public void onClick(final JSONObject jo)
	{
		String click=jo.getString("click");
		if(click==null||click.equals("video")){
		final ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("正在解析");
		pd.show();
		new Thread(){
			public void run()
			{
				try{
				final Map<String,String> url=Index.getModel(PostViewActivity.this,key).getVideoUrl(jo.getString("href"));
					if (url != null&&!url.isEmpty())
						runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									pa.addAll(url);
									sheet.show();
								}
							});
						else throw new NullPointerException();
				}catch(Exception e){
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								try{startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(jo.getString("href")), "text/html"));}catch(Exception e){
									try{startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(jo.getString("href"))));}catch(Exception ee){

									}
								}

							}
						});
				}
					
				pd.dismiss();
			}
		}.start();
		}else if("list".equals(click)){
			startActivity(new Intent(getApplicationContext(),ListActivity.class).putExtra("key",key).putExtra("type","comic").putExtra("url",jo.getString("href")));
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.retry:
				load();
				progress.setVisibility(View.VISIBLE);
				p1.setVisibility(View.INVISIBLE);
				break;
			case R.id.profile:
				if(profile.getMaxLines()==3)
					profile.setMaxLines(lineCount);
					else
					profile.setMaxLines(3);
				break;
		}
	}

	@Override
	public void onSuccess(Target t,Drawable d)
	{
		View v= getWindow().getDecorView();
		v.setSystemUiVisibility(v.getSystemUiVisibility()&(~v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
		
	}

	@Override
	public void onError(Target t,Drawable d,Throwable e)
	{
	}

	@Override
	public void onPlaceHolder(Target t,Drawable d)
	{
	}

	@Override
	public boolean onPreDraw()
	{
		profile.getViewTreeObserver().removeOnPreDrawListener(this);
		if((lineCount=profile.getLineCount())>3){
			profile.setMaxLines(3);
			profile.setOnClickListener(this);
		}
		return false;
	}







}
