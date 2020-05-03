package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;

public class Indexyouma extends Index
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
			Elements headers=doc.select("div.shutter-img > a");
			for(Element e:headers){
				JSONObject item=new JSONObject();
				header.add(item);
				item.put("href",e.selectFirst("a").absUrl("href"));
				item.put("title",e.selectFirst("a").attr("data-shutter-title"));
				item.put("src",e.selectFirst("img").absUrl("src"));
			}
			JSONArray tags=new JSONArray();
			index.add(tags);
			for(Element e:doc.select("ul.list > li > a")){
				JSONObject tag=new JSONObject();
				tags.add(tag);
				tag.put("title",e.text());
				tag.put("href",getHost()+"booklist?tag="+e.text()+"&page=%d");
			}
			for(Element title:doc.select("div.index-title")){
				JSONObject tit=new JSONObject();
				index.add(tit);
				tit.put("title",title.selectFirst("h2").text());
				tit.put("href",title.selectFirst("a").absUrl("href")+"?page=%d");
				for(Element e:title.nextElementSibling().select("li")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("title",e.selectFirst("a[title]").attr("title"));
					post.put("href",e.selectFirst("a").absUrl("href"));
					try{
						post.put("src",e.selectFirst("img").absUrl("src"));
					}catch(NullPointerException ee){
					String style=e.selectFirst("p.mh-cover").attr("style");
					post.put("src",style.substring(style.indexOf("(")+1,style.indexOf(")")));
					}
					try{
						post.put("desc",e.selectFirst("p.chapter").text());
						}catch(Exception ee){}
				}
			}
			for(Element e:doc.select("ul.switch-books > li")){
				JSONObject post=new JSONObject();
				index.add(post);
				post.put("title",e.selectFirst("a[title]").attr("title"));
				post.put("href",e.selectFirst("a").absUrl("href"));
				try{
					post.put("src",e.selectFirst("img").absUrl("src"));
				}catch(NullPointerException ee){
					String style=e.selectFirst("p.mh-cover").attr("style");
					post.put("src",style.substring(style.indexOf("(")+1,style.indexOf(")")));
				}
				try{
					post.put("desc",e.selectFirst("p.chapter").text());
				}catch(Exception ee){}
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
			Document doc=Jsoup.connect(url).referrer(url).userAgent("Mozilla (Linux;Android 10)").get();
			if(url.startsWith(getHost()+"chapter/")){
				list.put("page",1);
				list.put("count",1);
				JSONArray items=new JSONArray();
				list.put("item",items);
				for(Element e:doc.select("div#cp_img > img")){
					JSONObject item=new JSONObject();
					items.add(item);
					item.put("src",e.absUrl("data-original"));
					item.put("viewtype","listcomic");
				}
			}else{
			Element page=doc.selectFirst("div.pagination");
			if(page==null|| page.childrenSize()==0){
				list.put("page",1);
				list.put("count",1);
			}else{
			list.put("page",page.selectFirst("a.active").text());
			list.put("count",page.select("a[href]:not(#nextPage)").last().text());
			}
			JSONArray posts=new JSONArray();
			list.put("item",posts);
			for(Element e:doc.select("ul.mh-list > li,ul.book-list > li")){
				JSONObject post=new JSONObject();
				posts.add(post);
				post.put("title",e.selectFirst("a[title]").attr("title"));
				post.put("href",e.selectFirst("a").absUrl("href"));
				try{
					Element img=e.selectFirst("img");
					post.put("src",img.hasAttr("data-original")?img.absUrl("data-original"):img.absUrl("src"));
				}catch(NullPointerException ee){
					String style=e.selectFirst("p.mh-cover").attr("style");
					post.put("src",style.substring(style.indexOf("(")+1,style.indexOf(")")));
				}
				try{
					post.put("desc",e.selectFirst("p.chapter").text());
				}catch(Exception ee){}
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
			post.put("src",doc.selectFirst("div.cover img").absUrl("src"));
			post.put("title",doc.selectFirst("div.info > h1").text());
			post.put("desc",doc.select("div.banner_detail_form > div.info > p.subtitle,div.banner_detail_form > div.info > p.tip").toString());
			post.put("profile",doc.selectFirst("div.info > p.content").toString());
			JSONArray book_source=new JSONArray();
			post.put("video",book_source);
			for(Element ul:doc.select("ul.detail-list-select")){
			JSONArray read=new JSONArray();
			book_source.add(read);
			for(Element li:ul.children()){
				JSONObject item=new JSONObject();
				read.add(item);
				item.put("title",li.text());
				item.put("href",li.child(0).absUrl("href"));
				item.put("click","list");
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
		return null;
	}

	@Override
	public String getHost()
	{
		return "https://www.youmamh.com/";
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search?keyword="+key;
	}
	
}
