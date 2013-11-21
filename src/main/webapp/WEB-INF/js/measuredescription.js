function showMeasures(normId) {
	var responsetext = $.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures",
		type : "POST",
		contentType : "application/json",
	}).responseText;
	alert(responsetext);
	return false;
}