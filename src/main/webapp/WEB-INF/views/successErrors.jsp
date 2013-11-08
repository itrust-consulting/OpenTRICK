<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty(success)}">
	<div class="alert alert-success" id="success">
		<a href="#" onclick="$('#success').remove()" class="btn btn-danger btn-sm pull-right"><span class="glyphicon glyphicon-remove"></span></a>
		<spring:message code="${success}" text="${success}" />
	</div>
</c:if>
<c:if test="${!empty(errors)}">
	<div class="alert alert-danger" id="error">
		<a href="#" onclick="$('#error').remove()" class="btn btn-success btn-sm pull-right"><span class="glyphicon glyphicon-remove"></span> </a>
		<spring:message code="${errors}" text="${errors}" />
	</div>
</c:if>