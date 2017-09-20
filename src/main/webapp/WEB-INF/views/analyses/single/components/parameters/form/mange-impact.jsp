<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="manageImpactModal" tabindex="-1" role="dialog" data-aria-labelledby="manageImpactModal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true" data-loading-text='&times;'>&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.manage.analysis.impact_scale" text="Manage analysis impact scale" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 5px 20px;">
				<div class='alert alert-sm alert-danger' style="margin-top: 0; margin-bottom: 0"><spring:message code='info.manage.impact.remove'/></div>
				<form id="manage-analysis-impact-scale" action="/Analysis/Prameter/Impact-scale/Manage/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal">
					<div>
						<div>
							<h4 class="col-xs-7 bordered-bottom" style="padding-left: 0">
								<spring:message code='label.title.impact_scale' />
							</h4>
							<h4 class="col-xs-5 bordered-bottom text-center">
								<spring:message code='label.action' text="Action" />
							</h4>
						</div>
					</div>
					<spring:message code="label.action.include" text="Include" var="include" />
					<spring:message code="label.action.exclude" text="Exclude" var="exclude" />
					<div class="form-horizontal" style="height: 500px; overflow-x: hidden; clear: both;">
						<c:set var="defaultValue" value="${impacts.remove(quantitativeImpact)}" />
						<spring:message code='label.analysis.quantitative.impact' var="displayName" />
						<div class="form-group" data-trick-id='${quantitativeImpact.id}' style="margin-bottom: 0">
							<div class="col-xs-7">
								<strong style="vertical-align: middle;"><spring:message text="${displayName}" /></strong>
							</div>
							<div class="col-xs-5 text-center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-sm btn-default ${defaultValue?'active' : ''}">${include}<input ${defaultValue?'checked' : ''} name="${quantitativeImpact.id}" type="radio"
										value="true"></label> <label class="btn btn-sm btn-danger ${not defaultValue?'active' : ''}">${exclude}<input name="${quantitativeImpact.id}"
										${not defaultValue?'checked' : ''} type="radio" value="false"></label>
								</div>
							</div>
							<input name="default-value-${quantitativeImpact.id}" value="${defaultValue}" hidden="true">
						</div>
						<h5 class="bordered-bottom text-muted" style="margin-top: 0">
							<spring:message code="label.analysis.type.qualitative" />
						</h5>
						<c:forEach items="${impacts.keySet()}" var="impact">
							<c:set var="defaultValue" value="${impacts[impact]}" />
							<c:set var="displayName" value="${empty impact.translations[langue]? impact.displayName  :  impact.translations[langue].name}" />
							<div class="form-group" data-trick-id='${impact.id}'>
								<div class="col-xs-7">
									<strong style="vertical-align: middle;"><spring:message text="${displayName}" /></strong>
								</div>
								<div class="col-xs-5 text-center">
									<div class="btn-group" data-toggle="buttons">
										<label class="btn btn-sm btn-default ${defaultValue?'active' : ''}">${include}<input ${defaultValue?'checked' : ''} name="${impact.id}" type="radio" value="true"></label>
										<label class="btn btn-sm btn-danger ${not defaultValue?'active' : ''}">${exclude}<input name="${impact.id}" ${not defaultValue?'checked' : ''} type="radio"
											value="false"></label>
									</div>
								</div>
								<input name="default-value-${impact.id}" value="${defaultValue}" hidden="true">
							</div>
						</c:forEach>

					</div>
				</form>
				<div class='clearfix'></div>
			</div>
			<div class="modal-footer">
				<spring:message code="label.action.save" text="Save" var='textSave' />
				<spring:message code="label.action.cancel" text="Cancel" var='textCancel' />
				<button type="button" class="btn btn-primary" name='save' data-loading-text='${textSave}'>${textSave}</button>
				<button type="button" class="btn btn-default" name='cancel' data-dismiss="modal" data-loading-text='${textCancel}'>${textCancel}</button>
			</div>

		</div>
		<!-- /.modal-content -->
		<div class="hidden">
			<code data-lang-code='error.manage.impact.empty'>
				<spring:message code="error.manage.impact.empty" />
			</code>
			<code data-lang-code='info.manage.impact.remove'>
				<spring:message code="info.manage.impact.remove" />
			</code>
		</div>
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->