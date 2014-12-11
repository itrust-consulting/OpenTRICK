<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="editAnalysisModel" tabindex="-1" role="dialog" data-aria-labelledby="editAnalysis" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.menu.edit.analysis" text="Edit Analysis" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="analysis" action="/update" class="form-horizontal" id="analysis_form"></form>
				<div class="col-sm-12" style="margin: 5px auto -10px auto;">
					<div class="progress progress-striped active" hidden="true">
						<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button id="editAnalysisButton" type="button" class="btn btn-primary" onclick="saveAnalysis('analysis_form')">
					<spring:message code="label.action.edit" text="Edit" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteAnalysisModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteAnalysis" role="dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
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
			</div>
		</div>
	</div>
</div>