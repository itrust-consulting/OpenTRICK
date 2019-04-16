<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div data-view-content-name='word-report'>
	<c:choose>
		<c:when test="${not empty items}">
			<ul class="nav nav-tabs nav-justified">
				<c:forEach items="${items}" var="item" varStatus="status">
					<spring:message text="${fn:toLowerCase(item.name)}" var="itemName"/>
					<li ${itemName=="hybrid"? "class='active'" :""}><a href="#export-word-report-${itemName}" data-toggle="tab"><spring:message
								code='label.analysis.type.${itemName}' /></a></li>
				</c:forEach>
			</ul>
			<div class="tab-content" data-view-tab='secondary'>
				<c:forEach items="${items}" var="currentItem" varStatus="status">
					<c:set var="item" value="${currentItem}" scope="request" />
					<spring:message text="${fn:toLowerCase(item.name)}" var="itemName"/>
					<spring:message text="word-report-${fn:toLowerCase(item.name)}" var="typeName" />
					<div class='tab-pane ${itemName=="hybrid"? "active" :""}' id="export-${typeName}" data-view-name='word-report' data-view-process-url='${item.processURL}'
						data-view-extentions='${item.extensions}'>
						<fieldset>
							<legend>
								<spring:message code='label.title.data_manager.export.word_report_${fn:toLowerCase(item.name)}' />
							</legend>
							<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
								<spring:message code="info.data_manager.export.${fn:replace(typeName, '-','_')}" />
							</div>
							<jsp:include page="form.jsp" />
						</fieldset>
					</div>
				</c:forEach>
			</div>
		</c:when>
		<c:otherwise>
			<fieldset>
				<legend>
					<spring:message code='label.title.data_manager.export.word_report' />
				</legend>
				<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
					<spring:message code="info.data_manager.export.word_report" />
				</div>
				<jsp:include page="form.jsp" />
			</fieldset>
		</c:otherwise>
	</c:choose>

</div>