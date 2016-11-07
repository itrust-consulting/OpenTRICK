<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tabParameterImpact">
	<div class='section row' id='section_parameter_impact'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.impact_scales' text="Impact scales" />
					</h3>
				</div>
			</div>
		</div>
		<c:forEach items="${impactTypes}" var="impactType">
			<spring:message text="${impactType.name}" var="impactName" />
			<div class="col-md-6">
				<fieldset id="Scale_Impact_${impactName}">
					<legend>
						<spring:message code="label.title.parameter.extended.impact.${fn:toLowerCase(impactName)}"
							text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[language]}" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.value" /> k&euro;</th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.min" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.max" /></th>
							</tr>
						</thead>
						<tbody>
							<c:set var="length" value="${mappedParameters[impactName].size()-1}" />
							<c:set var="AllEditable" value="${mappedParameters[impactName].size() mod 2 == 0}" />
							<c:forEach items="${mappedParameters[impactName]}" var="parameter" varStatus="status">
								<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
									<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
											text="${parameter.acronym}" /></td>
									<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
											text="${parameter.description}" /></td>
									<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
										${AllEditable or (parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
											value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
									<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
									<td class="textaligncenter"><c:choose>
											<c:when test="${status.index!=length}">
												<fmt:formatNumber value="${parameter.bounds.to*0.001}" maxFractionDigits="0" />
											</c:when>
											<c:otherwise>
												<span style="font-size: 17px;">+&#8734;</span>
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:forEach>
	</div>
</div>