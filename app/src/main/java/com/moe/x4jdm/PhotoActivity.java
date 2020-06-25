package com.moe.x4jdm;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.bm.library.PhotoView;
import com.moe.pussy.Pussy;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import com.moe.pussy.utils.ProgressDrawable;
import android.view.Menu;
import java.io.File;
import android.os.Environment;
import android.webkit.URLUtil;
import com.moe.pussy.Listener;
import com.moe.pussy.Target;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener
{
	private PhotoView mPhotoView;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container_view);
		setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		((ViewGroup)findViewById(R.id.content)).addView(mPhotoView=new PhotoView(this),0);
		//mPhotoView.setBackgroundColor(0xff000000);
		mPhotoView.enable();
		mPhotoView.setOnClickListener(this);
		ProgressDrawable pd=new ProgressDrawable();
		Pussy.$(this).load(getIntent().getDataString()).listener(pd).execute().placeHolder(pd).into(mPhotoView);
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				onBackPressed();
				break;
			case 12:
				if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},418);
					break;
				}
				Pussy.$(this).load(getIntent().getDataString()).execute().listener(new Listener(){

						@Override
						public void onPlaceHolder(Target t, Drawable d)
						{
						}

						@Override
						public void onSuccess(Target t, Drawable d)
						{
							Toast.makeText(getApplicationContext(),"已保存",Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onError(Target t, Drawable d, Throwable e)
						{
							Toast.makeText(getApplicationContext(),"保存失败"+(e==null?"":e.getMessage()),Toast.LENGTH_SHORT).show();
						}
					}).download(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), URLUtil.guessFileName(getIntent().getDataString(), null, "image/*")));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,12,0,"Save");
		return true;
	}

	@Override
	public void onClick(View p1)
	{
		finish();
	}

	
}
