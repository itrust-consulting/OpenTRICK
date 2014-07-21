<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="deleteAnalysisModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteAnalysis" role="dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="deleteAnalysisModel-title">
					<spring:message code="label.title.delete.analysis" text="Delete an analysis" />
				</h4>
			</div>
			<div class="modal-body">
				<div id="deleteAnalysisBody" class="text-center">Your question here...</div>
				<div class="col-sm-12" style="margin: 20px 0 -10px 0;">
					<div id="deleteprogressbar" class="progress progress-striped active" hidden="true">
						<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button id="deleteanalysisbuttonYes" type="button" class="btn btn-danger">
					<spring:message code="label.yes_no.true" text="Yes" />
				</button>
				<button id="deleteanalysisbuttonNo" type="button" class="btn btn-default">
					<spring:message code="label.yes_no.false" text="No" />
				</button>
			</div>
		</div>
	</div>
</div>