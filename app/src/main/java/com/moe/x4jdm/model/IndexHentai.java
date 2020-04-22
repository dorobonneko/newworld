package com.moe.x4jdm.model;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexHentai extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost() + "hentai-on").get();
			for(Element section:doc.select("section.section.hentai")){
				JSONObject title=new JSONObject();
				index.add(title);
				title.put("title",section.selectFirst("h3.section-title").text());
				title.put("href",section.selectFirst("header a").absUrl("href")+"page/%d");
				for(Element item:section.select("article.post")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("viewtype","post");
					post.put("src",item.selectFirst("img[loading='lazy']").absUrl("data-lazy-src"));
					post.put("title",item.selectFirst("h2.entry-title").text());
					post.put("desc",item.selectFirst("span.views").text());
					post.put("score",item.selectFirst("span.rating").text());
					post.put("href",item.selectFirst("a.lnk-blk").absUrl("href"));
					//post.put("click",post.getString("href").startsWith(getHost()+"manga/")?"manga":"post");
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
			if(!url.startsWith(getHost())){
				list.put("page",1);
				list.put("count",1);
				JSONArray images=new JSONArray();
				list.put("item",images);
				for(Element e:Jsoup.parse(url).select("article.post")){
					JSONObject image=new JSONObject();
					images.add(image);
					image.put("src",e.selectFirst("img").absUrl("data-src"));
					image.put("viewtype","listcomic");
				}
			}else{
			Document doc=Jsoup.connect(url).get();
			Element pages=doc.selectFirst("div.nav-links");
			if(pages!=null){
				list.put("page",pages.selectFirst("a.current").text());
				list.put("count",pages.select("a.page-link").last().text());
			}else{
				list.put("page",1);
				list.put("count",1);
			}
			JSONArray posts=new JSONArray();
			list.put("item",posts);
			for(Element item:doc.select("ul.movies-lst > li article.post")){
				JSONObject post=new JSONObject();
				posts.add(post);
				post.put("viewtype","post");
				try{
					post.put("src",item.selectFirst("img[loading='lazy']").absUrl("data-lazy-src"));
					}catch(NullPointerException e){
						post.put("src",item.selectFirst("img.wp-post-image").absUrl("src"));
					}
				post.put("title",item.selectFirst("h2.entry-title").text());
				post.put("desc",item.selectFirst("span.views").text());
				post.put("score",item.selectFirst("span.rating").text());
				post.put("href",item.selectFirst("a.lnk-blk").absUrl("href"));
				//post.put("click",post.getString("href").startsWith(getHost()+"manga/")?"manga":"post");
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
			if(url.startsWith(getHost()+"manga/")){
				Document doc=Jsoup.connect(url).get();
				Element header=doc.selectFirst("header.entry-header");
				post.put("title",header.selectFirst("h1.entry-title").text());
				post.put("src",header.selectFirst("img").absUrl("data-lazy-src"));
				post.put("desc",header.selectFirst("ul.manga-details-lst").toString());
				JSONObject play_item=new JSONObject();
				play_item.put("title","read");
				play_item.put("href",doc.selectFirst("ul.mangas-list").toString());
				play_item.put("click","list");
				JSONArray play=new JSONArray();
				play.add(play_item);
				JSONArray video=new JSONArray();
				video.add(play);
				post.put("video",video);
			}else{
			Document doc=Jsoup.connect(url).get();
			Element header=doc.selectFirst("header.entry-header");
			post.put("title",header.selectFirst("h1.entry-title").text());
			post.put("src",header.selectFirst("img[loading='lazy']").absUrl("data-lazy-src"));
			post.put("desc",header.select("div.entry-meta").toString());
			post.put("profile",doc.selectFirst("div.description").toString());
			try{
				String video_url=doc.selectFirst("div.video iframe").absUrl("data-lazy-src").replace("/embed/","/download/");
				JSONObject play_item=new JSONObject();
				play_item.put("title","play");
				play_item.put("href",video_url);
				JSONArray play=new JSONArray();
				play.add(play_item);
				JSONArray video=new JSONArray();
				video.add(play);
				post.put("video",video);
				}catch(Exception e){}
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
			Document doc=Jsoup.connect(url).get();
			Elements a=doc.select(".download_links > a");
			Iterator<Element> a_i=a.iterator();
			while (a_i.hasNext())
			{
				Element link=a_i.next();
				urls.put(link.child(0).text(), link.absUrl("href"));
			}
		}
		catch (IOException e)
		{}

		return urls;
	}

	@Override
	public String getHost()
	{
		return "https://hentai.tv/";
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search/"+key+"/page/%d";
	}

	@Override
	public String getGold()
	{
		return getHost()+"trending/page/%d/";
	}
	
}
