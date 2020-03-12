package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;
import android.net.Uri;
import java.util.LinkedHashMap;

public class Indexmsiv extends Index
{

	

	@Override
	public String getFilter()
	{
		return null;
	}


	@Override
	public void clearCache()
	{
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc= Jsoup.connect(getHost() + "wp-content/themes/LightSNS_1.6.40/mobile/templates/page/topic-rank.php").userAgent("Mozilla (Linux;Android 10)").get();
			Elements tabs=doc.select("li");
			JSONArray tab=new JSONArray();
			index.add(tab);
			Iterator<Element> tabs_i=tabs.iterator();
			while(tabs_i.hasNext()){
				Element li=tabs_i.next();
				JSONObject tab_item=new JSONObject();
				tab.add(tab_item);
				tab_item.put("title",li.attr("title"));
				tab_item.put("href",makeUrl(li.selectFirst(".link").absUrl("href")));
			}
			//JSONArray main=new JSONArray();
			//index.put("main",main);
			JSONObject jo=JSONObject.parseObject(getList(getHost()+"wp-content/themes/LightSNS_1.6.40/mobile/module/post/data.php?page=1&type=all&load_type=more"));
			
			index.addAll(jo.getJSONArray("item"));
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		return null;
	}

	@Override
	public boolean hasTime()
	{
		return false;
	}


	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).userAgent("Mozilla (Linux;Android 10)").method(Connection.Method.POST).ignoreContentType(true).requestBody(url.substring(url.indexOf("?")+1)).post();
			Elements li= doc.select(".jinsom-post-words:has(h1)");
			if(li.isEmpty()){
				list.put("page",Uri.parse(url).getQueryParameter("page"));
				list.put("count",list.getString("page"));
			}else{
				list.put("page",Uri.parse(url).getQueryParameter("page"));
				if(url.endsWith("&search="))
					list.put("count",list.getString("page"));
					else
				list.put("count",list.getIntValue("page")+1);
				JSONArray data=new JSONArray();
				list.put("item",data);
				Iterator<Element> tabs_i=li.iterator();
				while(tabs_i.hasNext()){
					Element post=tabs_i.next();
					JSONObject tab_item=new JSONObject();
					data.add(tab_item);
					tab_item.put("title",post.selectFirst("h1").text());
					String src=post.selectFirst(".jinsom-video-img").attr("style");
					tab_item.put("src",src.substring(22,src.length()-2));
					
					String post_id=Uri.parse(post.selectFirst(".content .link").absUrl("href")).getQueryParameter("post_id");
					JSONObject post_jo=new JSONObject();
					post_jo.put("title",tab_item.getString("title"));
					post_jo.put("src",tab_item.getString("src"));
					JSONArray video=new JSONArray();
					post_jo.put("video",video);
					JSONArray play=new JSONArray();
					video.add(play);
					JSONObject play_item=new JSONObject();
					play.add(play_item);
					play_item.put("title","Play");
					play_item.put("href",post_id);
					tab_item.put("href",post_jo.toJSONString());
					
					}
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		return url;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			JSONObject play=JSONObject.parseObject(Jsoup.connect(getHost() + "wp-content/themes/LightSNS_1.6.40/mobile/module/post/video.php").ignoreContentType(true).method(Connection.Method.POST).userAgent("Mozilla (Linux;Android 10)").requestBody("post_id=" + url).execute().body());
			urls.put("Play",play.getString("url"));
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"wp-content/themes/LightSNS_1.6.40/mobile/module/post/search.php?keyword="+key+"&type=video&page=%d&search=";
	}

	@Override
	public String makeUrl(String url)
	{
		return getHost()+"wp-content/themes/LightSNS_1.6.40/mobile/module/post/topic.php"+url.substring(url.indexOf("?"))+"&type=video&page=%d&load_type=more";
	}

	@Override
	public String getHost()
	{
		return "https://l-2019.ru/";
	}

	@Override
	public String getGold()
	{
		return getHost()+"wp-content/themes/LightSNS_1.6.40/mobile/module/post/data.php?page=%d&type=commend&load_type=more";
	}
	
}
