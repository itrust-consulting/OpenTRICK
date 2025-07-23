<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="analysisProfileModal" tabindex="-1" role="dialog" data-aria-labelledby="newAnalysisProfile" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog" style="width: 705px;">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="createAnalysisProfile-title">
					<spring:message code="label.title.add.analysis.profile" text="Create new profile" />
				</h4>
			</div>
			<div class="modal-body">
				<form id="analysisProfileform" action="/AnalysisProfile/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal">
					<input type="hidden" id="id" name="id" value="${id}" />
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label" style="text-align: left;" data-helper-content='<spring:message code="help.analysis.profile.name" />' > <spring:message code="label.name" text="Name" /></label>
						<div class="col-sm-10">
							<input class="form-control" id="name" name="name">
						</div>
					</div>
					<div>
						<div>
							<h5 class="col-xs-7 bordered-bottom" style="padding-left: 0">
								<spring:message code='label.title.norm' />
							</h5>
							<h5 class="col-xs-5 bordered-bottom text-center">
								<spring:message code='label.action' text="Action" />
							</h5>
						</div>
					</div>
					<spring:message code="label.action.include" text="Include" var="include" />
					<spring:message code="label.action.exclude" text="Exclude" var="exclude" />
					<div class="form-horizontal" style="height: 450px; overflow-x: hidden; clear: both;">
						<c:forEach items="${analysisStandards}" var="analysisStandard">
							<c:set var='name' value="user_${analysisStandard.standard.id}" />
							<div class="form-group" data-trick-id="${analysisStandard.standard.id}" data-name='${name}'>
								<div class="col-xs-7">
									<strong style="vertical-align: middle; text-transform: capitalize;"><spring:message
											text="${fn:toLowerCase(analysisStandard.standard.label)} - ${fn:toLowerCase(analysisStandard.standard.version)}" /></strong>
									<p><spring:message text='${analysisStandard.standard.description}'/></p>
								</div>
								<div class="col-xs-5 text-center">
									<div class="btn-group" data-toggle="buttons">
										<label class="btn btn-sm btn-default active">${include}<input checked name="${name}" type="radio" value="true"></label> <label
											class="btn btn-sm btn-default">${exclude}<input  name="${name}" type="radio" value="false"></label>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</form>
				<div class='clearfix'></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->