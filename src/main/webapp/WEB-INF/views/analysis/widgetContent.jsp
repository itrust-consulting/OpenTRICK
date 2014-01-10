<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addAnalysisModel" tabindex="-1" role="dialog" aria-labelledby="addNewAnalysis" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addAnalysisModel-title">
					<spring:message code="label.analysis.add.menu" text="Add new Analysis" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="analysis" action="Analysis/Save" class="form-horizontal" id="analysis_form">
					<input type="hidden" name="id" value="-1" id="analysis_id">
					<div class="form-group">
						<label for="identifier" class="col-sm-2 control-label"> <spring:message code="label.analysis.identifier" text="Identifier" />
						</label>
						<div class="col-sm-10">
							<input name="identifier" id="analysis_identifier" class="form-control" type="text" readonly />
						</div>
					</div>
					<div class="form-group">
						<label for="version" class="col-sm-2 control-label"> <spring:message code="label.analysis.version" text="Version" />
						</label>
						<div class="col-sm-10">
							<input name="version" id="analysis_version" class="form-control" type="text" readonly />
						</div>
					</div>
					<div class="form-group">
						<label for="creationDate" class="col-sm-2 control-label"> <spring:message code="label.analysis.creationdate" text="Creation Date" />
						</label>
						<div class="col-sm-10">
							<input name="creationDate" id="analysis_creationDate" class="form-control" type="text" readonly />
						</div>
					</div>
					<div class="form-group">
						<label for="basedOnAnalysis" class="col-sm-2 control-label"> <spring:message code="label.analysis.basedOnAnalysis" text="Based On Analysis Version" />
						</label>
						<div class="col-sm-10">
							<input name="basedOnAnalysis" id="analysis_basedOnAnalysis" class="form-control" type="text" readonly />
						</div>
					</div>
										<div class="form-group">
						<label for="owner" class="col-sm-2 control-label"> <spring:message code="label.analysis.owner" text="Owner" />
						</label>
						<div class="col-sm-10">
							<input name="owner" id="analysis_owner" class="form-control" type="text" readonly />
						</div>
					</div>
					<div class="form-group">
						<label for="empty" class="col-sm-2 control-label"> <spring:message code="label.analysis.empty" text="Empty" />
						</label>
						<div class="col-sm-10">
							<input name="empty" id="analysis_empty" class="form-control" type="text" readonly/>
						</div>
					</div>
					<div class="form-group">
						<label for="customer" class="col-sm-2 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
						</label>
						<div class="col-sm-10" id="analysiscustomercontainer">
							<input name="customer" id="analysis_customer" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="language" class="col-sm-2 control-label"> <spring:message code="label.analysis.language" text="Language" />
						</label>
						<div class="col-sm-10" id="analysislanguagecontainer">
							<input name="language" id="analysis_language" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="label" class="col-sm-2 control-label"> <spring:message code="label.analysis.description" text="Description" />
						</label>
						<div class="col-sm-10">
							<textarea name="label" id="analysis_label" class="form-control">
							</textarea>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addAnalysisButton" type="button" class="btn btn-primary" onclick="saveAnalysis('analysis_form')">
					<spring:message code="label.analysis.add.form" text="Add Analysis" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteAnalysisModel" tabindex="-1" aria-hidden="true" aria-labelledby="deleteAnalysis" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteAnalysisModel-title">
					<spring:message code="title.analysis.delete" text="Delete an analysis" />
				</h4>
			</div>
			<div id="deleteAnalysisBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deleteanalysisbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.answer.yes" text="Yes" />
				</button>
				<button id="deleteanalysisbuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.answer.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<spring:url value="js/analysis.js" />"></script>