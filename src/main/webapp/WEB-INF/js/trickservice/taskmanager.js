function TaskManager(title) {

	this.tasks = [];
	this.progressBars = [];
	this.title = title;
	this.view = null;

	TaskManager.prototype.Start = function() {
		var instance = this;
		setTimeout(function() {
			instance.UpdateTaskCount();
		}, 1000);
	};

	TaskManager.prototype.__CreateView = function() {
		this.view = new Modal();
		this.view.Intialise();
		this.view.setTitle(this.title);
	};

	TaskManager.prototype.Show = function() {
		if (this.view == null || this.view == undefined)
			this.__CreateView();
		this.view.Show();
	};

	TaskManager.prototype.isEmpty = function() {
		return this.tasks.length == 0;
	};

	TaskManager.prototype.Destroy = function() {
		if (this.view != null)
			this.view.Destroy();
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function() {
		var instance = this;
		$.ajax({
			url : context + "/Task/InProcessing",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse == "")
					return false;
				else if (reponse.length) {
					for (var int = 0; int < reponse.length; int++) {
						if ($.isNumeric(reponse[int]) && !(reponse[int] in instance.tasks)) {
							instance.tasks.push(reponse[int]);
							instance.UpdateStatus(reponse[int]);
						}
					}

					if (!instance.isEmpty())
						instance.Show();
				}
			},error : unknowError
		});
		/*
		 * setTimeout(function() { instance.UpdateTaskCount(); }, 10000);
		 */
		return false;
	};

	TaskManager.prototype.createProgressBar = function(taskId) {
		if (this.view == null || this.view == undefined)
			this.__CreateView();
		var progressBar = new ProgressBar();
		var instance = this;
		progressBar.Initialise();
		progressBar.progress.setAttribute("id", "task_" + taskId);
		progressBar.Anchor(this.view.modal_body);
		progressBar.OnComplete(function(sender) {
			progressBar.setInfo("Complete");
			setTimeout(function() {
				progressBar.Destroy();
				instance.Remove(taskId);
				instance.Destroy();
			}, 10000);
		});
		return progressBar;
	};

	TaskManager.prototype.Remove = function(taskId) {
		var index = this.tasks.indexOf(taskId);
		if (index > -1)
			this.tasks.splice(index, 1);
		if (this.progressBars[taskId] != undefined && this.progressBars[taskId] != null) {
			this.progressBars[taskId].Remove();
			this.progressBars.splice(taskId, 1);
		}
		this.Destroy();
		return false;
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
				if (instance.progressBars[taskId] == null || instance.progressBars[taskId] == undefined) {
					instance.progressBars[taskId] = instance.createProgressBar(taskId);
				}
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
					}, 3000);
					if (reponse.asyncCallback != undefined && reponse.asyncCallback != null) {
						if (reponse.asyncCallback.args !=null && reponse.asyncCallback.args.length)
							window[reponse.asyncCallback.action].apply(null, reponse.asyncCallback.args);
						else
							eval(reponse.asyncCallback.action);
					} else if (reponse.taskName != null && reponse.taskName != undefined)
						eval(reponse.taskName.action);
				}
				return false;
			},error : unknowError
		});
	};
};