<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="modal-add-notification" tabindex="-1" role="dialog" data-aria-labelledby="modal-add-notification" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<c:choose>
						<c:when test="${form.data.isEmpty()}">
							<spring:message code="label.title.notification.add" text="Add new message" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.title.notification.edit" text="Edit new message" />
						</c:otherwise>
					</c:choose>
				</h4>
			</div>
			<div class="modal-body">
				<form name="notificationForm" action="" class="form-horizontal" id="notification-form">
					<input type="hidden" name="id" value="${form.data.id}"> <input type="hidden" name="code" value="${form.data.code}">
					<div class='form-group'>
						<label class='control-label col-md-4'><spring:message code='label.notification.type' text="Type"/></label>
						<div class="col-md-8">
							<select name="type" class="form-control">
								<c:forEach items="${types}" var="type">
									<option value="${type}" ${type==form.data.type?'selected':''}><spring:message code="label.log.level.${fn:toLowerCase(type)}" text="${fn:toLowerCase(type)}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<c:forEach items="${langues}" var="langue">
						<div class='form-group'>
							<label class='control-label col-md-4' style="text-transform: capitalize;"><spring:message text="${langue.getDisplayLanguage(locale)}" /></label>
							<div class='col-md-8'>
								<textarea class='form-control' lang="${langue.language}" name="messages[${langue.language}]"><spring:message text="${form.data.messages[langue.language]}" /></textarea>
							</div>
						</div>
					</c:forEach>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addcustomerbutton" type="button" name="save" class="btn btn-primary">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>