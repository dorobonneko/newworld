package com.moe.x4jdm.model;
import org.jsoup.Jsoup;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.Iterator;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONArray;
import android.net.Uri;
import android.text.TextUtils;
import java.util.Map;
import java.util.LinkedHashMap;

public class Indexadh extends Index
{


	@Override
	public void clearCache()
	{
		// TODO: Implement this method
	}

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Elements mains=doc.select("section.hentai");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element main_item=mains_i.next();
					Element header=main_item.selectFirst("header.section-header");
					Elements items=main_item.select("article.post");
					JSONObject main=new JSONObject();
					main.put("title", header.selectFirst("h3").text());
					main.put("href", header.selectFirst("a.btn").absUrl("href") + "page/%d/");
					index.add(main);
					//JSONArray item_json=new JSONArray();
					for (Element e:items)
					{
						JSONObject post=new JSONObject();
						post.put("title", e.selectFirst("h2").text());
						try{post.put("href", e.selectFirst("a.lnk-blk").absUrl("href"));}catch(Exception ee){}
						post.put("src", e.selectFirst("img.lazy").absUrl("data-src"));
						try{post.put("score", e.selectFirst(".quality").text());}catch(Exception ee){}
						
						index.add(post);
					}
					//main.put("item", item_json);
					
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
		// TODO: Implement this method
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
			Document doc=Jsoup.connect(url).timeout(10000).get();
			//list.put("title", doc.selectFirst("h3.section-title").text());
			try
			{
				list.put("page", Integer.parseInt(Uri.parse(url).getLastPathSegment()));
			}
			catch (Exception e)
			{
				list.put("page", 1);
			}
			try
			{
				list.put("count", Integer.parseInt(doc.select(".page-link").last().text()));
			}
			catch (Exception e)
			{
				list.put("count", list.getIntValue("page"));
			}
			Elements posts=doc.select("article.post");
			JSONArray item_json=new JSONArray();
			for (Element e:posts)
			{
				JSONObject post=new JSONObject();
				post.put("title", e.selectFirst("h2").text());
				post.put("href", e.selectFirst("a.lnk-blk").absUrl("href"));
				Element img=e.selectFirst("figure > img");
				if (img != null)
				{
					if (img.hasAttr("data-lazy-src"))
						post.put("src", img.absUrl("data-lazy-src"));
					else
						post.put("src", img.absUrl("src"));
				}
				Element num=e.selectFirst(".num-episode");
				if (num != null)
					post.put("desc", num.text());
				try{post.put("score", e.selectFirst(".quality").text());}catch(Exception ee){}
				
				item_json.add(post);
			}
			list.put("item", item_json);
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
			post.put("title", doc.selectFirst("h1.entry-title").text());
			post.put("profile", doc.selectFirst("div.description").toString());
			post.put("desc", doc.selectFirst("p.tagcloud").toString());
			Element img=doc.selectFirst("meta[property='og:image']");
			if (img != null)
				post.put("src", img.absUrl("content"));
			JSONObject list=JSONObject.parseObject(getList(doc.selectFirst("p > a.btn.lin.blk").absUrl("href")));
			JSONArray items=list.getJSONArray("item");
			Iterator<JSONObject> items_i=(Iterator<JSONObject>) items.iterator();
			while (items_i.hasNext())
			{
				JSONObject item=items_i.next();
				if (!TextUtils.isEmpty(item.getString("desc")))
					item.put("title", item.getString("desc"));
				if (post.getString("src") == null && url.equals(item.getString("href")))
				{
					post.put("src", item.getString("src"));
				}
			}
			JSONArray video=new JSONArray();
			video.add(items);
			post.put("video", video);
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String,String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element iframe=doc.selectFirst("div.video > div#play-iframe > iframe[data-lazy-src]");
			if (iframe != null)
			{
				String key=Uri.parse(iframe.absUrl("data-lazy-src")).getLastPathSegment();
				doc = Jsoup.connect("https://stream.ksplayer.com/download/" + key + "/").get();
				Elements a=doc.select(".download_links > a");
				Iterator<Element> a_i=a.iterator();
				while (a_i.hasNext())
				{
					Element link=a_i.next();
					urls.put(link.child(0).text(), link.absUrl("href"));
				}
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost() + "search/" + key + "/page/%d/";
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "https://animeidhentai.com/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost() + "trending/page/%d/";
	}

}
