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
		$("#task-counter").text($("li",this.view).length);
	};

	TaskManager.prototype.__CreateView = function() {
		this.view = $("#task-manager");
		return this;
	};

	TaskManager.prototype.SetTitle = function(title) {
		this.title = title;
		return this;
	};

	TaskManager.prototype.Show = function() {
		if (this.view == null)
			this.__CreateView();
		this.view.removeAttr("style");
		this.view.parent().addClass("open");
		return this;
	};
	
	TaskManager.prototype.Hide = function() {
		if (this.view == null)
			this.__CreateView();
		this.view.hide();
		this.view.parent().removeClass("open");
		return this;
	};

	TaskManager.prototype.isEmpty = function() {
		return this.tasks.length == 0;
	};

	TaskManager.prototype.Destroy = function() {
		this.Hide().view.empty();
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function() {
		var instance = this;
		$.ajax({
			url : context + "/Task/InProcessing",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse == ""){
					instance.Destroy();
					return false;
				}
				else if (reponse.length) {
					for (var i = 0; i < reponse.length; i++) {
						if ($.isNumeric(reponse[i]) && !(reponse[i] in instance.tasks)) {
							instance.tasks.push(reponse[i]);
							instance.UpdateStatus(reponse[i]);
						}
					}
					instance.UpdateUI();
					if(!instance.isEmpty())
						instance.Show();
				}
			},
			error : unknowError
		});
		return this;
	};

	TaskManager.prototype.createProgressBar = function(taskId) {
		if (this.view == null)
			this.__CreateView();
		var progressBar = new ProgressBar();
		var instance = this;
		progressBar.Initialise();
		progressBar.progress.setAttribute("id", "task_" + taskId);
		if (this.view != null) {
			$container = $("<li />").attr("id", "task-container-" + taskId);
			$container.appendTo(this.view);
			progressBar.Anchor($container[0]);
		}
		progressBar.OnComplete(function(sender) {
			setTimeout(function() {
				progressBar.Destroy();
				instance.Remove(taskId);
			}, 10000);
		});
		this.UpdateUI();
		return progressBar;
	};

	TaskManager.prototype.Remove = function(taskId) {
		$("#task-container-" + taskId).remove();
		var index = this.tasks.indexOf(taskId);
		if (index > -1)
			this.tasks.splice(index, 1);
		if (this.progressBars[taskId] != undefined && this.progressBars[taskId] != null) {
			this.progressBars[taskId].Remove();
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
				if (instance.progressBars[taskId] == null || instance.progressBars[taskId] == undefined)
					instance.progressBars[taskId] = instance.createProgressBar(taskId);

				if (reponse.message != null) {
					instance.progressBars[taskId].Update(reponse.progress, reponse.message);
				}
				if (reponse.flag == 3) {
					setTimeout(function() {
						instance.UpdateStatus(taskId);
					}, 1500);
				} else {
					setTimeout(function() {
						instance.Remove(taskId);
					}, 10000);
					if (reponse.asyncCallback != undefined && reponse.asyncCallback != null) {
						if (reponse.asyncCallback.args != null && reponse.asyncCallback.args.length)
							window[reponse.asyncCallback.action].apply(null, reponse.asyncCallback.args);
						else
							eval(reponse.asyncCallback.action);
					} else if (reponse.taskName != null && reponse.taskName != undefined)
						eval(reponse.taskName.action);
				}
				return false;
			},
			error : unknowError
		});
	};
};