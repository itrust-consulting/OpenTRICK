function showMeasures(normId, languageId) {
	var requestdata = {"languageId":languageId};
	$.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures",
		data: JSON.stringify(requestdata),
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			var $contextMenu = $("#contextMenu");
			var editRow = $contextMenu.find("li[name='edit_row'] a");
			var deleteElement = $contextMenu.find("li[name='delete'] a");
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var header = $(doc).find("#measures_header");
			var body = $(doc).find("#measures_body");
			var showMeasuresContext = $contextMenu.find("li[name='show_measures'] a");

			oldHeader = $("#showMeasuresModel-title");
			oldBody = $("#showmeasuresbody");
			$(oldHeader).html(header.html());
			$(oldBody).html(body.html());
			measurestable=$('#measurestable').dataTable();

			measurestable.fnDestroy();
			measurestable = $('#measurestable').dataTable({});
					
			$("#measurestable_wrapper").on(
					"contextmenu",
					"table tbody tr",
					function(e) {
						$contextMenu.css("z-index","1070");					
						var rowTrickId = $(e.currentTarget).attr('trick-id');
						var reference = $(e.currentTarget).children(":eq(1)").text();
						$contextMenu.attr("trick-selected-id", rowTrickId);
						showMeasuresContext.parent().attr("hidden", true);
						editRow.attr("onclick", "javascript:return editSingleMeasure(" + rowTrickId + ", "+ normId +");");
						
						deleteElement.attr("onclick", "javascript:return deleteMeasure("+rowTrickId+","+reference+", '" + $("#normLabel").attr("value") +"');");
						$contextMenu.css({
							display : "block",
							left : e.pageX,
							top : e.pageY
						});
						return false;
			});
			
			$contextMenu.on("click", "a", function() {
				$contextMenu.hide();
			});

			$('html').click(function() {
				$contextMenu.hide();
			});

			$('#contextMenu').click(function(event) {
				event.stopPropagation();
			});
			
			$("#measurestable").removeAttr( "style" );
			$("#measurestable").addClass("table table-striped");
			$("#languageselect").change(function(){
				  var language = $(this).find("option:selected").attr("value");
				  var normId = $("#normId").attr("value");
				  //alert(normId + ":::" + language);
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
				var language = $("#languageselect").find("option:selected").attr("value");
				var normId = $("#normId").attr("value");
				return showMeasures(normId, language); 
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteAMeasure(measureId, normId) {
	$.ajax({
		url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/Delete/" + measureId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			var language = $("#languageselect").find("option:selected").attr("value");
			var normId = $("#normId").attr("value");
			return showMeasures(normId, language); 
			return false;
		}
	});
	return false;
}

function deleteMeasure(measureId, reference, norm) {
	$("#deleteMeasureBody").html(
								MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: ") + 
								"&nbsp;<strong>" + reference + "</strong> from the norm <strong>" + norm + " </strong>?");
	var normId = $("#normId").attr("value");
	$("#deletemeasurebuttonYes").attr("onclick", "deleteAMeasure(" + measureId + ", "+ normId +")");
	$("#deleteMeasureModel").attr("style","z-index:1060");
	$("#deleteMeasureModel").modal('toggle');
	return false;
}

function newMeasure(normId) {
	$("#measure_id").prop("value", "-1");

	$("#measure_reference").prop("value", "");
	
	$("#measure_level").prop("value", "");
	
	$.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures/AddForm",
		type : "get",
		async : true,
		contentType : "application/json",
		success : function(response) {
			$("#measurelanguages").html(response);	
			
			var previous=0;
			$("#measurelanguageselect").focus(function(){
				
				previous = this.value;
			}).change(function(){
				
				$("div[measurelanguage='"+previous+"']").attr("style","display:none;");
				var language = $(this).find("option:selected").attr("value");
				$("div[measurelanguage='"+language+"']").removeAttr("style");
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

function editSingleMeasure(measureId, normId) {
	var rows = $("#measurestable_wrapper").find("tr[trick-id='" + measureId + "'] td");
	$("#measure_id").prop("value", measureId);
	$("#measure_reference").prop("value", $(rows[1]).text());
	$("#measure_level").prop("value", $(rows[0]).text());
	$.ajax({
		url : context + "/KnowledgeBase/Norm/"+normId+"/Measures/EditForm",
		type : "post",
		data : JSON.stringify({"measureId":measureId}),
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
	
	$("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Update", "Update new Measure"));
	$("#addmeasurebutton").text(MessageResolver("label.action.edit", "Edit"));
	
	$("#addMeasureModel").modal('toggle');
	$("#addMeasureModel").children(":first").attr("style","z-index:1060");
	return false;
}