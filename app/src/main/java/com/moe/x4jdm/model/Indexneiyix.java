package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.LinkedHashMap;
import android.net.Uri;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Indexneiyix extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			JSONArray header=new JSONArray();
			index.add(header);
			for(Element e:doc.select("ul.nav > li#nav-dianshiju > a")){
				JSONObject head=new JSONObject();
				header.add(head);
				head.put("title",e.text());
				head.put("href",e.absUrl("href").replace(".html","/page/%d.html"));
			}
			for(Element e:doc.select("div.layout-box")){
				JSONObject title=new JSONObject();
				title.put("title",e.selectFirst("div.box-title > h3").ownText());
				try{title.put("href",e.selectFirst("div.more > a").absUrl("href").replace(".html","/page/%d.html"));}catch(Exception ee){}
				index.add(title);
				for(Element item:e.select("ul.clearfix > li.col-md-2")){
					JSONObject post=new JSONObject();
					index.add(post);
					Element a=item.selectFirst("a.video-pic");
					post.put("title",a.attr("title"));
					post.put("src",a.absUrl("data-original"));
					post.put("href",a.attr("href"));
					post.put("desc",item.selectFirst("div.subtitle").text());
					post.put("click","video");
				}
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
		try
		{
			Document doc=Jsoup.connect(url).get();
			String page=Uri.parse(doc.select("div#long-page > ul > li > a").last().absUrl("href")).getLastPathSegment();
			
			list.put("count",page.substring(0,page.indexOf(".")));
			page=Uri.parse(url).getLastPathSegment();
			list.put("page",page.substring(0,page.indexOf(".")));
			JSONArray items=new JSONArray();
			list.put("item",items);
			for(Element e:doc.select("ul#content > li")){
				JSONObject post=new JSONObject();
				items.add(post);
				Element a=e.selectFirst("a.video-pic");
				post.put("title",a.attr("title"));
				post.put("src",a.absUrl("data-original"));
				post.put("href",a.attr("href"));
				post.put("desc",e.selectFirst("div.subtitle").text());
				post.put("click","video");
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		return null;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		String id=Uri.parse(url).getLastPathSegment();
		id=id.substring(0,id.indexOf("."));
		try
		{
			Document doc=Jsoup.connect(getHost() + "/index.php/vod/play/id/" + id + "/sid/1/nid/1.html").get();
			JSONObject data=JSONObject.parseObject(doc.selectFirst("div.Player > script").html().substring(16));
			String video=data.getString("url");
			Matcher m=Pattern.compile("playlist\\s=\\s'\\[\\{\\\"url\\\":\\\"(.*?)\\\"\\}\\]';",Pattern.MULTILINE).matcher(Jsoup.connect(video).get().select("body > script").last().html());
			if(m.find()){
				Uri uri=Uri.parse(video);
				video=uri.getScheme()+"://"+uri.getHost()+m.group(1);
				urls.put(video,video);
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String getHost()
	{
		return "http://www.neiyix.com";
	}
	
}
