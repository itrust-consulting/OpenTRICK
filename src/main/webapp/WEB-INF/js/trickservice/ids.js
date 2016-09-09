/**
 * 
 */

function newIDS() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/form",
		type : "GET",
		success : processIDSForm,
		error : unknowError
	}).complete(() => $progress.hide());
	return false;
}

function editIDS() {
	var selectedIDS = findSelectItemIdBySection("section_ids");
	if (selectedIDS.length != 1)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/edit/"+selectedIDS[0],
		type : "GET",
		success : processIDSForm,
		error : unknowError
	}).complete(() => $progress.hide());
	return false;
}

function processIDSForm(response, textStatus, jqXHR) {
	var $view = $("#formIDSModal", new DOMParser().parseFromString(response, "text/html"));
	if (!$view.length) {
		$("#info-dialog .modal-body").html(MessageResolver("error.unknown.view.loading", "An unknown error occurred while loading view"));
		$("#alert-dialog").modal("toggle");
	} else {
		$view.appendTo("#dialog-body");
		
		$view.on("hidden.bs.modal", () => $view.remove());
		
		var $submitButton = $("form>#ids_form_submit_button", $view);
		
		$(".modal-footer>#ids_submit_button", $view).on("click", () => $submitButton.click());
		
		$("form", $view).on("submit",(e) => saveIDS($view, e.target));
		
		$view.modal("show");
	}
}


function saveIDS($view, form){
	$("label.label-danger",$view).remove();
	
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Save",
		type : "POST",
		data: $(form).serialize(),
		success : (response, textStatus, jqXHR) => {
			if(response["success"]!=undefined){
				$view.modal("hide");
				reloadSection("section_ids");
			}else {
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
					case "prefix":
						$(errorElement).appendTo($("#prefix", $view).parent());
						break;
					case "description":
						$(errorElement).appendTo($("#description", $view).parent());
						break;
					case "error":
					case "token":
					case "ids":
						$(errorElement).appendTo($("#ids-error-container", $view));
						break;
					}
				}
			}
		},
		error : unknowError
	}).complete( () => $progress.hide());
	return false;
}