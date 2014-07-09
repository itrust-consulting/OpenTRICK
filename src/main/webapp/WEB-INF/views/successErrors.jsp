<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty(success)}">
	<div class="alert alert-success" id="success">
		<a href="#" class="close" data-dismiss="alert">×</a>
		<spring:message code="${success}" text="${success}" />
	</div>
</c:if>
<c:if test="${!empty(errors)}">
	<div class="alert alert-danger" id="error">
		<a href="#" class="close" data-dismiss="alert">×</a>
		<spring:message code="${errors}" text="${errors}" />
	</div>
</c:if>