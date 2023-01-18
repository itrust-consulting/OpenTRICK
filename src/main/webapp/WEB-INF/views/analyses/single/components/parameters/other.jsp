<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter">
	<div class='section row' id='section_parameter'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.parameter' />
					</h3>
				</div>
			</div>
		</div>
		<div class="col-sm-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.simple.various" />
				</legend>
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.internal_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.external_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.default_life_time" /></th>
							<c:if test="${type.quantitative }">
								<th class="textaligncenter"><spring:message code="label.parameter.simple.max_rrf" /></th>
							</c:if>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" /></th>
							<c:if test="${isILR}">
								<th class="textaligncenter"><spring:message code="label.parameter.simple.ilr_rrf_threshold" /></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<tr data-trick-class="SimpleParameter" class='editable'>
							<c:forEach items="${mappedParameters['SINGLE']}" var="parameter">
								<c:choose>
									<c:when test="${parameter.description == 'soaThreshold'}">
										<td data-trick-id="${parameter.id}" data-name="soaThreshold" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value"
											data-trick-field-type="double" onclick="return editField(this);" data-trick-callback='soaThresholdUpdate()'><fmt:formatNumber value="${parameter.value}"
												maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description == 'max_rrf'}">
										<c:if test="${type.quantitative }">
											<td data-trick-id="${parameter.id}" data-name="max_rrf" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value"
												data-trick-field-type="double" onclick="return editField(this);" data-trick-callback-pre="fixMinRRFFromRRFThreshold()" data-trick-callback='updateMaxRRF()'><fmt:formatNumber value="${parameter.value}"
													maxFractionDigits="0" pattern="#" /></td>
										</c:if>
									</c:when>
									<c:when test="${parameter.description == 'lifetime_default'}">
										<td data-trick-id="${parameter.id}" data-trick-min-value='1e-19' class="textaligncenter" data-trick-field="value" data-trick-callback='updateMeasuresCost()'
											data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description == 'ilr_rrf_threshold'}">
										<c:if test="${isILR}">
											<td data-trick-id="${parameter.id}" data-name="ilr_rrf_threshold"  data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value"
												data-trick-field-type="double" onclick="return editField(this);" data-trick-callback-pre="fixMaxRRFThresholdFromRRF()" ><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" /></td>
										</c:if>
									</c:when>
									<c:when test="${parameter.description == 'mandatoryPhase'}">
										<td data-trick-id="${parameter.id}" data-trick-callback-pre="extractPhase(this,true)" class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:otherwise>
										<td data-trick-id="${parameter.id}" class="textaligncenter" data-trick-field="value" data-trick-field-type="double" data-trick-callback='updateMeasuresCost()'
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:otherwise>
								</c:choose>

							</c:forEach>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
		
		<c:if test="${type.qualitative}">
			<spring:message code='label.nil' var="nil" />
			<spring:message code='label.all' var="all" />
			<spring:message code='label.compliant' var="compliant" />
			<div class="col-md-6">
				<fieldset>
					<legend>
						<spring:message code="label.title.parameter.simple.cssf" />
					</legend>
					<table class="table table-hover table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.impact_threshold" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.probability_threshold" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.direct_size" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.indirect_size" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.cia_size" /></th>
							</tr>
						</thead>
						<tbody>
							<tr data-trick-callback="reloadSection('section_riskregister');" data-trick-class="SimpleParameter">
								<c:forEach items="${mappedParameters['CSSF']}" var="parameter">
									<c:choose>
										<c:when test="${parameter.description=='cssfImpactThreshold' or parameter.description=='cssfProbabilityThreshold'}">
											<td data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='10' data-trick-step-value='1' class="editable textaligncenter"
												data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0"
													pattern="#" /></td>
										</c:when>
										<c:when test="${parameter.description== 'cssfCIASize' or parameter.description== 'cssfDirectSize' or parameter.description== 'cssfIndirectSize'}">
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" var="cssfSize" />
											<td data-trick-id="${parameter.id}" data-trick-choose-translate='${nil},${all},${compliant}' data-trick-min-value='-2' data-trick-step-value='1'
												data-trick-max-value='1000' class="editable textaligncenter" data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><c:choose>
													<c:when test="${parameter.value <= -2 }">
																${nil}
																</c:when>
													<c:when test="${parameter.value == -1}">
																${all}
																</c:when>
													<c:when test="${parameter.value == 0}">
																${compliant}
																</c:when>
													<c:otherwise>${cssfSize}</c:otherwise>
												</c:choose></td>
										</c:when>
									</c:choose>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</fieldset>
			</div>

			<div class="col-sm-3">
				<fieldset>
					<legend>
						<spring:message code="label.parameter.probability.label" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.label" /></th>
								<c:if test="${isILR}" >
									<th class="textaligncenter"><spring:message code="label.parameter.ilr" /></th>
								</c:if>
						
							</tr>
						</thead>
						<tbody>

							<c:forEach items="${mappedParameters['PROBA']}" var="parameter" varStatus="status">
								<tr data-trick-class="LikelihoodParameter" data-trick-id="${parameter.id}">
									<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td data-trick-field="label" data-trick-field-type="string" class="editable textaligncenter"
										data-trick-callback="reloadSection('section_parameter_impact_probability');reloadRiskHeatMapSection();" onclick="return editField(this);"><spring:message
											text="${parameter.label}" /></td>
									<c:if test="${isILR}" >
										<td class="editable textaligncenter" class="editable textaligncenter" data-trick-min-value='-1' data-trick-step-value='1' data-trick-max-value='4' 
											data-trick-field="ilrLevel"  data-trick-field-type="integer" onclick="return editField(this);"><spring:message text="${parameter.ilrLevel}" /></td>
									</c:if>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
			<div class="col-sm-3">
				<fieldset>
					<legend>
						<spring:message code="label.parameter.impact.label" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.label" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${mappedParameters[impactLabel]}" var="parameter" varStatus="status">
								<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
									<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td data-trick-field="label" data-trick-field-type="string" class="editable textaligncenter"
										data-trick-callback="reloadSection('section_parameter_impact_probability');reloadRiskHeatMapSection();" onclick="return editField(this);"><spring:message
											text="${parameter.label}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
			<div class="col-sm-6">
				<jsp:include page="risk-acceptance.jsp" />
			</div>
		</c:if>
		<c:if test="${isILR}">
			<div class="col-sm-6">
				<fieldset>
					<legend>
						<c:choose>
							<c:when test="${isEditable}">
								<spring:message code="label.title.parameter.ilr_soa_scale" />
								<span class="pull-right">
									<button class='btn btn-xs btn-link' onclick="return manageIlrSoaScale(true)" style="font-size: 15px">
										<i class="fa fa-cog" aria-hidden="true"></i>
										<spring:message code='label.action.manage' />
									</button>
								</span>
							</c:when>
							<c:otherwise>
								<spring:message code="label.title.parameter.ilr_soa_scale" />
							</c:otherwise>
						</c:choose>
					</legend>
					<table class="table table-hover table-condensed" id="table_parameter_ilr_soa_scale">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.implementation.rate.threshold" /></th>
								<th style="width: 50%"><spring:message code="label.description" /></th>
								<th class="textaligncenter"><spring:message code="label.color" /></th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty mappedParameters['ILR_SOA_SCALE']}">
									<tr class='warning'>
										<td colspan="4"><spring:message code='info.ilr_soa_scale.current.empty' /></td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:set var="size" value="${mappedParameters['ILR_SOA_SCALE'].size()}" />
									<c:forEach items="${mappedParameters['ILR_SOA_SCALE']}" var="parameter" varStatus="status">
										<tr data-trick-class="IlrSoaScaleParameter" data-trick-id="${parameter.id}" ${isEditable? 'ondblclick="return manageIlrSoaScale()"':''}>
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" var="value" />
											<td class='textaligncenter' data-trick-field="value"><c:choose>
												<c:when test="${status.index==0 and value eq -1}">
													<spring:message code="label.title.measure.status.na" />
												</c:when>
												<c:otherwise>
													${empty prevValue || prevValue eq 0?'[' : ']' } <span class='text-muted'>${empty prevValue?0 : prevValue}</span> ; ${value} ]
												</c:otherwise>
												</c:choose></td>
											<c:set var="prevValue" value="${value == -1? 0 : value}" />
											<spring:message text="${parameter.color}" var="color" />
											<td class='editable' data-trick-field='description' data-trick-content="text" data-trick-field-type='string' onclick="return editField(this);"><spring:message
													text="${parameter.description}" /></td>
											<td style="background-color: ${color};" data-trick-field='color' data-trick-content="color" data-trick-field-type='string' data-real-value='${color}'
												onclick="return editField(this);"></td>
										</tr>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:if>

		<div class='col-sm-6'>
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.report.setting" />
				</legend>
				<table class="table table-hover table-condensed" id="table_parameter_report_setting">
					<thead>
						<tr>
							<th style="width: 90%"><spring:message code="label.name" /></th>
							<th class="textaligncenter"><spring:message code="label.color" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${reportSettings}" var="setting" varStatus="status">
							<tr data-trick-class="ReportSetting" data-trick-id="${setting.key}">
								<spring:message text="${setting.value}" var="color" />
								<td><spring:message code="label.report.setting.${fn:toLowerCase(setting.key)}"/></td>
								<td style="background-color: #${color};" data-real-value='#${color}' data-trick-field="${setting.key}" data-trick-field='value' data-trick-content='color' onclick="return editField(this);"></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div class='col-sm-6'>
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.export.filename" />
				</legend>
				<table class="table table-hover table-condensed" id="table_parameter_export_filename">
					<thead>
						<tr>
							<th><spring:message code="label.export.filename.name" /></th>
							<th class="textaligncenter"><spring:message code="label.export.filename.prefix" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${exportFilenames}" var="setting" varStatus="status">
							<tr data-trick-class="ExportFileName" data-trick-id="${setting.key}">
								<td><spring:message code="label.export.filename.${fn:toLowerCase(setting.key)}"/></td>
								<td class="editable textaligncenter" data-trick-field="${setting.key}" data-trick-field='value' data-trick-field-type="string" onclick="return editField(this);"><spring:message text="${setting.value}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
	</div>
</div>