<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:message code="label.menu.show.impact_scale" var="impactScaleMenu" />
<spring:message code="label.menu.show.probability_scale" var="probabilityScaleMenu" />
<spring:message code="label.title.impact_scale" var="impactScaleTitle" />
<spring:message code="label.title.probability_scale" var="probabilityScaleTitle" />
<spring:message code="label.action.next" var="nextSelected" />
<spring:message code="label.action.previous" var="prevSelected" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersTitle" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersMenu" />
<spring:message code="label.menu.analysis.parameter.probability" var="probablityMenu" />

<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:if test="${empty langue}">
	<c:set var="langue" value="${fn:toUpperCase(locale.language) }" scope="request" />
</c:if>
<c:set var="impactScaleTitle">
	${fn:replace(impactScaleTitle,"'", "\\'" )}
</c:set>
<c:set var="probabilityScaleTitle">
	${fn:replace(probabilityScaleTitle,"'", "\\'" )}
</c:set>
<fmt:setLocale value="fr" scope="session" />
<div class="col-md-10 trick-ui" id="section_scenario_assessment" data-view='estimation-ui' data-trick-asset-id='-1' data-trick-scenario-id='-1' data-trick-content='scenario'>
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3 role="title"><spring:message code="label.assessment.for.scenario" arguments="${scenario.name}" /></h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_scenario_assessment">
		<li><a href="#" onclick="return switchTab('tabScenario')"><span class="fa fa-home"></span> <spring:message code="label.menu.analysis.scenario" /></a></li>
		<c:choose>
			<c:when test="${type == 'QUALITATIVE'}">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${impactScaleMenu} <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<c:forEach items="${impactTypes}" var="impactType">
							<spring:message var="impactName" text="${impactType.name}" />
							<li><a href="#" onclick='return displayParameters("#Scale_Impact_${impactName}")'><spring:message
										code="label.title.parameter.extended.impact.${fn:toLowerCase(impactName)}"
										text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[language]}" /></a></li>
						</c:forEach>
					</ul></li>
				<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
			</c:when>
			<c:otherwise>
				<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${probabilityScaleMenu} <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleTitle}</a></li>
						<li><a href="#" onclick="return displayParameters('#DynamicParameters')">${dynamicParametersTitle}</a></li>
					</ul></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<table class="table table-hover table-condensed table-fixed-header-analysis">
		<thead>
			<c:choose>
				<c:when test="${type=='QUALITATIVE'}">
					<tr>
						<th rowspan="2" style="width: 10%" title='<spring:message code="label.assessment.asset" />'><spring:message code="label.assessment.asset" /></th>
						<th rowspan="2" style="width: 2%" title='<spring:message code="label.assessment.asset.value" />'><spring:message code="label.assessment.asset.value" /></th>
						<th style="text-align: center;" colspan="${impactTypes.size()}"><spring:message code="label.title.impact" /></th>
						<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
						<th rowspan="2" width="2%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th rowspan="2" style="width: 29%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
						<th rowspan="2" style="width: 29%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
					</tr>
					<tr>
						<c:forEach items="${impactTypes}" var="impactType">
							<spring:message code="label.title.assessment.impact_${fn:toLowerCase(impactType.name)}"
								text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue]}" var="impactTitle" />
							<th style="width: 2%" title='${impactTitle}'><spring:message code="label.assessment.impact_${fn:toLowerCase(impactType.name)}" text="${impactType.sortName}" /></th>
						</c:forEach>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th style="width: 10%" title='<spring:message code="label.assessment.asset" />'><spring:message code="label.assessment.asset" /></th>
						<th style="width: 2%" title='<spring:message code="label.assessment.asset.value" />'><spring:message code="label.assessment.asset.value" /></th>
						<th style="width: 5%" title='<spring:message code="label.title.impact" />'><spring:message code="label.assessment.impact" /></th>
						<th style="width: 5%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 2%" title='<spring:message code="label.title.uncertainty" />'><spring:message code="label.assessment.uncertainty" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.assessment.aleo" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.alep" />'><spring:message code="label.assessment.alep" /></th>
							</c:when>
							<c:otherwise>
								<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
							</c:otherwise>
						</c:choose>
						<th width="2%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th style="width: 29%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
						<th style="width: 29%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
					</tr>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<c:forEach items="${assessments}" var="assessment">
				<tr data-trick-class="Assessment" data-trick-id="${assessment.id}">
					<td style="height: 32px;"><spring:message text="${assessment.asset.name}" /></td>
					<td title="<fmt:formatNumber value="${assessment.asset.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.asset.value*0.001,0)}" /></td>
					<c:set var="likelihood" value="${valueFactory.findExp(assessment.likelihood)}" />
					<c:choose>
						<c:when test="${type == 'QUALITATIVE'}">
							<c:forEach items="${impactTypes}" var="impactType">
								<spring:message text="${impactType.name}" var="impactName" />
								<c:set var="impact" value="${assessment.getImpact(impactName)}" />
								<c:choose>
									<c:when test="${empty impact}">
										<td data-trick-field="${impactName}" data-trick-field-type="string" class="success" title='<spring:message text="${impactType.acronym}"/>0'
											onclick="return editField(this);">0</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="${impactName}" data-trick-field-type="string" class="success" title='<spring:message text="${impact.variable}"/>' onclick="return editField(this);">${impact.level}</td>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<c:choose>
								<c:when test="${empty likelihood}">
									<spring:message text="${assessment.likelihood}" var="likelihood" />
									<td data-trick-field="likelihood" data-trick-field-type="string" class="success" title='${likelihood}' onclick="return editField(this);">${likelihood}</td>
								</c:when>
								<c:otherwise>
									<td data-trick-field="likelihood" data-trick-field-type="string" class="success" onclick="return editField(this);" title='<spring:message text="${likelihood.variable}"/>'><spring:message
											text="${likelihood.level}" /></td>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:set var="impact" value="${assessment.getImpact('IMPACT')}" />
							<c:choose>
								<c:when test="${empty impact}">
									<td data-trick-field="IMPACT" data-trick-field-type="string" class="success" title='0 &euro;' onclick="return editField(this);">0</td>
								</c:when>
								<c:otherwise>
									<td data-trick-field="IMPACT" data-trick-field-type="string" class="success" onclick="return editField(this);" title='${impact.variable}'><c:choose>
											<c:when test="${impact.real < 10000}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" />
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty likelihood}">
									<spring:message text="${assessment.likelihood}" var="likelihood" />
									<td data-trick-field="likelihood" data-trick-field-type="string" class="success" title='${likelihood}' onclick="return editField(this);">${likelihood}</td>
								</c:when>
								<c:otherwise>
									<td data-trick-field="likelihood" data-trick-field-type="string" class="success" onclick="return editField(this);" title='<spring:message text="${likelihood.variable}" />'><fmt:formatNumber
											value="${fct:round(likelihood.real,3)}" /></td>
								</c:otherwise>
							</c:choose>
							<c:if test="${show_uncertainty}">
								<td data-trick-field="uncertainty" data-trick-field-type="double" class="success" data-trick-min-value="1.0000000000001"
									data-real-value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />' onclick="return editField(this);"><fmt:formatNumber
										value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
								<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
							</c:if>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
							<c:if test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
							</c:if>
						</c:otherwise>
					</c:choose>
					<td class="success" onclick="return editField(this);" data-trick-field="owner" data-trick-field-type="string"><spring:message text="${assessment.owner}" /></td>
					<td class="success" onclick="return editField(this.firstChild);"><pre data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${assessment.comment}" /></pre></td>
					<td class="success" onclick="return editField(this.firstChild);"><pre data-trick-field="hiddenComment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${assessment.hiddenComment}" /></pre></td>
				</tr>
			</c:forEach>
			<c:if test="${type == 'QUANTITATIVE'}">
				<tr class="panel-footer" style="font-weight: bold;">
					<c:choose>
						<c:when test="${show_uncertainty}">
							<td colspan="5"><spring:message code="label.total.ale" /></td>
							<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,1)}" /></td>
						</c:when>
						<c:otherwise>
							<td colspan="4"><spring:message code="label.total.ale" /></td>
							<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
						</c:otherwise>
					</c:choose>
					<td colspan="3" />
				</tr>
			</c:if>
		</tbody>
	</table>
</div>