<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tabParameterImpact">
	<div class='section row' id='section_parameter_extended'>
		<c:if test="${isEditable}">
			<ul class="nav nav-pills bordered-bottom" id="menu_parameter_extended" style="margin-bottom: 10px;">
				<li><a href="#"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
				<li class="disabled" data-trick-selectable="true"><a onclick="return editAsset();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.action.edit" /> </a></li>
				<li class="disabled pull-right" data-trick-selectable="multi"><a class="text-danger"><span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.action.delete" /> </a></li>
			</ul>
		</c:if>
		<jsp:include page="financial.jsp" />
		<div class="col-md-6">
			<fieldset id="Scale_Impact_reputation">
				<legend>
					<spring:message code="label.title.parameter.extended.impact.reputation" />
				</legend>
				<table class="table table-hover table-condensed">
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
						<c:forEach items="${mappedParameters['IMPACT_REP']}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
								<!--<td>${itemInformation.id}</td>-->
								<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
								<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.acronym}" /></td>
								<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.description}" /></td>
								<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
									${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
										value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><c:choose>
										<c:when test="${status.index!=10}">
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

		<div class="col-md-6">
			<fieldset id="Scale_Impact_operational">
				<legend>
					<spring:message code="label.title.parameter.extended.impact.operational" />
				</legend>
				<table class="table table-hover table-condensed">
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
						<c:forEach items="${mappedParameters['IMPACT_OPE']}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
								<!--<td>${itemInformation.id}</td>-->
								<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
								<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.acronym}" /></td>
								<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.description}" /></td>
								<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
									${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
										value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><c:choose>
										<c:when test="${status.index!=10}">
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


		<div class="col-md-6">
			<fieldset id="Scale_Impact_legal">
				<legend>
					<spring:message code="label.title.parameter.extended.impact.legal" />
				</legend>
				<table class="table table-hover table-condensed">
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
						<c:forEach items="${mappedParameters['IMPACT_LEG']}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
								<!--<td>${itemInformation.id}</td>-->
								<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
								<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.acronym}" /></td>
								<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.description}" /></td>
								<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
									${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
										value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
								<td class="textaligncenter"><c:choose>
										<c:when test="${status.index!=10}">
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
	</div>
</div>