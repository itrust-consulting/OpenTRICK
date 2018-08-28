<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${empty item.viewURL}">
	<fieldset>
		<spring:message text="${fn:replace(item.name,'-','_')}" var="viewName" />
		<legend>
			<spring:message code="label.title.data_manager.import.${viewName}" />
		</legend>
		<div class='alert alert-sm alert-danger' style="margin-bottom: 15px">
			<spring:message code="info.data_manager.import.${viewName}" />
		</div>
		<form name="${item.name}" method="post" action="${pageContext.request.contextPath}${item.processURL}?${_csrf.parameterName}=${_csrf.token}" class="form-inline"
			id="form-${item.name}" enctype="multipart/form-data">
			<div class="row">
				<label class="col-lg-12" for="name"> <spring:message code="label.import.${viewName}.choose_file" /></label>
				<div class="col-lg-12">
					<div class="input-group-btn">
						<spring:message text="${fn:replace(item.extensions,'.','')}" var="extension" />
						<input id="file-${item.name}" type="file" accept="${item.extensions}" maxlength="${maxFileSize}"
							onchange='{$("#upload-file-info-${item.name}").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info-${item.name}").val(),"${extension}","#btn-import");}'
							name="file" style="display: none;" /> <input id="upload-file-info-${item.name}" class="form-control" readonly="readonly" required="required" style="width: 88%;"
							placeholder="${maxSizeInfo}" />
						<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file-${item.name}]').click();" style="margin-left: -5px;">
							<spring:message code="label.action.browse" text="Browse" />
						</button>
					</div>
				</div>
			</div>
		</form>
		<div class="col-lg-12" style="color: #d9534f;" align="left" data-view-notification="${item.name}"></div>
	</fieldset>
</c:if>