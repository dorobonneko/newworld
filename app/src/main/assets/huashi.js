function getIndex(){
	var doc=Http.sync({method:"get",url:getHost()+"/",header:{"Referer":getHost(),"User-Agent":"Rhino Proxy"}});
	var elements=doc.select("div.p-hot-works,div.p-share-works");
	var index=[];
	for(var i=0;i<elements.size();i++){
		var title={};
		var element=elements.get(i);
		title.title=element.selectFirst("div.p-classify-nav > span").text();
		title.href=element.selectFirst("div.p-classify-nav a").absUrl("href")+"/%d";
		index.push(title);
		var items=element.select("div.item-box");
		for(var n=0;n<items.size();n++){
			var item=items.get(n);
			var post={};
			post.viewtype="post";
			post.title=item.selectFirst("div.u-title").text();
			post.src=item.selectFirst("img").absUrl("src");
			post.click="photo";
			index.push(post);
		}
	}
	return index;
}
function getList(url){
	var path=Uri.parse(url).getPathSegments();
	var json=Http.sync({method:"get",url:"https://rt.huashi6.com/front/works/"+path.get(0)+"list?index="+path.get(1),header:{"Referer":getHost(),"User-Agent":"Rhino Proxy"},"type":"string"});
	var data=eval('('+json+')').data;
	var list={};
	list.page=data.index;
	list.count=data.pageCount;
	var items=[];
	list.item=items;
	var datas=data.datas;
	for(var i=0;i<datas.length;i++){
		var post={};
		var item=datas[i];
		items.push(post);
		post.title=item.title;
		if(item.coverImageUrl){
		post.src="http://img.huashi6.com/"+item.coverImageUrl;
		}else{
			post.src="http://img.huashi6.com/"+item.coverImage.path;
		}
		post.viewtype="post";
		post.click="photo";
	}
	return list;
}
function getHost(){
	return "https://www.huashi6.com";
}
function getGold(){
	return getHost()+"/hot/%d";
}
