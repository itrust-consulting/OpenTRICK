<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="rrfEditor" tabindex="-1" role="dialog" data-aria-labelledby="rrfEditor" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 100%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<fmt:message key="label.title.editor.rrf" />
				</h4>
			</div>
			<div class="modal-body" style="height: 800px">
				<c:if test="${!notenoughdata}">
					<div class="section" id="section_rrf">
						<div class="row" style="margin: 0;">
							<div class="col-md-4">
								<div class="panel panel-primary" style="height: 343px;">
									<div class="panel-body">
										<div class="list-group" style="min-height: 252px; max-height: 252px; overflow: auto;" id="selectable_rrf_scenario_controls">
											<c:forEach items="${scenarios.keySet()}" var="scenarioType" varStatus="status">
												<div class="list-group" trick-id="${scenarioType.value}">
													<h4 class="list-group-item-heading">
														<a href="#" trick-id="${scenarioType.value}" trick-value='<spring:message text="${scenarioType.name}" />' onclick="return false;" trick-class="ScenarioType"
															class="list-group-item${status.index==0?' active':''}"> <spring:message text="${scenarioType.name}" />
														</a>
													</h4>
													<div class="list-group" trick-id="${scenarioType.value}" trick-value='<spring:message text="${scenarioType.name}" />'>
														<c:forEach items="${scenarios.get(scenarioType)}" var="scenario" varStatus="statusScanrio">
															<a href="#" onclick="return false;" title='<spring:message text="${scenario.name}"/>' trick-class="Scenario" trick-id="${scenario.id}" class="list-group-item"
																style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message text="${scenario.name}" />
															</a>
															<c:set var="selectedScenario" value="${scenario}" />
														</c:forEach>
													</div>
												</div>
											</c:forEach>
										</div>
									</div>
									<div class="panel-footer">
										<fmt:message key="label.rrf.scenario" />
									</div>
								</div>
								<div class="panel panel-primary" style="height: 390px; margin-bottom: -41px;">
									<div class="panel-body">
										<div class="list-group" style="min-height: 319px; max-height: 319px; overflow: auto; margin-bottom: 0;" id="selectable_rrf_measures_chapter_controls">
											<c:forEach items="${measures.keySet()}" var="chapter" varStatus="status">
												<div class="list-group" trick-id="${chapter.standard.id}">
													<h4 class="list-group-item-heading">
														<a href="#" onclick="return false;" class="list-group-item${status.index==0?' active':''}" trick-class="Standard"
															title='<spring:message text="${chapter.reference}"/>' trick-id="${chapter.standard.id}" trick-value=<spring:message text="${chapter.reference}"/>> <spring:message
																text="${chapter.standard.label}" /> - <spring:message code="label.measure.chapter" arguments="${chapter.reference}" text="Chapter ${chapter.reference}" />
														</a>
													</h4>
													<div class="list-group" trick-id="${chapter.standard.id}" trick-value=<spring:message text="${chapter.reference}"/>>
														<c:forEach items="${measures.get(chapter)}" var="currentMeasure">
															<a href="#" onclick="return false;" trick-class="Measure" trick-id="${currentMeasure.id}"
																class="list-group-item ${standardid==chapter.standard.id && currentMeasure.getId()==measureid?'active':''}"
																style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message
																	text="${currentMeasure.getMeasureDescription().reference} - ${currentMeasure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(language).domain}" />
															</a>
														</c:forEach>
													</div>
												</div>
											</c:forEach>
										</div>
									</div>
									<div class="panel-footer">
										<fmt:message key="label.rrf.standard" />
									</div>
								</div>
							</div>
							<div class="col-md-8">
								<div class="col-md-12" id="chart_rrf" style="height: 343px; margin-bottom: 17px; padding-right: 14px;">
									<div id="chart-container" class="rrfCharts panel panel-primary">
										<div style="width: 100%; height: 340px; padding-top: 172px; padding-left: 15px; padding-right: 15px;">
											<div class="progress progress-striped active">
												<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-12" id="control_rrf">
									<div class="panel panel-primary" id="control_rrf_scenario" hidden="true"></div>
									<div class="panel panel-primary" id="control_rrf_measure">
										<div class="panel-body">
											<div style="overflow: auto;">
												<table class="table" style="margin-bottom: 0;">
													<thead>
														<tr>
															<th class="warning"><fmt:message key="label.rrf.measure.strength_measure" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.strength_sectoral" /></th>
															<c:if test="${!empty(categories)}">
																<c:forEach items="${categories.keySet()}" var="category">
																	<th class="info" trick-class="Category" trick-value=<spring:message text="${category}" />><fmt:message
																			key="label.rrf.category.${fn:toLowerCase(fn:replace(category,'_','.'))}" /></th>
																</c:forEach>
															</c:if>
															<th class="success"><fmt:message key="label.rrf.measure.preventive" /></th>
															<th class="success"><fmt:message key="label.rrf.measure.detective" /></th>
															<th class="success"><fmt:message key="label.rrf.measure.limitative" /></th>
															<th class="success"><fmt:message key="label.rrf.measure.corrective" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.intentional" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.accidental" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.environmental" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.internal_threat" /></th>
															<th class="warning"><fmt:message key="label.rrf.measure.external_threat" /></th>
															<c:if test="${!empty(assetTypes)}">
																<c:forEach items="${assetTypes}" var="assetType">
																	<th><fmt:message key='label.asset_type.${fn:toLowerCase(assetType.assetType.type)}' /></th>
																</c:forEach>
															</c:if>
															<c:if test="${!empty(assets)}">
																<c:forEach items="${assets}" var="asset">
																	<th><spring:message text='${asset.asset.name}' /></th>
																</c:forEach>
															</c:if>
														</tr>
													</thead>
													<tbody>
														<tr>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_fmeasure" value="${strength_measure}" data-slider-min="0"
																data-slider-max="10" data-slider-step="1" data-slider-value="${strength_measure}" name="fmeasure" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_fSectoral" value="${strength_sectorial}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${strength_sectorial}" name="fsectoral" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<c:if test="${!empty(categories)}">
																<c:forEach items="${categories.keySet()}" var="category">
																	<td class="info" trick-class="MeasureProperties" trick-value=<spring:message text="${category}"/>><input type="text" class="slider"
																		id="measure_category_${category}" value="${categories.get(category)}" data-slider-min="0" data-slider-max="4" data-slider-step="1"
																		data-slider-value="${categories.get(category)}" name=<spring:message text="${category}" /> data-slider-orientation="vertical" data-slider-selection="after"
																		data-slider-tooltip="show"></td>
																</c:forEach>
															</c:if>
															<td class="success" trick-class="MeasureProperties"><input type="text" id="measure_preventive" class="slider" value="${preventive}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${preventive}" data-slider-orientation="vertical" data-slider-selection="after" name="preventive"
																data-slider-tooltip="show"></td>
															<td class="success" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_detective" value="${detective}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${detective}" name="detective" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="success" trick-class="MeasureProperties"><input type="text" id="measure_limitative" class="slider" value="${limitative}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${limitative}" data-slider-orientation="vertical" data-slider-selection="after" name="limitative"
																data-slider-tooltip="show"></td>
															<td class="success" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_corrective" value="${corrective}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${corrective}" name="corrective" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_intentional" value="${intentional}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${intentional}" name="intentional" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_accidental" value="${accidental}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${accidental}" name="accidental" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_environmental" value="${environmental}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${environmental}" name="environmental" data-slider-orientation="vertical" data-slider-selection="after"
																data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_internalThreat" value="${internalThreat}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${internalThreat}" name="internalThreat" data-slider-orientation="vertical"
																data-slider-selection="after" data-slider-tooltip="show"></td>
															<td class="warning" trick-class="MeasureProperties"><input type="text" class="slider" id="measure_externalThreat" value="${externalThreat}" data-slider-min="0"
																data-slider-max="4" data-slider-step="1" data-slider-value="${externalThreat}" name="externalThreat" data-slider-orientation="vertical"
																data-slider-selection="after" data-slider-tooltip="show"></td>
															<c:forEach items="${assetTypes}" var="assetType">
																<td trick-class="AssetType"><input type="text" class="slider" id='measure_assetType_<spring:message text="${assetType.assetType.type}"/>'
																	value="${assetType.value}" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="${assetType.value}"
																	name=<spring:message text="${assetType.assetType.type}"/> data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
															</c:forEach>
															<c:if test="${!empty(assets)}">
																<c:forEach items="${assets}" var="asset">
																	<td trick-class="MeasureAssetValue"><input type="text" class="slider" id='measure_<spring:message text="${asset.asset.name}"/>' value="${asset.asset.value}"
																		data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="${asset.asset.value}" name="<spring:message text="${asset.asset.name}"/>"
																		data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
																</c:forEach>
															</c:if>
														</tr>
														<tr>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_fmeasure_value" value="${strength_measure}" name="fmeasure"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_fsectoral_value" value="${strength_sectorial}" name="fsectoral"></td>
															<c:if test="${!empty(categories)}">
																<c:forEach items="${categories.keySet()}" var="category">
																	<td class="info" trick-class="Category" trick-value="<spring:message text="${category}" />"><input type="text"
																		id='measure_<spring:message text="${category}"/>_value' readonly="readonly" class="form-control" value="${categories.get(category)}"
																		name="<spring:message text="${category}" />"></td>
																</c:forEach>
															</c:if>
															<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_preventive_value" value="${preventive}" name="preventive"></td>
															<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_detective_value" value="${detective}" name="detective"></td>
															<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_limitative_value" value="${limitative}" name="limitative"></td>
															<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_corrective_value" value="${corrective}" name="corrective"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_intentional_value" value="${intentional}" name="intentional"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_accidental_value" value="${accidental}" name="accidental"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_environmental_value" value="${environmental}" name="environmental"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_internalThreat_value" value="${internalThreat}"
																name="internalThreat"></td>
															<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_externalThreat_value" value="${externalThreat}"
																name="externalThreat"></td>
															<c:if test="${!empty(assetTypes)}">
																<c:forEach items="${assetTypes}" var="assetType">
																	<td trick-class="AssetTypeValue"><input type="text" id='measure_<spring:message text="${assetType.assetType.type}"/>_value' style="min-width: 50px;"
																		readonly="readonly" class="form-control" value="${assetType.value}" name="<spring:message text="${assetType.assetType.type}" />"></td>
																</c:forEach>
															</c:if>
															<c:if test="${!empty(assets)}">
																<c:forEach items="${assets}" var="asset">
																	<td trick-class="MeasureAssetValue"><input type="text" id='measure_asset_<spring:message text="${asset.asset.name}"/>_value' style="min-width: 50px;" readonly="readonly"
																		class="form-control" value="${asset.asset.value}" name="<spring:message text="${asset.asset.name}" />"></td>
																</c:forEach>
															</c:if>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
										<div class="panel-footer">
											<fmt:message key="label.rrf.control.measure" />
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${notenoughdata}">
					<fmt:message key="error.label.rrf.not_enough_data" />
				</c:if>
			</div>
		</div>
	</div>
</div>