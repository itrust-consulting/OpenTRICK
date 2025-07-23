<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="ticketingSystemEmailTempalteModel" tabindex="-1" role="dialog" data-aria-labelledby="ticketingSystemEmailTempalteModel" data-aria-hidden="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header bg-primary">
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
								<input id="email_template_title_entry" list="dataListEmailSubject" class="form-control" data-value="<spring:message text="${form.title}" />"  required/>
								<input id="email_template_title" name="title" class="form-control hidden" value='<spring:message text="${form.title}" />' required/>
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
							<label for="format" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.email.template.format" />'> <spring:message code="label.email.template.format" text="Format" />
							</label>
							<div class="col-sm-9" align="center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-default ${ form.format eq "HTML" ? 'active' : ''}"><spring:message code="label.email.template.format.html" /><input
										${ form.format eq 'HTML' ? 'checked' : ''} name="format" type="radio" value="HTML"></label> <label
										class="btn btn-default ${empty(form) or form.format eq 'TEXT' ? 'active' :  ''} "><spring:message code="label.email.template.format.text" /><input
										${empty(form) or form.format eq 'HTML' ? 'checked' : ''} name="format" type="radio" value="TEXT"></label>
									<label class="btn btn-default ${ form.format eq 'JSON' ? 'active' : ''}"><spring:message code="label.email.template.format.json" /><input 
										${ form.format eq 'JSON' ? 'checked' : ''} name="format" type="radio" value="JSON"></label>
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

						<datalist id="dataListEmailSubject" hidden="hidden">
							<option data-value="default"><spring:message code="label.standard" />: <spring:message code="label.measure.domain" /></option>
							<option data-value="domain" ${form.title eq 'domain' ? 'selected' : ''}><spring:message code="label.measure.domain" /></option>
							<option data-value="todo" ${form.title eq 'todo' ? 'selected' : ''}><spring:message code="label.measure.todo" /></option>
							<option data-value="tocheck" ${form.title eq 'tocheck' ? 'selected' : ''}><spring:message code="label.measure.tocheck" /></option>
						</datalist>
						<div id="measureAvailableParameters" class="hidden">[
							{"name": "Std", "title": "<spring:message code='label.standard' />"},
							{"name":"Ref", "title": "<spring:message code='label.reference' />"},
							{"name":"SecMeasure", "title": "<spring:message code='label.measure.domain' />"},
							{"name":"Desc", "title": "<spring:message code='label.measure.description'/>"},
							{"name":"ST", "title": "<spring:message code='label.title.measure.status' />"},
							{"name":"IR", "title": "<spring:message code='label.title.measure.ir' />"},
							{"name":"Owner", "title": "<spring:message code='label.title.measure.responsible' />"},
							{"name":"IW", "title": "<spring:message code='label.title.measure.iw' />"},
							{"name":"EW", "title": "<spring:message code='label.title.measure.ew' />"},
							{"name":"INV", "title": "<spring:message code='label.title.measure.inv' />"},
							{"name":"LT", "title": "<spring:message code='label.title.measure.lt' />"},
							{"name":"IM", "title": "<spring:message code='label.title.measure.im' />"},
							{"name":"EM", "title": "<spring:message code='label.title.measure.em' />"},
							{"name":"RM", "title": "<spring:message code='label.title.measure.ri'/>'"},
							{"name":"CS", "title": "<spring:message code='label.title.measure.cost' />"},
							{"name":"PH", "title": "<spring:message code='label.title.measure.phase' />"},
							{"name":"PHB", "title": "<spring:message code='label.title.measure.phase.begin' />"},
							{"name":"PHE", "title": "<spring:message code='label.title.measure.phase.end' />"},
							{"name":"Imp", "title": "<spring:message code='label.title.measure.importance' />"},
							{"name":"ToDo", "title": "<spring:message code='label.measure.todo' />"},
							{"name":"Comment", "title": "<spring:message code='label.comment' />"},
							{"name":"ToCheck", "title": "<spring:message code='label.measure.tocheck' />"}
						]</div>
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
