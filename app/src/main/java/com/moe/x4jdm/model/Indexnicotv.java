package com.moe.x4jdm.model;
import android.net.Uri;
import android.util.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.text.TextUtils;
import java.net.URLDecoder;

public class Indexnicotv extends Index
{
	private String time;
	@Override
	public void clearCache()
	{
		time = null;
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost() + "dongman").get();
			Elements headers=doc.select("div.carousel-inner > div.item > a");
			if (headers != null)
			{
				JSONArray header=new JSONArray();
				index.add(header);
				Iterator<Element> headers_i=headers.iterator();
				while (headers_i.hasNext())
				{
					Element header_item=headers_i.next();
					JSONObject head=new JSONObject();
					header.add(head);
					head.put("href", header_item.absUrl("href"));
					head.put("src", header_item.selectFirst("img").absUrl("src"));
					head.put("title", header_item.text());
				}
			}
			Elements a_tab=doc.select("dl.types > dd > ul > li > a");
			if (a_tab != null)
			{
				JSONArray tabs=new JSONArray();
				index.add(tabs);
				Iterator<Element> a_tab_i=a_tab.iterator();
				while (a_tab_i.hasNext())
				{
					Element a=a_tab_i.next();
					JSONObject tab=new JSONObject();
					tabs.add(tab);
					tab.put("title", a.text());
					tab.put("href", makeUrl(a.absUrl("href")));
				}
			}
			
			Elements mains=doc.select(".container:has(.page-header)");
			if (mains != null)
			{
				mains.remove(0);
				//JSONArray main=new JSONArray();
				//index.put("main", main);
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					main_item.put("title", list.selectFirst(".page-header h2").ownText());
					if (TextUtils.isEmpty(main_item.getString("title")))
					{
						main_item.put("title", list.selectFirst(".page-header a").text());
					}
					main_item.put("href", makeUrl(list.selectFirst(".page-header a").absUrl("href")));
					Elements items=list.select("ul > li");
					if (items != null)
					{
						JSONArray item=new JSONArray();
						main_item.put("item", item);
						Iterator<Element> items_i=items.iterator();
						while (items_i.hasNext())
						{
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							item.add(post);
							post.put("title", post_item.selectFirst("img").attr("alt"));
							post.put("href", post_item.selectFirst("a").absUrl("href"));
							post.put("desc", post_item.selectFirst(".continu").text());
							post.put("src", post_item.selectFirst("img").absUrl("data-original"));
							try
							{post.put("score", post_item.selectFirst(".new-icon").text());}
							catch (Exception e)
							{}
						}
					}
				}
			}
			JSONArray time=new JSONArray();
			Elements uls=doc.select("div.weekDayContent > div > ul");
			if (uls != null)
			{
				Iterator<Element> uls_i=uls.iterator();
				while (uls_i.hasNext())
				{
					Elements ul=uls_i.next().select("li");
					JSONArray item=new JSONArray();
					time.add(item);
					Iterator<Element> ul_i=ul.iterator();
					while (ul_i.hasNext())
					{
						Element a=ul_i.next();
						JSONObject a_item=new JSONObject();
						item.add(a_item);
						a_item.put("title", a.selectFirst("img").attr("alt"));
						a_item.put("href", a.selectFirst("a").absUrl("href"));
						a_item.put("desc", a.select(".continu").text());
					}
				}
				time.add(0, time.remove(time.size() - 1));
			}
			this.time = time.toJSONString();
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		if (time == null)
			getIndex();
		return time;
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element page=doc.selectFirst("ul.pagination");
			if (page == null)
			{
				list.put("page", 1);
				list.put("count", 1);
			}
			else
			{
				list.put("page", page.selectFirst("li.disabled").text());
				Elements li= page.children();
				String href=li.get(li.size()-2).child(0).attr("href");
				try
				{
					list.put("count", Integer.parseInt(href.substring(href.lastIndexOf("-") + 1, href.length() - 5)));
				}
				catch (NumberFormatException e)
				{
					href=li.get(li.size()-1).child(0).attr("href");
					try
					{
						list.put("count", Integer.parseInt(href.substring(href.lastIndexOf("-") + 1, href.length() - 5)));
					}
					catch (NumberFormatException ee)
					{}
				}
			}
			Elements li=doc.select(".container:has(.page-header) ul.list-unstyled > li");
			JSONArray item=new JSONArray();
			list.put("item", item);
			Iterator<Element> items_i=li.iterator();
			while (items_i.hasNext())
			{
				Element post_item=items_i.next();
				JSONObject post=new JSONObject();
				item.add(post);
				post.put("title", post_item.selectFirst("img").attr("alt"));
				post.put("href", post_item.selectFirst("a").absUrl("href"));
				post.put("desc", post_item.selectFirst(".continu").text());
				post.put("src", post_item.selectFirst("img").absUrl("data-original"));
				try
				{post.put("score", post_item.selectFirst(".new-icon").text());}
				catch (Exception e)
				{}
			}

		}
		catch (Exception e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title", doc.selectFirst(".media .media-body .ff-text").text());
			post.put("src", doc.selectFirst(".media .media-object.img-thumbnail").absUrl("data-original"));
			Elements p=doc.selectFirst("dl.dl-horizontal").children();
			p.remove(p.size() - 1);
			p.remove(p.size()-1);
			post.put("desc", p.toString().replaceAll("</dd>","</dd><br />"));
			post.put("profile", doc.selectFirst("span.vod-content.ff-collapse.text-justify").text());
			Elements play=doc.select("ul.ff-playurl");
			if (play != null && !play.isEmpty())
			{
				JSONArray video=new JSONArray();
				post.put("video", video);
				Iterator<Element> player=play.iterator();
				while(player.hasNext()){
				JSONArray plays=new JSONArray();
				video.add(plays);
				Iterator<Element> play_i=player.next().children().iterator();
				while (play_i.hasNext())
				{
					Element play_item=play_i.next().child(0);
					JSONObject item=new JSONObject();
					plays.add(item);
					item.put("title", play_item.text());
					item.put("href", play_item.absUrl("href"));
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
		Map<String,String> urls=new LinkedHashMap<>();
		if(url.startsWith(getHost()))
		try
		{
			Document doc=Jsoup.connect(url).get();
			String data=Jsoup.connect(doc.selectFirst("#cms_player > script").absUrl("src")).ignoreContentType(true).execute().body();
			JSONObject jo=JSONObject.parseObject(data.substring(data.indexOf("{"),data.lastIndexOf("}")+1));
			Uri.Builder ub=Uri.parse(jo.getString("url")).buildUpon();
			ub.appendQueryParameter("auth_key",jo.getString("auth_key"));
			ub.appendQueryParameter("time",jo.getString("time"));
			data=Jsoup.connect(ub.toString()).ignoreContentType(true).execute().body();
			Matcher matcher=Pattern.compile("<video\\ssrc=\\\"(.*?)\\\"[\\s\\S.]*?url:\\s\\\"(.*?)\\\"",Pattern.MULTILINE).matcher(data);
			if(matcher.find()){
				urls.put("1",matcher.group(1));
				urls.put("2",matcher.group(2));
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost().concat("vod-search-wd-" + key + "-p-%d.html");
	}

	@Override
	public String makeUrl(String url)
	{
		url=URLDecoder.decode(url);
		int index=url.lastIndexOf(".");
		return  url.substring(0, index) + "-%d" + url.substring(index);
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return "http://www.nicotv.club/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost() + "video/type3/-----4-gold-%d.html";
	}


}
