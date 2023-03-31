<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div data-view-content-name='risk-sheet'>
	<ul class="nav nav-tabs nav-justified">
		<li class='active'><a href="#import-${rawRiskSheetItem.name}" data-toggle="tab"><spring:message code='label.export.raw.data' /></a></li>
		<li><a href="#import-${reportRiskSheetItem.name}" data-toggle="tab"><spring:message code='label.export.report.sheet'/></a></li>
	</ul>
	<div class="tab-content" data-view-tab='secondary'>
		<c:set var="item" value="${rawRiskSheetItem}" scope="request" />
		<div class='tab-pane active' id="import-${item.name}" data-view-name='${item.name}' data-view-process-url='${item.processURL}' data-view-extentions='${item.extensions}'>
			<c:set var="type" value="RAW" scope="request" />
			<jsp:include page="form.jsp" />
		</div>
		<c:set var="item" value="${reportRiskSheetItem}" scope="request" />
		<div class='tab-pane' id="import-${item.name}" data-view-name='${item.name}' data-view-process-url='${item.processURL}' data-view-extentions='${item.extensions}'>
			<c:set var="type" value="REPORT" scope="request" />
			<jsp:include page="form.jsp" />
		</div>
	</div>
</div>