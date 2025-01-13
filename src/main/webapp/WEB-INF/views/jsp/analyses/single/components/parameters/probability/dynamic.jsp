<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:if test="${type.quantitative and showDynamicAnalysis}">
	<div class="col-md-6 probability-dynamic">
		<fieldset id="DynamicParameters">
			<legend>
				<c:choose>
					<c:when test="${isEditable}">
						<spring:message code="label.parameter.dynamic.probability" />
						<span class="pull-right">
							<span style="margin-right:10px"><spring:message code="label.display.exclude.dynamic.parameter" /></span>
							<div class="material-switch pull-right">
                            	<input id="display-exclude-dynamic-parameter" name="display-exclude-dynamic-parameter" ${showExcludeDynamic? 'checked': ''} onchange="swtichExcludeDynamic(this)" type="checkbox"/>
                            	<label for="display-exclude-dynamic-parameter" class="label-success"></label>
                        	</div>
						</span>
					</c:when>
					<c:otherwise>
						<spring:message code="label.parameter.dynamic.probability" />
					</c:otherwise>
				</c:choose>
			</legend>
			<table class="table table-hover">
				<thead>
					<tr>
						<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
						<th class="textaligncenter"><spring:message code="label.parameter.value" /></th>
						<c:if test="${isEditable}">
							<th class="textaligncenter" data-name='action'><spring:message code="label.action" /></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<fmt:setLocale value="fr" scope="session" />
					<c:forEach items="${mappedParameters['DYNAMIC']}" var="parameter" varStatus="status">
						<spring:message text="${parameter.acronym}" var="dynamicAcronym"/>
						<tr data-trick-class="DynamicParameter" data-trick-id="${parameter.id}">
							<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter">${dynamicAcronym}</td>
							<td data-trick-field="value" class="textaligncenter"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="4" minFractionDigits="4" /></td>
							<c:if test="${isEditable}">
								<td class='text-center' data-name='action'><button class='btn btn-danger' onclick="deleteDynamicParameter(${parameter.id},'${dynamicAcronym}')"><i class='glyphicon glyphicon-trash'></i></button></td>
							</c:if>
						</tr>
					</c:forEach>
					<c:if test="${showExcludeDynamic and not empty excludeAcronyms}">
						<tr data-trick-class="ExcludeDynamicParameter">
							<th class="textaligncenter" colspan='${isEditable? 3:2 }'><spring:message code="label.exclude.dynamic.parameters"/></th>
						</tr>
						<c:forEach items="${excludeAcronyms}" var="dynamicAcronym" varStatus="status">
							<spring:message text="${dynamicAcronym}" var="acronym"/>
							<tr data-trick-class="ExcludeDynamicParameter" data-trick-id="${acronym}">
								<td class="textaligncenter">${acronym}</td>
								<td class="textaligncenter"><fmt:formatNumber value="${0}" maxFractionDigits="4" minFractionDigits="4" /></td>
								<c:if test="${isEditable}">
									<td class='text-center' data-name='action'><button class='btn btn-warning' onclick="restoreDynamicParameter('${acronym}')"><i class='glyphicon glyphicon-repeat'></i></button></td>
								</c:if>
							</tr>
						</c:forEach>
					</c:if>
					<fmt:setLocale value="${language}" scope="session" />
				</tbody>
			</table>
		</fieldset>
	</div>
</c:if>