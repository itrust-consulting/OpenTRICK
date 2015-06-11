(function($, window) {
	'use strict';

	var name = 'stickyTableHeaders';
	var defaults = {
		fixedOffset : 0
	};

	function Plugin(el, options) {
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;
		base.hasResized = false;
		// Listen for destroyed, call teardown
		base.$el.bind('destroyed', $.proxy(base.teardown, base));

		// Cache DOM refs for performance reasons
		base.$window = $(window);
		base.$clonedHeader = null;
		base.$originalHeader = null;
		base.scrollDirection = 0;
		base.scrollTop = 0;

		// Keep track of state
		base.isSticky = false;
		base.leftOffset = null;
		base.topOffset = null;
		base.cssTargetTopOffset = options.cssTopOffset;
		base.$targetTopOffset = $(options.cssTopOffset);
		base.init = function() {
			base.options = $.extend({}, defaults, options);

			base.$el.each(function() {
				var $this = $(this);

				// remove padding on <table> to fix issue #7
				$this.css('padding', 0);

				base.$originalHeader = $('thead:first', this);
				base.$clonedHeader = base.$originalHeader.clone();

				base.$clonedHeader.addClass('tableFloatingHeader');
				base.$clonedHeader.css('display', 'none');

				base.$originalHeader.addClass('tableFloatingHeaderOriginal');

				base.$originalHeader.after(base.$clonedHeader);

				base.$printStyle = $('<style type="text/css" media="print">' + '.tableFloatingHeader{display:none !important;}'
						+ '.tableFloatingHeaderOriginal{position:static !important;}' + '</style>');
				$('head').append(base.$printStyle);
			});

			base.updateWidth();
			base.toggleHeaders();
			base.bind();
		};

		base.destroy = function() {
			base.$el.unbind('destroyed', base.teardown);
			base.teardown();
		};

		base.teardown = function() {
			if (base.isSticky) {
				base.$originalHeader.css('position', 'static');
			}
			$.removeData(base.el, 'plugin_' + name);
			base.unbind();
			base.$clonedHeader.remove();
			base.$originalHeader.removeClass('tableFloatingHeaderOriginal');
			base.$originalHeader.css('visibility', 'visible');
			base.$printStyle.remove();
			base.el = null;
			base.$el = null;
		};

		base.bind = function() {
			base.$window.on('scroll.' + name, base.toggleHeaders);
			base.$window.on('resize.' + name, base.toggleHeaders);
			base.$window.on('resize.' + name, base.updateWidth);
		};

		base.unbind = function() {
			// unbind window events by specifying handle so we don't remove too
			// much
			base.$window.off('.' + name, base.toggleHeaders);
			base.$window.off('.' + name, base.updateWidth);
			base.$el.off('.' + name);
			base.$el.find('*').off('.' + name);
		};

		base.topPosistion = function() {
			if (base.$targetTopOffset.length) {
				var $targetOffset = base.$targetTopOffset.position(), $targetHeight = base.$targetTopOffset.height();
				base.$originalHeader.css({
					'top' : ($targetOffset.top + $targetHeight) + (isNaN(base.options.fixedOffset) ? 0 : base.options.fixedOffset)
				});
			} else {
				base.$originalHeader.css({
					'top' : 5
				});
			}
		}

		base.canFix = function(scrollTop, headerPosition, headerData) {
			if (!base.$targetTopOffset.length)
				return scrollTop > headerPosition;
			if (base.isSticky && base.scrollDirection > -1)
				return true;
			return (base.$targetTopOffset.offset().top + base.$targetTopOffset.height()) >= (headerData.top + headerData.height * 0.1);
		}

		base.toggleHeaders = function(e) {
			if (e && e.type === "resize")
				base.hasResized = true;
			if (!base.$el.is(":visible"))
				return;
			base.$el.each(function() {
				var $this = $(this);
				var $header = $this.find("thead");
				var newTopOffset = isNaN(base.options.fixedOffset) ? base.options.fixedOffset.height() : base.options.fixedOffset;
				var offset = $this.offset(), data = {
					top : offset.top,
					scrollTop : base.$window.scrollTop(),
					left : offset.left,
					height : $header.height()
				};
				base.scrollDirection = data.scrollTop > base.scrollTop ? 1 : data.scrollTop == base.scrollTop ? 0 : -1;
				base.scrollTop = data.scrollTop;
				var scrollTop = data.scrollTop + newTopOffset;
				var scrollLeft = base.$window.scrollLeft();
				var headerPosition = offset.top - data.height * 2;
				if (base.canFix(scrollTop, headerPosition, data)) {
					var newLeft = offset.left - scrollLeft;
					if (base.isSticky && (newLeft === base.leftOffset) && (newTopOffset === base.topOffset)) {
						return;
					}
					base.$originalHeader.css({
						'position' : 'fixed',
						'left' : newLeft,
						'z-index' : 1, // #18: opacity bug
						'background-color' : 'white'
					});
					base.topPosistion();
					if(!base.isSticky)// fix scroll up when header is fixed
						base.scrollTop = 0;
					base.isSticky = true;
					base.leftOffset = newLeft;
					base.topOffset = newTopOffset;
					base.$clonedHeader.css('display', '');
					// make sure the width is correct: the user might have
					// resized the browser while in static mode
					base.updateWidth();
				} else if (base.isSticky) {
					base.$originalHeader.css('position', 'static');
					base.$clonedHeader.css('display', 'none');
					base.isSticky = false;
				}
			});
		};

		base.updateWidth = function() {
			if (!base.isSticky || !base.$el.is(":visible")) {
				base.hasResized = true;
				return;
			}
			if (base.hasResized) {
				// Copy cell widths from clone
				var $origHeaders = $('th,td', base.$originalHeader);
				$('th,td', base.$clonedHeader).each(function(index) {
					var width, $this = $(this);
					if ($this.css('box-sizing') === 'border-box') {
						width = $this.outerWidth(); // #39: border-box bug
					} else {
						width = $this.width();
					}
					$origHeaders.eq(index).css({
						'min-width' : width,
						'max-width' : width
					});
				});
				// Copy row width from whole table
				base.$originalHeader.css('width', base.$clonedHeader.width());
				base.$originalHeader.css('height', base.$clonedHeader.height());
				base.hasResized = false;
			}
		};

		base.updateOptions = function(options) {
			base.options = $.extend({}, defaults, options);
			base.updateWidth();
			base.toggleHeaders();
		};

		// Run initializer
		base.init();
	}

	// A plugin wrapper around the constructor,
	// preventing against multiple instantiations
	$.fn[name] = function(options) {
		return this.each(function() {
			var instance = $.data(this, 'plugin_' + name);
			if (instance) {
				if (typeof options === "string") {
					instance[options].apply(instance);
				} else {
					instance.updateOptions(options);
				}
			} else if (options !== 'destroy') {
				$.data(this, 'plugin_' + name, new Plugin(this, options));
			}
		});
	};

})(jQuery, window);