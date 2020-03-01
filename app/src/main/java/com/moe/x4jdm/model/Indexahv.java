package com.moe.x4jdm.model;

import android.net.Uri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Indexahv extends Index
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
			Elements tabs= doc.select(".categories-list > .categories-list-item > a");
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
			Elements mains=doc.select(".thumbs_video");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element main_item=mains_i.next();
					JSONObject main=new JSONObject();
					try{main.put("title", main_item.selectFirst(".thumbs-header-title").text());}catch(NullPointerException e){}
					try{main.put("href", makeUrl(main_item.selectFirst(".thumbs-footer > a").absUrl("href")));}catch(NullPointerException e){}
					JSONArray item_json=new JSONArray();

					for (Element e:main_item.select(".thumbs-list-item"))
					{
						JSONObject post=new JSONObject();
						try{post.put("title", e.selectFirst(".thumbs-list-item-title").text());}catch(Exception ee){}
						try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
						try{post.put("src", e.selectFirst("img").absUrl("src"));}catch(Exception ee){}
						try{post.put("desc",e.selectFirst(".thumbs-list-item-time").text());}catch(Exception ee){}
						try{post.put("score",e.selectFirst(".thumbs-list-item-info-elem_view").text());}catch(Exception ee){}
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
			Document doc=Jsoup.connect(url).userAgent("Mozilla (Linux;Android 10)").get();
			//list.put("title", doc.selectFirst("h3.section-title").text());

			Element page=doc.selectFirst("ul.pager-list");
			if(page==null){
				list.put("page",1);
				list.put("count",1);
			}else{
				list.put("page",page.selectFirst(".pager-list-item_current").text());
				list.put("count",list.getIntValue("page")+(page.selectFirst(".pager-list-item_next")==null?0:1));
			}

			JSONArray item_json=new JSONArray();

			for (Element e:doc.select(".thumbs-list-item"))
			{
				JSONObject post=new JSONObject();
				try{post.put("title", e.selectFirst(".thumbs-list-item-title").text());}catch(Exception ee){}
				try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
				try{post.put("src", e.selectFirst("img").absUrl("src"));}catch(Exception ee){}
				try{post.put("desc",e.selectFirst(".thumbs-list-item-time").text());}catch(Exception ee){}
				try{post.put("score",e.selectFirst(".thumbs-list-item-info-elem_view").text());}catch(Exception ee){}
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
			post.put("title", doc.selectFirst("div.video-block-header-title").text());
			//post.put("profile", doc.selectFirst("div.info > em > .item").toString());
			post.put("desc", doc.selectFirst("div.video-block-main-stats").toString());
			Matcher m=Pattern.compile("rnd:\\s'([0-9]*?)',\\svideo_url:\\s'function/0/(.*?)',[\\s\\S.]*?preview_url:\\s'(.*?)',",Pattern.MULTILINE).matcher(doc.select("script").toString());
			if(m.find()){
			try{post.put("src",m.group(3));}catch(NullPointerException e){}
			JSONArray items=new JSONArray();
			JSONArray video=new JSONArray();
			video.add(items);
			post.put("video", video);
			JSONObject item=new JSONObject();
				items.add(item);

				item.put("title","Play");
				item.put("href",getHost()+"embed/"+doc.selectFirst("input[name='video_id']").attr("value"));
				//item.put("href",m.group(2)+"?rnd="+m.group(1));
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String,String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search/"+key+"/?mode=async&function=get_block&block_id=list_videos_videos_list_search_result&sort_by=ctr&from=%d&q="+key;
	}

	@Override
	public String makeUrl(String url)
	{
		return url+"?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=ctr&from=%d";
	}

	@Override
	public String getHost()
	{
		return "https://www.animehentaivideos.xxx/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost() + "?mode=async&function=get_block&block_id=list_videos_latest_videos&sort_by=post_date&from=%d";
	}
}
