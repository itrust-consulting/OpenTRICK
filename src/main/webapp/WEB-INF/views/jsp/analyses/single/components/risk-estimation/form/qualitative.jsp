<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
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
<spring:message code='label.status.na' var="naValue" />
<spring:message text="${assessment.owner}" var="owner" />
<spring:message text="${riskProfile.identifier}" var="identifier" />
<c:set var="scenarioType" value="${fn:toLowerCase(scenario.type.name)}" />
<c:set var="cssHeader" value="${type.quantitative && show_uncertainty? 'col-sm-4' : 'col-sm-3'}" />
<div class="page-header tab-content-header hidden-xs">
	<div class="container">
		<div class="row-fluid">
			<h3>
				<spring:message text="${asset.name} - ${scenario.name}" />
			</h3>
		</div>
	</div>
</div>
<div class='form-horizontal form-group-fill' style="margin-bottom: 4px;">
	<div class='${cssHeader}'>
		<div class="form-group">
			<label class="control-label col-xs-6"><spring:message code="label.title.risk_identifier" /></label>
			<div class='col-xs-6'>
				<input name="riskProfile.identifier" class="form-control" value="${identifier}" placeholder="${identifier}" data-trick-type='string'>
			</div>
		</div>
	</div>
	<div class='${cssHeader}'>
		<div class="form-group">
			<label class='control-label col-xs-6'><spring:message code="label.risk_register.category" /></label>
			<div class='col-xs-6'>
				<strong class='form-control form-control-static'><spring:message code="label.scenario.type.${fn:replace(scenarioType,'-','_')}" text="${scenarioType}" /></strong>
			</div>
		</div>
	</div>
	<div class='${cssHeader}'>
		<div class="form-group">
			<label class="control-label col-xs-6"> <spring:message code="label.title.owner" text="Owner" />
			</label>
			<div class='col-xs-6'>
				<input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'>
			</div>
		</div>
	</div>
	<div class='${cssHeader}'>
		<div class='form-group'>
			<span class="control-label col-xs-6"><spring:message code="label.risk_register.strategy" /></span>
			<div class='col-xs-6'>${strategyForm}</div>
		</div>
	</div>

	<c:if test="${type.quantitative && show_uncertainty}">
		<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" var="uncertainty" />
		<div class='${cssHeader}'>
			<div class="form-group">
				<label class="control-label col-xs-6"><spring:message code="label.title.uncertainty" /></label>
				<div class='col-xs-6'>
					<input name="uncertainty" class="form-control numeric" data-trick-type='double' value='${uncertainty}' placeholder="${uncertainty}">
				</div>
			</div>
		</div>
	</c:if>
	<div class="clearfix"></div>
</div>

<c:forEach items="${impactTypes}" var="impactType">	
	<c:if test="${impactType.name != 'IMPACT'}">
		<c:set var="qualitativeImpactCount" value="${qualitativeImpactCount + 1}" />
	</c:if>
</c:forEach>

<table class='table form-no-fill'>
	<thead>
		<tr class='form-group-fill'>
			<th width="15px" rowspan="2"></th>
			<th rowspan="2" style="text-align: center; vertical-align: middle; min-width: 90px;"><spring:message code="label.title.likelihood" /></th>
			<c:choose>
				<c:when test="${type.quantitative}">
					<th width="${ ((qualitativeImpactCount+qualitativeImpactCount/2) / (( show_uncertainty? 6 : 4) + qualitativeImpactCount + (isILR? 2: 0))) * 100 }%" colspan="${qualitativeImpactCount}" style="text-align: center;"><spring:message code="label.title.impact" /></th>
				</c:when>
				<c:otherwise>
					<th colspan="${qualitativeImpactCount}" style="text-align: center;"><spring:message code="label.title.impact" /></th>
				</c:otherwise>
			</c:choose>
			<c:if test="${isILR}">
				<th class="form-estimation form-estimation-left" colspan="3" style="min-width: 75px; text-align: center; vertical-align: middle;"><spring:message code="label.title.ilr"
					text="ILR" /></th>
			</c:if>
			<th class="form-estimation form-estimation-left" rowspan="2" style="width: 80px; text-align: center; vertical-align: middle;"><spring:message code="label.title.importance"
					text="Importance" /></th>
			
			<c:if test="${type.quantitative}">
				<th class="form-estimation form-estimation-left form-estimation-right" rowspan="2" style="text-align: center; vertical-align: middle; min-width: 90px;"><spring:message
						code="label.analysis.quantitative.impact" /></th>
				<c:choose>
					<c:when test="${show_uncertainty}">
						<th colspan="3" style="text-align: center; vertical-align: middle; min-width: 270px;"><spring:message code="label.title.ale" /></th>
					</c:when>
					<c:otherwise>
						<th rowspan="2" style="text-align: center; vertical-align: middle; min-width: 90px;"><spring:message code="label.title.ale" /></th>
					</c:otherwise>
				</c:choose>
			</c:if>
		</tr>
		<tr class='form-group-fill'>
			<c:forEach items="${impactTypes}" var="impactType">
				<c:if test="${impactType.name!='IMPACT'}">
					<spring:message code="label.title.assessment.impact_${fn:toLowerCase(impactType.name)}"
						text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue].name}" var="impactTitle" />
					<th title='${impactTitle}' style="text-align: center; min-width: 90px;"><spring:message code="label.impact.${fn:toLowerCase(impactType.name)}"
							text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue].name}" /></th>
				</c:if>
			</c:forEach>
			<c:if test="${isILR}">
				<th class="form-estimation form-estimation-left" colspan="2" style="min-width: 65px; text-align: center; vertical-align: middle;"><spring:message code="label.title.ilr.input"
					text="Input" /></th>
				<th class="form-estimation form-estimation-left" style="text-align: center; vertical-align: middle;"><spring:message code="label.title.ilr.result"
					text="Result" /></th>
			</c:if>
			<c:if test="${type.quantitative and show_uncertainty}">
				<th class='text-center' title='<spring:message code="label.title.aleo" />'><spring:message code="label.optimistic" text='Optimistic' /></th>
				<th class="text-center" title='<spring:message code="label.title.ale" />'><spring:message code="label.normal.ale" text='Normal ALE' /></th>
				<th class="text-center" title='<spring:message code="label.title.alep" />'><spring:message code="label.pessimistic" text='Pessimistic' /></th>
			</c:if>
		</tr>
	</thead>
	<tbody>
		<c:if test="${showRawColumn}">
			<tr class="form-group-raw">
				<!-- RAW -->
				<td style="transform: rotate(270deg);">${raw}</td>
				<td class='form-estimation  form-estimation-right'>
					<div class="input-group" align="right">
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Probability">
								<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
								</span>
							</button></span>
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.probability}">
								<spring:message var="rawProTitle" code='${empty probabilities?"label.title.status.na" : ""}' text="${empty probabilities?'Not Applicable' : probabilities[0].label}" />
								<select class="form-control" title="${rawProTitle}" name="riskProfile.rawProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
									<c:forEach items="${probabilities}" var="parameter">
										<c:choose>
											<c:when test="${parameter.level == 0}">
												<c:set var="rawProbaValue" value="${naValue}" />
												<c:set var="threatProbability" value="${parameter.ilrLevel}" />
												<spring:message code='label.parameter.label.na' text="${parameter.label}" var="rawProbaTitle" />
											</c:when>
											<c:otherwise>
												<spring:message text="${parameter.label}" var="rawProbaTitle" />
												<c:set var="rawProbaValue" value="${parameter.level}" />
											</c:otherwise>
										</c:choose>
										<option value="${parameter.id}" title='${rawProbaTitle}'>${rawProbaValue}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<spring:message var="rawProTitle" text="${riskProfile.rawProbaImpact.probability.label}" />
								<select class="form-control" title="${rawProTitle}" name="riskProfile.rawProbaImpact.probability" data-trick-value='${riskProfile.rawProbaImpact.probability.id}'
									data-trick-type='integer'>
									<c:forEach items="${probabilities}" var="parameter">
										<c:choose>
											<c:when test="${parameter.level == 0}">
												<c:set var="rawProbaValue" value="${naValue}" />
												<spring:message code='label.parameter.label.na' text="${parameter.label}" var="rawProbaTitle" />
											</c:when>
											<c:otherwise>
												<spring:message text="${parameter.label}" var="rawProbaTitle" />
												<c:set var="rawProbaValue" value="${parameter.level}" />
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${riskProfile.rawProbaImpact.probability eq parameter}">
												<c:set var="threatProbability" value="${parameter.ilrLevel}" />
												<option value="${parameter.id}" selected title='${rawProbaTitle}'>${rawProbaValue}</option>
											</c:when>
											<c:otherwise>
												<option value="${parameter.id}" title='${rawProbaTitle}'>${rawProbaValue}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</div>
				</td>
				<c:forEach items="${impactTypes}" var="impactType">
					<c:if test="${impactType.name!='IMPACT'}">
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
										<spring:message var="rawTitle" text="${empty impacts[impactType.name]? '' : impacts[impactType.name][0].label}" />
										<select class="form-control" title="${rawTitle}" name="riskProfile.rawProbaImpact.${impactName}" data-trick-value='0' data-trick-type='integer'>
											<c:forEach items="${impacts[impactType.name]}" var="parameter">
												<option value="${parameter.id}" title='<spring:message text="${parameter.label}"/>'><c:choose>
														<c:when test="${parameter.level == 0}">${naValue}</c:when>
														<c:otherwise>${parameter.level}</c:otherwise>
													</c:choose></option>
											</c:forEach>
										</select>
									</c:when>
									<c:otherwise>
										<spring:message var="rawTitle" text="${impact.label}" />
										<select class="form-control" title="${rawTitle}" name="riskProfile.rawProbaImpact.${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
											<c:forEach items="${impacts[impactType.name]}" var="parameter">
												<option value="${parameter.id}" ${impact==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.label}"/>'><c:choose>
														<c:when test="${parameter.level == 0}">${naValue}</c:when>
														<c:otherwise>${parameter.level}</c:otherwise>
													</c:choose></option>
											</c:forEach>
										</select>
									</c:otherwise>
								</c:choose>
							</div>
						</td>
					</c:if>
				</c:forEach>
				<c:if test="${isILR}">
					<td class="form-estimation form-estimation-left"> <span style="transform: rotate(-90deg);display: inline-block;width: 52px;margin-left: -22px;margin-right: -30px;" title="Threat probability"><spring:message text="Threat" /></span></td>
					<td><label data-name="ILR-VALUE-THREAT-PROBABILITY" class="form-control form-control-static text-right disabled" data-trick-type='string' title="${threatProbability}">${threatProbability}</label></td>
					<td class='text-center'>-</td>
				</c:if>
				<td class='form-estimation  form-estimation-left'><spring:message var="rawImpColor" text="${computedRawImportance.color}" /> <input name="computedRawImportance"
					disabled="disabled" class="form-control numeric" title='<spring:message text='${computedRawImportance.title}'/>' value="${computedRawImportance.value}"
					style="border: solid 2px ${empty rawImpColor? '#eee' : rawImpColor}"></td>
				<c:if test="${type.quantitative}">
					<td class='form-estimation form-estimation-left form-estimation-right'><c:choose>
							<c:when test="${empty riskRegister}">
								<c:set var="impact" value="${valueFactory.findValue(0.0, 'IMPACT')}" />
								<c:set var="aleRaw" value="${0}" />
							</c:when>
							<c:otherwise>
								<c:set var="impact" value="${valueFactory.findValue(riskRegister.rawEvaluation.impact, 'IMPACT')}" />
								<c:set var="aleRaw" value="${riskRegister.rawEvaluation.importance}" />
							</c:otherwise>
						</c:choose>
						<div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span>
							<c:choose>
								<c:when test="${empty impact}">
									<label data-name="IMPACT-RAW" class="form-control form-control-static text-right disabled" data-trick-type='string' title="i0">${naValue}</label>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${impact.real<100}">
											<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
										</c:when>
										<c:when test="${impact.real<1000}">
											<fmt:formatNumber value="${fct:round(impact.real*0.001,2)}" var="impactValue" />
										</c:when>
										<c:when test="${impact.real<10000}">
											<fmt:formatNumber value="${fct:round(impact.real*0.001,1)}" var="impactValue" />
										</c:when>
										<c:otherwise>
											<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
										</c:otherwise>
									</c:choose>
									<label data-name="IMPACT" class="form-control form-control-static text-right disabled" data-trick-type='string' title="${impact.variable}">${impactValue}</label>
								</c:otherwise>
							</c:choose>
						</div></td>
					<c:if test="${show_uncertainty}">
						<c:set var="aleoRaw" value="${aleRaw /  assessment.uncertainty}" />
						<td><div class="input-group">
								<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALEO-RAW' class='form-control form-control-static numeric disabled'
									title="<fmt:formatNumber value="${aleoRaw}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleoRaw*0.001,1)}" /></label>
							</div></td>
					</c:if>
					<td><div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALE-RAW' class='form-control form-control-static numeric disabled'
								title="<fmt:formatNumber value="${aleRaw}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleRaw*0.001,1)}" /></label>
						</div></td>
					<c:if test="${show_uncertainty}">
						<c:set var="alepRaw" value="${aleRaw *  assessment.uncertainty}" />
						<td><div class="input-group">
								<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name="ALEP-RAW" class="form-control form-control-static numeric disabled"
									title="<fmt:formatNumber value="${alepRaw}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alepRaw*0.001,1)}" /></label>
							</div></td>
					</c:if>
				</c:if>
			</tr>
		</c:if>
		<!-- NET -->
		<tr class='form-group-net'>
			<td style="transform: rotate(270deg);">${net}</td>
			<td class='form-estimation  form-estimation-right'>
				<div class="input-group" align="right">
					<spring:message code='label.status.na' var="na" />
					<c:set var="likelihood" value="${assessment.likelihood}" />
					<spring:message code='label.parameter.label.na' text="${parameter.label}" var="labelNA" />
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal='#Scale_Probability'>
							<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
							</span>
						</button></span>
					<c:choose>
						<c:when test="${type.quantitative}">
							<c:set var="likelihood" value="${assessment.likelihood}" />
							<c:choose>
								<c:when test="${empty likelihood}">
									<input name="likelihood" class="form-control" value="${na}" list="dataList-parameter-probability" title="${labelNA}" placeholder="${na}" data-trick-type='string'>
								</c:when>
								<c:when test="${likelihood['class'].simpleName=='RealValue' && likelihood.real==0}">
									<input name="likelihood" class="form-control" value="${naValue}" list="dataList-parameter-probability" title="0" placeholder="${naValue}" data-trick-type='string'>
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${fct:round(likelihood.real,3)}" var="probaValue" />
									<input name="likelihood" class="form-control" value="${likelihood.variable}" list="dataList-parameter-probability" title="${probaValue}"
										placeholder="${likelihood.variable}" data-trick-type='string'>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<spring:message var="netProTitle" text="${empty likelihood or likelihood['class'].simpleName=='FormulaValue'? '' :  likelihood.parameter.label}" />
							<select class="form-control" title='${netProTitle}' name="likelihood" data-trick-type='string' data-trick-value='${empty likelihood? "na": 1}'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.acronym}" ${likelihood.level == parameter.level? "selected='selected'" : ""} title='<spring:message text="${parameter.label}"/>'><c:choose>
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
				<c:if test="${impactType.name!='IMPACT'}">
					<td><spring:message text='${impactType.name}' var="impactName" /> <c:set var="impact" value="${assessment.getImpact(impactType.name)}" />
						<div class="input-group">
							<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Impact_${impactName}">
									<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
									</span>
								</button></span>
							<c:choose>
								<c:when test="${empty impact}">
									<spring:message var="netTitle" text="${empty impacts[impactType.name]? '' : impacts[impactType.name][0].label}" />
									<select class="form-control" title="${netTitle}" name="${impactName}" data-trick-value='0' data-trick-type='integer'>
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
									<spring:message var="netTitle" text="${impact.parameter.label}" />
									<select class="form-control" title="${netTitle}" name="${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
										<c:forEach items="${impacts[impactType.name]}" var="parameter">
											<option value="${parameter.acronym}" ${impact.parameter==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.label}"/>'><c:choose>
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
				</c:if>
			</c:forEach>
			<c:if test="${isILR}">
				<td class="form-estimation form-estimation-left"> <span style="transform: rotate(-90deg);display: inline-block;width: 52px;margin-left: -22px;margin-right: -30px;" title="Vulnerability"><spring:message text="Vulner." /></span></td>
				<td>
					<div><select name='vulnerability' class='form-control' data-trick-type='integer'
								title="<spring:message text='${assessment.vulnerability}'/>" ><c:forEach end="3" begin="0" step="1" var="vulnerability">
						<option value="${vulnerability}" ${vulnerability eq assessment.vulnerability ? 'selected' : ''}>${vulnerability}</option>
						</c:forEach></select>
					</div>
				</td>
				<td><c:choose>
					<c:when test="${empty ilrMaxRisk or  ilrMaxRisk < 0 }" >
						<label data-name="ILR-VALUE-MAX-RISK" class="form-control form-control-static text-right disabled" data-trick-type='integer' >-</label>
					</c:when>
					<c:otherwise>
						<label data-name="ILR-VALUE-MAX-RISK" class="form-control form-control-static text-right disabled" data-trick-type='integer' ><spring:message text="${ilrMaxRisk}" /></label>
					</c:otherwise>
				</c:choose></td>
			</c:if>
			<td class='form-estimation  form-estimation-left'><spring:message var="netImpColor" text="${computedNetImportance.color}" /> <input name="computedNetImportance"
				disabled="disabled" class="form-control numeric" title='<spring:message text='${computedNetImportance.title}'/>' value="${computedNetImportance.value}"
				style="border: solid 2px ${empty netImpColor? '#eee' : netImpColor}"></td>
			<c:if test="${type.quantitative}">
				<td class='form-estimation form-estimation-left form-estimation-right'><c:set var="impact" value="${assessment.getImpact('IMPACT')}" />
					<div class="input-group">
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" data-scale-modal="#Scale_Impact">k&euro;</button></span>
						<c:choose>
							<c:when test="${empty impact}">
								<input name="IMPACT" class="form-control text-right" value='0' list="dataList-parameter-impact" placeholder="0" data-trick-type='string' title="${impactTypes[0].acronym}0">
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${impact.real<100}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
									</c:when>
									<c:when test="${impact.real<1000}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,2)}" var="impactValue" />
									</c:when>
									<c:when test="${impact.real<10000}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,1)}" var="impactValue" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${impact.raw.equals(impact.real)}">
										<input name="IMPACT" class="form-control text-right" value="${impactValue}" list="dataList-parameter-impact" placeholder="${impactValue}" data-trick-type='string'
											title="${impact.variable}">
									</c:when>
									<c:otherwise>
										<spring:message var="impactVariable" text="${impact.raw}" />
										<input name="IMPACT" class="form-control text-right" value="${impactVariable}" list="dataList-parameter-impact" placeholder="${impactVariable}" data-trick-type='string'
											title="${impactValue}">
									</c:otherwise>
								</c:choose>

							</c:otherwise>
						</c:choose>
					</div></td>
				<c:if test="${show_uncertainty}">
					<td><div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALEO' class='form-control form-control-static numeric disabled'
								title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></label>
						</div></td>
				</c:if>
				<td><div class="input-group">
						<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALE' class='form-control form-control-static numeric disabled'
							title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></label>
					</div></td>
				<c:if test="${show_uncertainty}">
					<td><div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name="ALEP" class="form-control form-control-static numeric disabled"
								title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></label>
						</div></td>
				</c:if>
			</c:if>
		</tr>
		<!-- EXP -->
		<tr class="form-group-exp">
			<td style="transform: rotate(270deg);">${exp}</td>
			<td class='form-estimation  form-estimation-right'>
				<div class="input-group" align="right">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 0px" data-scale-modal="#Scale_Probability">
							<span class="fa-stack"> <i class="fa fa-arrows-v" aria-hidden="true"></i> <i class="fa fa-list-ol" aria-hidden="true"></i>
							</span>
						</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.probability}">
							<spring:message var="expProTitle" code='${empty probabilities?"label.title.status.na" : ""}' text="${empty probabilities?'Not Applicable' : probabilities[0].label}" />
							<select class="form-control" title='${expProTitle}' name="riskProfile.expProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title='<spring:message text="${parameter.label}"/>'><c:choose>
											<c:when test="${parameter.level == 0}">
												<spring:message code='label.status.na' />
											</c:when>
											<c:otherwise>${parameter.level}</c:otherwise>
										</c:choose></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<spring:message var="expProTitle" text="${riskProfile.expProbaImpact.probability.label}" />
							<select class="form-control" title='${expProTitle}' name="riskProfile.expProbaImpact.probability" data-trick-value='${riskProfile.expProbaImpact.probability.id}'
								data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.probability==parameter?"selected='selected'":""} title='<spring:message text="${parameter.label}"/>'><c:choose>
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
				<c:if test="${impactType.name!='IMPACT'}">
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
									<spring:message var="expTitle" text="${empty impacts[impactType.name]? '' : impacts[impactType.name][0].label}" />
									<select class="form-control" title='${expTitle}' name="riskProfile.expProbaImpact.${impactName}" data-trick-value='0' data-trick-type='integer'>
										<c:forEach items="${impacts[impactType.name]}" var="parameter">
											<option value="${parameter.id}" title='<spring:message text="${parameter.label}"/>'><c:choose>
													<c:when test="${parameter.level == 0}">
														<spring:message code='label.status.na' />
													</c:when>
													<c:otherwise>${parameter.level}</c:otherwise>
												</c:choose></option>
										</c:forEach>
									</select>
								</c:when>
								<c:otherwise>
									<spring:message var="expTitle" text="${impact.label}" />
									<select class="form-control" title='${expTitle}' name="riskProfile.expProbaImpact.${impactName}" data-trick-value='${impact.id}' data-trick-type='integer'>
										<c:forEach items="${impacts[impactType.name]}" var="parameter">
											<option value="${parameter.id}" ${impact==parameter? "selected='selected'" : ""} title='<spring:message text="${parameter.label}"/>'><c:choose>
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
				</c:if>
			</c:forEach>
			<c:if test="${isILR}">
				<td class="form-estimation form-estimation-left"> <span style="transform: rotate(-90deg);display: inline-block;width: 52px;margin-left: -22px;margin-right: -30px;" title="Vulnerability reduction"><spring:message text="V Redu." /></span></td>
				<td><select name='riskProfile.expProbaImpact.vulnerability' ${riskProfile.riskStrategy eq 'ACCEPT'? 'disabled': ''} class='form-control' data-trick-type='integer'
								title="<spring:message text='${riskProfile.expProbaImpact.vulnerability}'/>" ><c:forEach end="3" begin="0" step="1" var="vulnerability">
						<option value="${vulnerability}" ${ vulnerability > assessment.vulnerability ? "disabled hidden" : ''} ${vulnerability eq 1 && empty riskProfile.expProbaImpact or vulnerability eq riskProfile.expProbaImpact.vulnerability ? 'selected' : ''}>${vulnerability}</option>
						</c:forEach></select></td>
				<td><c:choose>
					<c:when test="${empty ilrTargetedRisk or  ilrTargetedRisk < 0 }" >
						<label data-name="ILR-VALUE-TARGET-RISK" class="form-control form-control-static text-right disabled" data-trick-type='integer' >-</label>
					</c:when>
					<c:otherwise>
						<label data-name="ILR-VALUE-TARGET-RISK" class="form-control form-control-static text-right disabled" data-trick-type='integer' ><spring:message text="${ilrTargetedRisk}" /></label>
					</c:otherwise>
				</c:choose></td>
			</c:if>
			<td class='form-estimation  form-estimation-left'><spring:message var="expImpColor" text="${computedExpImportance.color}" /><input name="computedExpImportance"
				disabled="disabled" class="form-control numeric" title='<spring:message text='${computedExpImportance.title}'/>' value="${computedExpImportance.value}"
				style="border: solid 2px ${empty expImpColor? '#eee' : expImpColor}"></td>
			<c:if test="${type.quantitative}">
				<c:choose>
					<c:when test="${empty riskRegister}">
						<c:set var="impact" value="${valueFactory.findValue(0.0, 'IMPACT')}" />
						<c:set var="aleExp" value="${0}" />
					</c:when>
					<c:otherwise>
						<c:set var="impact" value="${valueFactory.findValue(riskRegister.expectedEvaluation.impact, 'IMPACT')}" />
						<c:set var="aleExp" value="${riskRegister.expectedEvaluation.importance}" />
					</c:otherwise>
				</c:choose>
				<td class='form-estimation form-estimation-left form-estimation-right'>
					<div class="input-group">
						<span class="input-group-addon" style="padding: 1px;">k&euro;</span>
						<c:choose>
							<c:when test="${empty impact}">
								<label data-name="IMPACT-EXP" class="form-control form-control-static disabled text-right" title="i0">0</label>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${impact.real<100}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
									</c:when>
									<c:when test="${impact.real<1000}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,2)}" var="impactValue" />
									</c:when>
									<c:when test="${impact.real<10000}">
										<fmt:formatNumber value="${fct:round(impact.real*0.001,1)}" var="impactValue" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
									</c:otherwise>
								</c:choose>
								<label data-name="IMPACT-EXP" class="form-control form-control-static disabled text-right" title="${impact.variable}">${impactValue}</label>
							</c:otherwise>
						</c:choose>
					</div>
				</td>
				<c:if test="${show_uncertainty}">
					<c:set var="aleoExp" value="${aleExp /  assessment.uncertainty}" />
					<td><div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALEO-EXP' class='form-control form-control-static numeric disabled'
								title="<fmt:formatNumber value="${aleoExp}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleoExp*0.001,1)}" /></label>
						</div></td>
				</c:if>
				<td><div class="input-group">
						<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name='ALE-EXP' class='form-control form-control-static numeric disabled'
							title="<fmt:formatNumber value="${aleExp}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleExp*0.001,1)}" /></label>
					</div></td>
				<c:if test="${show_uncertainty}">
					<c:set var="alepExp" value="${aleExp *  assessment.uncertainty}" />
					<td><div class="input-group">
							<span class="input-group-addon" style="padding: 1px;">k&euro;</span> <label data-name="ALEP-EXP" class="form-control form-control-static numeric disabled"
								title="<fmt:formatNumber value="${alepExp}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alepExp*0.001,1)}" /></label>
						</div></td>
				</c:if>
			</c:if>
		</tr>
	</tbody>
</table>

<div class='form-group form-group-fill'>
	<spring:message code="label.comment_argumentation" text="Comment / Argumentation" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea id="assessment-comment" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
</div>
<div class='form-group form-group-fill'>
	<spring:message code="label.risk_treatment" text="Risk treatment" var='riskTreatment' />
	<spring:message text='${riskProfile.riskTreatment}' var="riskTreatmentContent" />
	<label class='label-control'>${riskTreatment}</label>
	<textarea id="assessment-riskTreatment" class="form-control" name="riskProfile.riskTreatment" title="${riskTreatment}" style="resize: vertical;"
		placeholder="${riskTreatmentContent}" data-trick-type='string'>${riskTreatmentContent}</textarea>
</div>
<div class='form-group form-group-fill'>
	<spring:message code="label.cockpit" text="Cockpit" var='cockpit' />
	<spring:message text='${assessment.cockpit}' var="cockpitContent" />
	<label class='label-control'>${cockpit}</label>
	<textarea id="assessment-cockpit" class="form-control" name="cockpit" title="${cockpit}" style="resize: vertical;"
		placeholder="${cockpitContent}" data-trick-type='string'>${cockpitContent}</textarea>
</div>
<div class='form-group' id="section_estimation_action_plan">
	<spring:message code="label.action_paln.including.deadlines" text="Action plan (including deadlines)" var='actionPlan' />
	<ul class='nav nav-pills' id="menu_estimation_action_plan">
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
	<spring:message code="info.analysis.estimation.action_plan.addition.field" var="actionPlanInfo" />
	<textarea id="assessment-actionPlan" class="form-control" name="riskProfile.actionPlan" title="${actionPlanInfo}"
		style="resize: vertical; margin-top: 5px; display: ${empty actionPlanContent? 'none' : 'inline-block'};"
		placeholder="${empty actionPlanContent? actionPlanInfo : actionPlanContent}" data-trick-type='string'>${actionPlanContent}</textarea>
	<table id="riskProfileMeasure" class="table table-hover form-no-fill">
		<thead>
			<tr class='form-group-fill'>
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
					<td><spring:message text='${ measure.measureDescription.standard.name}' /></td>
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
</div>
<c:if test="${showHiddenComment}">
	<div class='form-group form-group-fill'>
		<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
		<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
		<label class='label-control'>${hiddenComment}</label>
		<textarea id="assessment-hiddenComment" class="form-control" name="hiddenComment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}"
			data-trick-type='string'>${hiddenCommentContent}</textarea>
	</div>
</c:if>