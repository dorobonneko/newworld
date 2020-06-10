package com.moe.x4jdm.model;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.net.Uri;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedHashMap;
import java.util.function.UnaryOperator;

public class Indexhb extends Index
{
	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Elements a_tab=doc.select(".toptx > a");
			if (a_tab != null)
			{
				a_tab.remove(a_tab.size() - 1);
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
				JSONObject tab=new JSONObject();
				tabs.add(tab);
				tab.put("title", "新番动漫");
				tab.put("href", getHost()+"search.php?page=%d&searchtype=5&year=2020");
				tab=new JSONObject();
				tabs.add(tab);
				tab.put("title", "里番动漫");
				tab.put("href", "https://m.emddm.net/search.php?page=%d&searchtype=5&tid=1&jq=肉片");
				
			}

			Elements mains=doc.select(".index1");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					main_item.put("title", list.selectFirst("div.t1").text());
					//main_item.put("href",makeUrl(list.selectFirst("a.listtitle").absUrl("href")));
					Elements items=list.select("li,dd");
					if (items != null)
					{
						//JSONArray item=new JSONArray();
						//main_item.put("item", item);
						Iterator<Element> items_i=items.iterator();
						while (items_i.hasNext())
						{
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							index.add(post);
							post.put("title", post_item.selectFirst("a").attr("title"));
							post.put("href", post_item.selectFirst("a").absUrl("href"));
							post.put("desc", post_item.selectFirst("span").text());
							try
							{post.put("src", post_item.selectFirst("img").absUrl("src"));}
							catch (Exception e)
							{}
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
			if (url.endsWith(".html"))
				list.put("page", Integer.parseInt(url.substring(url.lastIndexOf("/") + 6, url.length() - 5)));
			else
			{
				list.put("page",Integer.parseInt(Uri.parse(url).getQueryParameter("page")));
				}
			if (url.endsWith("/index1.html"))url = url.substring(0, url.length() - 11);
			Document doc=Jsoup.connect(url).get();
			Elements li=doc.select(".index1 li,div.tops > ul > div,div.tops > ul > li");
			if (li == null || li.isEmpty())
			{
				//
			}
			else
			{
				try
				{
					String page=doc.selectFirst("div.pege > span").text();
					list.put("count", page.substring(page.indexOf("/") + 2, page.length() - 1));
				}
				catch (NullPointerException e)
				{
					list.put("count", list.getIntValue("page"));
					
				}
				JSONArray item=new JSONArray();
				list.put("item", item);
				Iterator<Element> items_i=li.iterator();
				while (items_i.hasNext())
				{
					Element post_item=items_i.next();
					JSONObject post=new JSONObject();
					item.add(post);
					post.put("title", post_item.selectFirst("a").attr("title"));
					post.put("href", post_item.selectFirst("a").absUrl("href"));
					try
					{
						post.put("desc", post_item.selectFirst("strong").text());
					}
					catch (NullPointerException e)
					{
						post.put("desc", post_item.selectFirst("span").text());
					}
					try
					{post.put("src", post_item.selectFirst("img").absUrl("src"));}
					catch (Exception e)
					{}
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
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title",doc.selectFirst("div.info_z h1").text());
			post.put("src",doc.selectFirst("div.info_z img").absUrl("src"));
			Elements p=doc.select("div.info_z p");
			p.replaceAll(new UnaryOperator<Element>(){

					@Override
					public Element apply(Element p1)
					{
						p1.tagName("span").appendElement("br");
						return p1;
					}
				});
			post.put("desc",p.toString());
			post.put("profile",doc.selectFirst("div.playpdes").text());
			Elements play=doc.select("div.playerlist > ul");
			if(play!=null&&!play.isEmpty()){
				JSONArray video=new JSONArray();
				post.put("video",video);
				Iterator<Element> play_i=play.iterator();
				while(play_i.hasNext()){
				JSONArray plays=new JSONArray();
				video.add(plays);
				Iterator<Element> plays_i=play_i.next().select("li > a").iterator();
				while(plays_i.hasNext()){
					Element play_item=plays_i.next();
					JSONObject item=new JSONObject();
					plays.add(item);
					item.put("title",play_item.text());
					item.put("href",play_item.absUrl("href"));
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
		
		try
		{
			Document doc=Jsoup.connect(url).get();
			String text=doc.selectFirst("div.play_2 > script").toString();
			Matcher matcher=Pattern.compile("var\\snow=\\\"(.*?)\\\";",Pattern.MULTILINE).matcher(text);
			if(matcher.find()){
				urls.put(matcher.group(1),matcher.group(1).startsWith("http")?matcher.group(1):"https://jx.5xdmw.com/v.php?id="+matcher.group(1));
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search.php?page=%d&searchword="+key;
	}

	@Override
	public String makeUrl(String url)
	{
		int index=url.lastIndexOf("/");
		return url.substring(0, index + 1) + "index%d.html";
	}

	@Override
	public String getHost()
	{
		return "https://www.huabandm.com/";
	}

	@Override
	public String getGold()
	{
		return getHost() + "top.html?page=%d";
	}

}
