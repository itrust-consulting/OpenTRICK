<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="addCustomerModel" tabindex="-1"
	role="dialog" data-aria-labelledby="addNewCustomer"
	data-aria-hidden="true">
	<div
		class="modal-dialog customer-modal ${adminaAllowedTicketing? 'modal-mdl' : '' }">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addCustomerModel-title">
					<spring:message code="label.customer.add.menu"
						text="Add new customer" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="credential"
					action="/Account/Credential/Save?${_csrf.parameterName}=${_csrf.token}"
					class="form-horizontal" id="credential_form">

					<div class="form-group">
						<label for="type" class="col-sm-3 control-label"
							data-helper-content='<spring:message code="help.credential.type" />'>
							<spring:message code="label.credential.type" />
						</label>
						<div class="col-sm-9">
							<div class="btn-group btn-group-justified" data-toggle="buttons">
								<c:forEach items="${types}" var="type"
									varStatus="status">
									<label class="btn btn-default ${status.index==0?'active':''}"><spring:message
											code="label.credential.type.${fn:toLowerCase(type)}" />
										<input name="type" type="radio"
										value="${type}" ${status.index==0?'checked="checked"':''}>
									</label>
								</c:forEach>
							</div>
						</div>
					</div>

					<div class="form-group">
						<label for="organisation" class="col-sm-3 control-label"
							data-helper-content='<spring:message code="help.credential.customer" />'>
							<spring:message code="label.customer"/>
						</label>
						<div class="col-sm-9">
						</div>
					</div>
					<div class="form-group">
						<label for="name" class="col-sm-3 control-label"
							data-helper-content='<spring:message code="help.credential.name" />'>
							<spring:message code="label.credential.name" />
						</label>
						<div class="col-sm-9">
							<input name="name" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="value" class="col-sm-3 control-label"
							data-helper-content='<spring:message code="help.credential.value" />'>
							<spring:message code="label.credential.value"
								/>
						</label>
						<div class="col-sm-9">
							<input name="value"
								class="form-control" type="password" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" name="cancel" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
