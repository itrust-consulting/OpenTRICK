<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="ticketingSystemEmailTempalteModel" tabindex="-1" role="dialog" data-aria-labelledby="ticketingSystemEmailTempalteModel" data-aria-hidden="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<c:choose>
						<c:when test="${form.id>0}">
							<spring:message code="label.title.edit.email.template" text="Edit email template" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.title.add.email.template" text="Add new email template" />
						</c:otherwise>
					</c:choose>
				</h4> 
			</div>
			<div class="modal-body">
				<span id="success" hidden="hidden"></span>
				<form name="form" action="/Admin/Customer/${customerID}/Tickecting-system/Email-template/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="email_template_form">
					<input type="hidden" name="id" value="${form.id}" id="email_template_id" ${form.id > 0 ? 'readonly':''}>
						
						<div class="form-group">
							<label for="email" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.email" />'> <spring:message
									code="label.email.template.email" text="Email" />
							</label>
							<div class="col-sm-9">
								<input id="email_template_email" name="email" class="form-control" type="email" value='<spring:message text="${form.email}" />' required />
							</div>
						</div>
						
						<div class="form-group">
							<label for="title" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.title" />'> <spring:message code="label.email.template.title"
									text="Subject" />
							</label>
							<div class="col-sm-9">
								<select id="email_template_title" name="title" class="form-control"required="required">
									<option value="default" ${empty form.title or form.title eq 'default' ? 'selected' : ''}><spring:message code="label.standard" />: <spring:message code="label.measure.domain" /></option>
									<option value="domain" ${form.title eq 'domain' ? 'selected' : ''}><spring:message code="label.measure.domain" /></option>
									<option value="todo" ${form.title eq 'todo' ? 'selected' : ''}><spring:message code="label.measure.todo" /></option>
									<option value="tocheck" ${form.title eq 'tocheck' ? 'selected' : ''}><spring:message code="label.measure.tocheck" /></option>
								</select>
							</div>
						</div>

						<div class="form-group">
							<label for="internalTime" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.internal_time" />'> <spring:message
									code="label.email.template.internal_time" text="Internal time between two 2 emails (ms)" />
							</label>
							<div class="col-sm-9">
								<input id="email_template_internal_time" name="internalTime" class="form-control" value='<spring:message text="${form.internalTime}" />' type="number" min="0" step="10" max="1000000" required />
							</div>
						</div>
						<div class="form-group">
							<label for="html" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.html" />'> <spring:message code="label.email.template.html" text="Html" />
							</label>
							<div class="col-sm-9" align="center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-default ${empty(form) or form.html ? 'active' : ''}"><spring:message code="label.yes_no.yes" /><input
										${empty(form) or form.html ? 'checked' : ''} name="html" type="radio" value="true"></label> <label
										class="btn btn-default ${empty(form) or form.html ? '' : 'active'} "><spring:message code="label.yes_no.no" /><input
										${empty(form) or form.html ? '' : 'checked'} name="html" type="radio" value="false"></label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="template" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.template" />'> <spring:message code="label.email.template.template.template" text="Template" />
							</label>
							<div class="col-sm-9" align="center">
								<textarea id="email_template_template" size="10000" style="overflow-y: auto; " rows="17" name="template" class="form-control" required><spring:message text="${form.template}"/></textarea> 
							</div>
						</div>

						<input name="submit" type="submit" hidden="hidden" /> 


				</form>
			</div>
			<div class="modal-footer">
				<button name="save" type="button" class="btn btn-primary">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
