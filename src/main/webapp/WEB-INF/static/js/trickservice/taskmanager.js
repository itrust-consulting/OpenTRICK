function TaskManager(title) {
	this.tasks = [];
	this.progressBars = [];
	this.title = title;
	this.view = null;
	this.stomp = null;
	this.disposing = false;
	this.reconnecting = false;

	TaskManager.prototype.Start = function() {
		if(!this.stomp){
			this.__createStompClient();
			if(this.stomp)
				this.UpdateTaskCount();
		}
		
		if(!this.stomp){
			var instance = this;
			setTimeout(function() {
				instance.UpdateTaskCount();
			}, 500);
		}
		return this;
	};
	
	TaskManager.prototype.__createStompClient = function(){
		try {
			var self = this,  socket = new SockJS(context+"/Messaging");
			this.reconnecting = true;
			this.stomp = Stomp.over(socket);
			this.stomp.debug = () => {};
			this.stomp.connect({}, (e) =>{
				self.reconnecting = false;
				self.stomp.subscribe("/User/Task", (message) =>{
					self.__process(JSON.parse(message.body));
				});
			}, (e) => {
				if(self.disposing || self.reconnecting)
					return;
				socket.close();
				delete this.stomp;
				delete socket;
				this.__createStompClient();
			});
		} catch (e) {
			if(this.stomp)
				delete this.stomp
			console.log(e);
		}
	};

	TaskManager.prototype.SetTitle = function(title) {
		this.title = title;
		return this;
	};

	TaskManager.prototype.isEmpty = function() {
		return this.tasks.length == 0;
	};

	TaskManager.prototype.Destroy = function() {
		this.disposing = true;
		console.log("disposing.....")
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function() {
		var instance = this;
		$.ajax({
			url : context + "/Task/InProcessing",
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse == "")
					return false;
				else if (reponse.length) {
					for (var i = 0; i < reponse.length; i++) {
						if ($.isNumeric(reponse[i]) && !(reponse[i] in instance.tasks)) {
							instance.tasks.push(reponse[i]);
							instance.UpdateStatus(reponse[i]);
						}
					}
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
		return this;
	};
	
	TaskManager.prototype.__process = function(reponse){
		var instance = this, taskId = reponse.taskID, downloading = false;
		if (reponse.flag == 3 && !instance.progressBars[taskId])
			instance.progressBars[taskId] = instance.createProgressBar(taskId, reponse.name? MessageResolver(reponse.name) :  undefined, reponse.message);
		
		if (reponse.flag == 3) {
			if(!instance.stomp){
				setTimeout(function() {
					instance.UpdateStatus(taskId);
				}, 1500);
			}
		} else {
			instance.Remove(taskId);
			if (reponse.asyncCallbacks) {
				for (let callback of reponse.asyncCallbacks) {
					switch (callback.action) {
					case "download":
						downloading = true;
						setTimeout(() => {
							showStaticDialog("download", MessageResolver("info.download.exported.file") ,MessageResolver(reponse.name),generateDownloadURL(callback.args));
						}, 600);
						break;
					case "reload":
						setTimeout(() => {location.reload();}, 1500);
						break;
					default:
						if (window[callback.action])
							window[callback.action].apply(null, callback.args);
					}
				}
			} else if (reponse.action)
				eval(reponse.action);
		}
		
		if (reponse.message != null) {
			if (reponse.flag < 3)
				showDialog("error", reponse.message);
			else if (reponse.flag == 3) {
				instance.progressBars[taskId].update('progress', reponse.progress);
				instance.progressBars[taskId].update('message', reponse.message);
				if (reponse.name)
					instance.progressBars[taskId].update('title', MessageResolver(reponse.name));
			} else if(!downloading)
				setTimeout(() => {showDialog("success", reponse.message);}, 600);
		}

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
				instance.__process(reponse);
				return false;
			},
			error : unknowError
		});
	};
};

function generateDownloadURL(data){
	return context + "/Account/"+data.join("/")+"/Download";
}
