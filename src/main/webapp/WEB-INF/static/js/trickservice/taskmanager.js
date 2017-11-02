function TaskManager(title) {
	this.tasks = [];
	this.progressBars = [];
	this.title = title;
	this.view = null;

	TaskManager.prototype.Start = function() {
		this.Hide();
		var instance = this;
		setTimeout(function() {
			instance.UpdateTaskCount();
		}, 500);
		return this;
	};

	TaskManager.prototype.UpdateUI = function() {
	};

	TaskManager.prototype.__CreateView = function() {
		return this;
	};

	TaskManager.prototype.SetTitle = function(title) {
		this.title = title;
		return this;
	};

	TaskManager.prototype.Show = function() {
		return this;
	};

	TaskManager.prototype.Hide = function() {
		return this;
	};

	TaskManager.prototype.isEmpty = function() {
		return this.tasks.length == 0;
	};

	TaskManager.prototype.Destroy = function() {
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function() {
		var instance = this;
		$.ajax({
			url : context + "/Task/InProcessing",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse == "") {
					instance.Destroy();
					return false;
				} else if (reponse.length) {
					for (var i = 0; i < reponse.length; i++) {
						if ($.isNumeric(reponse[i]) && !(reponse[i] in instance.tasks)) {
							instance.tasks.push(reponse[i]);
							instance.UpdateStatus(reponse[i]);
						}
					}
					instance.UpdateUI();
					if (!instance.isEmpty())
						instance.Show();
				}
			},
			error : unknowError
		});
		return this;
	};

	TaskManager.prototype.createProgressBar = function(taskId, title, message) {
		var notificationType = NOTIFICATION_TYPE.INFO;
		return $.notify({
			title : title,
			icon : notificationType.icon,
			message : message
		}, {
			type : notificationType.type,
			showProgressbar : true,
			allow_dismiss : false,
			z_index : application.notification.z_index,
			offset : application.notification.offset,
			placement : application.notification.placement,
			delay : -1
		});
	};

	TaskManager.prototype.Remove = function(taskId) {
		var index = this.tasks.indexOf(taskId);
		if (index > -1)
			this.tasks.splice(index, 1);
		if (this.progressBars[taskId] != undefined && this.progressBars[taskId] != null) {
			this.progressBars[taskId].close();
			this.progressBars.splice(taskId, 1);
		}
		this.UpdateUI();
		if (this.isEmpty())
			this.Destroy();
		return this;
	};

	TaskManager.prototype.UpdateStatus = function(taskId) {
		if (!$.isNumeric(taskId))
			return;
		var instance = this;
		$.ajax({
			url : context + "/Task/Status/" + taskId,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse.flag == undefined) {
					if (!instance.progressBars.length)
						instance.Remove(taskId);
					return false;
				}

				if (reponse.flag == 3 && !instance.progressBars[taskId])
					instance.progressBars[taskId] = instance.createProgressBar(taskId, reponse.name? MessageResolver(reponse.name) :  undefined, reponse.message);

				if (reponse.message != null) {
					if (reponse.flag < 3)
						showDialog("error", reponse.message);
					else if (reponse.flag > 3)
						showDialog("success", reponse.message);
					else {
						instance.progressBars[taskId].update('progress', reponse.progress);
						instance.progressBars[taskId].update('message', reponse.message);
						if (reponse.name)
							instance.progressBars[taskId].update('title', MessageResolver(reponse.name));
					}
				}

				if (reponse.flag == 3) {
					setTimeout(function() {
						instance.UpdateStatus(taskId);
					}, 1500);
				} else {
					instance.Remove(taskId);
					if (reponse.asyncCallback) {
						if (reponse.asyncCallback.args && reponse.asyncCallback.args.length) {
							if (window[reponse.asyncCallback.action])
								window[reponse.asyncCallback.action].apply(null, reponse.asyncCallback.args);
						} else
							eval(reponse.asyncCallback.action);
					} else if (reponse.action)
						eval(reponse.action);
				}
				return false;
			},
			error : unknowError
		});
	};
};