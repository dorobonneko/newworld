package com.moe.x4jdm;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;
import android.text.method.ArrowKeyMovementMethod;
import android.net.Uri;
import android.content.ClipboardManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

public class CrashActivity extends Activity
{
	StringBuffer sb;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		TextView tv=new TextView(this);
		tv.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
		sb= new StringBuffer(tv.getText());
		tv.setMovementMethod(new ArrowKeyMovementMethod());
		tv.setFitsSystemWindows(true);
		tv.setTextColor(0xff000000);
		tv.setTextIsSelectable(true);
		setContentView(tv);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem item=menu.add(0,0,0,"发送给开发者");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		/*((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setText(sb.insert(0, "@千羽樱 ").toString());
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://www.coolapk.com/apk/com.moe.LiveVisualizer"));
		startActivity(intent);*/
		return true;
	}
	
}
