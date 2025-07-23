<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="analysisSettingModal" tabindex="-1" role="dialog" data-aria-labelledby="analysisSettingModal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog" style="width: 705px;">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.manage.analysis.settings" text="Manage analysis settings" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 5px 20px;">
				<form id="manage-analysis-setting" action="/Analysis/Manage-settings/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal">
					<div>
						<div>
							<h4 class="col-xs-7 bordered-bottom" style="padding-left: 0">
								<spring:message code='label.title.analysis.setting' />
							</h4>
							<h4 class="col-xs-5 bordered-bottom text-center">
								<spring:message code='label.action' text="Action" />
							</h4>
						</div>
					</div>
					<spring:message code="label.yes_no.yes" var="yes" />
					<spring:message code="label.yes_no.no" var="no" />
					<div class="form-horizontal" style="height: 500px; overflow-x: hidden; clear: both;">
						<c:forEach items="${settings.keySet()}" var="setting">
							<c:set var='name' value="${setting.name()}" />
							<c:set var="defaultValue" value="${settings[setting]}" />
							<div class="form-group" data-trick-name='${name}'>
								<div class="col-xs-7">
									<strong style="vertical-align: middle;"><spring:message code="${setting.code}" /></strong>
								</div>
								<div class="col-xs-5 text-center">
									<c:choose>
										<c:when test="${setting.type.simpleName=='Boolean'}">
											<div class="btn-group" data-toggle="buttons">
												<label class="btn btn-sm btn-default ${defaultValue?'active' : ''}">${yes}<input ${defaultValue?'checked' : ''} name="${name}" type="radio" value="true"></label>
												<label class="btn btn-sm btn-default ${not defaultValue?'active' : ''}">${no}<input name="${name}" ${not defaultValue?'checked' : ''} type="radio" value="false"></label>
											</div>
										</c:when>
										<c:otherwise>
											<input name="${name}" value="${defaultValue}" class="form-control">
										</c:otherwise>
									</c:choose>
								</div>
								<input name="${name}-default-value" value="${defaultValue}" hidden="true">
							</div>
						</c:forEach>
					</div>
				</form>
				<div class='clearfix'></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" name='save'>
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