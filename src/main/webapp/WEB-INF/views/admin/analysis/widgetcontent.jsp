<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widget">
	<div class="modal fade" id="addAnalysisModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewAnalysis" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="addAnalysisModel-title">
						<spring:message code="label.analysis.add.menu" text="Add new Analysis" />
					</h4>
				</div>
				<div class="modal-body">
					<form name="analysis" action="Analysis/Save" class="form-horizontal" id="analysis_form"></form>
					<div class="progress progress-striped active" hidden="true">
						<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addAnalysisButton" type="button" class="btn btn-primary" onclick="saveAnalysis('analysis_form', 'section_admin_analysis')">
						<spring:message code="label.analysis.add.form" text="Add Analysis" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="manageAnalysisAccessModel" tabindex="-1" role="dialog" data-aria-labelledby="manageAnalysisAccessModel" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="manageAnalysisAccessModel-title">
						<spring:message code="label.analysis.manage.access" text="Manage Analysis Access Rights" />
					</h4>
				</div>
				<div id="manageAnalysisAccessModelBody" class="modal-body"></div>
				<div class="modal-footer">
					<button id="manageAnalysisAccessModelButton" type="button" class="btn btn-primary" onclick="updatemanageAnalysisAccess('userrightsform')">
						<spring:message code="label.analysis.update" text="Update" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>