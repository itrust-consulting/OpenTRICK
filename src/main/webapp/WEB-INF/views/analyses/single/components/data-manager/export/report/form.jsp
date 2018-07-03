<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="nameControl" value="${fn:toLowerCase(item.name)}" />
<form class="form form-inline" action="${pageContext.request.contextPath}/Analysis/Export/Report/${analysis.id}" method="post" enctype="multipart/form-data"
	data-type-control='${nameControl}'>

	<input type="hidden" name="analysis" id="exportWord.analysis" value="${analysis.id}"> <input type="hidden" name="type" value="${item.name}">

	<fieldset style="margin-bottom: 10px">
		<legend style="font-size: 15px" data-helper-content='<spring:message code="help.export.report.internal" />'>
			<spring:message code='label.internal.template' text='Internal template' />
		</legend>

		<div class='col-sm-12 text-center' data-trick-info="template">
			<select name='template' class="form-control" id="exportWord.template.${nameControl}" required="required" style="width: 100%">
				<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
				<c:forEach items="${templates}" var="template">
					<c:if test="${template.type == item.name}">
						<option value="${template.id}" class='${template.outToDate?"text-danger":""}' data-trick-type='${template.type}'>
							<spring:message text="${template.label}" /> - V
							<spring:message text="${template.version}" />
						</option>
					</c:if>
				</c:forEach>
			</select>
		</div>
	</fieldset>

	<fieldset data-trick-info='file'>
		<legend class='text-warning' style="font-size: 15px" data-helper-content='<spring:message code="help.export.report.external" />'>
			<spring:message code="label.external.template" />
		</legend>

		<c:choose>
			<c:when test="${ maxFileSize< 1024}">
				<spring:message code="label.max.unit.data.byte" arguments="${maxFileSize}" var="maxSizeInfo" />
			</c:when>
			<c:when test="${ maxFileSize < 1048576}">
				<spring:message code="label.max.unit.data.kilo.byte" arguments="${maxFileSize / 1024}" var="maxSizeInfo" />
			</c:when>
			<c:otherwise>
				<spring:message code="label.max.unit.data.mega.byte" arguments="${maxFileSize / 1048576}" var="maxSizeInfo" />
			</c:otherwise>
		</c:choose>


		<div class="col-sm-12">
			<div class="input-group-btn">
				<input id="exportWord.file.${nameControl}" type="file" accept=".docx" name="file" style="display: none;" maxlength="${maxFileSize}" /> <input
					id="exportWord.file.info.${nameControl}" name="filename" placeholder="${maxSizeInfo}" class="form-control" readonly="readonly" required="required" style="width: 88%" />
				<button class="btn btn-primary" type="button" id="exportWord.file.browse.button.${nameControl}" name="browse" style="margin-left: -5px;">
					<spring:message code="label.action.browse" text="Browse" />
				</button>
			</div>
		</div>
	</fieldset>

	<button name='submit' type="submit" style="display: none"></button>

</form>
