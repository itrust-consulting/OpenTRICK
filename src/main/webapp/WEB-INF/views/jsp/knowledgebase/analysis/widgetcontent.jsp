<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="editAnalysisModel" tabindex="-1" role="dialog" data-aria-labelledby="editAnalysis" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.edit.info.analysis.profile" text="Edit profile analysis information" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="analysis" action="/update?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="analysis_form"></form>
				<div class="col-sm-12" style="margin: 5px auto -10px auto;">
					<div class="progress progress-striped active" hidden="true">
						<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button id="editAnalysisButton" type="button" class="btn btn-primary" onclick="saveAnalysis('analysis_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteAnalysisModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteAnalysis" role="dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-danger">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteAnalysisModel-title">
					<spring:message code="title.delete.analysis" text="Delete an analysis" />
				</h4>
			</div>
			<div class="modal-body">
				<div id="deleteAnalysisBody"></div>
			</div>
			<div class="col-sm-12" style="margin: 5px auto -10px auto;">
				<div id="deleteprogressbar" class="progress progress-striped active" hidden="true">
					<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button id="deleteanalysisbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.action.confirm.yes" text="Yes" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>