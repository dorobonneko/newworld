package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import com.moe.x4jdm.video.VideoParse;
import java.net.URLDecoder;

public class Indexqqdm extends Index
{

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost() + "/appdata/recommend.php").ignoreContentType(true).execute().body());
			if(data.getIntValue("status")==1){
				data=data.getJSONObject("data");
				index.add(data.getJSONArray("focus"));
				JSONArray arr=index.getJSONArray(0);
				for(int i=0;i<arr.size();i++){
					JSONObject item=arr.getJSONObject(i);
					item.put("href",item.remove("key"));
					item.put("src",makeUrl(item.remove("pic").toString()));
				}
				index.add(data.getJSONArray("cate"));
				arr=index.getJSONArray(1);
				for(int i=0;i<arr.size();i++){
					JSONObject item=arr.getJSONObject(i);
					item.put("href",makeUrl(item.remove("key").toString()));
					item.put("title",item.remove("txt"));
				}
				arr=data.getJSONArray("vod");
				for(int i=0;i<arr.size();i++){
					JSONObject item=arr.getJSONObject(i);
					item.put("href",item.remove("id"));
					item.put("src",makeUrl(item.remove("pic").toString()));
					item.put("desc",item.remove("status"));
					index.add(item);
				}
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String makeUrl(String url)
	{
		if(url.startsWith("/"))
		return "http://simg.99hd.net"+url;
		return getHost()+"/appdata/search.php?type="+url+"&key=&page=%d&pagesize=20";
	}

	@Override
	public String getList(String url)
	{
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
			data.put("page",data.remove("currentPage"));
			data.put("count",data.remove("totalPage"));
			data.put("item",data.remove("data"));
			JSONArray arr=data.getJSONArray("item");
			for(int i=0;i<arr.size();i++){
				JSONObject item=arr.getJSONObject(i);
				item.put("href",item.remove("id"));
				item.put("src",makeUrl(item.remove("pic").toString()));
				item.put("desc",item.remove("status"));
			}
			return data.toJSONString();
		}
		catch (IOException e)
		{}
		return null;
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost() + "/appdata/vod.php?id=" + url).ignoreContentType(true).execute().body());
			if(data.getIntValue("status")==1){
				data=data.getJSONArray("data").getJSONObject(0);
				post.put("title",data.remove("title"));
				post.put("src",makeUrl(data.remove("pic").toString()));
				post.put("desc",data.remove("tag").toString()+"<br/><br/>"+data.remove("status").toString());
				post.put("profile",Jsoup.connect(getHost()+"/appdata/appContent.php?id="+url).get().getElementById("content").text());
				JSONArray video=new JSONArray();
				post.put("video",video);
				JSONArray arr=data.getJSONArray("playList");
				for(int i=0;i<arr.size();i++){
					JSONObject item=arr.getJSONObject(i);
					video.add(item.remove("plays"));
					JSONArray plays=video.getJSONArray(i);
					for(int n=0;n<plays.size();n++){
						JSONObject play=plays.getJSONObject(n);
						play.put("title",play.remove("playTitle"));
						play.put("href",play.remove("playKey"));
					}
				}
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new HashMap<>();
		if(url.startsWith("http://vparse.99hd.net/?id=")){
			url=URLDecoder.decode(url.substring(27,url.lastIndexOf("&")));
			
			urls.put(url,VideoParse.parseQQ(url));
		}
		return urls;
	}

	@Override
	public String getHost()
	{
		//http://app.qiqidongman.com
		return "http://sdata.99hd.net";
	}
	
}
