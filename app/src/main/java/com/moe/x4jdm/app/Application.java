package com.moe.x4jdm.app;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import com.moe.x4jdm.CrashActivity;
import java.security.SecureRandom;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import android.content.Context;
import com.tencent.wstt.gt.controller.GTRController;

public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(final Thread p1,final Throwable p2)
	{
		new Thread(){
			public void run(){
				if(p2==null)return;
				StringBuffer sb=new StringBuffer(p2.getMessage());
				try
				{
					sb.append("\n").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).append("\n").append(Build.MODEL).append(" ").append(Build.VERSION.RELEASE).append("\n");
				}
				catch (PackageManager.NameNotFoundException e)
				{}
				for (StackTraceElement element:p2.getStackTrace())
					sb.append("\n").append(element.toString());
				Intent intent=new Intent(getApplicationContext(),CrashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
				startActivity(intent);
			}
		}.start();
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
@Override
	public void onCreate()
	{
		super.onCreate();
		
		//UMConfigure.init(this, "5e743b09978eea0774044ead", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
		//Bugly.init(this,"39c93f2bb3",false);
		Thread.currentThread().setUncaughtExceptionHandler(this);
		trustEveryone();
		GTRController.init(this);
		//Fresco.initialize(this);
		//LeakCanary.install(this);
	}
	
	public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
								 public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
								 }

								 public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
								 }

								 public X509Certificate[] getAcceptedIssuers() {
									 return new X509Certificate[0];
								 }
							 } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}
