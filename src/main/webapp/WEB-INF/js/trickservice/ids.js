/**
 * 
 */

function newIDS() {
	if (findSelectItemIdBySection("section_ids").length > 0)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Add",
		type : "GET",
		success : processIDSForm,
		error : unknowError
	}).complete(() => $progress.hide());
	return false;
}

function editIDS(id) {
	if(id == undefined || id < 1){
		var selectedIDS = findSelectItemIdBySection("section_ids");
		if (selectedIDS.length != 1)
			return false;
		id = selectedIDS[0]
	}
	
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Edit/"+id,
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

function deleteIDS(){
	var selectedIDS = findSelectItemIdBySection("section_ids");
	if (selectedIDS.length <1)
		return false;
	var $confirmModal = $("#confirm-dialog") , $progress = $("#loading-indicator").show();
	try {
		$confirmModal.find(".modal-body").html(selectedIDS.length > 1 ? MessageResolver("confirm.delete.multi.ids", "Are you sur, you want to delete selected IDSs") : MessageResolver("confirm.delete.single.ids", "Are you sur, you want to delete selected IDS"));
		$confirmModal.find(".modal-footer>button[name='yes']").one("click", (e) => {
			$progress.show();
			$.ajax({
				url : context + "/Admin/IDS/Delete",
				type : "POST",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(selectedIDS),
				success : (response, textStatus, jqXHR) => {
					if(response["success"]!=undefined)
						reloadSection("section_ids");
					else {
						$("#info-dialog .modal-body").html(MessageResolver("error.unknown.delete", "An unknown error occurred while deleting"));
						$("#alert-dialog").modal("toggle");
					}
				},
				error : unknowError
			}).complete(() =>$progress.hide());
		});
		$confirmModal.modal("show");
	} finally{
		$progress.hide();
	}
	return false;
}