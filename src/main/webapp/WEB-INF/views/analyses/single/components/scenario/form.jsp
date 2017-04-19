<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="addScenarioModal" tabindex="-1" role="dialog" data-aria-labelledby="addNewScenario" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addScenarioModel-title">
					<spring:message code="label.title.scenario.${empty(scenario)? 'add':'edit'}" />
				</h4>
			</div>
			<div class="modal-body" style="padding-bottom: 5px; padding-top: 5px;">
				<c:if test="${type == 'QUANTITATIVE' }">
					<ul id="scenario_form_tabs" class="nav nav-tabs">
						<li class="active"><a href="#tab_scenario_general" data-toggle="tab"><spring:message code="label.menu.general" text="General" /></a></li>
						<li><a href="#tab_scenario_properties" data-toggle="tab" data-helper-content='<spring:message code="help.scenario.properties" />' ><spring:message code="label.menu.properties" text="Properties" /></a></li>
						<li id="error_scenario_container" style="padding-top: 10px; padding-left: 10px"></li>
					</ul>
				</c:if>
				<form name="scenario" action="${pageContext.request.contextPath}/Scenario/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal tab-content" id="scenario_form">
					<input type="hidden" name="id" value="${!empty(scenario)?scenario.id:'-1'}" id="scenario_id">
					<div id="tab_scenario_general" class="tab-pane active"  style="padding-top: ${type=='QUANTITATIVE'? '8px':'3px'};" >
						<div class="form-group">
							<label for="type" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.asset_linked"/>'><spring:message
									code="label.scenario.applicable.to" /></label>
							<div class="col-sm-9" align="center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-default ${empty(scenario) or not scenario.assetLinked? 'active':''}"><spring:message code="label.scenario.apply.to.asset_type" /><input
										${empty(scenario) or not scenario.assetLinked? 'checked':''} name="assetLinked" type="radio" value="false"></label> <label
										class="btn btn-default ${empty(scenario) or not scenario.assetLinked? '':'active'}"><spring:message code="label.scenario.apply.to.asset" /><input
										${empty(scenario) or not scenario.assetLinked? '':'checked'} name="assetLinked" type="radio" value="true"></label>
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="name" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.name" />'  > <spring:message code="label.scenario.name" />
							</label>
							<div class="col-sm-9">
								<input name="name" id="scenario_name" class="form-control" value='<spring:message text="${empty(scenario)? '':scenario.name}"/>' />
							</div>
						</div>
						<div class="form-group">
							<label for="scenarioType.id" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.type" />' > <spring:message code="label.scenario.type" />
							</label>
							<div class="col-sm-9">
								<select name="scenarioType" class="form-control" id="scenario_scenariotype_id">
									<c:choose>
										<c:when test="${!empty(scenariotypes)}">
											<option value='-1'><spring:message code="label.scenario.type.select" /></option>
											<c:forEach items="${scenariotypes}" var="scenariotype">
												<option value="${scenariotype.value}" ${scenario.type == scenariotype?'selected':''}><spring:message
														code="label.scenario.type.${fn:toLowerCase(fn:replace(scenariotype.name,'-','_'))}" /></option>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<option value='-1'><spring:message code="label.scenario.type.loading" /></option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="selected" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.status" />' > <spring:message code="label.status" />
							</label>
							<div class="col-sm-9" align="center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-default ${empty(scenario) or scenario.selected ? 'active' : ''}"><spring:message code="label.action.select" /><input
										${empty(scenario) or scenario.selected ? 'checked' : ''} name=selected type="radio" value="true"></label> <label
										class="btn btn-default ${empty(scenario) or scenario.selected ? '' : 'active'} "><spring:message code="label.action.unselect" /><input
										${empty(scenario) or scenario.selected ? '' : 'checked'} name="selected" type="radio" value="false"></label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="comment" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.comment" />' > <spring:message code="label.scenario.description" />
							</label>
							<div class="col-sm-9">
								<textarea name="description" class="form-control resize_vectical_only" rows="15" id="scenario_description"><spring:message
										text="${empty(scenario)? '': scenario.description}" /></textarea>
							</div>
						</div>
						<div id='scenario-asset-type-values' class='form-group' ${not empty scenario and scenario.assetLinked? 'hidden' : ''  }>
							<label class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.application.asset.types" />' ><spring:message code="label.scenario.application.asset.types" /></label>
							<spring:message var="typeChooseText" code='label.asset_type.choose.multi' />
							<div class="col-sm-9">
								<c:choose>
									<c:when test="${!empty(scenario)}">
										<select name="assetTypeValues" multiple="multiple" class="form-control resize_vectical_only">
											<option value="-1">${typeChooseText}</option>
											<c:forEach items="${assetTypeValues.keySet()}" var="assetType" varStatus="status">
												<option ${assetTypeValues[assetType] > 0 ? 'selected' : ''} value="${assetType.id}"><spring:message code="label.asset_type.${fn:toLowerCase(assetType.name)}" /></option>
											</c:forEach>
										</select>
									</c:when>
									<c:when test="${!empty(assetTypeValues)}">
										<select name="assetTypeValues" multiple="multiple" class="form-control resize_vectical_only">
											<option value="-1">${typeChooseText}</option>
											<c:forEach items="${assetTypeValues.keySet()}" var="assetType" varStatus="status">
												<option value="${assetType.id}"><spring:message code="label.asset_type.${fn:toLowerCase(assetType.name)}" /></option>
											</c:forEach>
										</select>
									</c:when>
								</c:choose>
							</div>
						</div>
						<div class='form-group' id='scenario-asset-values' ${not empty scenario and scenario.assetLinked? '' : 'hidden'  }>
							<label class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scenario.application.asset" />' ><spring:message code="label.scenario.applicable.asset" /></label>
							<spring:message var="assetChooseText" code='label.asset.choose.multi' />
							<div class="col-sm-9">
								<c:choose>
									<c:when test="${!empty(scenario)}">
										<select name="assetValues" multiple="multiple" class="form-control resize_vectical_only">
											<option value="-1">${assetChooseText}</option>
											<c:forEach items="${assetValues.keySet()}" var="asset" varStatus="status">
												<option ${assetValues[asset] > 0 ? 'selected' : ''} value="${asset.id}"><spring:message text="${asset.name}" /></option>
											</c:forEach>
										</select>
									</c:when>
									<c:when test="${!empty(assetValues)}">
										<select name="assetValues" multiple="multiple" class="form-control resize_vectical_only">
											<option value="-1">${assetChooseText}</option>
											<c:forEach items="${assetValues.keySet()}" var="asset" varStatus="status">
												<option value="${asset.id}"><spring:message text="${asset.name}" /></option>
											</c:forEach>
										</select>
									</c:when>
								</c:choose>
							</div>
						</div>
					</div>
					<c:if test="${type == 'QUANTITATIVE' }">
						<div id="tab_scenario_properties" class="tab-pane" style="padding-top: 17px;">
							<spring:message text="${empty scenario or scenario.hasControlCharacteristics()?'success':'danger'}" var="cssclass" />
							<div class="sceneario-sliders">
								<table data-trick-controller-name='scenario' class="table table-condensed" style="margin-bottom: 0;">
									<thead>
										<tr>
											<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.preventive" /></th>
											<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.detective" /></th>
											<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.limitative" /></th>
											<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.corrective" /></th>
											<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.intentional" /></th>
											<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.accidental" /></th>
											<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.environmental" /></th>
											<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.internalThreat" /></th>
											<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.externalThreat" /></th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${empty scenario}">
												<tr style="text-align: center;">
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_preventive" class="slider" value="0.25" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="0.25" data-slider-orientation="vertical" data-slider-selection="after" name="preventive"
														data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_detective" value="0.25" data-slider-min="0" data-slider-max="1"
														data-slider-step="0.05" data-slider-value="0.25" name="detective" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_limitative" class="slider" value="0.25" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="0.25" data-slider-orientation="vertical" data-slider-selection="after" name="limitative"
														data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_corrective" value="0.25" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="0.25" name="corrective" data-slider-orientation="vertical" data-slider-selection="after"
														data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_intentional" value="0" data-slider-min="0" data-slider-max="1"
														data-slider-step="1" data-slider-value="0" name="intentional" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_accidental" value="0" data-slider-min="0" data-slider-max="1"
														data-slider-step="1" data-slider-value="0" name="accidental" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_environmental" value="0" data-slider-min="0" data-slider-max="1"
														data-slider-step="1" data-slider-value="0" name="environmental" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_internalThreat" value="0" data-slider-min="0" data-slider-max="1"
														data-slider-step="1" data-slider-value="0" name="internalThreat" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_externalThreat" value="0" data-slider-min="0" data-slider-max="1"
														data-slider-step="1" data-slider-value="0" name="externalThreat" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
												</tr>
												<tr>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_preventive_value" readonly="readonly"
														class="form-control" value="0.25"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_detective_value" value="0.25"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_limitative_value" readonly="readonly"
														class="form-control" value="0.25"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_corrective_value" value="0.25"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_intentional_value" value="0"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_accidental_value" value="0"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_environmental_value" value="0"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_internalThreat_value" value="0"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_externalThreat_value" value="0"></td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr style="text-align: center;">
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_preventive" class="slider" value="${scenario.preventive}" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="${scenario.preventive}" data-slider-orientation="vertical" data-slider-selection="after"
														name="preventive" data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_detective" value="${scenario.detective}" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="${scenario.detective}" name="detective" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_limitative" class="slider" value="${scenario.limitative}" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="${scenario.limitative}" data-slider-orientation="vertical" data-slider-selection="after"
														name="limitative" data-slider-tooltip="show"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_corrective" value="${scenario.corrective}" data-slider-min="0"
														data-slider-max="1" data-slider-step="0.05" data-slider-value="${scenario.corrective}" name="corrective" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_intentional" value="${scenario.intentional}" data-slider-min="0"
														data-slider-max="1" data-slider-step="1" data-slider-value="${scenario.intentional}" name="intentional" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_accidental" value="${scenario.accidental}" data-slider-min="0"
														data-slider-max="1" data-slider-step="1" data-slider-value="${scenario.accidental}" name="accidental" data-slider-orientation="vertical" data-slider-selection="after"
														data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_environmental" value="${scenario.environmental}" data-slider-min="0"
														data-slider-max="1" data-slider-step="1" data-slider-value="${scenario.environmental}" name="environmental" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_internalThreat" value="${scenario.internalThreat}" data-slider-min="0"
														data-slider-max="1" data-slider-step="1" data-slider-value="${scenario.internalThreat}" name="internalThreat" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
													<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_externalThreat" value="${scenario.externalThreat}" data-slider-min="0"
														data-slider-max="1" data-slider-step="1" data-slider-value="${scenario.externalThreat}" name="externalThreat" data-slider-orientation="vertical"
														data-slider-selection="after" data-slider-tooltip="show"></td>
												</tr>
												<tr>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_preventive_value" readonly="readonly"
														class="form-control" value="${scenario.preventive}"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_detective_value" value="${scenario.detective}"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_limitative_value" readonly="readonly"
														class="form-control" value="${scenario.limitative}"></td>
													<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_corrective_value" value="${scenario.corrective}"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_intentional_value" value="${scenario.intentional}"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_accidental_value" value="${scenario.accidental}"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_environmental_value" value="${scenario.environmental}"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_internalThreat_value" value="${scenario.internalThreat}"></td>
													<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control"
														id="scenario_externalThreat_value" value="${scenario.externalThreat}"></td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
							</div>
						</div>
					</c:if>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="return saveScenario('scenario_form')">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->