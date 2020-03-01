package com.moe.x4jdm.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.video.VideoParse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.moe.x4jdm.util.EscapeUnescape;

public class Indexjyk extends Index
{
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
			Document doc=Jsoup.connect(getHost()).get();


			Elements a_tab=doc.select("ul.nav-main > li > a");
			if (!a_tab.isEmpty())
			{
				a_tab.remove(0);
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
			Elements mains=doc.select("div.catehr");
			if (mains != null)
			{
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					main_item.put("title", list.selectFirst("strong").text());
					main_item.put("href", makeUrl(list.selectFirst("a").absUrl("href")));
					Elements items=list.nextElementSibling().select("div.post");
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
							post.put("title", post_item.selectFirst("a").attr("title"));
							post.put("href", post_item.selectFirst("a").absUrl("href"));
							//post.put("desc",post_item.selectFirst("div.itemimgtext").text());
							post.put("src", post_item.selectFirst("img.thumb").absUrl("data-src"));
						}
					}
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
			Document doc=Jsoup.connect(url.replace("-1.", ".")).get();
			Element li=doc.selectFirst("ul.stui-page span.num");
			if (li == null)
			{
				list.put("page", 1);
				list.put("count", list.getIntValue("page"));
			}
			else
			{
				String page=li.ownText();
				int index=page.indexOf("/");
				list.put("page", page.substring(0, index));
				list.put("count", page.substring(index + 1));
			}
			JSONArray item=new JSONArray();
			list.put("item", item);
			Iterator<Element> items_i=doc.select("div.posts > div.post").iterator();
			while (items_i.hasNext())
			{
				Element post_item=items_i.next();
				JSONObject post=new JSONObject();
				item.add(post);
				post.put("title", post_item.selectFirst("a").attr("title"));
				post.put("href", post_item.selectFirst("a").absUrl("href"));
				//post.put("desc",post_item.selectFirst("div.itemimgtext").text());
				post.put("src", post_item.selectFirst("img.thumb").absUrl("data-src"));

			}

		}
		catch (IOException e)
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
			post.put("title", doc.selectFirst("h1.article-title").text());
			Element article=doc.selectFirst(".article-content");
			Matcher m=Pattern.compile("var\\spostimg\\s=\\s'(.*?)';", Pattern.MULTILINE).matcher(article.child(0).toString());
			if (m.find())
			{
				post.put("src", m.group(1));
			}
			article = doc.selectFirst(".stui-player__video > script");
			String playurl=article.absUrl("src");
			String data=Jsoup.connect(playurl).ignoreContentType(true).execute().body();
			m = Pattern.compile("=unescape\\('(.*?)'\\);", Pattern.MULTILINE).matcher(data);
			if (m.find())
			{
				String videourl=EscapeUnescape.unescape(m.group(1));
				JSONArray video=new JSONArray();
				JSONArray plays=new JSONArray();
				video.add(plays);
				post.put("video", video);
				JSONObject item=new JSONObject();
				plays.add(item);
				item.put("title", "Play");
				item.put("href", videourl);
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
		urls.put(url,url);
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
		return url.replace(".html", "-%d.html");
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return "https://xn--h1xp5hczt.xyz/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return null;
	}
}
