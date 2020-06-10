package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.HttpStatusException;
import android.net.Uri;
import org.jsoup.select.Elements;
import java.util.function.UnaryOperator;

public class Indexgufeng extends Index
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
			for(Element e:doc.select("mip-carousel#w0 > a")){
				JSONObject post=new JSONObject();
				header.add(post);
				post.put("title",e.selectFirst("div.mip-carousle-subtitle").text());
				post.put("src",e.selectFirst("mip-img").absUrl("src"));
				post.put("href",e.absUrl("href"));
			}
			for(Element e:doc.select("div.imgBox")){
				JSONObject title=new JSONObject();
				index.add(title);
				title.put("title",e.selectFirst("span.Title").text());
				title.put("href",e.selectFirst("a.icon_More2").absUrl("href")+"%d/");
				for(Element ee:e.select("ul > li")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("title",ee.selectFirst("a.txtA").text());
					post.put("desc",ee.selectFirst("span.info").text());
					post.put("src",ee.selectFirst("mip-img").absUrl("src"));
					post.put("href",ee.selectFirst("a.txtA").absUrl("href"));
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
			Document doc=Jsoup.connect(url.replace("/1/","/")).get();
			if(url.endsWith(".html")){
				list.put("page",1);
				list.put("count",1);
				Matcher m=Pattern.compile("chapterImages\\s=\\s(.*?);.*?chapterPath\\s=\\s\\\"(.*?)\\\";",Pattern.MULTILINE).matcher(doc.select("script").toString());
				if(m.find()){
					JSONArray arr=JSONArray.parseArray(m.group(1));
					list.put("item",arr);
					for(int i=0;i<arr.size();i++){
						JSONObject item=new JSONObject();
						item.put("viewtype", "listcomic");
						//int index=url.lastIndexOf("/");
						item.put("src","https://res.xiaoqinre.com/"+m.group(2)+arr.getString(i));
						arr.set(i, item);
					}
				}
			}else if(url.endsWith("/search/")){
				list.put("page",1);
				list.put("count",1);
				JSONArray index=new JSONArray();
				list.put("item",index);
				for(Element e:doc.select("div.filter-item")){
					JSONObject title=new JSONObject();
					index.add(title);
					title.put("title",e.selectFirst("label").text());
					for(Element ee:e.select("ul > li > a:not(.active)")){
						JSONObject post=new JSONObject();
						index.add(post);
						post.put("title",ee.text());
						post.put("href",ee.absUrl("href")+"%d/");
						post.put("click","list");
					}
				}
			}else{
				Element ul_page=doc.selectFirst("ul.pagination");
				if(ul_page==null){
					list.put("page",1);
					list.put("count",1);
				}else{
					String page=getPage(url);
					list.put("page",page);
					list.put("count",list.getIntValue("page")+1);
				}
				JSONArray items=new JSONArray();
				list.put("item",items);
				for(Element ee:doc.select("div.itemBox,div#w0 > ul > li.list-comic")){
					JSONObject post=new JSONObject();
					items.add(post);
					if(ee.tagName().equalsIgnoreCase("li")){
					try{post.put("title",ee.selectFirst("a.txtA").text());}
					catch(Exception e){}
					post.put("desc",ee.selectFirst("span.info").text());
					post.put("src",ee.selectFirst("mip-img").absUrl("src"));
					post.put("href",ee.selectFirst("a.txtA").absUrl("href"));
					}else{
					post.put("title",ee.selectFirst("a.title").text());
					try{
						post.put("desc",ee.selectFirst("a.coll").text());
						}catch(NullPointerException e){}
					post.put("src",ee.selectFirst("mip-img").absUrl("src"));
					post.put("href",ee.selectFirst("a").absUrl("href"));
					post.put("score",ee.selectFirst("span.date").text());
					}
				}
			}
		}
		catch(HttpStatusException e){
			if(e.getStatusCode()==404){
				String page=getPage(url);
				list.put("page",page);
				list.put("count",page);
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}
	private String getPage(String url){
		Uri uri=Uri.parse(url);
		String page=uri.getQueryParameter("page");
		if(page==null)
			page=uri.getLastPathSegment();
		if(page.matches("^\\d*$"))
			return page;
		return "1";
	}
	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title",doc.selectFirst("h1.title").text());
			post.put("src",doc.selectFirst("div#Cover > mip-img").absUrl("src"));
			Elements desc=doc.select("dl.pic_zi");
			/*desc.replaceAll(new UnaryOperator<Element>(){

					@Override
					public Element apply(Element p1)
					{
						p1.tagName("span").appendElement("br");
						return p1;
					}
				});*/
			post.put("desc",desc.toString());
			post.put("profile",doc.selectFirst("p.txtDesc").text());
			JSONArray video=new JSONArray();
			post.put("video",video);
			JSONArray list=new JSONArray();
			video.add(list);
			for(Element e:doc.select("ul#chapter-list-1 > li > a")){
				JSONObject item=new JSONObject();
				list.add(item);
				item.put("title",e.text());
				item.put("click","list");
				item.put("href",e.absUrl("href"));
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "http://m.gufengmh8.com/";
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search/?keywords="+key+"&page=%d";
	}

	@Override
	public String getGold()
	{
		return getHost()+"search/";
	}
	
}
