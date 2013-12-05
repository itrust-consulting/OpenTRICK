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
			var header = $(doc).find("#measures_header");
			var body = $(doc).find("#measures_body");
			oldHeader = $("#showMeasuresModel-title");
			oldBody = $("#showmeasuresbody");
			$(oldHeader).html(header.html());
			$(oldBody).html(body.html());
			measurestable=$('#measurestable').dataTable();

			measurestable.fnDestroy();
			measurestable = $('#measurestable').dataTable({
				"bLengthChange" : false,
				"bAutoWidth" : false,
				"aoColumns": [
								{ "sWidth": "20px" },
								{ "sWidth": "20px" },
								{ "sWidth": "20px" },
								null,
								null,
								{ "sWidth": "70px" }
							]
			});
			$("#measurestable").removeAttr( "style" );
			$("#languageselect").change(function(){
				  var language = $(this).find("option:selected").attr("value");
				  var normId = $("#normId").attr("value");
				  alert(normId + ":::" + language);
				  showMeasures(normId, language);
			}); 
						 
			$("#showMeasuresModel").modal("show");

		}
	});
	return false;
}

function saveMeasure(form) {
	result = "";
	return $.ajax({
		url : $("#measure_form").prop("action"),
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addMeasureModel").modal("hide");
				reloadSection("section_measures");
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteAMeasure(measureId) {
	$.ajax({
		url : context + "/KnowledgeBase/Norm/+normId+/Measure/Delete/" + normId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			reloadSection("section_norm");
			return false;
		}
	});
	return false;
}

function deleteMeasure(measureId, name) {
	$("#deleteNormBody").html(MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deletenormbuttonYes").attr("onclick", "deleteAMeasure(" + measureId + ")");
	$("#deleteNormModel").modal('toggle');
	return false;
}

function newMeasure(normId) {
	$("#measure_id").prop("value", "-1");
	$("#norm_id").prop("value", "");
	
	$.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures/AddForm",
		type : "get",
		async : true,
		contentType : "application/json",
		success : function(response) {
			$("#measurelanguages").html(response);	
			$("#measurelanguageselect").focus(function(){
				
				previous = this.value;
			}).change(function(){
				var language = $(this).find("option:selected").attr("value");
				alert(previous + "::" + language);
				$("div[measurelanguage="+previous+"]").attr("style","display:none;");
				$("div[measurelanguage="+language+"]").removeAttr("style");
			});
			$("#measure_form").prop("action", context + "/KnowledgeBase/Norm/"+normId+"/Measures/Save");
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	
	
	
	$("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$("#addmeasurebutton").text(MessageResolver("label.action.add", "Add"));
	
	$("#addMeasureModel").modal('toggle');
	$("#addMeasureModel").children(":first").attr("style","z-index:1060");
	return false;
}

function editSingleMeasure(normId) {
	var rows = $("#section_norm").find("tr[trick-id='" + normId + "'] td");
	$("#norm_id").prop("value", normId);
	$("#norm_label").prop("value", $(rows[0]).text());
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.Norm.Update", "Update a Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
	return false;
}