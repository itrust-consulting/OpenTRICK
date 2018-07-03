<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div data-view-content-name='${item.name}' data-view-token='${item.token}'>
	<fieldset>
		<legend>
			<spring:message code="label.title.data-manager.export.measure" />
		</legend>
		<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
			<spring:message code="info.data-manager.export.measure" />
		</div>
		<form action='<spring:url value="${item.processURL}?token=${item.token}" />' class="form" method="post" enctype="application/x-www-form-urlencoded">
			<div class="form-group">
				<label data-helper-content='<spring:message code="help.export.measure.standard" />'>
				<spring:message code="label.standards" /></label> <select class="form-control"
					name="standards" multiple="multiple" required="required">
					<c:forEach items="${standards}" var="standard">
						<option title='<spring:message text="${standard.label} - v.${standard.version}" />' value="${standard.id}"><spring:message text="${standard.label}" /></option>
					</c:forEach>
				</select>
			</div>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<button type="submit" class='hidden' name="${item.name}-submit"></button>
		</form>
	</fieldset>
</div>