<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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