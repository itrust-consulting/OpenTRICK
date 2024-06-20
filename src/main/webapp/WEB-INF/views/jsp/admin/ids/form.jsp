<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
			<spring:message code="label.action.enable" text="Enable" var="enable" />
			<spring:message code="label.action.disable" text="Disable" var="disable" />
			<div class="modal-body">
				<jsp:include page="../../template/successErrors.jsp" />
				<form:form modelAttribute="ids" class="form-horizontal" name="ids" method="post">
					<form:hidden path="id" />

					<div class="form-group">
						<label for="prefix" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.id.prefix"/>'> <spring:message code="label.name" text="Name" /></label>
						<div class="col-sm-9">
							<form:input path="prefix" cssClass="form-control" maxlength="32" size='32' required="true" />
						</div>
					</div>
					<div class="form-group">
						<label for="token" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.id.token"/>'> <spring:message code="label.ids.token" text="Token" /></label>
						<div class="col-sm-9">
							<form:textarea path="token" cssClass="form-control resize_vectical_only" maxlength="512" readonly="true" size="256" rows="2" />
						</div>
					</div>
					<div class="form-group">
						<label for="description" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.id.description"/>'> <spring:message code="label.description"
								text="Name" /></label>
						<div class="col-sm-9">
							<form:textarea path="description" cssClass="form-control resize_vectical_only" maxlength="255" size='255' rows="4" required="true" />
						</div>
					</div>
					<div class='form-group' data-index='0'>
						<label for="enable" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.id.status"/>'> <spring:message code="label.ids.status"
								text="Status" /></label>
						<div class='col-sm-9 text-center'>
							<div class="btn-group" data-toggle="buttons">
								<label class="btn btn-default ${ids.enable?'active':''}">${enable}<input ${ids.enable?'checked':''} name="enable" type="radio" value="true"></label><label
									class="btn btn-default ${ids.enable?'':'active'}">${disable}<input ${ids.enable?'':'checked'} name="enable" type="radio" value="false"></label>
							</div>
						</div>
					</div>
					<form:button type="submit" hidden="hidden" id="ids_form_submit_button" />
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