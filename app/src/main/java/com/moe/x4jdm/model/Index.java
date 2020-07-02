package com.moe.x4jdm.model;
import android.content.Context;
import java.util.Map;
import java.util.HashMap;

public abstract class Index
{
	private static Map<String,Index> models=new HashMap<>();
	public synchronized static Index getModel(Context context){
		return getModel(context,getKey(context));
	}
	public synchronized static Index getModel(Context context,String web){
		Index index=models.get(web);
		if(index!=null)return index;
		switch(web){
			case "x4j":
				models.put(web,index=Indexx4jdm.getInstance());
				break;
			case "fjs":
				models.put(web,index=new Indexfjs());
				break;
			case "tl":
				models.put(web,index=new Indextldm());
				break;
			case "adh":
				models.put(web,index=new Indexadh());
				break;
			case "hh":
				models.put(web,index=new Indexhh());
				break;
			case "sakura":
				models.put(web,index=new Indexsakura());
				break;
			case "hb":
				models.put(web,index=new Indexhb());
				break;
			case "xyg":
				models.put(web,index=new Indexxyg());
				break;
			case "jn":
				models.put(web,index=new Indexjn());
				break;
			case "nico":
				models.put(web,index=new Indexnicotv());
				break;
			case "msiv":
				models.put(web,index=new Indexmsiv());
				break;
			case "hhr":
				models.put(web,index=new IndexHentaiHavenRed());
				break;
			case "ppc":
				models.put(web,index=new Indexppc());
				break;
			case "ahv":
				models.put(web,index=new Indexahv());
				break;
			case "dm530":
				models.put(web,index=new Indexdm530());
				break;
			case "llias":
				models.put(web,index=new Indexllias());
				break;
			case "sakura2":
				models.put(web,index=new Indexsakura2());
				break;
			case "xb":
				models.put(web,index=new Indexxbdm());
				break;
			case "nieta":
				models.put(web,index=new IndexNieta());
				break;
			case "tm":
				models.put(web,index=new Indextmdm());
				break;
			case "jyk":
				models.put(web,index=new Indexjyk());
				break;
			case "qqdm":
				models.put(web,index=new Indexqqdm());
				break;
			case "pnext":
				models.put(web,index=new Indexpnext());
				break;
			case "3atv":
				models.put(web,index=new Index3atv());
				break;
			case "dh":
				models.put(web,index=new Indexdhdy());
				break;
			case "moeero":
				models.put(web,index=new Indexmoeero());
				break;
			case "lh":
				models.put(web,index=new Indexlh());
				break;
			case "yh":
				models.put(web,index=new Indexyh());
				break;
			case "hentai":
				models.put(web,index=new IndexHentai());
				break;
			case "youma":
				models.put(web,index=new Indexyouma());
				break;
			case "gufeng":
				models.put(web,index=new Indexgufeng());
				break;
			case "qinqin":
				models.put(web,index=new Indexqinqin());
				break;
			case "sg":
				models.put(web,index=new indexsg());
				break;
			case "neiyix":
				models.put(web,index=new Indexneiyix());
				break;
			case "ex":
				models.put(web,index=new Indexex());
				break;
			case "huashi":
				models.put(web,index=new IndexJs(context,"huashi.js"));
				break;
			default:
			models.put(web,index=new None());
			break;
		}
		return index;
	}
	public static String getKey(Context c){
		String web=c.getSharedPreferences("web",0).getString("web","x4j");
		return web;
	}
	
	public void clearCache(){}
	public abstract String getIndex(int page);
	public boolean indexHasMore(){return false;}
	public String getTime(){
		return null;
	}
	public abstract String getList(String url);
	public abstract String getPost(String url);
	public abstract Map<String,String> getVideoUrl(String url);
	public String search(String key){
		return null;
	}
	public String makeUrl(String url){
		return url;
	}
	public abstract String getHost();
	public String getGold(){
		return null;
	}
	public boolean hasTime(){
		return false;
	}
	public String getFilter(){
		return null;
	}
	
	public String makeFilter(String filter){
		return null;
	}
}
