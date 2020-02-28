package com.moe.x4jdm.model;

import android.net.Uri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexppc extends Index
{
	@Override
	public void clearCache()
	{
		// TODO: Implement this method
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Elements tabs= doc.select(".tags-cloud > a");
			JSONArray tab=new JSONArray();
			index.add(tab);
			Iterator<Element> tabs_i=tabs.iterator();
			while(tabs_i.hasNext()){
				JSONObject item=new JSONObject();
				tab.add(item);
				Element a=tabs_i.next();
				item.put("title",a.text());
				item.put("href",makeUrl(a.absUrl("href")));
			}
			Elements mains=doc.select("#list_videos_videos_watched_right_now_items,#list_videos_most_recent_videos");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element main_item=mains_i.next();
					JSONObject main=new JSONObject();
					try{main.put("title", main_item.selectFirst(".headline > h2").text());}catch(NullPointerException e){}
					//main.put("href", main_item.selectFirst(".see-all").absUrl("href") + "page/%d/");
					JSONArray item_json=new JSONArray();
					
					for (Element e:main_item.select(".item"))
					{
						JSONObject post=new JSONObject();
						try{post.put("title", e.selectFirst("strong.title").text());}catch(Exception ee){}
						try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
						try{post.put("src", e.selectFirst("img.thumb").absUrl("data-original"));}catch(Exception ee){}
						try{post.put("desc",e.selectFirst(".duration").text());}catch(Exception ee){}
						try{post.put("score",e.selectFirst(".views").text());}catch(Exception ee){}
						item_json.add(post);
					}
					main.put("item", item_json);
					index.add(main);
				}

			}
			
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).userAgent("Mozilla (Linux;Android 10)").get();
			//list.put("title", doc.selectFirst("h3.section-title").text());
			
			Element page=doc.selectFirst(".pagination");
			if(page==null){
				list.put("page",1);
				list.put("count",1);
			}else{
				list.put("page",page.selectFirst("li.page-current > span").text());
				list.put("count", page.selectFirst("li.last > a").attr("data-parameters").substring(23));
			}
			
			Elements posts=doc.select("#list_videos_most_recent_videos .item,#list_videos_common_videos_list_items > .item");
				JSONArray item_json=new JSONArray();
				for (Element e:posts)
				{
					JSONObject post=new JSONObject();
					try{post.put("title", e.selectFirst("strong.title").text());}catch(Exception ee){}
					try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
					try{post.put("src", e.selectFirst("img.thumb").absUrl("data-original"));}catch(Exception ee){}
					try{post.put("desc",e.selectFirst(".duration").text());}catch(Exception ee){}
					try{post.put("score",e.selectFirst(".views").text());}catch(Exception ee){}
					item_json.add(post);
				}
				list.put("item", item_json);
		}catch(HttpStatusException e){
			if(e.getStatusCode()==404){
				list.put("page", Integer.parseInt(Uri.parse(url).getLastPathSegment()));
				list.put("count", list.getIntValue("page"));
			}
		}
		catch (Exception e)
		{

		}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title", doc.selectFirst("div.headline > h1").ownText());
			post.put("profile", doc.selectFirst("div.info > em > .item").toString());
			post.put("desc", doc.selectFirst("div.info > div.item").toString());
			try{post.put("src", doc.selectFirst("meta[property='og:image']").attr("content"));}catch(NullPointerException e){}
			JSONArray items=new JSONArray();
			JSONArray video=new JSONArray();
			video.add(items);
			post.put("video", video);
			for(Element li:doc.select("a[data-attach-session='PHPSESSID']")){
				JSONObject item=new JSONObject();
				items.add(item);

				item.put("title",li.text());
				item.put("href",li.text()+":"+ li.absUrl("href"));
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String,String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			urls.put(url.substring(0,url.indexOf(":")),url.substring(url.indexOf("?")+1));
		}
		catch (Exception e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return null;
	}

	@Override
	public String makeUrl(String url)
	{
		return url+"?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from=%d";
	}

	@Override
	public String getHost()
	{
		return "https://porntopic.com/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost() + "?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=rating&from=%d";
	}
	
}
