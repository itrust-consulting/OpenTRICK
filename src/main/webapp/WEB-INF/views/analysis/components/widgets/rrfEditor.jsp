<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_rrf">
	<div class="row">
		<div class="col-md-4">
			<div class="panel panel-primary">
				<div class="panel-body">
					<div class="list-group" style="max-height: 360px; overflow: auto;" id="selectable_rrf_scenario_controls">
						<c:forEach items="${scenarios.keySet()}" var="scenarioType" varStatus="status">
							<div class="list-group" trick-id="${scenarioType.id}">
								<c:choose>
									<c:when test="${status.index==0}">
										<h4 class="list-group-item-heading">
											<a href="#" trick-id="${scenarioType.id}" trick-value=<spring:message text="${scenarioType.name}" /> onclick="return false;" trick-class="ScenarioType"
												class="list-group-item active">
												
													<spring:message text="${scenarioType.name}" />
												
											</a>
										</h4>
									</c:when>
									<c:otherwise>
										<h4 class="list-group-item-heading">
											<a href="#" trick-id="${scenarioType.id}" trick-value=<spring:message text="${scenarioType.name}" /> onclick="return false;" trick-class="ScenarioType"
												class="list-group-item">
												
													<spring:message text="${scenarioType.name}" />
												
										</a>
										</h4>
									</c:otherwise>
								</c:choose>
								<div class="list-group" trick-id="${scenarioType.id}" trick-value=<spring:message text="${scenarioType.name}" />>
									<c:forEach items="${scenarios.get(scenarioType)}" var="scenario" varStatus="statusScanrio">
										<c:choose>
											<c:when test="${status.index==0 && statusScanrio.index==0 }">
												<a href="#" onclick="return false;" title=<spring:message text="${scenario.name}"/> trick-class="Scenario" trick-id="${scenario.id}" class="list-group-item active"
													style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message text="${scenario.name}" />
												</a>
												<c:set var="selectedScenario" value="${scenario}" />
											</c:when>
											<c:otherwise>
												<a href="#" onclick="return false;" title=<spring:message text="${scenario.name}"/> trick-class="Scenario" trick-id="${scenario.id}" class="list-group-item"
													style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message text="${scenario.name}" />
												</a>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
			<div class="panel panel-primary">
				<div class="panel-body">
					<div class="list-group" style="max-height: 360px; overflow: auto;" id="selectable_rrf_measures_chapter_controls">
						<c:forEach items="${measures.keySet()}" var="chapter" varStatus="status">
							<div class="list-group" trick-id="${chapter.norm.id}">
								<c:choose>
									<c:when test="${status.index==0}">
										<h4 class="list-group-item-heading">
											<a href="#" onclick="return false;" class="list-group-item active" trick-class="Norm" title=<spring:message text="${chapter.reference}"/> trick-id="${chapter.norm.id}"
												trick-value=<spring:message text="${chapter.reference}"/>>
												
													<spring:message text="${chapter.norm.label}" />
													-
													<spring:message code="label.measure.chapter" arguments="${chapter.reference}" text="Chapter ${chapter.reference}" />
												
											</a>
										</h4>
									</c:when>
									<c:otherwise>
										<h4 class="list-group-item-heading">
											<a href="#" onclick="return false;" class="list-group-item" trick-class="Norm" trick-id="${chapter.norm.id}" title=<spring:message text="${chapter.reference}"/>
												trick-value=<spring:message text="${chapter.reference}"/>>
												
													<spring:message text="${chapter.norm.label}" />
													-
													<spring:message code="label.measure.chapter" arguments="${chapter.reference}" text="Chapter ${chapter.reference}" />
												
											</a>
										</h4>
									</c:otherwise>
								</c:choose>
								<div class="list-group" trick-id="${chapter.norm.id}" trick-value=<spring:message text="${chapter.reference}"/>>
									<c:forEach items="${measures.get(chapter)}" var="measure">
										<a href="#" onclick="return false;" trick-class="Measure" trick-id="${measure.id}" class="list-group-item"
											style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"> <spring:message
												text="${measure.getMeasureDescription().reference} - ${measure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(language).domain}" />
										</a>
									</c:forEach>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-8">
			<div class="col-md-12" id="chart_rrf">
				<div style="width: 1151px; height: 400px; padding-top: 12%">
					<div class="progress progress-striped active">
						<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
			<div class="col-md-12" id="control_rrf">
				<div class="panel panel-primary" id="control_rrf_scenario">
					<div class="panel-body">
						<div style="overflow: auto;">
							<c:if test="${!empty(selectedScenario)}">
								<c:set var="typeClass">
									${selectedScenario.preventive + selectedScenario.limitative+ selectedScenario.detective+selectedScenario.corrective!=1? "danger" : "success"}
								</c:set>
								<table class="table">
									<thead>
										<tr>
											<th class="${typeClass}" trick-type="type"><spring:message code="label.scenario.preventive" text="Preventive" /></th>
											<th class="${typeClass}" trick-type="type"><spring:message code="label label.scenario.limitative" text="Limitative" /></th>
											<th class="${typeClass}" trick-type="type"><spring:message code="label.scenario.detective" text="Detective" /></th>
											<th class="${typeClass}" trick-type="type"><spring:message code="label.scenario.corrective" text="Corrective" /></th>
											<th class="warning" trick-type="source"><spring:message code="label.scenario.accidental" text="Accidental" /></th>
											<th class="warning" trick-type="source"><spring:message code="label.scenario.intentional" text="Intentional" /></th>
											<th class="warning" trick-type="source"><spring:message code="label.scenario.environmental" text="Environmental" /></th>
											<th class="warning" trick-type="source"><spring:message code="label.scenario.internalThreat" text="Internal-threat" /></th>
											<th class="warning" trick-type="source"><spring:message code="label.scenario.externalThreat" text="External-threat" /></th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td class="${typeClass}" trick-type="type"><input type="text" id="scenario_preventive" class="slider" value="${selectedScenario.preventive}" data-slider-min="0"
												data-slider-max="1" data-slider-step="0.1" data-slider-value="${selectedScenario.preventive}" data-slider-orientation="vertical" data-slider-selection="after"
												name="preventive" data-slider-tooltip="show"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" id="scenario_limitative" class="slider" value="${selectedScenario.limitative}" data-slider-min="0"
												data-slider-max="1" data-slider-step="0.1" data-slider-value="${selectedScenario.limitative}" data-slider-orientation="vertical" data-slider-selection="after"
												name="limitative" data-slider-tooltip="show"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" class="slider" id="scenario_detective" value="${selectedScenario.detective}" data-slider-min="0"
												data-slider-max="1" data-slider-step="0.1" data-slider-value="${selectedScenario.detective}" name="detective" data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" class="slider" id="scenario_corrective" value="${selectedScenario.corrective}" data-slider-min="0"
												data-slider-max="1" data-slider-step="0.1" data-slider-value="${selectedScenario.corrective}" name="corrective" data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" trick-type="source"><input type="text" class="slider" id="scenario_accidental" value="${selectedScenario.accidental}" data-slider-min="0"
												data-slider-max="4" data-slider-step="1" data-slider-value="${selectedScenario.accidental}" name="accidental" data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" trick-type="source"><input type="text" class="slider" id="scenario_intentional" value="${selectedScenario.intentional}" data-slider-min="0"
												data-slider-max="4" data-slider-step="1" data-slider-value="${selectedScenario.intentional}" name="intentional" data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" trick-type="source"><input type="text" class="slider" id="scenario_environmental" value="${selectedScenario.environmental}" data-slider-min="0"
												data-slider-max="4" data-slider-step="2" data-slider-value="${selectedScenario.environmental}" name="environmental" data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" trick-type="source"><input type="text" class="slider" id="scenario_internalThreat" value="${selectedScenario.internalThreat}"
												data-slider-min="0" data-slider-max="4" data-slider-step="1" data-slider-value="${selectedScenario.internalThreat}" name="internalThreat"
												data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
											<td class="warning" trick-type="source"><input type="text" class="slider" id="scenario_externalThreat" value="${selectedScenario.externalThreat}"
												data-slider-min="0" data-slider-max="4" data-slider-step="1" data-slider-value="${selectedScenario.externalThreat}" name="externalThreat"
												data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										</tr>
										<tr>
											<td class="${typeClass}" trick-type="type"><input type="text" id="scenario_preventive_value" readonly="readonly" class="form-control"
												value="${selectedScenario.preventive}"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" id="scenario_limitative_value" readonly="readonly" class="form-control"
												value="${selectedScenario.limitative}"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" readonly="readonly" class="form-control" id="scenario_detective_value"
												value="${selectedScenario.detective}"></td>
											<td class="${typeClass}" trick-type="type"><input type="text" readonly="readonly" class="form-control" id="scenario_corrective_value"
												value="${selectedScenario.corrective}"></td>
											<td class="warning" trick-type="source"><input type="text" readonly="readonly" class="form-control" id="scenario_accidental_value"
												value="${selectedScenario.accidental}"></td>
											<td class="warning" trick-type="source"><input type="text" readonly="readonly" class="form-control" id="scenario_intentional_value"
												value="${selectedScenario.intentional}"></td>
											<td class="warning" trick-type="source"><input type="text" readonly="readonly" class="form-control" id="scenario_environmental_value"
												value="${selectedScenario.environmental}"></td>
											<td class="warning" trick-type="source"><input type="text" readonly="readonly" class="form-control" id="scenario_internalThreat_value"
												value="${selectedScenario.internalThreat}"></td>
											<td class="warning" trick-type="source"><input type="text" readonly="readonly" class="form-control" id="scenario_externalThreat_value"
												value="${selectedScenario.externalThreat}"></td>
										</tr>
									</tbody>
								</table>
							</c:if>
						</div>
					</div>
					<div class="panel-footer">
						<spring:message code="label.rrf.control.scenario" text="Scenario controls" />
					</div>
				</div>
				<div class="panel panel-primary" id="control_rrf_measure" hidden="true">
					<div class="panel-body">
						<div style="overflow: auto;">
							<table class="table">
								<thead>
									<tr>
										<th class="success"><spring:message code="label.scenario.preventive" text="Preventive" /></th>
										<th class="success"><spring:message code="label label.scenario.limitative" text="Limitative" /></th>
										<th class="success"><spring:message code="label.scenario.detective" text="Detective" /></th>
										<th class="success"><spring:message code="label.scenario.corrective" text="Corrective" /></th>
										<th class="warning"><spring:message code="label.scenario.accidental" text="Accidental" /></th>
										<th class="warning"><spring:message code="label.scenario.intentional" text="Intentional" /></th>
										<th class="warning"><spring:message code="label.scenario.environmental" text="Environmental" /></th>
										<th class="warning"><spring:message code="label.scenario.internalThreat" text="Internal threat" /></th>
										<th class="warning"><spring:message code="label.scenario.externalThreat" text="External threat" /></th>
										<th class="success"><spring:message code="label.scenario.fmeasure" text="Strength Measure" /></th>
										<th class="success"><spring:message code="label.scenario.fSectoral" text="Strength Sectoral" /></th>
										<c:forEach items="${assetTypes}" var="assetType">
											<th class="warning"><spring:message code='label.assetType.${assetType.type}' text="${assetType.type}" /></th>
										</c:forEach>
										<c:forEach items="${categories}" var="category">
											<th class="success" trick-class="Category" trick-value=<spring:message text="${category}" />><spring:message code="label.category.${category}" text="${category}" /></th>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="success"><input type="text" id="measure_preventive" class="slider" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" data-slider-orientation="vertical" data-slider-selection="after" name="preventive" data-slider-tooltip="show"></td>
										<td class="success"><input type="text" id="measure_limitative" class="slider" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" data-slider-orientation="vertical" data-slider-selection="after" name="limitative" data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_detective" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="detective" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_corrective" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="corrective" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_accidental" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="accidental" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_intentional" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="intentional" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_environmental" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="environmental" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_internalThreat" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="internalThreat" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_externalThreat" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="externalThreat" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_fmeasure" value="0" data-slider-min="0" data-slider-max="10" data-slider-step="1"
											data-slider-value="0" name="fmeasure" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_fSectoral" value="0" data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="0" name="fsectoral" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										<c:forEach items="${assetTypes}" var="assetType">
											<td class="warning" trick-class="AssetType"><input type="text" class="slider" id='measure_assetType_<spring:message text="${assetType.type}"/>' value="50"
												data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="50" name=<spring:message text="${assetType.type}"/> data-slider-orientation="vertical"
												data-slider-selection="after" data-slider-tooltip="show"></td>
										</c:forEach>
										<c:forEach items="${categories}" var="category">
											<td class="success" trick-class="Category" trick-value=<spring:message text="${category}"/>><input type="text" class="slider"
												id="measure_category_${category.replace('.','_')}" value="0" data-slider-min="0" data-slider-max="1" data-slider-step="1" data-slider-value="0"
												name=<spring:message text="${category}" /> data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
										</c:forEach>
									</tr>
									<tr>
										<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_preventive_value" value="0" name="preventive"></td>
										<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_limitative_value" value="0" name="limitative"></td>
										<td class="success"><input type="text" value="0" readonly="readonly" class="form-control" id="measure_detective_value" name="detective"></td>
										<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_corrective_value" value="0" name="corrective"></td>
										<td class="warning"><input type="text" readonly="readonly" class="form-control" value="0" id="measure_accidental_value" name="accidental"></td>
										<td class="warning"><input type="text" readonly="readonly" class="form-control" value="0" id="measure_intentional_value" name="intentional"></td>
										<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_environmental_value" value="0" name="environmental"></td>
										<td class="warning"><input type="text" readonly="readonly" class="form-control" id="measure_internalThreat_value" value="0" name="internalThreat"></td>
										<td class="warning"><input type="text" readonly="readonly" class="form-control" value="0" id="measure_externalThreat_value" name="externalThreat"></td>
										<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_fmeasure_value" value="0" name="fmeasure"></td>
										<td class="success"><input type="text" readonly="readonly" class="form-control" id="measure_fSectoral_value" value="0" name="fsectoral"></td>
										<c:forEach items="${assetTypes}" var="assetType">
											<td class="warning" trick-class="AssetType"><input type="text" id='measure_assetType_<spring:message text="${assetType.type}"/>_value' style="min-width: 50px;"
												readonly="readonly" class="form-control" value="50" name=<spring:message text="${assetType.type}" />></td>
										</c:forEach>
										<c:forEach items="${categories}" var="category">
											<td class="success" trick-class="Category" trick-value=<spring:message text="${category}" />><input type="text"
												id='measure_category_<spring:message text="${category.replace('.','_')}"/>_value' readonly="readonly" class="form-control" value="0"
												name=<spring:message text="${category}" />></td>
										</c:forEach>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="panel-footer">
						<spring:message code="label.rrf.control.measure" text="Measure controls" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>