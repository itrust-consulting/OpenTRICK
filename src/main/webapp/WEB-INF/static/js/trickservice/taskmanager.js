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

	TaskManager.prototype.Start = function() {
		if(!(this.stomp || this.legacy))
			this.__createStompClient();
		else if(this.legacy)
			this.UpdateTaskCount();
		return this;
	};
	
	TaskManager.prototype.__loadCSRF = function(){
		try {
			this.csrfHeader = $("meta[name='_csrf_header']").attr("content");
			this.csrfToken =  $("meta[name='_csrf']").attr("content");
		} catch (e) {
		}
	};
	
	TaskManager.prototype.__createStompClient = function(){
		
		try {
			var self = this;
			var headers = {};
			var socket = new SockJS(context+"/Messaging");
			
			if(!(self.csrfHeader && self.csrfToken))
				self.__loadCSRF();
			
			self.reconnecting = true;
			self.stomp = Stomp.over(socket);
			self.stomp.debug = () => {};
			
			headers[self.csrfHeader] = self.csrfToken;
			
			self.stomp.connect(headers, (e) =>{
				self.reconnecting = false;
				self.subscribing = true;
				self.stomp.subscribe("/Task", (message) =>{
					if(self.subscribing)
						self.subscribing = false;
					self.__process(JSON.parse(message.body));
				});
			}, (e) => {
				try {
					console.log(e);
					socket.close();
					delete self.stomp;
					delete socket;
				} finally{
					if(self.disposing || self.reconnecting || self.subscribing)
						self.__switchToLegacyClient();
					else self.__createStompClient();
				}
			});
		} catch (e) {
			this.__switchToLegacyClient();
			if(this.stomp)
				delete this.stomp
		}
	};
	
	TaskManager.prototype.__switchToLegacyClient = function(){
		var self = this;
		self.legacy = true;
		if(!self.locker){
			self.locker = true;
			setTimeout(function() {
				self.UpdateTaskCount();
				self.locker = false;
			}, 500);
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
		return true;
	};

	TaskManager.prototype.UpdateTaskCount = function() {
		var self = this;
		$.ajax({
			url : context + "/Task/InProcessing?legacy="+(self.legacy),
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || !reponse.length)
					return false;
				else if (reponse.length) {
					for (var i = 0; i < reponse.length; i++) {
						if ($.isNumeric(reponse[i]) && !(reponse[i] in self.tasks)) {
							self.tasks.push(reponse[i]);
							self.UpdateStatus(reponse[i]);
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
		var self = this, taskId = reponse.taskID, downloading = false;
		if (reponse.flag == 3 && !self.progressBars[taskId])
			self.progressBars[taskId] = self.createProgressBar(taskId, reponse.name? MessageResolver(reponse.name) :  undefined, reponse.message);
		
		if (reponse.flag == 3) {
			if(self.legacy){
				setTimeout(function() {
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
				self.progressBars[taskId].update('progress', reponse.progress);
				self.progressBars[taskId].update('message', reponse.message);
				if (reponse.name)
					self.progressBars[taskId].update('title', MessageResolver(reponse.name));
			} else if(!downloading)
				setTimeout(() => {showDialog("success", reponse.message);}, 600);
		}

	};

	TaskManager.prototype.UpdateStatus = function(taskId) {
		if (!$.isNumeric(taskId))
			return;
		var self = this;
		$.ajax({
			url : context + "/Task/Status/" + taskId,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse == null || reponse.flag == undefined) {
					if (!self.progressBars.length)
						self.Remove(taskId);
					return false;
				}
				self.__process(reponse);
				return false;
			},
			error : unknowError
		});
	};
};

function generateDownloadURL(data){
	return context + "/Account/"+data.join("/")+"/Download";
}
