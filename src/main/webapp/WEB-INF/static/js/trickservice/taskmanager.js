

/**
 * Represents a Task Manager.
 * @constructor
 * @param {string} title - The title of the Task Manager.
 */
function TaskManager(title) {
	this.tasks = [];
	this.progressBars = [];
	this.legacyTimers = [];
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

	/**
	 * Starts the Task Manager.
	 * @returns {TaskManager} The Task Manager instance.
	 */
	TaskManager.prototype.Start = function () {
		if (!(this.stomp || this.legacy))
			this.__createStompClient();
		else if (this.legacy)
			this.UpdateTaskCount();
		return this;
	};

	/**
	 * Loads the CSRF token and header.
	 * @private
	 */
	TaskManager.prototype.__loadCSRF = function () {
		try {
			this.csrfHeader = $("meta[name='_csrf_header']").attr("content");
			this.csrfToken = $("meta[name='_csrf']").attr("content");
		} catch (e) {
		}
	};

	/**
	 * Gets the language of the application.
	 * @returns {string} The language of the application.
	 */
	TaskManager.prototype.getLangue = function () {
		return application.language;
	};

	/**
	 * Creates a Stomp client for WebSocket communication.
	 * @private
	 */
	TaskManager.prototype.__createStompClient = function () {

		try {
			let self = this;
			let headers = {};

			if (!(self.csrfHeader && self.csrfToken))
				self.__loadCSRF();

			self.legacyTimers.push(setTimeout(() => { self.__switchToLegacyClient(); }, 700));

			headers[self.csrfHeader] = self.csrfToken;
			self.reconnecting = true;
			self.stomp = new StompJs.Client({
				connectHeaders: headers,
				reconnectDelay: 5000,
				heartbeatIncoming: 0, // Disable incoming heartbeats
    			heartbeatOutgoing: 0, // Disable outgoing heartbeats
				webSocketFactory: () => {
					return new SockJS('/Messaging/');
				}
			});

			self.stomp.onConnect = (e) => {
				self.reconnecting = false;
				self.subscribing = true;
				self.stomp.subscribe("/User/Task", (message) => {
					if (self.subscribing)
						self.subscribing = false;
					let tasks = JSON.parse(message.body);
					if (Array.isArray(tasks)) {
						for (let task of tasks)
							self.__process(task);
					} else self.__process(tasks);
					$(document).trigger("session:resquest:send");
				}, headers);

				self.stomp.subscribe("/Notification", (data) => {
					self.__processSystemMessage(data);
					$(document).trigger("session:resquest:send");
				}, headers);
				self.stomp.subscribe("/User/Notification", (data) => {
					self.__processSystemMessage(data);
					$(document).trigger("session:resquest:send");
				}, headers);

				self.__stopLegacyClient();

				self.UpdateTaskCount();

				$(document).trigger("session:resquest:send");
			};

			self.stomp.onStompError = (e) => {
				try {
					console.log('Broker reported error: ' + frame.headers['message']);
					console.log('Additional details: ' + frame.body);
					self.stomp.deactivate();
					delete self.stomp;
				} finally {
					if (self.disposing || self.reconnecting || self.subscribing)
						self.__switchToLegacyClient();
					else self.__createStompClient();
				}
			};

			self.stomp.onWebSocketError = (e) => {
				if (!self.legacy && (self.disposing || self.reconnecting || self.subscribing)) {
					self.__switchToLegacyClient();
					self.stomp.deactivate();
				}
			}

			self.stomp.activate();

		} catch (e) {
			self.__switchToLegacyClient();

			if (self.stomp)
				self.stomp.deactivate();

			console.log(e);
		}
	};

	/**
	 * Processes a system message received from the server.
	 * @param {Object} data - The system message data.
	 * @private
	 */
	TaskManager.prototype.__processSystemMessage = function (data) {
		var self = this, message = JSON.parse(data.body);
		if (message.type) {
			var content = message.messages[self.getLangue()], notification = application.currentNotifications[message.id]
			if (!content)
				content = MessageResolver(message.code, content, message.parameters);
			if (content === null)
				content = "...";
			if (notification)
				notification.update("message", content);
			else {
				var callback = (e) => { self.Remove(message.id); }
				switch (message.type) {
					case "ERROR":
						notification = showStaticDialog("error", content, undefined, undefined, callback);
						break;
					case "SUCCESS":
						notification = showStaticDialog("success", content, undefined, undefined, callback);
						break;
					case "WARNING":
						notification = showStaticDialog("warning", content, undefined, undefined, callback);
						break;
					default:
						notification = showStaticDialog("info", content, undefined, undefined, callback);
						break;
				}
				application.currentNotifications[message.id] = notification;
			}
		} else showStaticDialog("info", message);

	};

	/**
	 * Switches to the legacy client for communication.
	 * @private
	 */
	TaskManager.prototype.__switchToLegacyClient = function () {
		var self = this;
		self.legacy = true;
		if (!self.locker) {
			self.locker = true;
			self.legacyTimers.push(setTimeout(function () {
				self.UpdateTaskCount();
				self.locker = false;
			}, 500));
		}
	};

	/**
	 * Stops the legacy client.
	 * @private
	 */
	TaskManager.prototype.__stopLegacyClient = function () {
		var self = this;
		self.legacy = false;
		if (!self.locker) {
			self.locker = true;
			for (let id of self.legacyTimers.slice()) {
				clearTimeout(id);
			}

		}
	};

	/**
	 * Sets the title of the Task Manager.
	 * @param {string} title - The new title.
	 * @returns {TaskManager} The Task Manager instance.
	 */
	TaskManager.prototype.SetTitle = function (title) {
		this.title = title;
		return this;
	};

	/**
	 * Checks if the Task Manager is empty.
	 * @returns {boolean} True if the Task Manager is empty, false otherwise.
	 */
	TaskManager.prototype.isEmpty = function () {
		return this.tasks.length == 0;
	};

	/**
	 * Disconnects the Task Manager.
	 * @returns {boolean} True if the Task Manager is disconnected, false otherwise.
	 */
	TaskManager.prototype.Disconnect = function () {
		this.disposing = true;
		if (this.stomp)
			this.stomp.deactivate();
		return true;
	};

	/**
	 * Destroys the Task Manager.
	 * @returns {boolean} True if the Task Manager is destroyed, false otherwise.
	 */
	TaskManager.prototype.Destroy = function () {
		this.disposing = true;
		return true;
	};

	/**
	 * Updates the task count of the Task Manager.
	 * @returns {TaskManager} The Task Manager instance.
	 */
	TaskManager.prototype.UpdateTaskCount = function () {
		var self = this;
		if (self.legacy) {
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
		} else this.stomp.publish({ destination: "/Application/Task/In-progress", body: "false" });
		return this;
	};

	/**
	 * Creates a progress bar for a task.
	 * @param {number} taskId - The ID of the task.
	 * @param {string} title - The title of the progress bar.
	 * @param {string} message - The message of the progress bar.
	 * @returns {Object} The progress bar object.
	 */
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

	/**
	 * Removes a task or a notification.
	 * @param {number} id - The ID of the task or notification.
	 * @returns {TaskManager} The Task Manager instance.
	 */
	TaskManager.prototype.Remove = function (id) {
		if (application.currentNotifications[id])
			delete application.currentNotifications[id]
		else this.__removeTask(id);
		return this;
	};

	/**
	 * Removes a task.
	 * @param {number} id - The ID of the task.
	 * @private
	 */
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
					url: context + "/Task/" + id + "/Done",
					contentType: "application/json;charset=UTF-8",
					error: unknowError
				});
			}
			else this.stomp.publish({ destination: "/Application/Task/Done", body: id });
		} catch (e) {
			console.log(e);
		}
		return this;
	}

	/**
	 * Processes a task response received from the server.
	 * @param {Object} reponse - The task response data.
	 * @private
	 */
	TaskManager.prototype.__process = function (reponse) {
		var self = this, taskId = reponse.taskID, downloading = false;
		if (reponse.flag == 3 && !self.progressBars[taskId])
			self.progressBars[taskId] = self.createProgressBar(taskId, reponse.name ? MessageResolver(reponse.name) : undefined, reponse.message);

		if (reponse.flag == 3) {
			if (self.legacy) {
				self.legacyTimers.push(setTimeout(function () {
					self.UpdateStatus(taskId);
				}, 1500));
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
							setTimeout(() => { location.reload(); }, 1600);
							break;
						case "gotToPage":
							setTimeout(() => {
								if (window[callback.action])
									window[callback.action].apply(null, callback.args);
							}, 1500);
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

	/**
	 * Updates the status of a task.
	 * @param {number} taskId - The ID of the task.
	 * @returns {TaskManager} The Task Manager instance.
	 */
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
