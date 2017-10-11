Chart.defaults.global.defaultFontColor = "#333";
Chart.defaults.global.defaultFontFamily = "'Corbel', 'Lucida Grande', 'Lucida Sans Unicode', 'Verdana', 'Arial', 'Helvetica', 'sans-serif'";
Chart.defaults.global.defaultFontSize = 13;
Chart.defaults.global.defaultTitleFontSize = 16;

// Define a plugin to provide data labels
Chart.plugins.register({
	afterDatasetsDraw : function(chartInstance, easing) {
		// To only draw at the end of animation, check for easing === 1
		var ctx = chartInstance.chart.ctx, valueFormat = chartInstance.chart.config.options.valueFormat;
		if (!chartInstance.chart.config.options.displayValue)
			return;
		chartInstance.data.datasets
				.forEach(function(dataset, i) {
					var meta = chartInstance.getDatasetMeta(i);
					if (!meta.hidden) {
						meta.data
								.forEach(function(element, index) {
									if (dataset.data[index] != 0) {
										var position = element.tooltipPosition(), value = isFunction(valueFormat) ? valueFormat(dataset.data[index]) : dataset.data[index]
												.toString(), base = element._view.base, y = (base + position.y) / 2.0, fontSize = Chart.defaults.global.defaultFontSize * .5;
										if ((base - position.y) > fontSize) {
											ctx.fillStyle = Chart.defaults.global.defaultFontColor;
											ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, 'normal', Chart.defaults.global.defaultFontFamily);
											ctx.textAlign = 'center';
											ctx.textBaseline = 'middle';
											ctx.fillText(value, position.x, y);
										}
									}
								});
					}
				});
	}
});

function aleChartOption(title) {
	return {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		legend : {
			position : "bottom"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.currencyFormat.format(item.yLabel).replace("€", "k€");
				}
			}
		},
		displayValue : true,
		valueFormat : function(value) {
			return application.currencyFormat.format(value).replace("€", "k€");
		},
		scales : {
			xAxes : [ {
				stacked : false,
				ticks : {
					autoSkip : false
				}
			} ],
			yAxes : [ {
				stacked : false,
				ticks : {
					min : 0,
					userCallback : function(value, index, values) {
						return application.currencyFormat.format(value.toString()).replace("€", "k€");
					}
				}
			} ]
		}
	};
}

function riskOptions(title) {
	return {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.numberFormat.format(item.yLabel);
				}
			}
		},
		displayValue : true,
		valueFormat : function(value) {
			return application.numberFormat.format(value);
		},
		scales : {
			xAxes : [ {
				stacked : true,
				ticks : {
					autoSkip : false
				}
			} ],
			yAxes : [ {
				stacked : true,
				ticks : {
					userCallback : function(value, index, values) {
						return application.numberFormat.format(value);
					}
				}
			} ]
		}
	}
}

function evolutionProfitabilityComplianceOption(id, title) {
	return id.startsWith("chart_evolution_profitability_") ? {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		legend : {
			position : "bottom"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.currencyFormat.format(item.yLabel).replace("€", "k€");
				}
			}
		},
		displayValue : true,
		valueFormat : function(value) {
			return application.currencyFormat.format(value).replace("€", "k€");
		},
		scales : {
			xAxes : [ {
				stacked : true
			} ],
			yAxes : [ {
				stacked : true,
				ticks : {
					userCallback : function(value, index, values) {
						return application.currencyFormat.format(value.toString()).replace("€", "k€");
					}
				}
			} ]
		}
	} : {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		legend : {
			position : "bottom"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.percentageFormat.format(item.yLabel);
				}
			}
		},
		scales : {
			yAxes : [ {
				stacked : false,
				ticks : {
					min : 0,
					max : 1,
					userCallback : function(value, index, values) {
						return application.percentageFormat.format(value);
					}
				}
			} ]
		}
	};
}

function budgetChartOption(id, title) {
	return id.startsWith("chart_budget_cost_") ? {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		legend : {
			position : "bottom"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.currencyFormat.format(item.yLabel).replace("€", "k€");
				}
			}
		},
		displayValue : true,
		valueFormat : function(value) {
			return application.currencyFormat.format(value).replace("€", "k€");
		},
		scales : {
			xAxes : [ {
				stacked : true
			} ],
			yAxes : [ {
				stacked : true,
				ticks : {
					userCallback : function(value, index, values) {
						return application.currencyFormat.format(value.toString()).replace("€", "k€");
					}
				}
			} ]
		}
	} : {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		legend : {
			position : "bottom"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.numberFormat.format(item.yLabel) + " " + MessageResolver("label.metric.man_day");
				}
			}
		},
		displayValue : true,
		valueFormat : function(value) {
			return application.numberFormat.format(value) + " " + MessageResolver("label.metric.man_day");
		},
		scales : {
			ticks : {
				beginAtZero : true,
				max : 100
			},
			yAxes : [ {
				stacked : true,
				ticks : {
					userCallback : function(value, index, values) {
						return application.numberFormat.format(value) + " " + MessageResolver("label.metric.man_day");
					}
				}
			} ],
			xAxes : [ {
				stacked : true
			} ]
		}
	};
}

function heatMapOption() {
	return {
		scales : {
			yAxes : [ {
				scaleLabel : {
					display : true,
					labelString : MessageResolver("label.title.impact", "Impact"),
					fontFamily : Chart.defaults.global.defaultFontFamily,
					fontSize : Chart.defaults.global.defaultTitleFontSize,
				}
			} ],
			xAxes : [ {
				scaleLabel : {
					display : true,
					labelString : MessageResolver("label.title.likelihood", "Probability"),
					fontFamily : Chart.defaults.global.defaultFontFamily,
					fontSize : Chart.defaults.global.defaultTitleFontSize,
				}
			} ]
		},
		tooltips : {
			enabled : false
		},
		legend : {
			display : true,
			position : 'top',
			onClick : function() {
				return false;
			},
			labels : {
				generateLabels : function(chart) {
					var data = chart.data;
					return helpers.isArray(data.legends) ? data.legends.map(function(legend) {
						return {
							text : legend.label,
							fillStyle : legend.color
						};
					}, this) : [];
				}
			}
		}
	};
}

function evolutionHeatMapOption(xLabels, yLabels) {
	return {
		radiusScale : 0.025,
		paddingScale : 0.025,
		scales : {
			yAxes : [ {
				id: "y-axis-0",
				labels : yLabels,
				type : 'heatmap',
				position : 'left',
				gridLines : {
					display : false,
					offsetGridLines : false,
					drawBorder : false,
					drawTicks : false
				},ticks: {
			        reverse: true
			    },
				scaleLabel : {
					display : true,
					labelString : MessageResolver("label.title.impact", "Impact"),
					fontFamily : Chart.defaults.global.defaultFontFamily,
					fontSize : Chart.defaults.global.defaultTitleFontSize,
				}
			}],
			xAxes : [ {
				type : 'heatmap',
				position : 'left',
				labels: xLabels,
				gridLines : {
					display : false,
					offsetGridLines : false,
					drawBorder : false,
					drawTicks : false
				},
				 
				scaleLabel : {
					display : true,
					labelString : MessageResolver("label.title.likelihood", "Probability"),
					fontFamily : Chart.defaults.global.defaultFontFamily,
					fontSize : Chart.defaults.global.defaultTitleFontSize,
				}
			}]
		},
		tooltips : {
			enabled : true,
			callbacks : {
				title : function(tooltipItems, data) {
					var item = tooltipItems[0]; dataset = data.datasets[item.datasetIndex], xLabel = "", yLabel ="";
					if(dataset.type ==="heatmap"){
						var me = this, yScale = me._chart.chart.scales["y-axis-0"], yIndex = yScale.getValueForPixel(item.y) ;
						yLabel = yLabels[yIndex];
						xLabel = item.xLabel
					}else {
						var value = data.datasets[item.datasetIndex].data[item.index];
						yLabel = yLabels[value.y];
						xLabel = xLabels[value.x];
					}
					return xLabel + " : "+ yLabel;
				},
				label : function(tooltipItem, data) {
					return data.datasets[tooltipItem.datasetIndex].title;
				}
			}
		},
		legend : {
			display : true,
			position : 'bottom',
			labels : {
				generateLabels: function(chart) {
					var data = chart.data;
					return helpers.isArray(data.datasets) ? data.datasets.filter(dataset => dataset.type!=="heatmap").map(function(dataset, i) {
						return {
							text: dataset.title,
							fillStyle: (!helpers.isArray(dataset.backgroundColor) ? dataset.backgroundColor : dataset.backgroundColor[0]),
							hidden: !chart.isDatasetVisible(i),
							lineCap: dataset.borderCapStyle,
							lineDash: dataset.borderDash,
							lineDashOffset: dataset.borderDashOffset,
							lineJoin: dataset.borderJoinStyle,
							lineWidth: dataset.borderWidth,
							strokeStyle: dataset.borderColor,
							pointStyle: dataset.pointStyle,
							datasetIndex: i
						};
					}, this) : [];
				}
			}
		}
	};
}

function complianceOptions(title) {
	return {
		legend : {
			position : 'bottom',
		},
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		scale : {
			ticks : {
				beginAtZero : true,
				max : 100,
				min : 0,
				stepSize : 20
			}
		}
	};
}

function aleEvolutionOptions(title) {
	return {
		legend : {
			position : 'bottom',
		},
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.currencyFormat.format(item.yLabel).replace("€", "k€");
				}
			}
		},
		scales : {
			xAxes : [ {
				stacked : false
			} ],
			yAxes : [ {
				stacked : false,
				ticks : {
					mim : 0,
					userCallback : function(value, index, values) {
						return application.currencyFormat.format(value.toString()).replace("€", "k€");
					}
				}
			} ]
		}
	};
}

function dynamicParameterEvolutionOptions(title) {
	return {
		legend : {
			position : 'bottom',
		},
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.numberFormat.format(item.yLabel);
				}
			}
		},
		scales : {
			xAxes : [ {
				stacked : false
			} ],
			yAxes : [ {
				stacked : false,
				ticks : {
					mim : 0,
					userCallback : function(value, index, values) {
						return application.numberFormat.format(value);
					}
				}
			} ]
		}
	};
}

function rffOptions(title) {
	return {
		title : {
			display : title != undefined,
			fontSize : Chart.defaults.global.defaultTitleFontSize,
			text : title
		},
		maintainAspectRatio : false,
		legend : {
			position : "right"
		},
		tooltips : {
			callbacks : {
				label : function(item, data) {
					return application.percentageFormat.format(item.yLabel);
				}
			}
		},
		scales : {
			xAxes : [ {
				stacked : false
			} ],
			yAxes : [ {
				stacked : false,
				ticks : {
					autoSkip : false,
					min : 0,
					max : 1,
					userCallback : function(value, index, values) {
						return application.percentageFormat.format(value);
					}

				}
			} ]
		}
	}
}
