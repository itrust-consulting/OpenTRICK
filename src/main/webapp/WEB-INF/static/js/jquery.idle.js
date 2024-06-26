/**
 * File: jquery.idle.js Title: JQuery Idle. A dead simple jQuery plugin that
 * executes a callback function if the user is idle. About: Author Henrique
 * Boaventura (hboaventura@gmail.com). About: Version 1.2.6 About: License
 * Copyright (C) 2013, Henrique Boaventura (hboaventura@gmail.com). MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: - The above
 * copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software. - THE SOFTWARE IS PROVIDED "AS IS",
 * WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/* jslint browser: true */
/* global jQuery: false */
(function($) {
	'use strict';

	$.fn.idle = function(options) {
		var defaults = {
			idle : 72000, // idle time in ms
			sessionTimeout : 90000,
			refreshTime : 30000,
			idleRefreshTime : 18000,
			events : 'mousemove keydown mousedown touchstart session:resquest:send', // events that
																// will trigger
																// the idle
																// resetter
			onIdle : function() {
			}, // callback function to be executed after idle time
			onActive : function() {
			}, // callback function to be executed after back from idleness
			onHide : function() {
			}, // callback function to be executed when window is hidden
			onShow : function() {
			}, // callback function to be executed when window is visible
			onRefreshSession : function() {
			},//callback function to be executed when refreshing session
			onSessionTimeout : function() {
			},//callback function to be executed when session is expired
			onIdleRefreshTime : function(){
			},
			keepTracking : true, // set it to false if you want to track only
									// the first time
			timeoutEnabled : true,
			startAtIdle : false,
			recurIdleCall : false
		}, idle = options.startAtIdle || false, visible = !options.startAtIdle || true, settings = $.extend({}, defaults, options), refreshing = false, lastRefresh = new Date(), lastId = null, refreshId = null, idleRefreshId = null, idleRefreshReseting = false, idleRefreshTimout, resetTimeout, timeout, refreshSession, resetRefresh, resetidleRefresh;

		// event to clear all idle events
		$(this).on("idle:stop", {}, function(event) {
			$(this).off(settings.events);
			settings.keepTracking = false;
			resetTimeout(lastId, settings);
		});

		$(this).on("session:timeout", {}, function(event) {
			idle = false;
			settings.timeoutEnabled = false
			$(this).trigger("idle:stop");
		});

		resetTimeout = function(id, settings) {
			clearTimeout(id);
			if (idle) {
				settings.onActive.call();
				idle = false;
			}
			if (settings.timeoutEnabled) {
				var dateTime = new Date();
				if (idleRefreshReseting || !refreshing && dateTime.getTime() - lastRefresh.getTime() >= settings.refreshTime) {
					refreshing = true;
					idleRefreshReseting = false;
					refreshId = resetRefresh(refreshId, settings);
					lastRefresh = dateTime;
				} else
					idleRefreshId = resetidleRefresh(idleRefreshId,settings);
			} else if (refreshId != null) 
				refreshId = resetRefresh(refreshId, settings)
			
			if (settings.keepTracking)
				return timeout(settings);
		};

		timeout = function(settings) {
			var timer = (settings.recurIdleCall ? setInterval : setTimeout), id;
			id = timer(function() {
				idle = true;
				settings.onIdle.call();
			}, settings.idle);
			return id;
		};

		resetidleRefresh = function(id, settings) {
			clearTimeout(id);
			if (settings.timeoutEnabled)
				return idleRefreshTimout(settings);
		};

		resetRefresh = function(id, settings) {
			clearTimeout(id);
			if (refreshing)
				settings.onRefreshSession.call();
			refreshing = false
			if (settings.timeoutEnabled)
				return refreshSession(settings);
		};

		idleRefreshTimout = function(settings) {
			return setTimeout(function() {
				idleRefreshReseting = true;
				settings.onIdleRefreshTime.call();
				lastId = resetTimeout(lastId,settings);
			}, settings.idleRefreshTime);
		}

		refreshSession = function(settings) {
			return setTimeout(function() {
				$(document).trigger("session:timeout");
				settings.onSessionTimeout.call();
			}, settings.sessionTimeout);
		};

		return this.each(function() {
			lastId = timeout(settings);
			if (settings.timeoutEnabled) {
				refreshId = refreshSession(settings);
				idleRefreshId = idleRefreshTimout(settings);
			}

			$(this).on(settings.events, function(e) {
				lastId = resetTimeout(lastId, settings);
			});
			if (settings.onShow || settings.onHide) {
				$(document).on("visibilitychange webkitvisibilitychange mozvisibilitychange msvisibilitychange", function() {
					if (document.hidden || document.webkitHidden || document.mozHidden || document.msHidden) {
						if (visible) {
							visible = false;
							settings.onHide.call();
						}
					} else {
						if (!visible) {
							visible = true;
							settings.onShow.call();
						}
					}
				});
			}
		});

	};
}(jQuery));
