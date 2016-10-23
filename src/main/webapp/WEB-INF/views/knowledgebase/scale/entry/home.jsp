<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request).language" var="locale" />
</c:if>
<c:set var="language" value="${fn:toUpperCase(locale)}" />
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab_kb_scale">
	<div class='section row' id='section_kb_scale'>
		<c:forEach items="${scales}" var="scale">
			<div class="col-md-6">
				<fieldset id="section_kb_scale_${scale.id}">
					<legend>
						<spring:message text="${empty scale.type.translations[language]? scale.type.name : scale.type.translations[language]}" />
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
							<c:set var="maxLevel" value="${scale.level-1}" />
							<c:forEach items="${scale.scaleEntries}" var="scaleEntry" varStatus="status">
								<tr data-trick-class="ScaleEntry" data-trick-id="${scaleEntry.id}">
									<td class="textaligncenter"><spring:message text="${scaleEntry.level}" /></td>
									<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
											text="${scaleEntry.acronym}" /></td>
									<td data-trick-field="qualifications['${language}']" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
											text="${scaleEntry.qualifications[language]}" /></td>
									<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${scaleEntry.value}" maxFractionDigits="0" />&euro;'><fmt:formatNumber
											value="${scaleEntry.value*0.001}" maxFractionDigits="0" /></td>
									<td class="textaligncenter"><fmt:formatNumber value="${scaleEntry.bounds.from*0.001}" maxFractionDigits="0" /></td>
									<td class="textaligncenter"><c:choose>
											<c:when test="${scaleEntry.level < maxLevel}">
												<fmt:formatNumber value="${scaleEntry.bounds.to*0.001}" maxFractionDigits="0" />
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