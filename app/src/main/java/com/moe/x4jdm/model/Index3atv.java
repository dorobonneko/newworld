package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import com.moe.x4jdm.util.EscapeUnescape;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Index3atv extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Elements tabs=doc.select("ul#doc-header-pintuer > li > a");
			JSONArray tab=new JSONArray();
			index.add(tab);
			for(Element a:tabs){
				JSONObject item=new JSONObject();
				item.put("title",a.text());
				item.put("href",a.absUrl("href").replace(".html","-%d.html"));
				tab.add(item);
			}
			Elements posts=doc.select("div.media-inline div.item");
			for(Element item:posts){
				JSONObject post=new JSONObject();
				index.add(post);
				post.put("title",item.selectFirst(".title").text());
				post.put("src",item.selectFirst("img").absUrl("data-original"));
				post.put("href",post.getString("src")+"#"+item.selectFirst(".title > a").absUrl("href"));
				post.put("desc",item.selectFirst(".time").text());
				post.put("viewtype","poster");
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		url=url.replace("-1.html",".html");
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element pages=doc.selectFirst(".pages");
			if(pages==null){
				list.put("count",1);
				list.put("page",1);
			}else{
				list.put("page",doc.selectFirst(".pagenow").text());
				String pagecount=doc.selectFirst(".pagebtn").attr("onclick");
				list.put("count",pagecount.substring(pagecount.lastIndexOf(",")+1,pagecount.length()-1));
			}
			JSONArray posts=new JSONArray();
			list.put("item",posts);
			Elements post_s=doc.select("div.media-inline div.item");
			for(Element item:post_s){
				JSONObject post=new JSONObject();
				posts.add(post);
				post.put("title",item.selectFirst(".title").text());
				post.put("src",item.selectFirst("img").absUrl("data-original"));
				post.put("href",post.getString("src")+"#"+item.selectFirst(".title > a").absUrl("href"));
				post.put("desc",item.selectFirst(".time").text());
				post.put("viewtype","poster");
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
		String[] urls=url.split("#");
		post.put("src",urls[0]);
		try
		{
			Document doc=Jsoup.connect(urls[1]).get();
			String data=doc.selectFirst("#playview > script").absUrl("src");
			data=Jsoup.connect(data).ignoreContentType(true).execute().body();
			Pattern pattern=Pattern.compile("mac_name='(.*?)'.*?mac_url=unescape\\('(.*?)'\\);",Pattern.MULTILINE);
			Matcher matcher=pattern.matcher(data);
			if(matcher.find()){
				post.put("title",matcher.group(1));
				JSONArray video=new JSONArray();
				post.put("video",video);
				JSONArray play=new JSONArray();
				video.add(play);
				JSONObject item=new JSONObject();
				play.add(item);
				item.put("title","play");
				item.put("href",matcher.group(2));
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
		url=EscapeUnescape.unescape(url);
		urls.put(url,url);
		return urls;
	}

	@Override
	public String getHost()
	{
		return "https://app5600.com/";
	}
	
}
