<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="credential-modal-form" tabindex="-1" role="dialog" data-aria-labelledby="credential-modal-form" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.${form.customer> 1? 'edit' : 'add' }.credential" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="credential" action="/Account/Credential/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="credential_form">
					<div class="form-group">
						<label for="type" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.credential.type" />'> <spring:message code="label.type" />
						</label>
						<div class="col-sm-9">
							<div id="radio-btn-credential-type" class="btn-group btn-group-justified" data-toggle="buttons">
								<c:forEach items="${types}" var="type" varStatus="status">
									<label class="btn btn-default ${type==form.type?'active':''}"><spring:message code="label.credential.type.${fn:toLowerCase(type)}" /> <input name="type"
										type="radio" value="${type}" ${type==form.type?'checked="checked"':''} required="required"> </label>
								</c:forEach>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="organisation" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.credential.customer" />'> <spring:message
								code="label.customer" />
						</label>
						<div class="col-sm-9">
							<select name="customer" class="form-control" required="required">
								<option value="0" disabled="disabled" ${form.customer == 0?'selected' : ''}><spring:message code="label.action.choose" /></option>
								<c:forEach items="${customers}" var="customer">
									<option value="${customer.id}" ${customer.id==form.customer?'selected' : ''}><spring:message text="${customer.organisation}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="name" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.credential.name" />'> <spring:message code="label.username" />
						</label>
						<div class="col-sm-9">
							<input name="name" class="form-control" type="text" value='<spring:message text="${form.name}"/>' />
						</div>
					</div>
					<div class="form-group">
						<label for="value" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.credential.value" />'> <spring:message code="label.credential.value" />
						</label>
						<div class="col-sm-9">
							<input name="value" class="form-control" type="password" value='<spring:message text="${form.value}"/>' required="required" />
						</div>
					</div>
					<div class="form-group">
						<label for="publicUrl" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.credential.public.url" />'> <spring:message code="label.credential.public.url" />
						</label>
						<div class="col-sm-9">
							<input name="publicUrl" class="form-control" type="url" value='<spring:message text="${form.publicUrl}"/>' />
						</div>
					</div>
					<input type="submit" name="submit" class="hidden" id="btn-crdential-submit">
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
