function Modal() {
	this.modal = null;
	this.modal_dialog = null;
	this.modal_header = null;
	this.modal_body = null;
	this.modal_title = null;
	this.modal_footer = null;
	this.modal_head_buttons = [];
	this.modal_footer_buttons = [];

	Modal.prototype.FormContent = function(content){
		this.modal = $(content);
		this.modal_dialog = $(this.modal).find(".modal-dialog");
		this.modal_header = $(this.modal).find(".modal-header");
		
	};

	Modal.prototype.Size = function(map) {
		var size = 0;
		for ( var value in map)
			size++;
		return size;
	};

	Modal.prototype.DefaultHeaderButton = function() {
		var button_head_close = document.createElement("button");
		button_head_close.setAttribute("class", "close");
		button_head_close.setAttribute("data-dismiss", "modal");
		$(button_head_close).html("&times;");
		this.modal_header.insertBefore(button_head_close, this.modal_header.firstChild);
		return false;
	};

	Modal.prototype.DefaultFooterButton = function() {
		var button_footer_OK = document.createElement("button");
		var button_footer_cancel = document.createElement("button");
		button_footer_OK.setAttribute("class", "btn btn-default");
		button_footer_OK.setAttribute("data-dismiss", "modal");
		button_footer_cancel.setAttribute("class", "btn btn-default");
		button_footer_cancel.setAttribute("data-dismiss", "modal");
		$(button_footer_OK).html("OK");
		$(button_footer_cancel).html("Cancel");
		this.modal_footer.appendChild(button_footer_OK);
		this.modal_footer.appendChild(button_footer_cancel);
		return false;
	};
	
	Modal.prototype.__Create = function() {
		// declare modal
		this.modal = document.createElement("div");
		this.modal_header = document.createElement("div");
		this.modal_body = document.createElement("div");
		this.modal_title = document.createElement("h4");
		this.modal_footer = document.createElement("div");
		this.modal_dialog = document.createElement("div");
		var modal_content = document.createElement("div");

		// design modal
		this.modal.setAttribute("id", "modalBox");
		this.modal.setAttribute("data-backdrop", "static");
		this.modal.setAttribute("class", "modal fade in");
		this.modal.setAttribute("role", "dialog");
		this.modal.setAttribute("tabindex", "-1");
		this.modal.setAttribute("aria-hidden", "true");
		this.modal.setAttribute("aria-hidden", "true");
		this.modal_dialog.setAttribute("class", "modal-dialog");
		modal_content.setAttribute("class", "modal-content");
		this.modal_header.setAttribute("class", "modal-header");
		this.modal_title.setAttribute("class", "modal-title");
		this.modal_body.setAttribute("class", "modal-body");
		this.modal_footer.setAttribute("class", "modal-footer");

		// build modal
		this.modal.appendChild(this.modal_dialog);
		this.modal_dialog.appendChild(modal_content);
		modal_content.appendChild(this.modal_header);
		this.modal_header.appendChild(this.modal_title);
		modal_content.appendChild(this.modal_body);
		modal_content.appendChild(this.modal_footer);

		if (!this.Size(this.modal_head_buttons))
			this.DefaultHeaderButton();
		else
			this.__addHeadButton();

		if (!this.Size(this.modal_footer_buttons))
			this.DefaultFooterButton();
		else
			this.__addFooterButton();

		return false;

	};

	Modal.prototype.setTitle = function(title) {
		if (this.modal_title != null)
			$(this.modal_title).text(title);
		return false;
	};

	Modal.prototype.setBody = function(body) {
		if (this.modal_body != null)
			$(this.modal_body).html(body);
		return false;
	};

	Modal.prototype.__addHeadButton = function() {
		if (this.modal_header == null)
			return false;

		$(this.modal_header).find("button").each(function(i) {
			$(this).remove();
		});

		for ( var button in this.modal_head_buttons)
			this.modal_header.insertBefore(this.modal_head_buttons[button], this.modal_header.firstChild);
		return false;
	};

	Modal.prototype.__addFooterButton = function() {
		if (this.modal_footer == null)
			return false;

		$(this.modal_footer).find("button").each(function(i) {
			$(this).remove();
		});

		for ( var button in this.modal_footer_buttons)
			this.modal_footer.appendChild(this.modal_footer_buttons[button]);
		return false;
	};

	Modal.prototype.Intialise = function() {
		this.__Create();
	};

	Modal.prototype.Hide = function() {
		try {
			if (this.modal != null && this.modal != undefined)
				$(this.modal).modal("hide");
		} catch (e) {
			console.log(e);
		}
	};

	Modal.prototype.Distroy = function() {
		var instance = this;
		instance.Hide();
		instance.modal.remove();
		setTimeout(function() {
			delete instance;
		}, 10);
		return false;
	};

	Modal.prototype.Show = function() {
		try {
			if (this.modal != null && this.modal != undefined)
				$(this.modal).modal("toggle");
			else {
				this.Intialise();
				$(this.modal).modal("toggle");
			}
		} catch (e) {
			console.log(e);
		}
	};
}