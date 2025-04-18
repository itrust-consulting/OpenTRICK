<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="phase-modal-form" tabindex="-1" role="dialog" data-aria-labelledby="phase-modal-form" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<div class="modal-title">
					<h4 id="phaseNewModal-title" class="col-md-6">
						<spring:message code="label.title.phase.add_edit">
							<spring:argument value="${form.id}"/>
							<spring:argument value="${form.number}"/>
						</spring:message>
					</h4>
				</div>
			</div>
			<div class="modal-body">
				<form name="phase" action="${pageContext.request.contextPath}/Phase/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="phaseForm">
					<input name="id" id="phase.id" value=" ${form.id} " type="hidden" />
					<div class="form-group">
						<label for="date" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.asset.period" />'> <spring:message code="label.phase_period" />
						</label>
						<div class="col-sm-9 phase-date-range" align="center">
							<input name="begin" type="date" class="form-control" style="width: 45%;display: inline-block; vertical-align: middle;"
								value="<fmt:formatDate value="${form.begin}" pattern="yyyy-MM-dd"/>" placeholder='<spring:message code="label.phase.date.pattern"/>'
								${form.beginEnabled? 'required' : 'disabled readonly' } /> <span class="add-on" style="height: 31px; width: 10%"> <spring:message code="label.date.to" />
							</span> <input name="end" type="date" class="form-control" style="width: 45%; display: inline-block;vertical-align: middle;"
								value="<fmt:formatDate value="${form.end}" pattern="yyyy-MM-dd"/>" placeholder='<spring:message code="label.phase.date.pattern"/>'
								${form.endEnabled? 'required' : 'disabled readonly'} />
						</div>
					</div>
					<button name="submit" type="submit" style="display: none"></button>
				</form>

			</div>
			<div class="modal-footer">

				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" />
				</button>

				<button type="button" class="btn btn-default" name="cancel" data-dismiss="modal">
					<spring:message code="label.action.cancel" />
				</button>

			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->