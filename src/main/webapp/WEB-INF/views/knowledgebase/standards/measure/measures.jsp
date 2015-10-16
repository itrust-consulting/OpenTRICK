<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab_measure" data-trigger="showMeasures" data-update-required="true">
	<c:if test="${not empty languages}">
		<c:set var="selectedLanguage" value="${languages[0]}" scope="request"/>
	</c:if>
	<jsp:include page="section.jsp" />
</div>
