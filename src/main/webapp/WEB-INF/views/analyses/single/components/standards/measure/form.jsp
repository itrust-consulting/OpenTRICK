<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modalMeasureForm" tabindex="-1" role="dialog" data-aria-labelledby="measureForm" style="z-index: 1042" data-aria-hidden="true">
	<div class="modal-dialog" style="width: 50%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<c:choose>
						<c:when test="${measureForm.id<1}">
							<fmt:message key="label.tile.add.measure" />
						</c:when>
						<c:otherwise>
							<fmt:message key="label.tile.edit.measure" />
						</c:otherwise>
					</c:choose>

				</h4>
			</div>
			<div class="modal-body">
				<ul id="measure_form_tabs" class="nav nav-tabs">
					<c:if test="${not empty(isAnalysisOnly) and isAnalysisOnly}">
						<li class="active"><a href="#tab_general" data-toggle="tab"><spring:message code="label.menu.assetmeasure.gerneral" text="General" /></a></li>
						<c:if test="${measureForm.type == 'ASSET' }">
							<li><a href="#tab_asset" data-toggle="tab"><spring:message code="label.menu.assetmeasure.assets" text="Assets" /></a></li>
						</c:if>
					</c:if>
					<c:if test="${isComputable or not isAnalysisOnly}">
						<li ${empty(isAnalysisOnly) or not isAnalysisOnly ?'class="active"':''}><a href="#tab_properties" data-toggle="tab"><spring:message
									code="label.menu.assetmeasure.properties" text="Properties" /></a></li>
					</c:if>
					<li id="error_container" style="padding-top: 10px"></li>
				</ul>
				<form name="measureForm" style="height: 478px;" action="/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal tab-content" id="measure_form" method="post">
					<input type="hidden" name="id" value="${measureForm.id}" id="id"> <input type="hidden" name="idStandard" value="${measureForm.idStandard}" id="idStandard">
					<c:if test="${not empty(isAnalysisOnly) and isAnalysisOnly}">
						<div id="tab_general" class="tab-pane active" style="padding-top: 17px;">

							<div class="form-group">
								<label for="reference" class="col-sm-3 control-label"> <fmt:message key="label.reference" /></label>
								<div class="col-sm-9">
									<input name="reference" id="reference" value='<spring:message text="${measureForm.reference}"/>' class="form-control" />
								</div>
							</div>

							<div class="form-group">
								<label for="level" class="col-sm-3 control-label"> <fmt:message key="label.measure.level" /></label>
								<div class="col-sm-9">
									<input name="level" id="level" value="${measureForm.level}" class="form-control" type="number" min="1" />
								</div>
							</div>

							<div class="form-group">
								<label for="computable" class="col-sm-3 control-label"> <fmt:message key="label.measure.computable" /></label>
								<div class="col-sm-9" align="center">
									<input name="computable" id="computable" ${measureForm.computable?'checked':''} class="checkbox" type="checkbox" />
								</div>
							</div>

							<div class="form-group">
								<label for="domain" class="col-sm-3 control-label"> <fmt:message key="label.measure.domain" /></label>
								<div class="col-sm-9">
									<textarea name="domain" id="domain" rows="4" class="form-control resize_vectical_only"><spring:message text="${measureForm.domain}" /></textarea>
								</div>
							</div>

							<div class="form-group">
								<label for="description" class="col-sm-3 control-label"><fmt:message key="label.measure.description" /></label>
								<div class="col-sm-9">
									<textarea name="description" id="description" rows="10" class="form-control resize_vectical_only"><spring:message text="${measureForm.description}" /></textarea>
								</div>
							</div>
						</div>
						<c:if test="${measureForm.type == 'ASSET' }">
							<div id="tab_asset" class="tab-pane">
								<div class="row">
									<div class="col-sm-12">
										<h3>
											<fmt:message key="label.assetmeasure.assets.title" />
										</h3>
										<p>
											<fmt:message key="label.assetmeasure.assets.description" />
										</p>
									</div>
									<hr class="center-block" style="width: 96%">
									<div class="form-group" style="width: 47%; margin: 5px 15px;">
										<label class="col-xs-3" style="padding: 5px;"><fmt:message key="label.asset_type" /></label>
										<div class="col-xs-9">
											<select class="form-control" name="assettypes" id="assettypes">
												<option value="ALL"><spring:message code="label.all" text="All" /></option>
												<c:forEach items="${assetTypes}" var="assetType">
													<option value="${assetType.type}" ${assetType.type.equals(selectedAssetType)?"selected='selected'":"" }><spring:message text="${assetType.type}" /></option>
												</c:forEach>
											</select>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6">
										<h3>
											<fmt:message key="label.assetmeasure.assetlist" />
										</h3>
										<p>
											<fmt:message key="label.assetmeasure.assets.clickforselect" />
										</p>

										<ul class="asset-measure" data-trick-type="available">
											<c:forEach items="${availableAssets}" var="availableAsset">
												<li data-trick-id="${availableAsset.id}" class="list-group-item" data-trick-selected="false" data-trick-type="${availableAsset.assetType.type}"><spring:message
														text="${availableAsset.name}" /></li>
											</c:forEach>
										</ul>
									</div>
									<div class="col-sm-6">
										<h3>
											<fmt:message key="label.assetmeasure.selected" />
										</h3>
										<p>
											<fmt:message key="label.assetmeasure.assets.clickfordeselect" />
										</p>
										<ul class="asset-measure" data-trick-type="measure">
											<c:forEach items="${measureForm.assetValues}" var="assetValue">
												<li data-trick-id="${assetValue.id}" data-trick-selected="true" class="list-group-item" data-trick-type="${assetValue.type}"><spring:message
														text="${assetValue.name}" /><input name="assets" value="${assetValue.id}" hidden="hidden"></li>
											</c:forEach>
										</ul>
									</div>
								</div>
							</div>
						</c:if>
					</c:if>
					<c:if test="${isComputable or not isAnalysisOnly}">
						<div id="tab_properties" class="tab-pane ${empty(isAnalysisOnly) or not isAnalysisOnly ?'active':''}" style="padding-top: 17px;">
							<div style="overflow: auto;">
								<table class="table">
									<thead>
										<tr id="slidersTitle">
											<th class="warning"><fmt:message key="label.rrf.measure.strength_measure" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.strength_sectoral" /></th>
											<c:forEach items="${measureForm.properties.categories.keySet()}" var="category">
												<th class="info" data-trick-class="Category" data-trick-value=<spring:message text="${category}" />><fmt:message
														key="label.rrf.category.${fn:toLowerCase(fn:replace(category,'_','.'))}" /></th>
											</c:forEach>
											<th class="success"><fmt:message key="label.rrf.measure.preventive" /></th>
											<th class="success"><fmt:message key="label.rrf.measure.detective" /></th>
											<th class="success"><fmt:message key="label.rrf.measure.limitative" /></th>
											<th class="success"><fmt:message key="label.rrf.measure.corrective" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.intentional" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.accidental" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.environmental" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.internal_threat" /></th>
											<th class="warning"><fmt:message key="label.rrf.measure.external_threat" /></th>
											<c:choose>
												<c:when test="${measureForm.type == 'ASSET' }">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<th data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><spring:message text='${assetValue.name}' /></th>
													</c:forEach>
												</c:when>
												<c:when test="${measureForm.type == 'NORMAL' }">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<th ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><spring:message text='${assetValue.name}' /></th>
													</c:forEach>
												</c:when>
											</c:choose>
										</tr>
									</thead>
									<tbody>
										<tr id="sliders">
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" class="slider" id="fmeasure" value="${measureForm.properties.getFMeasure()}"
												data-slider-min="0" data-slider-max="10" data-slider-step="1" data-slider-value="${measureForm.properties.getFMeasure()}" name="fmeasure"
												data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" class="slider" id="fsectoral" value="${measureForm.properties.getFSectoral()}"
												data-slider-min="0" data-slider-max="4" data-slider-step="1" data-slider-value="${measureForm.properties.getFSectoral()}" name="fsectoral"
												data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
											<c:forEach items="${measureForm.properties.categories.keySet()}" var="category">
												<td class="info" data-trick-class="Category" data-trick-value=<spring:message text="${category}"/>><input type="text" class="slider"
													id="${fn:replace(category,'.','_')}" value="${measureForm.properties.categories.get(category)}" data-slider-min="0" data-slider-max="4" data-slider-step="1"
													data-slider-value="${measureForm.properties.categories.get(category)}" name=<spring:message text="${fn:replace(fn:toLowerCase(category),'.','')}" />
													data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
											</c:forEach>
											<td class="success"><input type="text" id="preventive" class="slider" value="${measureForm.properties.preventive}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.preventive}" data-slider-orientation="vertical" data-slider-selection="after" name="preventive"
												data-slider-tooltip="show"></td>
											<td class="success"><input type="text" class="slider" id="detective" value="${measureForm.properties.detective}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.detective}" name="detective" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="success"><input type="text" id="limitative" class="slider" value="${measureForm.properties.limitative}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.limitative}" data-slider-orientation="vertical" data-slider-selection="after" name="limitative"
												data-slider-tooltip="show"></td>
											<td class="success"><input type="text" class="slider" id="corrective" value="${measureForm.properties.corrective}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.corrective}" name="corrective" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="warning"><input type="text" class="slider" id="intentional" value="${measureForm.properties.intentional}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.intentional}" name="intentional" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="warning"><input type="text" class="slider" id="accidental" value="${measureForm.properties.accidental}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.accidental}" name="accidental" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="warning"><input type="text" class="slider" id="environmental" value="${measureForm.properties.environmental}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.environmental}" name="environmental" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="warning"><input type="text" class="slider" id="internalThreat" value="${measureForm.properties.internalThreat}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.internalThreat}" name="internalThreat" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<td class="warning"><input type="text" class="slider" id="externalThreat" value="${measureForm.properties.externalThreat}" data-slider-min="0" data-slider-max="4"
												data-slider-step="1" data-slider-value="${measureForm.properties.externalThreat}" name="externalThreat" data-slider-orientation="vertical" data-slider-selection="after"
												data-slider-tooltip="show"></td>
											<c:choose>
												<c:when test="${measureForm.type == 'ASSET'}">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<td data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><input type="text" class="slider"
															id='asset_slider_<spring:message text="${assetValue.id}"/>' value="${assetValue.value}" data-slider-min="0" data-slider-max="100" data-slider-step="1"
															data-slider-value="${assetValue.value}" name="<spring:message text="${assetValue.id}"/>" data-slider-orientation="vertical" data-slider-selection="after"
															data-slider-tooltip="show"></td>
													</c:forEach>
												</c:when>
												<c:when test="${measureForm.type == 'NORMAL'}">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<td ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><input type="text" class="slider"
															id='asset_slider_<spring:message text="${assetValue.id}"/>' value="${assetValue.value}" data-slider-min="0" data-slider-max="100" data-slider-step="1"
															data-slider-value="${assetValue.value}" name="<spring:message text="${assetValue.id}"/>" data-slider-orientation="vertical" data-slider-selection="after"
															data-slider-tooltip="show"></td>
													</c:forEach>
												</c:when>
											</c:choose>
										</tr>
										<tr id="values">
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="fvalue"
												value="${measureForm.properties.getFMeasure()}" name="fmeasure"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="fsectoral_value"
												value="${measureForm.properties.getFSectoral()}" name="fsectoral"></td>
											<c:forEach items="${measureForm.properties.categories.keySet()}" var="category" varStatus="catStatus">
												<td class="info" data-trick-class="Category" data-trick-value="<spring:message text="${category}" />"><input type="text"
													id='<spring:message text="${category}"/>_value' readonly="readonly" class="form-control" value="${measureForm.properties.categories.get(category)}"
													name="<spring:message text="${fn:replace(fn:toLowerCase(category),'.','')}" />"></td>
											</c:forEach>
											<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="preventive_value"
												value='<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.preventive}</fmt:formatNumber>' name="preventive"></td>
											<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="detective_value"
												value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.detective}</fmt:formatNumber>" name="detective"></td>
											<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="limitative_value"
												value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.limitative}</fmt:formatNumber>" name="limitative"></td>
											<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="corrective_value"
												value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.corrective}</fmt:formatNumber>" name="corrective"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="intentional_value"
												value="${measureForm.properties.intentional}" name="intentional"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="accidental_value"
												value="${measureForm.properties.accidental}" name="accidental"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="environmental_value"
												value="${measureForm.properties.environmental}" name="environmental"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="internalThreat_value"
												value="${measureForm.properties.internalThreat}" name="internalThreat"></td>
											<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="externalThreat_value"
												value="${measureForm.properties.externalThreat}" name="externalThreat"></td>
											<c:choose>
												<c:when test="${measureForm.type == 'ASSET' }">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<td data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><input type="text"
															id='property_asset_<spring:message text="${assetValue.id}"/>_value' style="min-width: 50px;" readonly="readonly" class="form-control" value="${assetValue.value}"
															name="<spring:message text="${assetValue.id}" />"></td>
													</c:forEach>
												</c:when>
												<c:when test="${measureForm.type == 'NORMAL' }">
													<c:forEach items="${measureForm.assetValues}" var="assetValue">
														<td ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><input type="text"
															id='property_asset_type_<spring:message text="${assetValue.id}"/>_value' style="min-width: 50px;" readonly="readonly" class="form-control"
															value="${assetValue.value}" name="<spring:message text="${assetValue.id}" />"></td>
													</c:forEach>
												</c:when>
											</c:choose>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
					</c:if>
				</form>
				<div class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="return saveMeasure('#measure_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
