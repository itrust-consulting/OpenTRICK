$(function() { // Wrap it all in jQuery documentReady because we use jQuery UI
	// Dialog

	// StringHelpers Module
	// Call by using StringHelpers.padLeft("1", "000");
	var StringHelpers = function() {
		return {
			// Pad string using padMask. string '1' with padMask '000' will
			// produce '001'.
			padLeft : function(string, padMask) {
				string = '' + string;
				return (padMask.substr(0, (padMask.length - string.length)) + string);
			}
		};
	}();

	// SessionManager Module
	var SessionManager = function() {

		var originalTitle = document.title, extending = false, minutetext = MessageResolver("info.session.minute", "minute"), minutestext = MessageResolver("info.session.minutes",
				"minutes"), secondtext = MessageResolver("info.session.second", "second"), secondstext = MessageResolver("info.session.seconds", "seconds"), expireSessionUrl = ($(
				"#nav-container").attr("data-trick-id") != undefined) ? (context + "/Analysis/" + $("#nav-container").attr("data-trick-id") + "/Select?open=" + application.openMode.value)
				: window.location.href;
		var sessionTimeoutSeconds = 14.9999 * 60, countdownSeconds = 60, secondsBeforePrompt = sessionTimeoutSeconds - countdownSeconds, displayCountdownIntervalId, promptToExtendSessionTimeoutId, count = countdownSeconds, extendSessionUrl = context
				+ '/IsAuthenticate';
		
		var endSession = function() {
			location.href = expireSessionUrl;
		};

		var displayCountdown = function() {
			var countdown = function() {
				var cd = new Date(count * 1000);
				minutes = cd.getUTCMinutes();
				seconds = cd.getUTCSeconds();

				if (minutes === 0)
					minutesDisplay = '';
				else {
					if (minutes === 1)
						minutesDisplay = '<strong>1</strong> ' + minutetext + '.';
					else
						minutesDisplay = '<strong>' + minutes + "</strong> " + minutestext + '.';
				}

				if (seconds === 0)
					secondsDisplay = '';
				else {
					if (seconds === 1)
						secondsDisplay = '<strong>1</strong> ' + secondtext + '.';
					else
						secondsDisplay = '<strong>' + seconds + "</strong> " + secondstext + '.';
				}

				cdDisplay = minutesDisplay + secondsDisplay;

				document.title = 'Expire in ' + StringHelpers.padLeft(minutes, '00') + ':' + StringHelpers.padLeft(seconds, '00');
				$('#sm-countdown').html(cdDisplay);
				if (count === 0) {
					document.title = 'Session Expired';
					endSession();
				}
				count--;
			};
			countdown();
			displayCountdownIntervalId = window.setInterval(countdown, 1000);
		};

		var promptToExtendSession = function() {
			$('#sm-countdown-dialog').modal("show");

			$("#sm-continuebutton").on("click", function() {
				refreshSession();
				$('#sm-countdown-dialog').modal("hide");
			});

			count = countdownSeconds;
			displayCountdown();
		};

		var refreshSession = function() {
			try {
				if (extending)
					return;
				extending = true;
				$.get(extendSessionUrl, function(expired) {
					if (expired === true) {
						window.clearInterval(displayCountdownIntervalId);
						document.title = originalTitle;
						window.clearTimeout(promptToExtendSessionTimeoutId);
						startSessionManager();
					} else
						endSession();
				});
			} finally {
				extending = false;
			}
		};

		var startSessionManager = function() {
			promptToExtendSessionTimeoutId = window.setTimeout(promptToExtendSession, secondsBeforePrompt * 1000);
		};

		// Public Functions
		return {
			start : function() {
				startSessionManager();
			},

			extend : function() {
				refreshSession();
			}
		};
	}();

	SessionManager.start();

	$(document).ajaxStart(function() {
		SessionManager.extend();
	});
});
