package com.moe.x4jdm.model;

import android.net.Uri;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.HttpStatusException;
import org.jsoup.Connection;

public class IndexHentaiHavenRed extends Index
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
			Elements tabs= doc.select("ul.releases.scrolling > li > a");
			JSONArray tab=new JSONArray();
			index.add(tab);
			Iterator<Element> tabs_i=tabs.iterator();
			while(tabs_i.hasNext()){
				JSONObject item=new JSONObject();
				tab.add(item);
				Element a=tabs_i.next();
				item.put("title",a.text());
				item.put("href",a.absUrl("href")+"page/%d");
			}
			Elements mains=doc.select("header:has(h2)");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element main_item=mains_i.next();
					JSONObject main=new JSONObject();
					main.put("title", main_item.selectFirst("h2").text());
					main.put("href", main_item.selectFirst(".see-all").absUrl("href") + "page/%d/");
					index.add(main);
					//JSONArray item_json=new JSONArray();
					main_item=main_item.nextElementSibling().nextElementSibling();
					for (Element e:main_item.select("article"))
					{
						JSONObject post=new JSONObject();
						post.put("title", e.selectFirst("h3").text());
						post.put("href", e.selectFirst("h3 > a").absUrl("href"));
						post.put("src", e.selectFirst("div.poster > img").absUrl("src"));
						index.add(post);
					}
					//main.put("item", item_json);
					//index.add(main);
				}

			}
			{
				Element main_item=doc.selectFirst("nav.genres");
				JSONObject main=new JSONObject();
				main.put("title", main_item.selectFirst("h2").text());
				//main.put("href", main_item.selectFirst(".see-all").absUrl("href") + "page/%d/");
				index.add(main);
				//JSONArray item_json=new JSONArray();
				for (Element e:main_item.select("li"))
				{
					JSONObject post=new JSONObject();
					post.put("title", e.selectFirst("a").text());
					post.put("href", e.selectFirst("a").absUrl("href")+"page/%d/");
					post.put("desc", e.selectFirst("i").text());
					post.put("list",true);
					index.add(post);
				}
				//main.put("item", item_json);
				//index.add(main);
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
			try
			{
				list.put("page", Integer.parseInt(Uri.parse(url).getLastPathSegment()));
			}
			catch (Exception e)
			{
				list.put("page", 1);
			}
			if(doc.selectFirst(".resppages")==null){
				list.put("count", list.getIntValue("page"));
			}else{
				list.put("count", list.getIntValue("page")+1);
			}
			if(url.startsWith(getHost()+"page/")){
				Elements posts=doc.select("article:has(.thumbnail)");
				JSONArray item_json=new JSONArray();
				for (Element e:posts)
				{
					JSONObject post=new JSONObject();
					try{post.put("title", e.selectFirst(".title > a").text());}catch(Exception ee){}
					try{post.put("href", e.selectFirst(".title > a").absUrl("href"));}catch(Exception ee){}
					try{post.put("src", e.selectFirst("img").absUrl("src"));}catch(Exception ee){}
					//try{post.put("desc",e.selectFirst(".metadata > span").text());}catch(Exception ee){}
					//try{post.put("score",e.selectFirst(".quality").text());}catch(Exception ee){}
					item_json.add(post);
				}
				list.put("item", item_json);
			}else{
			Elements posts=doc.select("article.item");
			JSONArray item_json=new JSONArray();
			for (Element e:posts)
			{
				JSONObject post=new JSONObject();
				try{post.put("title", e.selectFirst("h3").text());}catch(Exception ee){}
				try{post.put("href", e.selectFirst("h3 > a").absUrl("href"));}catch(Exception ee){}
				try{post.put("src", e.selectFirst("div.poster > img").absUrl("src"));}catch(Exception ee){}
				try{post.put("desc",e.selectFirst(".metadata > span").text());}catch(Exception ee){}
				try{post.put("score",e.selectFirst(".quality").text());}catch(Exception ee){}
				item_json.add(post);
			}
			list.put("item", item_json);
			}
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
			post.put("title", doc.selectFirst("span.last").text());
			post.put("profile", doc.selectFirst("div[itemprop='description']").toString());
			post.put("desc", doc.selectFirst("div.sgeneros").toString());
			post.put("src", doc.selectFirst(".poster > img").absUrl("src"));
			JSONArray items=new JSONArray();
			JSONArray video=new JSONArray();
			video.add(items);
			post.put("video", video);
			for(Element li:doc.select("ul#playeroptionsul > li")){
			JSONObject item=new JSONObject();
			items.add(item);
			
			item.put("title",li.selectFirst(".title").text());
			item.put("href",li.attr("data-post"));
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
			Document doc=Jsoup.connect(getHost()+"wp-admin/admin-ajax.php").method(Connection.Method.POST).requestBody(String.format("action=doo_player_ajax&post=%s&nume=1&type=movie",url)).post();
			doc= Jsoup.connect("https://stream.ksplayer.com/download/" + Uri.parse(doc.selectFirst("iframe").absUrl("src")).getLastPathSegment() + "/").get();
				Elements a=doc.select(".download_links > a");
				Iterator<Element> a_i=a.iterator();
				while (a_i.hasNext())
				{
					Element link=a_i.next();
					urls.put(link.child(0).text(), link.absUrl("href"));
				}
		}
		catch (Exception e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost() +"page/%d/?s="+key;
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "https://hentaihaven.red/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost() + "ratings/page/%d/";
	}
	
}
