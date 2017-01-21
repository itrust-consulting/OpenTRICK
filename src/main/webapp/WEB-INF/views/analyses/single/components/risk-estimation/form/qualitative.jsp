<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<c:set var="strategyForm">
	<select name="riskProfile.riskStrategy" class="form-control">
		<c:forEach items="${strategies}" var="strategy">
			<option value="${strategy}" ${riskProfile.riskStrategy==strategy?"selected='selected'":""}><spring:message code="label.risk_register.strategy.${strategy.nameToLower}"
					text="${strategy}" /></option>
		</c:forEach>
	</select>
</c:set>
<spring:message code='label.title.acro.raw' text='RAW' var="raw" />
<spring:message code='label.title.acro.net' text='NET' var="net" />
<spring:message code='label.title.acro.exp' text='EXP' var="exp" />
<spring:message text="${assessment.owner}" var="owner" />
<spring:message text="${riskProfile.identifier}" var="identifier" />
<c:set var="scenarioType" value="${fn:toLowerCase(scenario.type.name)}" />
<div class="page-header tab-content-header hidden-xs">
	<div class="container">
		<div class="row-fluid">
			<h3>
				<spring:message text="${asset.name} - ${scenario.name}" />
			</h3>
		</div>
	</div>
</div>
<div class='form-horizontal' style="border-bottom: 1px solid #efefef;">
	<div class='col-sm-4'>
		<div class="form-group">
			<label class='control-label col-xs-6'><spring:message code="label.risk_register.category" /></label> <strong class='col-xs-6 form-control-static'><spring:message
					code="label.scenario.type.${fn:replace(scenarioType,'-','_')}" text="${scenarioType}" /></strong>
		</div>
	</div>
	<div class='col-sm-4'>
		<div class="form-group">
			<label class="control-label col-xs-6"><spring:message code="label.title.risk_identifier" /></label>
			<div class='col-xs-6'>
				<input name="riskProfile.identifier" class="form-control" value="${identifier}" placeholder="${identifier}" data-trick-type='string'>
			</div>
		</div>
	</div>
	<div class='col-sm-4'>
		<div class="form-group">
			<label class="control-label col-xs-6"> <spring:message code="label.title.owner" text="Owner" />
			</label>
			<div class='col-xs-6'>
				<input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'>
			</div>
		</div>
	</div>
	<div class="clearfix"></div>
</div>
<table class='table'>
	<thead class='alert-primary'>
		<tr>
			<th width="15px" rowspan="2"></th>
			<th rowspan="2" style="text-align: center; vertical-align: middle; min-width: 90px;"><spring:message code="label.title.likelihood" /></th>
			<th colspan="${impactTypes.size()}" style="text-align: center;"><spring:message code="label.title.impact" /></th>
			<th rowspan="2" style="border-left: 2px solid window; width: 80px; text-align: center; vertical-align: middle;"><spring:message code="label.title.importance"
					text="Importance" /></th>
		</tr>
		<tr>
			<c:forEach items="${impactTypes}" var="impactType">
				<spring:message code="label.title.assessment.impact_${fn:toLowerCase(impactType.name)}"
					text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue].name}" var="impactTitle" />
				<th title='${impactTitle}' style="text-align: center; min-width: 90px;"><spring:message code="label.impact.${fn:toLowerCase(impactType.name)}"
						text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue].name}" /></th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<tr class="alert-warning">
			<!-- RAW -->
			<td style="transform: rotate(270deg);">${raw}</td>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Probability">
							<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
							</span>
						</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.probability}">
							<select class="form-control" name="riskProfile.rawProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title='<spring:message text="${parameter.description}"/>'>
										<c:choose>
											<c:when test="${parameter.level == 0}">
												<spring:message code='label.status.na' />
											</c:when>
											<c:otherwise>${parameter.level}</c:otherwise>
										</c:choose>
									</option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.probability" data-trick-value='${riskProfile.rawProbaImpact.probability.id}' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.probability==parameter?"selected='selected'" :""} title='<spring:message text="${parameter.description}"/>'><c:choose>
											<c:when test="${parameter.level == 0}">
												<spring:message code='label.status.na' />
											</c:when>
											<c:otherwise>${parameter.level}</c:otherwise>
										</c:choose></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<c:forEach items="${impactTypes}" var="impactType">
				<td>
					<div class="input-group">
						<spring:message text='${impactType.name}' var="impactName" />
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal='#Scale_Impact_${impactName}'>
								<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
								</span>
							</button></span>
						<c:set var="impact" value="${riskProfile.rawProbaImpact.get(impactType.name)}" />
						<c:choose>
							<c:when test="${empty impact}">
								<select class="form-control" name="riskProfile.rawProbaImpact.${impactName}" data-trick-value='0' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.id}" title='<fmt:formatNumber value="${fct:round(parameter.value,0)}" /> &euro;'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<select class="form-control" name="riskProfile.rawProbaImpact.${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.id}" ${impact==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.description}"/>'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</c:forEach>
			<td style="border-left: 2px solid #ddd;"><input name="rawComputedImportance" disabled="disabled" class="form-control numeric" value="${riskProfile.computedRawImportance}"></td>
		</tr>
		<!-- NET -->
		<tr class='alert-success'>
			<td style="transform: rotate(270deg);">${net}</td>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<spring:message text="${assessment.likelihood}" var="likelihood" />
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal='#Scale_Probability'>
							<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
							</span>
						</button></span> <select class="form-control" name="likelihood" data-trick-type='string' data-trick-value='${likelihood}'>
						<c:forEach items="${probabilities}" var="parameter">
							<option value="${parameter.acronym}" ${likelihood == parameter.acronym? "selected='selected'" : ""} title='<spring:message text="${parameter.description}"/>'><c:choose>
									<c:when test="${parameter.level == 0}">
										<spring:message code='label.status.na' />
									</c:when>
									<c:otherwise>${parameter.level}</c:otherwise>
								</c:choose></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<c:forEach items="${impactTypes}" var="impactType">
				<td><spring:message text='${impactType.name}' var="impactName" /> <c:set var="impact" value="${assessment.getImpact(impactType.name)}" />
					<div class="input-group">
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Impact_${impactName}">
								<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
								</span>
							</button></span>
						<c:choose>
							<c:when test="${empty impact}">
								<select class="form-control" name="${impactName}" data-trick-value='0' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.acronym}" title='<fmt:formatNumber value="${fct:round(parameter.value,0)}" /> &euro;'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<select class="form-control" name="${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.acronym}" ${impact.parameter==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.description}"/>'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</div></td>

			</c:forEach>
			<td style="border-left: 2px solid #ddd;"><input name="computedNextImportance" disabled="disabled" value="${computeNextImportance}" class="form-control numeric"></td>
		</tr>
		<!-- EXP -->
		<tr class="alert-info">
			<td style="transform: rotate(270deg);">${exp}</td>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Probability">
							<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
							</span>
						</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.probability}">
							<select class="form-control" name="riskProfile.expProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title='<spring:message text="${parameter.description}"/>'><c:choose>
											<c:when test="${parameter.level == 0}">
												<spring:message code='label.status.na' />
											</c:when>
											<c:otherwise>${parameter.level}</c:otherwise>
										</c:choose></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.probability" data-trick-value='${riskProfile.expProbaImpact.probability.id}' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.probability==parameter?"selected='selected'":""} title='<spring:message text="${parameter.description}"/>'><c:choose>
											<c:when test="${parameter.level == 0}">
												<spring:message code='label.status.na' />
											</c:when>
											<c:otherwise>${parameter.level}</c:otherwise>
										</c:choose></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<c:forEach items="${impactTypes}" var="impactType">
				<td>
					<div class="input-group">
						<spring:message text='${impactType.name}' var="impactName" />
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal='#Scale_Impact_${impactName}'>
								<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
								</span>
							</button></span>
						<c:set var="impact" value="${riskProfile.expProbaImpact.get(impactType.name)}" />
						<c:choose>
							<c:when test="${empty impact}">
								<select class="form-control" name="riskProfile.expProbaImpact.${impactName}" data-trick-value='0' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.id}" title='<fmt:formatNumber value="${fct:round(parameter.value,0)}" /> &euro;'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<select class="form-control" name="riskProfile.expProbaImpact.${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
									<c:forEach items="${impacts[impactType.name]}" var="parameter">
										<option value="${parameter.id}" ${impact==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.description}"/>'><c:choose>
												<c:when test="${parameter.level == 0}">
													<spring:message code='label.status.na' />
												</c:when>
												<c:otherwise>${parameter.level}</c:otherwise>
											</c:choose></option>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</c:forEach>
			<td style="border-left: 2px solid #ddd;"><input name="expComputedImportance" disabled="disabled" class="form-control numeric" value="${riskProfile.computedExpImportance}"></td>
		</tr>
	</tbody>
</table>
<div class='form-group'>
	<spring:message code="label.comment_argumentation" text="Comment / Argumentation" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
</div>
<div class='form-group'>
	<spring:message code="label.risk_treatment" text="Risk treatment" var='riskTreatment' />
	<spring:message text='${riskProfile.riskTreatment}' var="riskTreatmentContent" />
	<label class='label-control'>${riskTreatment}</label>
	<textarea class="form-control" name="riskProfile.riskTreatment" title="${riskTreatment}" style="resize: vertical;" placeholder="${riskTreatmentContent}" data-trick-type='string'>${riskTreatmentContent}</textarea>
</div>
<div class='form-group' id="section_estimation_action_plan">
	<spring:message code="label.action_paln.including.deadlines" text="Action plan (including deadlines)" var='actionPlan' />
	<ul class='nav nav-pills' style="padding-left: 0;" id="menu_estimation_action_plan">
		<li class='form-horizontal' style="margin-right: 25px;"><div class='form-group'>
				<span class="control-label col-xs-4"><spring:message code="label.risk_register.strategy" /></span>
				<div class='col-xs-8'>${strategyForm}</div>
			</div></li>
		<li style="padding-left: 0; margin-right: 15px; padding-top: 6px;">${actionPlan}</li>
		<c:if test="${isEditable}">
			<li><a href="#" data-action='manage' onclick="return false" style="padding: 6px 10px;"><i class="fa fa-plus" aria-hidden="true"></i> <spring:message
						code='label.action.add' /></a></li>
			<li data-trick-selectable="multi"><a href="#" data-action='delete' class="text text-danger" onclick="return false" style="padding: 6px 10px;"><i class="fa fa-remove"
					aria-hidden="true"></i> <spring:message code='label.action.delete' /></a></li>
		</c:if>
		<li data-trick-ignored='true' class='pull-right' style="display :${empty riskProfile.actionPlan? '' : 'none'};"><a href="#" data-action='show' style="padding: 6px 10px;"
			onclick="return false"><i class="fa fa-plus-square-o" aria-hidden="true"></i> <spring:message code='label.action.show.additional.field' /></a></li>
		<li data-trick-ignored='true' class='pull-right' style="display: ${empty riskProfile.actionPlan? 'none' : ''};"><a href="#" data-action='hide' style="padding: 6px 10px;"
			onclick="return false"><i class="fa fa-minus-square-o" aria-hidden="true"></i> <spring:message code='label.action.hide.additional.field' /></a></li>
	</ul>
	<spring:message text='${riskProfile.actionPlan}' var="actionPlanContent" />
	<table id="riskProfileMeasure" class="table table-hover">
		<thead>
			<tr>
				<c:if test="${isEditable}">
					<th style="width: 2%; padding-bottom: 5px;" title='<spring:message code="label.measure.norm" />'><input type="checkbox" data-menu-controller='menu_estimation_action_plan'
						onchange="return checkControlChange(this,'estimation_action_plan')"></th>
				</c:if>
				<th style="width: 2%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
				<th style="width: 3%;" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
				<th title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
				<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
				<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir_no_unit" /></th>
				<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
				<th style="width: 2%;" title='<spring:message code="label.title.measure.responsible" />'><spring:message code="label.measure.responsible" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${riskProfile.measures}" var="measure">
				<c:set var="implementationRateValue" value="${measure.getImplementationRateValue(valueFactory)}" />
				<tr data-trick-class="Measure" ${isEditable? 'onclick="selectElement(this)"' : ''} data-trick-id="${measure.id}"
					${implementationRateValue==100? 'class="warning"' : measure.status=='NA'? 'class="danger"':''}>
					<c:if test="${isEditable}">
						<td><input type="checkbox" class="checkbox" data-menu-controller='menu_estimation_action_plan'
							onchange="return updateMenu(this,'#section_estimation_action_plan','#menu_estimation_action_plan');"></td>
					</c:if>
					<td><spring:message text='${ measure.measureDescription.standard.label}' /></td>
					<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(langue)}" />
					<c:choose>
						<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
							<td><spring:message text="${measure.measureDescription.reference}" /></td>
						</c:when>
						<c:otherwise>
							<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'
								title='<spring:message
														text="${measureDescriptionText.description}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
						</c:otherwise>
					</c:choose>
					<td><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
					<c:choose>
						<c:when test="${measure.status=='NA'}">
							<td title="${titleStatusNA}">${statusNA}</td>
						</c:when>
						<c:when test="${measure.status=='AP'}">
							<td title="${titleStatusAP}">${statusAP}</td>
						</c:when>
						<c:otherwise>
							<td title="${titleStatusM}">${statusM}</td>
						</c:otherwise>
					</c:choose>
					<td><fmt:formatNumber value="${implementationRateValue}" maxFractionDigits="0" minFractionDigits="0" /></td>
					<td title='<fmt:formatDate value="${measure.phase.endDate}" pattern="YYYY-MM-dd" />'>${measure.phase.number}</td>
					<td><spring:message text="${measure.responsible}" /></td>
			</c:forEach>
		</tbody>
	</table>
	<spring:message code="info.analysis.estimation.action_plan.addition.field" var="actionPlanInfo" />
	<textarea class="form-control" name="riskProfile.actionPlan" title="${actionPlanInfo}"
		style="resize: vertical; margin-top: 5px; display: ${empty actionPlanContent? 'none' : 'inline-block'};"
		placeholder="${empty actionPlanContent? actionPlanInfo : actionPlanContent}" data-trick-type='string'>${actionPlanContent}</textarea>
</div>
<div class='form-group'>
	<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
	<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
	<label class='label-control'>${hiddenComment}</label>
	<textarea class="form-control" name="hiddenComment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
</div>