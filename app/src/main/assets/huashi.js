function getIndex(){
	var doc=Http.sync({method:"get",url:getHost(),header:{"Referer":getHost(),"User-Agent":"Rhino Proxy"}});
	const index=[];
	array(doc.select("div.p-hot-works,div.p-share-works")).forEach(element=>{
		index.push({
			title:element.selectFirst("div.p-classify-nav > span").text(),
			href:element.selectFirst("div.p-classify-nav a").absUrl("href")+"/%d"
		});
		array(element.select("div.item-box")).forEach(item=>{
			index.push({
				viewtype:"post",
				title:item.selectFirst("div.u-title").text(),
				src:item.selectFirst("img").absUrl("src"),
				click:"photo"});
		});
	});
	return index;
}
function getList(url){
	var path=Uri.parse(url).getPathSegments();
	var json=Http.sync({method:"get",url:"https://rt.huashi6.com/front/works/"+path.get(0)+"list?index="+path.get(1),header:{"Referer":getHost(),"User-Agent":"Rhino Proxy"},"type":"string"});
	var data=eval('('+json+')').data;
	return {
	page:data.index,
	count:data.pageCount,
	item:data.datas.map(item => {
		return {
			click:"photo",
			viewtype:"post",
			title:item.title,
			src:"https://img.huashi6.com/"+(item.coverImageUrl||item.coverImage.path)
			}
	})
	}
}
function getHost(){
	return "https://www.huashi6.com";
}
function getGold(){
	return getHost()+"/hot/%d";
}
function hasTime(){
	return false;
}
