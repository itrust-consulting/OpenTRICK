<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_rrf">
	<div class="row">
		<div class="col-md-4">
			<div class="list-group" style="max-height: 350px; overflow: auto;"
				id="selectable_rrf_scenario_controls">
				<c:forEach items="${scenarios}" var="scenario" varStatus="status">
					<c:choose>
						<c:when test="${status.index==0}">
							<a href="#" id="scenario_${scenario.id}" onclick="return false;"
								trick-id="${scenario.id}" class="list-group-item active"><spring:message
									text="${scenario.name}" /></a>
							<c:set value="${scenario}" var="selectedScenario" />
						</c:when>
						<c:otherwise>
							<a href="#" id="scenario_${scenario.id}" class="list-group-item"
								onclick="return false;" trick-id="${scenario.id}"><spring:message
									text="${scenario.name}" /></a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
			<div class="list-group" style="max-height: 350px; overflow: auto;"
				id="selectable_rrf_measures_chapter_controls">
				<c:forEach items="${measures.keySet()}" var="chapter"
					varStatus="status">
					<div class="list-group" trick-id="${chapter.norm.id}"
						trick-value="<spring:message text="${chapter.reference}" />">
						<c:choose>
							<c:when test="${status.index==0}">
								<a href="#" onclick="return false;"
									class="list-group-item active" trick-class="Norm"
									trick-id="${chapter.norm.id}"
									trick-value="<spring:message text="${chapter.reference}" />">
									<h4 class="list-group-item-heading">
										<spring:message text="${chapter.norm.label}" />
										-
										<spring:message code="label.measure.chapter"
											arguments="${chapter.reference}"
											text="Chapter ${chapter.reference}" />
									</h4>
								</a>
							</c:when>
							<c:otherwise>
								<a href="#" onclick="return false;" class="list-group-item"
									trick-value="<spring:message text="${chapter.reference}" />"
									trick-class="Norm" trick-id="${chapter.norm.id}"
									title="${chapter.reference}">
									<h4 class="list-group-item-heading">
										<spring:message text="${chapter.norm.label}" />
										-
										<spring:message code="label.measure.chapter"
											arguments="${chapter.reference}"
											text="Chapter ${chapter.reference}" />
									</h4>
								</a>
							</c:otherwise>
						</c:choose>
						<div class="list-group" trick-id="${chapter.norm.id}"
							trick-value="<spring:message text="${chapter.reference}" />">
							<c:forEach items="${measures.get(chapter)}" var="measure">
								<a href="#" id="measure_${measure.id}" onclick="return false;"
									onmouseover="$(this).popover('show')"
									title="${measure.getMeasureDescription().reference}"
									trick-class="Measure" trick-id="${measure.id}"
									class="list-group-item" data-toggle="popover"
									data-trigger="hover" data-placement="bottom"
									data-content="${measure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(language).domain}"
									style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;">
									<spring:message
										text="${measure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(language).domain}" />
								</a>
							</c:forEach>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		<div class="col-md-8">
			<div class="col-md-12" id="chart_rrf">
				<div style="width: 1151px; height: 400px; padding-top: 12%">
					<div class="progress progress-striped active">
						<div class="progress-bar" role="progressbar" aria-valuenow="100"
							aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
			<div class="col-md-12" id="control_rrf">
				<div class="panel panel-primary" id="control_rrf_scenario">
					<div class="panel-body">
						<c:if test="${!empty(selectedScenario)}">
							<table class="table" style="max-width: 800px;overflow: auto;" >
								<thead>
									<tr>
										<th class="danger"><spring:message
												code="label.scenario.preventive" text="Preventive" /></th>
										<th class="danger"><spring:message
												code="label label.scenario.limitative" text="Limitative" /></th>
										<th class="danger"><spring:message
												code="label.scenario.detective" text="Detective" /></th>
										<th class="danger"><spring:message
												code="label.scenario.corrective" text="Corrective" /></th>
										<th class="warning"><spring:message
												code="label.scenario.intentional" text="Intentional" /></th>
										<th class="warning"><spring:message
												code="label.scenario.environmental" text="Environmental" /></th>
										<th class="warning"><spring:message
												code="label.scenario.internalThreat" text="Internal-threat" /></th>
										<th class="warning"><spring:message
												code="label.scenario.externalThreat" text="External-threat" /></th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="danger"><input type="text"
											id="scenario_preventive" class="slider"
											value="${selectedScenario.preventive}" data-slider-min="0"
											data-slider-max="1" data-slider-step="0.1"
											data-slider-value="${selectedScenario.preventive}"
											data-slider-orientation="vertical"
											data-slider-selection="after" name="preventive"
											data-slider-tooltip="show"></td>
										<td class="danger"><input type="text"
											id="scenario_limitative" class="slider"
											value="${selectedScenario.limitative}" data-slider-min="0"
											data-slider-max="1" data-slider-step="0.1"
											data-slider-value="${selectedScenario.limitative}"
											data-slider-orientation="vertical"
											data-slider-selection="after" name="limitative"
											data-slider-tooltip="show"></td>
										<td class="danger"><input type="text" class="slider"
											id="scenario_detective" value="${selectedScenario.detective}"
											data-slider-min="0" data-slider-max="1"
											data-slider-step="0.1"
											data-slider-value="${selectedScenario.detective}"
											name="detective" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
										<td class="danger"><input type="text" class="slider"
											id="scenario_corrective"
											value="${selectedScenario.corrective}" data-slider-min="0"
											data-slider-max="1" data-slider-step="0.1"
											data-slider-value="${selectedScenario.corrective}"
											name="corrective" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
										<td class="warning"><input type="text" class="slider"
											id="scenario_intentional"
											value="${selectedScenario.intentional}" data-slider-min="0"
											data-slider-max="4" data-slider-step="1"
											data-slider-value="${selectedScenario.intentional}"
											name="intentional" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
										<td class="warning"><input type="text" class="slider"
											id="scenario_environmental"
											value="${selectedScenario.environmental}" data-slider-min="0"
											data-slider-max="4" data-slider-step="2"
											data-slider-value="${selectedScenario.environmental}"
											name="environmental" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
										<td class="warning"><input type="text" class="slider"
											id="scenario_internalThreat"
											value="${selectedScenario.internalThreat}"
											data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="${selectedScenario.internalThreat}"
											name="internalThreat" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
										<td class="warning"><input type="text" class="slider"
											id="scenario_externalThreat"
											value="${selectedScenario.externalThreat}"
											data-slider-min="0" data-slider-max="4" data-slider-step="1"
											data-slider-value="${selectedScenario.externalThreat}"
											name="externalThreat" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show">
										</td>
									</tr>
								</tbody>
							</table>
						</c:if>
					</div>
					<div class="panel-footer">
						<spring:message code="label.rrf.control.scenario" text="Scenario controls" />
					</div>
				</div>
				<div class="panel" id="control_rrf_measure" hidden="true">
					<div class="panel-body">
						<table class="table" style="max-width: 800px;overflow: auto;">
							<thead>
								<tr>
									<th class="danger"><spring:message
											code="label.scenario.preventive" text="Preventive" /></th>
									<th class="danger"><spring:message
											code="label label.scenario.limitative" text="Limitative" /></th>
									<th class="danger"><spring:message
											code="label.scenario.detective" text="Detective" /></th>
									<th class="danger"><spring:message
											code="label.scenario.corrective" text="Corrective" /></th>
									<th class="warning"><spring:message
											code="label.scenario.intentional" text="Intentional" /></th>
									<th class="warning"><spring:message
											code="label.scenario.environmental" text="Environmental" /></th>
									<th class="warning"><spring:message
											code="label.scenario.internalThreat" text="Internal threat" /></th>
									<th class="warning"><spring:message
											code="label.scenario.externalThreat" text="External threat" /></th>
									<th class="success"><spring:message
											code="label.scenario.fmeasure" text="Strength Measure" /></th>
									<th class="success"><spring:message
											code="label.scenario.fSectoral" text="Strength Sectoral" /></th>
									<c:forEach items="${assetTypes}" var="assetType">
										<th class="danger"><spring:message
												code="label.assetType.${assetType.type}"
												text="${assetType.type}" /></th>
									</c:forEach>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td class="danger"><input type="text"
										id="measure_preventive" class="slider" value="0"
										data-slider-min="0" data-slider-max="1" data-slider-step="0.1"
										data-slider-value="0" data-slider-orientation="vertical"
										data-slider-selection="after" name="preventive"
										data-slider-tooltip="show"></td>
									<td class="danger"><input type="text"
										id="measure_limitative" class="slider" value="0"
										data-slider-min="0" data-slider-max="1" data-slider-step="0.1"
										data-slider-value="0" data-slider-orientation="vertical"
										data-slider-selection="after" name="limitative"
										data-slider-tooltip="show"></td>
									<td class="danger"><input type="text" class="slider"
										id="measure_detective" value="0" data-slider-min="0"
										data-slider-max="1" data-slider-step="0.1"
										data-slider-value="0" name="detective"
										data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<td class="danger"><input type="text" class="slider"
										id="measure_corrective" value="0" data-slider-min="0"
										data-slider-max="1" data-slider-step="0.1"
										data-slider-value="0" name="corrective"
										data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<td class="warning"><input type="text" class="slider"
										id="measure_intentional" value="0" data-slider-min="0"
										data-slider-max="4" data-slider-step="1" data-slider-value="0"
										name="intentional" data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<td class="warning"><input type="text" class="slider"
										id="measure_environmental" value="0" data-slider-min="0"
										data-slider-max="4" data-slider-step="2" data-slider-value="0"
										name="environmental" data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<td class="warning"><input type="text" class="slider"
										id="measure_internalThreat" value="0" data-slider-min="0"
										data-slider-max="4" data-slider-step="1" data-slider-value="0"
										name="internalThreat" data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<td class="warning"><input type="text" class="slider"
										id="measure_externalThreat" value="0" data-slider-min="0"
										data-slider-max="4" data-slider-step="1" data-slider-value="0"
										name="externalThreat" data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>

									<td class="success"><input type="text" class="slider"
										id="measure_fmeasure" value="0" data-slider-min="0"
										data-slider-max="10" data-slider-step="1"
										data-slider-value="0" name="fmeasure"
										data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>

									<td class="success"><input type="text" class="slider"
										id="measure_fSectoral" value="0" data-slider-min="0"
										data-slider-max="4" data-slider-step="1" data-slider-value="0"
										name="fsectoral" data-slider-orientation="vertical"
										data-slider-selection="after" data-slider-tooltip="show">
									</td>
									<c:forEach items="${assetTypes}" var="assetType">
										<td class="danger" trick-class="AssetType"><input
											type="text" class="slider"
											id="measure_assetType_${assetType.type}" value="50"
											data-slider-min="0" data-slider-max="100"
											data-slider-step="1" data-slider-value="50"
											name="${assetType.type}" data-slider-orientation="vertical"
											data-slider-selection="after" data-slider-tooltip="show"></td>
									</c:forEach>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="panel-footer">
						<spring:message code="label.rrf.control.measure" text="Measure controls" />
					</div>
				</div>

			</div>
		</div>
	</div>
</div>