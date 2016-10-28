/**
 * 
 */

function addScaleType() {
	if (findSelectItemIdBySection("section_kb_scale_type").length > 0)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/KnowledgeBase/ScaleType/Add",
		type : "GET",
		success : processScaleTypeForm,
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function editScaleType(id) {
	if (id == undefined || id < 1) {
		var selectedIDScale = findSelectItemIdBySection("section_kb_scale_type");
		if (selectedIDScale.length != 1)
			return false;
		id = selectedIDScale[0]
	}

	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/KnowledgeBase/ScaleType/" + id + "/Edit",
		type : "GET",
		success : processScaleTypeForm,
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function processScaleTypeForm(response, textStatus, jqXHR) {
	var $view = $("#formScaleTypeModal", new DOMParser().parseFromString(response, "text/html"));
	if (!$view.length)
		showDialog("#info-dialog", MessageResolver("error.unknown.view.loading", "An unknown error occurred while loading view"));
	else {
		$view.appendTo("#dialog-body");

		$view.on("hidden.bs.modal", function() {
			$view.remove();
		});

		var $submitButton = $("form>#scale_type_form_submit_button", $view);

		$(".modal-footer>#scale_type_submit_button", $view).on("click", function() {
			return $submitButton.click();
		});

		$("form", $view).on("submit", function(e) {
			return saveScaleType($view, e.target);
		});

		$view.modal("show");
	}
}

function saveScaleType($view, form) {
	$("label.label-danger", $view).remove();
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/KnowledgeBase/ScaleType/Save",
		type : "POST",
		data : $(form).serialize(),
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				$view.modal("hide");
				reloadSection("section_kb_scale_type");
			} else {
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
					case "name":
						$(errorElement).appendTo($("#scale_type_name", $view).parent());
						break;
					case "acronym":
						$(errorElement).appendTo($("#scale_type_acronym", $view).parent());
						break;
					default:
						$(errorElement).appendTo($("#scale-type-error-container", $view));
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

function deleteScaleType() {
	var selectedIDScale = findSelectItemIdBySection("section_kb_scale_type");
	if (selectedIDScale.length < 1)
		return false;
	var $confirmModal = $("#confirm-dialog"), $progress = $("#loading-indicator").show();
	try {
		$confirmModal.find(".modal-body").html(
				selectedIDScale.length > 1 ? MessageResolver("confirm.delete.multi.scale", "Are you sure you want to delete selected scales") : MessageResolver(
						"confirm.delete.single.scale", "Are you sure, you want to delete selected scale"));
		$confirmModal.find(".modal-footer>button[name='yes']").one("click", function(e) {
			$progress.show();
			$.ajax({
				url : context + "/KnowledgeBase/ScaleType/Delete",
				type : "POST",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(selectedIDScale),
				success : function(response, textStatus, jqXHR) {
					if (response["success"] != undefined)
						reloadSection("section_kb_scale_type");
					else if (response["error"] != undefined)
						showDialog("#info-dialog", response["error"]);
					else
						showDialog("#info-dialog", MessageResolver("error.unknown.delete", "An unknown error occurred while deleting"));
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		});
		$confirmModal.modal("show");
	} finally {
		$progress.hide();
	}
	return false;
}