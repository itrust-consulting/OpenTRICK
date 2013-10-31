function changePassword() {
	$.ajax({
		url : context + "/changePassword",
		contentType : "text/html",
		success : extract
	});
	return false;
}

function extract(data) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(data, "text/html");
	$("#content")
			.html(
					doc.getElementById("login") == null ? doc
							.getElementById("content").innerHTML : doc
							.getElementById("login").outerHTML);
	return false;
}

function changeDisplay(source) {
	$.ajax({
		url : context + "/changeDisplay",
		data : {
			source : source
		},
		contentType : "text/html",
		success : extract
	});
	return false;
}

function nominateArg(label, input, link, index) {
	label.setAttribute("for", "args[" + index + "]");
	label.setAttribute("id", "arg_" + index + "_label");
	label.appendChild(document.createTextNode("Arg n°" + (index + 1)));
	input.setAttribute("name", "args[" + index + "]");
	input.setAttribute("type", "text");
	input.setAttribute("id", "arg_" + index + "_input");
	link.setAttribute("href", "#");
	link.setAttribute("id", "arg_" + index);
	link.setAttribute("onclick", "return removeArg('arg_" + index + "');");
	link.appendChild(document.createTextNode("X"));
	return false;
}

function createNewArg(index, args) {
	var label = document.createElement("label");
	var input = document.createElement("input");
	var link = document.createElement("a");
	var addbutton = document.getElementById("addbutton");
	nominateArg(label, input, link, index);
	args.insertBefore(label, addbutton);
	args.insertBefore(input, addbutton);
	args.insertBefore(link, addbutton);
	return false;
}

function consolidateArg(refIndex, args) {
	var index = parseInt(refIndex.substring(refIndex.lastIndexOf("_") + 1));
	var inputs = args.getElementsByTagName("input");
	for ( var int = index + 1; int < inputs.length + 1 && inputs.length > 0; int++) {
		var label = document.getElementById("arg_" + int + "_label");
		var input = document.getElementById("arg_" + int + "_input");
		var link = document.getElementById("arg_" + int);
		label.removeChild(label.lastChild);
		link.removeChild(link.lastChild);
		nominateArg(label, input, link, int - 1);
	}
	return false;
}

function checkProcessing(message) {
	if(parseInt($("#TaskInProcess").text(),0)==0)
		return true;
	return confirm(message);
}

function removeArg(index) {
	var args = document.getElementById("args");
	args.removeChild(document.getElementById(index));
	args.removeChild(document.getElementById(index + "_input"));
	args.removeChild(document.getElementById(index + "_label"));
	consolidateArg(index, args);
	return false;
}

function addArg() {
	var args = document.getElementById("args");
	if (args != null) {
		var inputs = args.getElementsByTagName("input");
		if (inputs == null || inputs.length == 0)
			return createNewArg(0, args);
		else
			return createNewArg(inputs.length, args);
	}
	return false;
}

function createLeftContent() {
	if (document.getElementById("content_side_left") == null) {
		var leftContent = document.createElement("div");
		leftContent.setAttribute("id", "content_side_left");
		leftContent.setAttribute("class", "content content_side_left");
		var content = document.getElementById("content");
		var parent = content.parentNode;
		parent.insertBefore(leftContent, content);
	}
	return true;
}

function processingTask() {
	$.ajax({
		url : context + "/task/update/count",
		contentType : "application/json",
		success : function(reponse) {
			if ($.isNumeric(reponse)) {
				$("#TaskInProcess").text(reponse);
				if (reponse > 0)
					setTimeout('processingTask();', 3000);
			}
			return false;
		},
		error : function() {
			$("#TaskInProcess").text("0");
			return false;
		}
	});
	return false;
}

function updateTaskStatus(data) {
	createLeftContent();
	var states = $("div[name='status']");
	var lastLength = states.length;
	for ( var int = 0; int < states.length; int++)
		$(states[int]).remove();
	states = $("div[name='task']");
	for ( var int = 0; int < states.length; int++)
		$(states[int]).remove();
	var parser = new DOMParser();
	var doc = parser.parseFromString(data, "text/html");
	states = $(doc).find("div[name='status']");
	for ( var int = 0; int < states.length; int++)
		$("#content_side_left").append($(states[int]));
	if (lastLength > 0 && lastLength != states.length)
		openLink(context + "/task");
	if ($("#content_side_left").is(':empty'))
		$("#content_side_left").remove();
	return states.length != 0;
}

function updateTask() {
	$.ajax({
		url : context + "/task/update",
		contentType : "application/json",
		success : function(reponse) {
			if (reponse == null)
				return false;
			if (updateTaskStatus(reponse)) {
				if (!lastProcessingCount)
					lastProcessingCount = true;
				setTimeout('updateTask();', 1500);
			}
			return false;
		},
		error : function() {
			if (lastProcessingCount)
				openLink(context + "/task");
			$("#content_side_left").remove();
			return false;
		}
	});
	return false;
}

function refraichContentSideLeft() {
	$.ajax({
		url : context + "/admin",
		contentType : "text/html",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			$("#content_side_left").html(
					doc.getElementById("content_side_left").innerHTML);
		}
	});
	return false;
}

function leftSideOpenLink(url, extractor) {
	if (extractor == null)
		extractor = extractLeftSide;
	$.ajax({
		url : url,
		contentType : "text/html",
		success : extractor
	});
	return false;
}

function extractLeftSide(data) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(data, "text/html");
	if (doc.getElementById("login") != null)
		$("#content").html(doc.getElementById("login").outerHTML);
	else
		$("#content_side_left").html(
				doc.getElementById("content_side_left").innerHTML);
	return false;
}

function openLink(url, confirmAction, message) {
	if (!confirmAction || confirm(message)) {
		$.ajax({
			url : url,
			contentType : "text/html",
			success : extract
		});
	}
	return false;
}

function openLinkWithReferer(url, referer, refraich) {
	$.ajax({
		url : url,
		data : {
			redirection : referer
		},
		contentType : "text/html",
		success : extract
	}).done(function() {
		if (refraich)
			refraichContentSideLeft();
	});
	return false;
}

function createProgressbar(completeText, waittingText, labeltext) {
	var dialog = $("#progressbar-dialog").dialog(
			{
				closeOnEscape : false,
				modal : true,
				open : function(event, ui) {
					$(this).parent().children().children(
							'.ui-dialog-titlebar-close').hide();
				}
			});
	var progressbar = $("#progressbar"), progressLabel = $(".progress-label");
	progressbar.progressbar({
		value : false,
		change : function() {
			progressLabel.text(labeltext + progressbar.progressbar("value")
					+ "%");

		},
		complete : function() {
			progressLabel.text(completeText);
			var buttons = {
				Close : {
					text : $("#dialog_button_close").text(),
					click : function() {
						$(this).dialog("close");
					}
				}
			};
			$("#progressbar-dialog").dialog('option', 'buttons', buttons);
			setTimeout("$('#progressbar-dialog').dialog('close')", 15000);
		}
	});
	progressLabel.text(waittingText);
	dialog.dialog('option', 'buttons', null);
	dialog
			.dialog('option', 'title', $('#label_dialog_waiting_indexing')
					.text());
	dialog.show();
	return progressbar;
}

function progress(progressbar, steep) {
	var val = progressbar.progressbar("value") || 0;
	if ((100 - (val + steep)) < val)
		progressbar.progressbar("value", 100);
	else
		progressbar.progressbar("value", val + steep);
}

function post(url, data, refraich) {
	$.ajax({
		url : url,
		type : "post",
		data : $("#" + data).serialize(),
		success : extract
	}).done(function() {
		if (refraich)
			refraichContentSideLeft();
	});
	return false;
}

function createDialogInfo(title, content) {
	var info = document.getElementById("info");
	if (info == null) {
		info = document.createElement("div");
		info.setAttribute("id", "info");
		info.setAttribute("title", title);
		document.getElementById("content").appendChild(info);
	} else
		info.setAttribute("title", title);
	$("#info").html(content);
	return false;
}

function addNewRole(id) {
	var role = $("#role_" + id).val();
	$.ajax({
		url : context + "/role/add/user/" + id,
		contentType : "text/html",
		data : {
			role : role
		},
		success : extract
	});
	return false;
}