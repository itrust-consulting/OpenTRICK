<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter-impact">
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
								<th class="textaligncenter"><spring:message code="label.parameter.label" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${mappedParameters[impactName]}" var="parameter">
								<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
									<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td data-trick-field="label" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message text="${parameter.label}" /></td>
									<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
											text="${parameter.description}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:forEach>
	</div>
</div>