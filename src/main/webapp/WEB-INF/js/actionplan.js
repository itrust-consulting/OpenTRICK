function displayActionPlanOptions(analysisId) {

	$.ajax({
		url : context + "/ActionPlan/" + analysisId + "/ComputeOptions",
		type : "GET",
		async : true,
		contentType : "application/json",
		success : function(response) {

			dialog = new Modal();

			var button_footer_compute = document.createElement("button");
			var button_footer_cancel = document.createElement("button");
			button_footer_compute.setAttribute("onclick", "return calculateActionPlanWithOptions(" + analysisId + " ,'#modalBox')");
			button_footer_compute.setAttribute("class", "btn btn-default");
			button_footer_compute.setAttribute("data-dismiss", "modal");
			button_footer_cancel.setAttribute("class", "btn btn-default");
			button_footer_cancel.setAttribute("data-dismiss", "modal");
			$(button_footer_compute).html(MessageResolver("label.actionplan.compute", "Compute"));
			$(button_footer_cancel).html("Cancel");

			dialog.modal_footer_buttons = [ button_footer_compute, button_footer_cancel ];

			dialog.Intialise();

			dialog.setTitle(MessageResolver("title.actionplan.compute.options", "Compute Action Plan: Options"));

			dialog.setBody(response);
			
			dialog.Show();

		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus, errorThrown);
		},
	});
	return false;
}

function removeModal(modalpopup) {
	$("*[class='modal-backdrop fade in']*").remove();

	$(modalpopup).remove();
}

function calculateActionPlanWithOptions(analysisId, modalBox) {
		
	var data = $(modalBox+" #actionplancomputationoptionsform").serializeArray();
	
	removeModal(modalBox);
	
	$.ajax({
		url : context + "/ActionPlan/"+analysisId+"/Compute",
		type : "post",
		data: data,
		async : true,
		contentType : "application/json",
		success : function(response) {
			if (response["success"] != undefined) {
				var taskManager = new TaskManager(MessageResolver("title.actionplan.compute", "Compute Action Plan"));
				taskManager.Start();
			} else if (message["error"]) {
				$("#alert-dialog .modal-body").html(message["error"]);
				$("#alert-dialog").modal("toggle");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	
	return false;
	
}