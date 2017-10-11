var helpers = Chart.helpers, defaults = Chart.defaults;

var CategoryScale = Chart.scaleService.getScaleConstructor("category");

var HeatMapScale = CategoryScale.extend({
	// Used to get data value locations. Value can either be an index or a
	// numerical value
	getPixelForValue : function(value, index, datasetIndex, includeOffset) {
		var me = this;
		var offset = me.options.offset || includeOffset;// fix bug
		// 1 is added because we need the length but we have the indexes
		var offsetAmt = Math.max((me.maxIndex + 1 - me.minIndex), 1);

		// If value is a data object, then index is the index in the data array,
		// not the index of the scale. We need to change that.
		
		var valueCategory;
		if (value !== undefined && value !== null) {
			valueCategory = me.isHorizontal() ? value.x : value.y;
		}
		
		if (valueCategory !== undefined || (value !== undefined && isNaN(index))) {
			if(typeof valueCategory === "string"){
				var labels = me.getLabels();
				value = valueCategory || value;
				var idx = labels.indexOf(value);
				index = idx !== -1 ? idx : index;
			}else index = valueCategory;
		}

		if (me.isHorizontal()) {
			var valueWidth = me.width / offsetAmt * 1.0;
			var widthOffset = (valueWidth * (index - me.minIndex));

			if (offset) {
				widthOffset += (valueWidth / 2.0);
			}

			return me.left + widthOffset;
		}
		var valueHeight = me.height / offsetAmt;
		var heightOffset = (valueHeight * (index - me.minIndex));

		if (offset) {
			heightOffset += (valueHeight / 2.0);
		}

		return me.top + heightOffset;
	},
	getPixelForTick : function(index) {
		return this.getPixelForValue(this.ticks[index], index + this.minIndex, undefined, true);
	},
	getValueForPixel : function(pixel) {
		var me = this, offset = me.options.offset,value;
		var offsetAmt = Math.max(me._ticks.length, 1), horz = me.isHorizontal();
		var valueDimension = (horz ? me.width : me.height) / offsetAmt;
		pixel -= horz ? me.left : me.top;
		if (pixel <= 0) {
			value = 0;
		} else {
			value = Math.round(pixel / valueDimension);
		}
		return value + me.minIndex;
	}
});

Chart.scaleService.registerScaleType('heatmap', HeatMapScale, {
	position : 'bottom'
});

Chart.plugins.register({
	beforeInit : function(chart) {
		if (chart.config.type === 'heatmap') {
			chart.data.yLabels = chart.data.datasets.map(function(ds) {
				return ds.label;
			});
		}
	},
	beforeUpdate : function(chart) {
		if (chart.config.type === 'heatmap') {
			chart.data.yLabels = chart.data.datasets.map(function(ds) {
				return ds.label;
			});
		}
	},
	afterDatasetsDraw : function(chartInstance, easing) {
		if (chartInstance.chart.config.type === 'heatmap') {
			var ctx = chartInstance.chart.ctx;
			chartInstance.data.datasets.forEach(function(dataset, i) {
				var meta = chartInstance.getDatasetMeta(i);
				if (!meta.hidden) {
					meta.data.forEach(function(element, index) {
						ctx.fillStyle = '#333';
						ctx.textAlign = 'center';
						ctx.textBaseline = 'middle';
						ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, "normal", Chart.defaults.global.defaultFontFamily);
						var dataString = "#" + dataset.data[index], position = element.tooltipPosition();
						ctx.fillText(dataString == "#" ? "" : dataString, position.x, position.y);
					});
				}
			});
		}
	}
});

Chart.defaults.heatmap = {
	radiusScale : 0.025,
	paddingScale : 0.025,
	legend : {
		display : false
	},
	scales : {
		xAxes : [ {
			type : 'heatmap',
			position : 'bottom',
			gridLines : {
				display : false,
				offsetGridLines : false,
				drawBorder : false,
				drawTicks : false
			}
		} ],
		yAxes : [ {
			type : 'heatmap',
			position : 'left',
			gridLines : {
				display : false,
				offsetGridLines : false,
				drawBorder : false,
				drawTicks : false
			}
		} ]
	},

	tooltips : {
		callbacks : {
			title : function(tooltipItems, data) {
				return data.labels[tooltipItems[0].index] + " : " + data.yLabels[tooltipItems[0].datasetIndex];
			},
			label : function(tooltipItem, data) {
				return data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
			}
		}
	}
};

Chart.controllers.heatmap = Chart.DatasetController.extend({
	dataElementType : Chart.elements.Rectangle,

	update : function(reset) {
		var me = this, meta = me.getMeta(), boxes = meta.data;
		// Update Boxes
		helpers.each(boxes, function(box, index) {
			me.updateElement(box, index, reset);
		});
	},
	findIndexOf : (field, value, array) => {
		var index = -1;
		for (var i = 0; i < array.length; i++) {
			if(array[i][field] === value)
				return i;
		}
		return index;
	},
	updateElement : function(box, index) {
		var me = this;
		var meta = me.getMeta();
		var xScale = me.getScaleForId(meta.xAxisID);
		var yScale = me.getScaleForId(meta.yAxisID);
		var dataset = me.getDataset();
		var data = dataset.data[index];
		var datasetIndex = me.index;
		var radiusScale = me.chart.options.radiusScale;
		var paddingScale = me.chart.options.paddingScale;
		var firstIndex = this.findIndexOf('type','heatmap',me.chart.data.datasets);
		var x = xScale.getPixelForValue(data, index, datasetIndex);
		var y = yScale.getPixelForValue(data, firstIndex ===-1? datasetIndex : datasetIndex - firstIndex , datasetIndex);
		var boxWidth = xScale.getPixelForValue(dataset.data[1], 1, datasetIndex) - xScale.getPixelForValue(dataset.data[0], 0, datasetIndex);
		// We only support 'category' scales on the y-axis for now
		var boxHeight = yScale.getPixelForValue(null, 1, 1) - yScale.getPixelForValue(null, 0, 0);
		
		var heightRatio = 1.0, widthRatio = 1.0;
		if (boxWidth > boxHeight)
			widthRatio = boxHeight / boxWidth;
		else
			heightRatio = boxWidth / boxHeight

			// Apply padding
		var horizontalPadding = paddingScale * boxWidth * widthRatio, verticalPadding = paddingScale * boxHeight * heightRatio;
		boxWidth = boxWidth - horizontalPadding;
		boxHeight = boxHeight - verticalPadding;
		y = y + verticalPadding / 2;
		x = x + horizontalPadding / 2;

		// var color = me.chart.options.colorFunction(data);
		var cornerRadius = boxWidth * radiusScale;

		helpers.extend(box, {
			// Utility
			_xScale : xScale,
			_yScale : yScale,
			_datasetIndex : datasetIndex,
			_index : index,
			_data : data,

			// Desired view properties
			_model : {
				// Position
				x : x + boxWidth / 2,
				y : y,

				// Appearance
				base : y + boxHeight,
				height : boxHeight,
				width : boxWidth,
				backgroundColor : dataset.backgroundColor[index],
				cornerRadius : cornerRadius,

				// Tooltip
				label : me.chart.data.labels[index],
				datasetLabel : dataset.label,
			},

			// Override to draw rounded rectangles without any borders
			draw : function() {
				var ctx = this._chart.ctx, vm = this._view, leftX = vm.x - vm.width / 2.0;
				ctx.fillStyle = vm.backgroundColor;
				helpers.drawRoundedRectangle(ctx, leftX, vm.y, vm.width, vm.height, vm.cornerRadius);
				ctx.fill();
			},

			// Override to position the tooltip in the center of the box
			tooltipPosition : function() {
				var vm = this._view;
				return {
					x : vm.x,
					y : vm.y + vm.height / 2
				};
			}
		});

		box.pivot();
	},

	setHoverStyle : function() {
	},
	removeHoverStyle : function() {
	}
});

Chart.defaults.heatmapline = Chart.defaults.line;

Chart.controllers.heatmapline = Chart.controllers.line.extend({
	
	boxSpliter : function(controller, width, height){
		var datasets = controller.config.data.datasets, positionMapper = {};
		datasets.forEach((chart, i) => {
			if(!(chart.type === "heatmapline" && controller.isDatasetVisible(i)))
				return;
			var tempMapper = {};
			for (let point of chart.data) {
				var key = point.x + "-" + point.y;
				if(tempMapper[key] === undefined)
					tempMapper[key] = i;
			}
		
			for ( var key in tempMapper) {
				if(positionMapper[key] === undefined)
					positionMapper[key] = new Map();
				positionMapper[key].set(i, {width: width /2, height: height / 2});
			}
		});
		
		for ( var key in positionMapper) {
			var map = positionMapper[key];
			if(map.size > 1){
				var tmpWidth = width / (map.size * 1.0),tmpHeight = height /  (map.size * 1.0), i = 1;
				for (let value of map.values()) {
					value.width = tmpWidth * i;
					value.height = tmpHeight * i++;
				};
			}
		}
		
		controller.positionMapper = positionMapper;
		
		positionMapper.getPosition = function (value, index) {
			var key = value.x + "-" + value.y, data = this[key] ;
			return data === undefined || !data.has(index)? {width : 0, height : 0} : data.get(index);
		}
	},
	
	updateElement: function(point, index, reset) {
		var me = this;
		var meta = me.getMeta();
		var custom = point.custom || {};
		var dataset = me.getDataset();
		var datasetIndex = me.index;
		var value = dataset.data[index];
		var yScale = me.getScaleForId(meta.yAxisID);
		var xScale = me.getScaleForId(meta.xAxisID);
		var pointOptions = me.chart.options.elements.point;
		
		var x, y;
		
		// Compatibility: If the properties are defined with only the old name,
		// use those values
		if ((dataset.radius !== undefined) && (dataset.pointRadius === undefined)) {
			dataset.pointRadius = dataset.radius;
		}
		if ((dataset.hitRadius !== undefined) && (dataset.pointHitRadius === undefined)) {
			dataset.pointHitRadius = dataset.hitRadius;
		}

		x = xScale.getPixelForValue(typeof value === 'object' ? value : NaN, index, datasetIndex);
		y = reset ? yScale.getBasePixel() : me.calculatePointY(value, index, datasetIndex);
		
		if(datasetIndex == 0 && index == 0){
			var width =  xScale.getPixelForValue(null, 1, datasetIndex) - xScale.getPixelForValue(null, 0, datasetIndex);
			var height = yScale.getPixelForValue(null, 1, 1) - yScale.getPixelForValue(null, 0, 0);
			me.boxSpliter(me.chart.controller, width, height);
		}
		
		var position = me.chart.controller.positionMapper.getPosition(value, datasetIndex) ;
		// Utility
		point._xScale = xScale;
		point._yScale = yScale;
		point._datasetIndex = datasetIndex;
		point._index = index;
		
		// Desired view properties
		point._model = {
			x: x + position.width,
			y: y + position.height,
			skip: custom.skip || isNaN(x) || isNaN(y),
			// Appearance
			radius: custom.radius || helpers.valueAtIndexOrDefault(dataset.pointRadius, index, pointOptions.radius),
			pointStyle: custom.pointStyle || helpers.valueAtIndexOrDefault(dataset.pointStyle, index, pointOptions.pointStyle),
			backgroundColor: me.getPointBackgroundColor(point, index),
			borderColor: me.getPointBorderColor(point, index),
			borderWidth: me.getPointBorderWidth(point, index),
			tension: meta.dataset._model ? meta.dataset._model.tension : 0,
			steppedLine: meta.dataset._model ? meta.dataset._model.steppedLine : false,
			// Tooltip
			hitRadius: custom.hitRadius || helpers.valueAtIndexOrDefault(dataset.pointHitRadius, index, pointOptions.hitRadius)
		};
	}
	
});

