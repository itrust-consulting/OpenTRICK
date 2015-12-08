/*! Copyright (c) 2011 by Jonas Mosbech - https://github.com/jmosbech/StickyTableHeaders
	MIT license info: https://github.com/jmosbech/StickyTableHeaders/blob/master/license.txt */

;
(function($, window, undefined) {
	'use strict';

	var name = 'stickyTableHeaders', id = 0, defaults = {
		fixedOffset : 0,
		leftOffset : 0,
		marginTop : 0,
		zindex : 3,
		scrollStartFixMulti : 1.09,
		objDocument : document,
		objHead : 'head',
		objWindow : window,
		scrollableArea : window,
		cacheHeaderHeight : true,
		backgroundColor : 'white',
		forceFirstUpdate : true
	};

	function Plugin(el, options) {
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;
		base.id = id++;

		// Listen for destroyed, call teardown
		base.$el.bind('destroyed', $.proxy(base.teardown, base));

		// Cache DOM refs for performance reasons
		base.$clonedHeader = null;
		base.$originalHeader = null;

		// Cache header height for performance reasons
		base.cachedHeaderHeight = null;

		// Keep track of state
		base.isSticky = false;
		base.hasBeenSticky = false;
		base.leftOffset = null;
		base.topOffset = null;

		base.height = base.$el.height();

		base.init = function() {
			base.setOptions(options);

			base.$el.each(function() {
				var $this = $(this);

				base.updateRequired = base.options.forceFirstUpdate;

				// remove padding on <table> to fix issue #7
				$this.css('padding', 0);

				base.$originalHeader = $('thead:first', this);
				base.$clonedHeader = base.$originalHeader.clone();
				$this.trigger('clonedHeader.' + name, [ base.$clonedHeader ]);

				base.$clonedHeader.addClass('tableFloatingHeader');
				base.$clonedHeader.css({
					display : 'none',
					opacity : 0.0
				});

				base.$originalHeader.addClass('tableFloatingHeaderOriginal');

				base.$originalHeader.after(base.$clonedHeader);

				base.$printStyle = $('<style type="text/css" media="print">' + '.tableFloatingHeader{display:none !important;}'
						+ '.tableFloatingHeaderOriginal{position:static !important;}' + '</style>');
				base.$head.append(base.$printStyle);
			});
			base.loadAttributes();
			base.updateWidth();
			base.toggleHeaders();
			base.bind();
		};

		base.destroy = function() {
			base.$el.unbind('destroyed', base.teardown);
			base.teardown();
		};

		base.fixHeader = function() {
			base.setPositionValues();
			base.$originalHeader.css({
				'position' : 'fixed',
				'margin-top' : base.options.marginTop,
				'left' : base.leftOffset,
				'z-index' : base.options.zindex, // #18: opacity
				// bug
				'background-color' : base.options.backgroundColor
			});
			base.$clonedHeader.css('display', '');
		}

		base.loadAttributes = function() {
			var attributes = base.el.attributes;
			for (var i = 0; i < attributes.length; i++) {
				switch (attributes[i].name) {
				case "data-fh-scroll-multi":
					base.options.scrollStartFixMulti = parseFloat(attributes[i].value);
					break;
				case "data-fh-offset-top":
					if (isNaN(attributes[i].value))
						base.options.fixedOffset = $(attributes[i].value);
					else
						base.options.fixedOffset = parseInt(attributes[i].value);
					break;
				case "data-fh-offset-left":
					if (!isNaN(attributes[i].value))
						base.options.leftOffset = parseInt(attributes[i].value);
					break;
				case "data-fh-margin-top":
					if (!isNaN(attributes[i].value))
						base.options.marginTop = parseInt(attributes[i].value);
					break;
				case "data-fh-z-index":
					if (!isNaN(attributes[i].value))
						base.options.zindex = parseInt(attributes[i].value);
					break;
				case "data-fh-cahing":
					base.options.cacheHeaderHeight = attributes[i].value === "true";
					break;
				case "data-fh-bg-color":
					base.options.backgroundColor = attributes[i].value;
					break;
				case "data-fh-force-first-update":
					base.options.forceFirstUpdate = attributes[i].value === "true";
					break;
				}
			}
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
			base.$scrollableArea.on('scroll.' + name, base.toggleHeaders);
			if (!base.isWindowScrolling) {
				base.$window.on('scroll.' + name + base.id, base.setPositionValues);
				base.$window.on('resize.' + name + base.id, base.toggleHeaders);
			}
			base.$scrollableArea.on('resize.' + name, base.toggleHeaders);
			base.$scrollableArea.on('resize.' + name, base.updateWidth);
			base.$el.on('enabledStickiness.' + name, base.updateWidth)
			base.$el.on('updateStickiness.' + name, base.fixHeader);
		};

		base.unbind = function() {
			// unbind window events by specifying handle so we don't remove too
			// much
			base.$scrollableArea.off('.' + name, base.toggleHeaders);
			if (!base.isWindowScrolling) {
				base.$window.off('.' + name + base.id, base.setPositionValues);
				base.$window.off('.' + name + base.id, base.toggleHeaders);
			}
			base.$scrollableArea.off('.' + name, base.updateWidth);
			base.$el.off('enabledStickiness.' + name, base.updateWidth)
			base.$el.off('updateStickiness.' + name, base.fixHeader);
		};

		// We debounce the functions bound to the scroll and resize events
		base.debounce = function(fn, delay) {
			var timer = null;
			return function() {
				var context = this, args = arguments;
				clearTimeout(timer);
				timer = setTimeout(function() {
					fn.apply(context, args);
				}, delay);
			};
		};

		base.toggleHeaders = base
				.debounce(
						function(e) {
							if (base.$el) {
								base.$el
										.each(function() {

											var $this = base.$el, newLeft, newTopOffset = base.isWindowScrolling ? (isNaN(base.options.fixedOffset) ? base.options.fixedOffset
													.position().top
													+ base.options.fixedOffset.outerHeight() : base.options.fixedOffset) : base.$scrollableArea.offset().top
													+ (!isNaN(base.options.fixedOffset) ? base.options.fixedOffset : 0), offset = $this.offset(), scrollTop = base.$scrollableArea
													.scrollTop()
													+ newTopOffset, scrollLeft = base.$scrollableArea.scrollLeft(),

											headerHeight = base.options.cacheHeaderHeight ? base.cachedHeaderHeight : base.$clonedHeader.height(),

											scrolledPastTop = base.isWindowScrolling ? scrollTop * base.options.scrollStartFixMulti > offset.top : newTopOffset > offset.top, notScrolledPastBottom = (base.isWindowScrolling ? scrollTop
													: 0) < (offset.top + $this.height() - (base.isSticky ? headerHeight : 0) - (base.isWindowScrolling ? 0 : newTopOffset));

											if (scrolledPastTop && notScrolledPastBottom) {
												newLeft = offset.left - scrollLeft + base.options.leftOffset;
												base.leftOffset = newLeft;
												base.topOffset = newTopOffset;
												if (!base.isSticky) {
													base.isSticky = true;
													// make sure the width is
													// correct: the user might
													// have resized the browser
													// while in static mode
													$this.trigger('enabledStickiness.' + name);
												}
												$this.trigger('updateStickiness.' + name);

											} else if (base.isSticky) {
												base.$originalHeader.css('position', 'static');
												base.isSticky = false;
												if (!base.$clonedHeaderCells)
													base.resetWidth($('td,th', base.$clonedHeader), $('td,th', base.$originalHeader));
												$this.trigger('disabledStickiness.' + name);
												base.$clonedHeader.css('display', 'none');
											}
										});
							}
						}, 0);

		base.setPositionValues = base.debounce(function() {
			var winScrollTop = base.$window.scrollTop(), winScrollLeft = base.$window.scrollLeft();
			if (!base.isSticky || winScrollTop < 0 || winScrollTop + base.$window.height() > base.$document.height() || winScrollLeft < 0
					|| winScrollLeft + base.$window.width() > base.$document.width()) {
				return;
			}
			base.$originalHeader.css({
				'top' : base.topOffset - (base.isWindowScrolling ? 0 : winScrollTop),
				'left' : base.leftOffset - (base.isWindowScrolling ? 0 : winScrollLeft)
			});
		}, 0);

		base.updateWidth = base.debounce(function(e) {

			try {
				if (!base.isSticky && !base.$el.is(":visible"))
					throw base.isSticky ? "Element is not visisble" : "Element is not fixed";
				if (base.updateRequired || !base.cacheHeaderHeight || e != undefined) {
					base.updateRequired = false;
					// Copy cell widths from clone
					if (!base.$originalHeaderCells)
						base.$originalHeaderCells = $('th,td', base.$originalHeader);

					if (!base.$clonedHeaderCells)
						base.$clonedHeaderCells = $('th,td', base.$clonedHeader);

					var cellWidths = base.getWidth(base.$clonedHeaderCells);

					base.setWidth(cellWidths, base.$clonedHeaderCells, base.$originalHeaderCells);

					// Copy row width from whole table
					// base.$originalHeader.css('width',
					// base.$clonedHeader.width());

					// If we're caching the height, we need to update the cached
					// value when the width changes
					if (base.options.cacheHeaderHeight)
						base.cachedHeaderHeight = base.$clonedHeader.height();
				}
			} catch (e) {
				base.updateRequired = true;
			}
		}, 0);

		base.getWidth = function($clonedHeaders) {
			var widths = [];
			$clonedHeaders.each(function(index) {
				var width, $this = $(this);

				if ($this.css('box-sizing') === 'border-box') {
					var boundingClientRect = $this[0].getBoundingClientRect();
					if (boundingClientRect.width) {
						width = boundingClientRect.width; // #39: border-box
						// bug
					} else {
						width = boundingClientRect.right - boundingClientRect.left; // ie8
						// bug:
						// getBoundingClientRect()
						// does
						// not
						// have
						// a
						// width
						// property
					}
				} else {
					var $origTh = $('th', base.$originalHeader);
					if ($origTh.css('border-collapse') === 'collapse') {
						if (window.getComputedStyle) {
							width = parseFloat(window.getComputedStyle(this, null).width);
						} else {
							// ie8 only
							var leftPadding = parseFloat($this.css('padding-left'));
							var rightPadding = parseFloat($this.css('padding-right'));
							// Needs more investigation - this is assuming
							// constant border around this cell and it's
							// neighbours.
							var border = parseFloat($this.css('border-width'));
							width = $this.outerWidth() - leftPadding - rightPadding - border;
						}
					} else {
						width = $this.width();
					}
				}

				widths[index] = width;
			});
			return widths;
		};

		base.setWidth = function(widths, $clonedHeaders, $origHeaders) {
			$clonedHeaders.each(function(index) {
				var width = widths[index];
				$origHeaders.eq(index).css({
					'min-width' : width,
					'max-width' : width
				});
			});
		};

		base.resetWidth = function($clonedHeaders, $origHeaders) {
			$clonedHeaders.each(function(index) {
				var $this = $(this);
				$origHeaders.eq(index).css({
					'min-width' : $this.css('min-width'),
					'max-width' : $this.css('max-width')
				});
			});
		};

		base.setOptions = function(options) {
			base.options = $.extend({}, defaults, options);
			base.$window = $(base.options.objWindow);
			base.$head = $(base.options.objHead);
			base.$document = $(base.options.objDocument);
			base.$scrollableArea = $(base.options.scrollableArea);
			base.isWindowScrolling = base.$scrollableArea[0] === base.$window[0];
		};

		base.updateOptions = function(options) {
			base.setOptions(options);
			// scrollableArea might have changed
			base.unbind();
			base.bind();
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
				if (typeof options === 'string') {
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
