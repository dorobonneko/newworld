package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import java.net.URLDecoder;
import org.jsoup.select.Elements;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.moe.x4jdm.util.EscapeUnescape;
import android.util.Base64;
import java.util.function.UnaryOperator;

public class Indexyh extends Index
{
	private String time;

	@Override
	public void clearCache()
	{
		time=null;
	}

	@Override
	public String getTime()
	{
		if(time==null)
			getIndex(0);
		return time;
	}

	@Override
	public boolean hasTime()
	{
		return true;
	}
	
	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			JSONArray header=new JSONArray();
			index.add(header);
			for(Element e:doc.select("ul.am-slides > li > a")){
				JSONObject item=new JSONObject();
				header.add(item);
				item.put("title",e.attr("title"));
				item.put("href",e.absUrl("href"));
				item.put("src",e.selectFirst("img").absUrl("src"));
			}
			JSONArray tab=new JSONArray();
			index.add(tab);
			for(Element e:doc.select("ul.am-main > li > a")){
				JSONObject item=new JSONObject();
				tab.add(item);
				item.put("title",e.text());
				item.put("href",makeUrl(e.absUrl("href")));
			}
			for(Element e:doc.select("div.am-titlebar.am-titlebar-multi.am-comictit.am-main:has(a)")){
				JSONObject title=new JSONObject();
				index.add(title);
				title.put("title",e.selectFirst("h2").text());
				try{title.put("href",makeUrl(e.selectFirst("a").absUrl("href")));}
				catch(Exception ee){}
				for(Element item:e.nextElementSibling().select("li")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("title",item.selectFirst("h3").text());
					post.put("href",item.selectFirst("a").absUrl("href"));
					post.put("desc",item.selectFirst("em").text());
					post.put("src",item.selectFirst("img").absUrl("src"));
				}
			}
			JSONArray time=new JSONArray();
			for(Element week:doc.select("div.am-tabs-bd > div.am-tab-panel > ul.am-indexlist")){
				JSONArray item=new JSONArray();
				time.add(item);
				for(Element day:week.select("li")){
					JSONObject post=new JSONObject();
					item.add(post);
					post.put("title",day.selectFirst("a").text());
					post.put("desc",day.selectFirst("span").text());
					post.put("href",day.selectFirst("a").absUrl("href"));
				}
			}
			time.add(0,time.remove(time.size()-1));
			this.time=time.toJSONString();
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element page=doc.selectFirst(".page");
			list.put("page",page.selectFirst("em").text());
			list.put("count",page.select("a").last().text().substring(2));
			JSONArray item=new JSONArray();
			list.put("item",item);
			for(Element e:doc.select("div.am_news_list_all > ul.am-list > li")){
				JSONObject post=new JSONObject();
				item.add(post);
				post.put("title",e.selectFirst("h2").text());
				post.put("src",e.selectFirst("img").absUrl("data-original"));
				post.put("desc",e.selectFirst("i.am-icon-heartbeat").text());
				post.put("score",e.selectFirst("div.am-list-pingfen").text());
				post.put("href",e.selectFirst("h2 > a").absUrl("href"));
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
			Element box=doc.selectFirst("div.am-content-box");
			post.put("title",box.selectFirst("h1.am_list_title").text());
			post.put("src",box.selectFirst("img").absUrl("data-original"));
			Elements desc=box.select("ul.am-list > li");
			desc.remove(desc.size()-1);
			desc.replaceAll(new UnaryOperator<Element>(){

					@Override
					public Element apply(Element p1)
					{
						p1.tagName("span").appendElement("br");
						return p1;
					}
				});
			post.put("desc",desc.toString());
			post.put("profile",doc.selectFirst("#tab2").text());
			JSONArray video=new JSONArray();
			post.put("video",video);
			for(Element ul:doc.select("#tab1 ul.am-content-moshiul")){
				JSONArray play=new JSONArray();
				video.add(play);
				for(Element li:ul.children()){
					JSONObject item=new JSONObject();
					play.add(item);
					item.put("title",li.text());
					item.put("href",li.child(0).absUrl("href"));
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
		try
		{
			String data=Jsoup.connect(url).userAgent("User-Agent:  Mozilla/5.0 (Linux; Android 10; Redmi K20 Pro Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/80.0.3987.99 Mobile Safari/537.36 Html5Plus/1.0 (Immersed/35.03386)").get().selectFirst("div.am-player > script").toString();
			Matcher m = Pattern.compile("=unescape\\(\\\"(.*?)\\\"\\)", Pattern.MULTILINE).matcher(data);
			if (m.find())
			{
				String videourl=EscapeUnescape.unescape(m.group(1));
				String[] video=videourl.split("\\$");
				//videourl=new String(Base64.decode(video[3],Base64.DEFAULT));
				url="https://tsdlrh.manhuafenxiao.com/GV/?u="+video[3];
				m=Pattern.compile("vodurl = \\\"(.*?)\\\"",Pattern.MULTILINE).matcher(Jsoup.connect(url).ignoreContentType(true).execute().body());
				if(m.find())
				urls.put(video[0],m.group(1));
				}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String getHost()
	{
		return "https://www.6111.tv/";
	}

	@Override
	public String makeUrl(String url)
	{
		return URLDecoder.decode(url)+"&page=%d";
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search.php?page=%d&searchword="+key+"&searchtype=";
	}
	
}
