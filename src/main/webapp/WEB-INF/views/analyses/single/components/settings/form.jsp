<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="analysisSettingModal" tabindex="-1" role="dialog" data-aria-labelledby="analysisSettingModal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.manage.analysis.settings" text="Manage analysis settings" />
				</h4>
			</div>
			<div class="modal-body">
				<form name='analysis-setting-form' action="/Analysis/Manage-setting/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form">
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label"> <spring:message code="label.parameter"/></label>
						<div class="panel-body col-sm-10">
							<c:if test="${!empty analysisStandards}">
								<select class="form-control" name="standards" id="standards" multiple="multiple" style="display: none;">
									<c:forEach items="${analysisStandards}" var="analysisStandard">
										<option value="standard_${analysisStandard.standard.id}">${analysisStandard.standard.label}</option>
									</c:forEach>
								</select>
								<ul class="list-group">
									<c:forEach items="${analysisStandards}" var="analysisStandard">
										<a data-trick-opt="standard_${analysisStandard.standard.id}" class="list-group-item active" style="border: 1px solid #dddddd;">${analysisStandard.standard.label}</a>
									</c:forEach>
								</ul>
							</c:if>
						</div>
					</div>
				</form>
				<div class='clearfix'></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveAnalysisProfile('analysisProfileform')">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->