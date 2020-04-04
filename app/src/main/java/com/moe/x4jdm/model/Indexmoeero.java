package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import android.net.Uri;
import org.jsoup.HttpStatusException;

public class Indexmoeero extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).userAgent("x4jdm"+Math.random()).get();
			JSONArray tab=new JSONArray();
			index.add(tab);
			for(Element e:doc.select("div#navi > div.navi-frame > ul > li > a")){
				JSONObject tab_item=new JSONObject();
				tab.add(tab_item);
				tab_item.put("title",e.text());
				tab_item.put("href",makeUrl(e.absUrl("href")));
			}
			for(Element e:doc.select("div.post")){
				if(e.selectFirst(".pc_ad")!=null){
					continue;
				}
				JSONObject post=new JSONObject();
				index.add(post);
				post.put("click","list");
				post.put("viewtype","postposter");
				post.put("title",e.selectFirst("a").attr("title"));
				post.put("src",e.selectFirst(".thumb-outer img").absUrl("src"));
				post.put("href",e.selectFirst("a").absUrl("href"));
				try{post.put("desc",e.selectFirst("div.cal").text());}catch(Exception ee){}
				post.put("score",e.selectFirst("a[rel*='category']").text());
				post.put("msg",e.selectFirst("h2.new")==null?null:"new");
				JSONArray tag=new JSONArray();
				post.put("tag",tag);
				for(Element tag_item:e.select("div.tag > a")){
					JSONObject tag_i=new JSONObject();
					tag.add(tag_i);
					tag_i.put("title",tag_item.text());
					tag_i.put("href",makeUrl(tag_item.absUrl("href")));
				}
			}
		}
		catch (Exception e)
		{}
		return index.toJSONString();
	}

	@Override
	public String makeUrl(String url)
	{
		Uri uri=Uri.parse(url);
		if(uri.getPath().startsWith("/category/"))
			url+="/page/%d";
		else if(uri.getPath().startsWith("/tag/"))
			url+="/page/%d";
		return url;
	}

	@Override
	public String getList(String url)
	{
		if(url.endsWith(".html"))return getPost(url);
		JSONObject list=new JSONObject();
		try
		{
			list.put("page", Integer.parseInt(Uri.parse(url).getLastPathSegment()));
		}
		catch (NumberFormatException e)
		{
			list.put("page",1);
		}
		try
		{
			Document doc=Jsoup.connect(url.replace("/taglist","/tagcloud")).get();
			
			list.put("count", list.getIntValue("page") + (doc.selectFirst(".pagenation") != null ?1: 0));
			JSONArray item=new JSONArray();
			list.put("item",item);
			if(url.endsWith("/taglist"))
				for(Element e:doc.select("[class^='tag-link-']")){

						JSONObject post=new JSONObject();
						item.add(post);
						post.put("click","list");
						post.put("title",e.selectFirst("a").text());
						post.put("href",makeUrl(e.selectFirst("a").absUrl("href")));
						post.put("desc",e.attr("title"));
					}
			else
			for(Element e:doc.select("div.post:has(.box),ul.wpp-ul-ranking > li")){
				if(e.selectFirst(".pc_ad")!=null){
					continue;
				}
				JSONObject post=new JSONObject();
				item.add(post);
				post.put("click","list");
				post.put("viewtype","postposter");
				try{post.put("title",e.selectFirst("span.wpp-title").text());}catch(Exception ee){
				post.put("title",e.selectFirst("a").attr("title"));}
				try{post.put("src",e.selectFirst(".thumb-outer img,img.wpp-thumbnail").absUrl("src"));}catch(Exception ee){}
				post.put("href",e.selectFirst("a").absUrl("href"));
				try{post.put("desc",e.selectFirst("div.cal").text());}catch(Exception ee){
					try{post.put("desc",e.selectFirst("span.wpp-views").text());}catch(Exception eee){
						post.put("desc",e.attr("title"));
					}
				}
				try{post.put("score",e.selectFirst("a[rel*='category']").text());}catch(Exception ee){}
				try{post.put("msg",e.selectFirst("h2.new")==null?null:"new");}catch(Exception ee){}
				JSONArray tag=new JSONArray();
				post.put("tag",tag);
				for(Element tag_item:e.select("div.tag > a")){
					JSONObject tag_i=new JSONObject();
					tag.add(tag_i);
					tag_i.put("title",tag_item.text());
					tag_i.put("href",makeUrl(tag_item.absUrl("href")));
				}
			}
			
		}catch(HttpStatusException e){
			if(e.getStatusCode()==404){
				list.put("count",list.getIntValue("page"));
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
		post.put("page",1);
		post.put("count",1);
		JSONArray posts=new JSONArray();
		post.put("item",posts);
		try
		{
			Document doc=Jsoup.connect(url).get();
			for(Element e:doc.select(".thumbnail_image")){
				JSONObject item=new JSONObject();
				posts.add(item);
				item.put("title",posts.size());
				item.put("src",e.absUrl("src"));
				item.put("viewtype","imagepreview");
			}
			for(Element e:doc.select(".commentlist li.comment")){
				JSONObject item=new JSONObject();
				posts.add(item);
				item.put("viewtype","comment");
				item.put("name",e.selectFirst("cite.fn").text());
				item.put("desc",e.selectFirst("div.commentmetadata").text());
				item.put("comment",e.selectFirst("li > div > p").toString());
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public String search(String key)
	{
		return getHost()+"page/%d?cat=0&s="+key;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "http://moeimg.net/";
	}
	
}
