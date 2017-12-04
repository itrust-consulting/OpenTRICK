function TaskManager(title) {
	this.tasks = [];
	this.progressBars = [];
	this.title = title;
	this.view = null;
	this.stomp = null;
	this.legacy = false;
	this.disposing = false;
	this.reconnecting = false;
	this.subscribing = false;
	this.csrfHeader = null;
	this.csrfToken = null;
	this.locker = false;

	TaskManager.prototype.Start = function () {
		if (!(this.stomp || this.legacy))
			this.__createStompClient();
		else if(this.legacy)
			this.UpdateTaskCount();
		return this;
	};

	TaskManager.prototype.__loadCSRF = function () {
		try {
			this.csrfHeader = $("meta[name='_csrf_header']").attr("content");
			this.csrfToken = $("meta[name='_csrf']").attr("content");
		} catch (e) {
		}
	};
	
	TaskManager.prototype.getLangue = function(){
		return application.language;
	};

	TaskManager.prototype.__createStompClient = function () {

		try {
			var self = this;
			var headers = {};
			var socket = new SockJS(context + "/Messaging");

			if (!(self.csrfHeader && self.csrfToken))
				self.__loadCSRF();

			self.reconnecting = true;
			self.stomp = Stomp.over(socket);
			self.stomp.debug = () => {};

			headers[self.csrfHeader] = self.csrfToken;

			self.stomp.connect(headers, (e) => {
				self.reconnecting = false;
				self.subscribing = true;
				self.stomp.subscribe("/User/Task", (message) => {
					if (self.subscribing)
						self.subscribing = false;
					var tasks = JSON.parse(message.body);
					if(Array.isArray(tasks) ){
						for (let task of tasks) 
							self.__process(task);
					}else self.__process(tasks);
				});
				
				self.stomp.subscribe("/Notification", (data) => {self.__processSystemMessage(data);});
				self.stomp.subscribe("/User/Notification", (data) => {self.__processSystemMessage(data);});
				this.UpdateTaskCount();
			}, (e) => {
				try {
					socket.close();
					delete self.stomp;
					delete socket;
				} finally {
					if (self.disposing || self.reconnecting || self.subscribing)
						self.__switchToLegacyClient();
					else self.__createStompClient();
				}
			});

		} catch (e) {
			this.__switchToLegacyClient();
			if (this.stomp)
				delete this.stomp
			console.log(e);
		}
	};
	
	
	TaskManager.prototype.__processSystemMessage = function(data){
		var self = this,  message = JSON.parse(data.body);
		if(message.type){
			var content =  message.messages[self.getLangue()], notification = application.currentNotifications[message.id]
			if(!content)
				content = MessageResolver(message.code, content, message.parameters);
			if(notification)
				notification.update("message",content);
			else {
				var callback = (e) => {self.Remove(message.id);}
				switch (message.type) {
				case "ERROR":
					notification = showStaticDialog("error",content, undefined, undefined, callback );
					break;
				case "SUCCESS":
					notification = showStaticDialog("success", content, undefined, undefined, callback );
					break;
				case "WARNING":
					notification = showStaticDialog("warning", content, undefined, undefined, callback );
					break;
				default:
					notification = showStaticDialog("info", content, undefined, undefined, callback );
					break;
				}
				application.currentNotifications[message.id]=notification;
			}
		}else  showStaticDialog("info", message);
	};
	
	TaskManager.prototype.__switchToLegacyClient = function () {
		var self = this;
		self.legacy = true;
		if (!self.locker) {
			self.locker = true;
			setTimeout(function () {
				self.UpdateTaskCount();
				self.locker = false;
			}, 500);
		}
	};

	TaskManager.prototype.SetTitle = function (title) {
		this.title = title;
		return this;
	};

	TaskManager.prototype.isEmpty = function () {
		return this.tasks.length == 0;
	};
	
	TaskManager.prototype.Disconnect = function () {
		this.disposing = true;
		if(this.stomp)
			this.stomp.disconnect();
		return true;
	};

	TaskManager.prototype.Destroy = function () {
		this.disposing = true;
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function () {
		var self = this;
		if(self.legacy){
			$.ajax({
				url: context + "/Task/In-progress?legacy=true",
				contentType: "application/json;charset=UTF-8",
				success: function (reponse) {
					 if (Array.isArray(reponse) && reponse.length) {
						for (var i = 0; i < reponse.length; i++) {
							if ($.isNumeric(reponse[i]) && !(reponse[i] in self.tasks)) {
								self.tasks.push(reponse[i]);
								self.UpdateStatus(reponse[i]);
							}
						}
					}
				},
				error: unknowError
			});
		}else this.stomp.send("/Application/Task/In-progress", {}, false);
		return this;
	};

	TaskManager.prototype.createProgressBar = function (taskId, title, message) {
		var notificationType = NOTIFICATION_TYPE.INFO;
		return $.notify({
			title: title,
			icon: notificationType.icon,
			message: message
		}, {
				type: notificationType.type,
				showProgressbar: true,
				allow_dismiss: false,
				z_index: application.notification.z_index,
				offset: application.notification.offset,
				placement: application.notification.placement,
				delay: -1
			});
	};

	TaskManager.prototype.Remove = function (id) {
		 if(application.currentNotifications[id])
			 delete application.currentNotifications[id]
		 else this.__removeTask(id);
		return this;
	};

	TaskManager.prototype.__removeTask = function (id) {
		try {
			
			var index = this.tasks.indexOf(id);
			if (index > -1)
				this.tasks.splice(index, 1);
			if (this.progressBars[id] != undefined && this.progressBars[id] != null) {
				this.progressBars[id].close();
				this.progressBars.splice(id, 1);
			}
			
			if (this.legacy) {
				$.ajax({
					url: context + "/Task/" +id+"/Done",
					contentType: "application/json;charset=UTF-8",
					error: unknowError
				});
			}
			else this.stomp.send("/Application/Task/Done", {}, id);
		} catch (e) {
			console.log(e);
		}
		return this;
	}

	TaskManager.prototype.__process = function (reponse) {
		var self = this, taskId = reponse.taskID, downloading = false;
		if (reponse.flag == 3 && !self.progressBars[taskId])
			self.progressBars[taskId] = self.createProgressBar(taskId, reponse.name ? MessageResolver(reponse.name) : undefined, reponse.message);

		if (reponse.flag == 3) {
			if (self.legacy) {
				setTimeout(function () {
					self.UpdateStatus(taskId);
				}, 1500);
			}
		} else {
			self.Remove(taskId);
			if (reponse.asyncCallbacks) {
				for (let callback of reponse.asyncCallbacks) {
					switch (callback.action) {
						case "download":
							downloading = true;
							setTimeout(() => {
								showStaticDialog("download", MessageResolver("info.download.exported.file"), MessageResolver(reponse.name), generateDownloadURL(callback.args));
							}, 600);
							break;
						case "reload":
							setTimeout(() => { location.reload(); }, 1500);
							break;
						case "gotToPage":
							setTimeout(() => {
								if (window[callback.action])
									window[callback.action].apply(null, callback.args);
							}, 1000);
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
				self.progressBars[taskId].update('progress', reponse.progress);
				self.progressBars[taskId].update('message', reponse.message);
				if (reponse.name)
					self.progressBars[taskId].update('title', MessageResolver(reponse.name));
			} else if (!downloading)
				setTimeout(() => { showDialog("success", reponse.message); }, 600);
		}

	};

	TaskManager.prototype.UpdateStatus = function (taskId) {
		if (!$.isNumeric(taskId))
			return;
		var self = this;
		$.ajax({
			url: context + "/Task/Status/" + taskId,
			contentType: "application/json;charset=UTF-8",
			success: function (reponse) {
				if (reponse == null || reponse.flag == undefined) {
					if (!self.progressBars.length)
						self.Remove(taskId);
					return false;
				}
				self.__process(reponse);
				return false;
			},
			error: unknowError
		});
	};
};

function generateDownloadURL(data) {
	return context + "/Account/" + data.join("/") + "/Download";
}
