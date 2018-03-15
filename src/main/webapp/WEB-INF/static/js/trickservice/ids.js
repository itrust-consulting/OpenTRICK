/**
 * 
 */

function newIDS() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Add",
		type : "GET",
		success : processIDSForm,
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function editIDS(id) {
	if (id == undefined || id < 1) {
		var selectedIDS = findSelectItemIdBySection("section_ids");
		if (selectedIDS.length != 1)
			return false;
		id = selectedIDS[0]
	}

	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Edit/" + id,
		type : "GET",
		success : processIDSForm,
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function processIDSForm(response, textStatus, jqXHR) {
	var $view = $("#formIDSModal", new DOMParser().parseFromString(response, "text/html"));
	if (!$view.length)
		showDialog("#alert-dialog", MessageResolver("error.unknown.view.loading", "An unknown error occurred while loading view"));
	else {
		$view.appendTo("#dialog-body");

		$view.on("hidden.bs.modal", function() {
			$view.remove();
		});

		var $submitButton = $("form>#ids_form_submit_button", $view);

		$(".modal-footer>#ids_submit_button", $view).on("click", function() {
			return $submitButton.click();
		});

		$("form", $view).on("submit", function(e) {
			return saveIDS($view, e.target);
		});

		$view.modal("show");
	}
}

function saveIDS($view, form) {
	$("label.label-danger", $view).remove();
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/IDS/Save",
		type : "POST",
		data : $(form).serialize(),
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				$view.modal("hide");
				reloadSection("section_ids");
			} else {
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
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function deleteIDS() {
	var selectedIDS = findSelectItemIdBySection("section_ids");
	if (selectedIDS.length < 1)
		return false;
	var $confirmModal = showDialog("#confirm-dialog", selectedIDS.length > 1 ? MessageResolver("confirm.delete.multi.ids", "Are you sure you want to delete selected IDSs")
			: MessageResolver("confirm.delete.single.ids", "Are you sure, you want to delete selected IDS"));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function(e) {
		$confirmModal.modal("hide");
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Admin/IDS/Delete",
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(selectedIDS),
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					reloadSection("section_ids");
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					showDialog("#alert-dialog", MessageResolver("error.unknown.delete", "An unknown error occurred while deleting"));
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	});
	return false;
}

function renewIDSToken() {
	var selectedIDS = findSelectItemIdBySection("section_ids");
	if (selectedIDS.length < 1)
		return false;
	var $confirmModal = showDialog("#confirm-dialog", selectedIDS.length > 1 ? MessageResolver("confirm.renew.multi.ids.token",
			"Are you sure you want to renew the tokens of selected IDS") : MessageResolver("confirm.delete.single.ids.token",
			"Are you sure you want to renew token of selected IDS"));
	$confirmModal.find(".modal-footer>button[name='yes']").unbind().one("click", function(e) {
		$confirmModal.modal("hide");
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Admin/IDS/Renew/Token",
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(selectedIDS),
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else if (typeof response == 'object') {
					$("table>tbody>tr[data-trick-id]", "#section_ids").each(function() {
						if (response[this.getAttribute("data-trick-id")])
							$("td[data-trick-field='token']", this).text(response[this.getAttribute("data-trick-id")]);
					});
				} else
					showDialog("#alert-dialog", MessageResolver("error.unknown.renew.ids.token", "An unknown error occurred while renewing token of IDS"));

			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	});
	return false;
}
