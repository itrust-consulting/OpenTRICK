<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
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
<div data-view-content-name='${item.name}' data-view-token='${item.token}'>
	<fieldset>
		<legend>
			<spring:message code="label.title.data_manager.export.ilr" />
		</legend>
		<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
			<spring:message code="info.data_manager.export.ilr" />
		</div>
		<form action='<c:url value="${item.processURL}?token=${item.token}&${_csrf.parameterName}=${_csrf.token}" />' class="form form-inline" method="post" enctype="multipart/form-data">
			<div class="form-group" style="width: 88%">
				<label data-helper-content='<spring:message code="help.export.measure.ilr.json" />'><spring:message code="label.ilr.export.json" /></label> 
				<div class="col-sm-12">
					<div class="input-group-btn">
						<input id="ilr-json-file" type="file" accept=".json" name="ilrData" style="display: none;" maxlength="${maxFileSize}" /> <input
							id="ilr-json-file-info" name="filename" placeholder="${maxSizeInfo}" class="form-control" readonly="readonly" required="required" style="width: 90%" />
						<button class="btn btn-primary" type="button" id="ilr-json-file-browse-button" name="browse" style="margin-left: -5px;">
							<spring:message code="label.action.browse" text="Browse" />
						</button>
					</div>
				</div>
			</div>

			<div class="form-group" style="width: 88%; margin-top:10px">
				<label data-helper-content='<spring:message code="help.export.measure.ilr.mapping" />'><spring:message code="label.ilr.export.mapping" /></label> 
				<div class="col-sm-12">
					<div class="input-group-btn">
						<input id="mapping-cvs-file" type="file" accept=".csv" name="mappingFile" style="display: none;" maxlength="${maxFileSize}" /> <input
							id="mapping-cvs-file-info" name="filename" placeholder="${maxSizeInfo}" class="form-control" readonly="readonly" required="required" style="width: 90%" />
						<button class="btn btn-primary" type="button" id="mapping-cvs-file-browse-button" name="browse" style="margin-left: -5px;">
							<spring:message code="label.action.browse" text="Browse" />
						</button>
					</div>
				</div>
			</div>
			<!--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
			<button type="submit" class='hidden' name="${item.name}-submit"></button>
		</form>
	</fieldset>
</div>