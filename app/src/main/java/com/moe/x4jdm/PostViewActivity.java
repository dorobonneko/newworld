package com.moe.x4jdm;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.model.Indexx4jdm;
import android.text.Html;
import com.moe.tinyimage.Pussy;
import com.moe.tinyimage.CropTransForm;
import android.view.Gravity;
import com.moe.tinyimage.RoundTransForm;
import android.util.TypedValue;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.view.WindowInsets;
import android.support.design.widget.AppBarLayout;
import com.moe.tinyimage.BlurTransForm;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import com.alibaba.fastjson.JSONArray;
import com.moe.x4jdm.adapter.PlayViewPagerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.text.method.LinkMovementMethod;
import com.moe.x4jdm.model.Index;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;
import java.util.Map;
import android.widget.Button;
import java.io.IOException;
import android.view.Menu;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import com.moe.x4jdm.model.Database;
import android.view.MenuItem;
import com.moe.tinyimage.Anim;

public class PostViewActivity extends AppCompatActivity implements View.OnApplyWindowInsetsListener,PlayViewPagerAdapter.OnClickListener,View.OnClickListener
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
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_view);
		setSupportActionBar(toolbar = (Toolbar)findViewById(R.id.toolbar));
		url = getIntent().getStringExtra("url");
		key=getIntent().getStringExtra("key");
		icon = findViewById(R.id.icon);
		title = findViewById(R.id.title);
		summary = findViewById(R.id.summary);
		summary.setMovementMethod(LinkMovementMethod.getInstance());
		profile = findViewById(R.id.profile);
		profile.setMovementMethod(ScrollingMovementMethod.getInstance());
		backicon = findViewById(R.id.backicon);
		retry=findViewById(R.id.retry);
		retry.setOnClickListener(this);
		CollapsingToolbarLayout ctl=findViewById(R.id.collapsing);
		ctl.setTitleEnabled(false);
		getSupportActionBar().setTitle(null);
		//AppBarLayout abl=findViewById(R.id.appbarlayout);
		//abl.setFitsSystemWindows(true);
		//abl.setOnApplyWindowInsetsListener(this);
		//abl.requestFitSystemWindows()
		mTabLayout = findViewById(R.id.tablayout);
		mViewPager = findViewById(R.id.viewpager);
		mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		mTabLayout.setupWithViewPager(mViewPager, true);
		mViewPager.setAdapter(new PlayViewPagerAdapter(play_data = new JSONArray()));
		((PlayViewPagerAdapter)mViewPager.getAdapter()).setOnClickListener(this);
		load();
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
					final JSONObject jo=JSONObject.parseObject(Index.getModel(key).getPost(url));
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
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
								if(profile!=null)
								PostViewActivity.this.profile.setText(Html.fromHtml(profile));
								if(jo.getString("src")!=null){
								Pussy.$(PostViewActivity.this).load(jo.getString("src")).transForm(new CropTransForm(Gravity.CENTER), new RoundTransForm((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()))).Anim(Anim.cicle(300)).into(icon);
								Pussy.$(PostViewActivity.this).load(jo.getString("src")).transForm(new CropTransForm(Gravity.CENTER), new BlurTransForm(getApplicationContext(), 15)).into(backicon);
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
							{retry.setVisibility(View.VISIBLE);
							}
						});
				}
			}
		}.start();
	}

	@Override
	public void onClick(final JSONObject jo)
	{
		final ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("正在解析");
		pd.show();
		new Thread(){
			public void run()
			{
				final Map<String,String> url=Index.getModel(key).getVideoUrl(jo.getString("href"));
				if (url != null&&!url.isEmpty())
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								final String[] urls=url.values().toArray(new String[0]);
								
								new AlertDialog.Builder(PostViewActivity.this).setItems(url.keySet().toArray(new String[0]), new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface p1, int p2)
										{
											startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(urls[p2]), "video/*"));

										}
									}).show();
							}
						});
					else
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
				pd.dismiss();
			}
		}.start();
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.retry:
				load();
				p1.setVisibility(View.INVISIBLE);
				break;
		}
	}



}
