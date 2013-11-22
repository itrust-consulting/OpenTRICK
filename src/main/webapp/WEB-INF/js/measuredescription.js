function showMeasures(normId, languageId) {
	var requestdata = {"languageId":languageId};
	$.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures",
		data: JSON.stringify(requestdata),
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id = 'measurestable']");
			oldSection = $("#measurestable");
			$(oldSection).html($(newSection).html());
			measurestable.fnDestroy();
			measurestable = $('#measurestable').dataTable({
				"bLengthChange" : false,
				"aoColumns": [
								{ "sWidth": "20px" },
								{ "sWidth": "20px" },
								{ "sWidth": "20px" },
								null,
								null,
								{ "sWidth": "20px" }
							]
			});
		}
	});
	return true;
}