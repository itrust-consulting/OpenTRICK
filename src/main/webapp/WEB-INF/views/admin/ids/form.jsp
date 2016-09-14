<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="formIDSModal" tabindex="-1" role="dialog" data-aria-labelledby="formIDSModal" data-backdrop="static" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="ids-Modal-title">
					<c:choose>
						<c:when test="${empty ids or ids.id <1}">
							<spring:message code="label.title.add.ids" text="Add new IDS" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.title.edit.ids" text="Edit IDS" />
						</c:otherwise>
					</c:choose>
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="../../template/successErrors.jsp" />
				<form:form commandName="ids" class="form-horizontal" name="ids" method="post">
					<form:hidden path="id"/>
					<div class="form-group">
						<form:label path="prefix" cssClass="col-sm-2 control-label"> <spring:message code="label.name" text="Name" /></form:label>
						<div class="col-sm-10">
							<form:input path="prefix" cssClass="form-control" maxlength="32" size='32' required="true"/>
						</div>
					</div>
					<div class="form-group">
						<form:label path="token" cssClass="col-sm-2 control-label"> <spring:message code="label.ids.token" text="Token" /></form:label>
						<div class="col-sm-10">
							<form:textarea path="token" cssClass="form-control resize_vectical_only" maxlength="512" readonly="true" size="256" rows="2"/>
						</div>
					</div>
					<div class="form-group">
						<form:label path="description" cssClass="col-sm-2 control-label"> <spring:message code="label.description" text="Name" /></form:label>
						<div class="col-sm-10">
							<form:textarea path="description" cssClass="form-control resize_vectical_only" maxlength="255" size='255' rows="4" required="true"/>
						</div>
					</div>
					<div class="form-group">
						<form:label path="enable" cssClass="col-sm-2 control-label"> <spring:message code="label.user.account.status" text="Status" /></form:label>
						<div class="col-sm-10" align="center">
							<form:checkbox path="enable" cssClass="checkbox"/>
						</div>
					</div>
					
					<form:button type="submit" hidden="hidden" id="ids_form_submit_button"/>
				</form:form>
				<div id="ids-error-container"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="ids_submit_button">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>