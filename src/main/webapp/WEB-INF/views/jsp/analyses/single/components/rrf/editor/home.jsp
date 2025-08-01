<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="rrfEditor" tabindex="-1" role="dialog" data-aria-labelledby="rrfEditor" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 98.0%;">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<div class="modal-title">
					<h4 class="col-lg-2">
						<spring:message code="label.title.editor.rrf" />
					</h4>
					<div class="col-lg-9" id="rrf-error" style="padding: 5px; font-size: 14px"></div>
					<div class="clearfix"></div>
				</div>
			</div>
			<div class="modal-body" style="height: 750px">
				<c:if test="${!notenoughdata}">
					<div class="section" id="section_rrf">
						<div class="row" style="margin: 0;">
							<div class="col-md-4">
								<div class="panel panel-primary" style="height: 343px;">
									<div class="panel-body">
										<div class="list-group" style="min-height: 252px; max-height: 252px; overflow: auto;" id="selectable_rrf_scenario_controls">
											<c:forEach items="${scenarios.keySet()}" var="scenarioType" varStatus="status">
												<div class="list-group" data-trick-id="${scenarioType.value}">
													<h4 class="list-group-item-heading">
														<a href="#" data-trick-id="${scenarioType.value}" data-trick-value='<spring:message text="${scenarioType.name}" />' onclick="return false;"
															data-trick-class="ScenarioType" class="list-group-item${status.index==0?' active':''}"> <spring:message text="${scenarioType.name}" />
														</a>
													</h4>
													<div class="list-group" data-trick-id="${scenarioType.value}" data-trick-value='<spring:message text="${scenarioType.name}" />'>
														<c:forEach items="${scenarios.get(scenarioType)}" var="scenario" varStatus="statusScanrio">
															<a href="#" onclick="return false;" title='<spring:message text="${scenario.name}"/>' data-trick-class="Scenario" data-trick-id="${scenario.id}"
																class="list-group-item" style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message text="${scenario.name}" />
															</a>
															<c:set var="selectedScenario" value="${scenario}" />
														</c:forEach>
													</div>
												</div>
											</c:forEach>
										</div>
									</div>
									<div class="panel-footer">
										<spring:message code="label.rrf.scenario" />
									</div>
								</div>
								<div class="panel panel-primary">
									<div class="panel-body">
										<select name="chapterselection" class="form-control" style="width: 50%; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
											<c:forEach items="${measures.keySet()}" var="chapter" varStatus="status">
												<option value="${status.index}"><spring:message text="${chapter.standard.name}" /> -
													<spring:message code="label.measure.chapter" arguments="${chapter.reference}" text="Chapter ${chapter.reference}" /></option>
											</c:forEach>
										</select>
										<div class="list-group" style="height: 240px; overflow: auto; margin-bottom: 0;" id="selectable_rrf_measures_chapter_controls">
											<c:forEach items="${measures.keySet()}" var="chapter" varStatus="status">
												<div class="list-group" data-trick-id="${chapter.standard.id}" data-trick-filter-value="${status.index}" style="${status.index==0?'display:block':'display:none'}">
													<h4 class="list-group-item-heading">
														<a href="#" onclick="return false;" class="list-group-item ${status.index==0?'active':''}" data-trick-class="Standard"
															title='<spring:message text="${chapter.reference}"/>' data-trick-id="${chapter.standard.id}" data-trick-value=<spring:message text="${chapter.reference}"/>> <spring:message
																text="${chapter.standard.name}" /> - <spring:message code="label.measure.chapter" arguments="${chapter.reference}" text="Chapter ${chapter.reference}" />
														</a>
													</h4>
													<div class="list-group" data-trick-id="${chapter.standard.id}" data-trick-value=<spring:message text="${chapter.reference}"/>>
														<c:forEach items="${measures.get(chapter)}" var="currentMeasure">
															<a href="#" onclick="return false;" data-trick-class="Measure" data-trick-id="${currentMeasure.id}" data-trick-value='${currentMeasure.measureDescription.reference}'
																class="list-group-item ${standardid==chapter.standard.id && currentMeasure.getId()==measureid?'active':''}"
																style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message
																	text="${currentMeasure.measureDescription.reference} - ${currentMeasure.measureDescription.getMeasureDescriptionTextByAlpha3(language).domain}" />
															</a>
														</c:forEach>
													</div>
												</div>
											</c:forEach>
										</div>
									</div>
									<div class="panel-footer">
										<spring:message code="label.rrf.standard" />
									</div>
								</div>
							</div>
							<div class="col-md-8">
								<div class="col-md-12" id="chart_rrf">
									<div id="chart-container" class="rrfCharts panel panel-primary" style="height: 343px;width:100%, padding-right: 5px; padding-left: 5px;">
										<canvas id="chart_rrf_canvas"></canvas>
									</div>
								</div>
								<div class="col-md-12" id="control_rrf">
									<div class="panel panel-primary" id="control_rrf_scenario" style="display: none;">
										<div class="panel-body">
											<div style="overflow-x: auto;">
												<label data-trick-controller-name='scenario' class="label label-danger"><spring:message code="error.rrf.no_scenrario" /> </label>
											</div>
										</div>
										<div class="panel-footer">
											<spring:message code="label.rrf.control.scenario" />
										</div>
									</div>
									<div class="panel panel-primary" id="control_rrf_measure">
										<div class="panel-body">
											<div style="overflow-x: auto;">
												<jsp:include page="./measure.jsp" />
											</div>
										</div>
										<div class="panel-footer" style="padding: 0px">
											<ul class="nav nav-pills">
												<li class="disabled text"><a style="cursor: default; color: inherit;"><spring:message code="label.rrf.control.measure" /></a></li>
												<li id='measure-control-apply-sub-chapter' class="pull-right text-danger"><a href="#"><spring:message code="label.rrf.control.measure.apply.sub.chapter" /></a></li>
												<li id='measure-control-apply-selective-sub-chapter' class="pull-right text-danger"><a href="#"><spring:message
															code="label.rrf.control.measure.apply.selective.sub.chapter" /></a></li>


											</ul>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${notenoughdata}">
					<spring:message code="error.label.rrf.not_enough_data" />
				</c:if>
			</div>
		</div>
	</div>
</div>