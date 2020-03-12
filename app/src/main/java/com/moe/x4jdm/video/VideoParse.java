package com.moe.x4jdm.video;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.apache.commons.codec.digest.Md5Crypt;
import com.moe.x4jdm.util.Md5;
import android.util.Base64;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoParse
{
	private static Pattern mUriMatcher=Pattern.compile("http(|s)://(v.youku.com|v.qq.com|www.iqiyi.com|tv.cctv.com|my.tv.sohu.com|m.tv.sohu.com|www.bilibili.com|www.le.com|www.dilidili.wang)/.*");
	public static boolean match(String url){
		return mUriMatcher.matcher(url).find();
	}
	public static String parse(String url,String site){
		switch(site){
			//case "pptv":
			//	return parsePptv(url);
			case "qq":
				return parseQQ(url);
			//case "sohu":
				//return parseSohu(url);
			default:
			return parseIqiyi(url);
		}
		//return null;
	}
	public static String parseQqSign(String sign){
		try
		{
			Connection.Response res=Jsoup.connect("https://quan.qq.com/video/" + sign.substring(0,37)).method(Connection.Method.HEAD).followRedirects(false).ignoreContentType(true).execute();
			if(res.statusCode()==301||res.statusCode()==302){
			String location=res.header("Location");
			if(location!=null)
				location="https:/"+location.substring(location.indexOf("/",9));
				return location;
			}
		}
		catch (IOException e)
		{}
		return null;
	}
	public static String parseQQ(String url){
		try{
			if(url.matches("[0-9]{0,4}_.*")){
			/*/JSONObject jo=JSONObject.parseObject(Jsoup.connect("http://test4.diyiwl.wang/testapi777.php?time=1578841013&url="+url.substring(0,37)).userAgent("Mozilla (Linux,Android 10.0)").ignoreContentType(true).method(Connection.Method.GET).execute().body());
			if(jo.getIntValue("success")==1){
				return jo.getString("url");
			}*/
			return parseQqSign(url);
			}else if(url.startsWith("http://quan.qq.com/video/")){
				return parseQqSign(url.substring(25));
			}
			}catch(Exception e){}
			return url;
	}
	public static String parsePptv(String url){
		return null;
	}
	public static String parseSohu(String url){
		return null;
	}
	public static String parseIqiyi(String url){
		String src=null;
		try
		{
			String doc=Jsoup.connect("https://api.6uzi.com/vip.php?url="+url).referrer("http://api.6uzi.com/").execute().body();
			Matcher m=Pattern.compile("post.*?time':'(.*?)','url':'(.*?)','url':'(.*?)','",Pattern.MULTILINE).matcher(doc);
			if(m.find()){
			String body=String.format("time=%s&url=%s&referer=%s&ref=1&type=&other=%s&ios=0",m.group(1),m.group(3),Base64.encodeToString(("http://api.6uzi.com/?url="+url).getBytes(),Base64.NO_WRAP),Base64.encodeToString(url.getBytes(),Base64.NO_WRAP));
			String data=Jsoup.connect(String.format("https://api.6uzi.com/918_jxi.php")).ignoreContentType(true).method(Connection.Method.POST).requestBody(body).execute().body();
			JSONObject jo=JSONObject.parseObject(data);
			if(jo.getIntValue("code")==200){
				src=new String(Base64.decode(jo.getString("url").substring(4),Base64.NO_WRAP));
				//src=src.substring(src.indexOf("http"));
				if(src.startsWith("//"))
					src="https:"+src;
				if(src.length()==0)
					src=null;
			}
			}
		}
		catch (Exception e)
		{}
		return src;
	}
}
