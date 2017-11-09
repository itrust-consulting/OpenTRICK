<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="risk-information-modal" tabindex="-1" role="dialog" data-aria-labelledby="risk-information-modal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.modal.import.risk.information" text="Import brainstorming" />
				</h4>
			</div>
			<div class="modal-body">
				<div class='alert alert-sm alert-danger' style="margin-bottom: 10px"><spring:message code="info.import.risk.information"/></div>
				<form name="importRiskInformation" method="post" action="${pageContext.request.contextPath}/Analysis/RiskInformation/Import?${_csrf.parameterName}=${_csrf.token}" class="form-inline"
					id="importRiskInformationForm" enctype="multipart/form-data">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.import.risk.information.choose_file" text="Choose the file containing brainstorming to import" /></label>
						<div class="col-lg-12">
							<div class="input-group-btn">
								<input id="file" type="file" accept=".xls,.xlsx"
									onchange='{$("#upload-file-info").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info").val(),"xls,xlsx","#btnImportRiskInformation");}'
									name="file" style="display: none;" /> <input id="upload-file-info" class="form-control" readonly="readonly" required="required" style="width: 88%;"/>
								<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file]').click();" style="margin-left: -5px;">
									<spring:message code="label.action.browse" text="Browse" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<div class="col-lg-8" style="color: #d9534f;" align="left" id="riskInfromationNotification"></div>
				<div class="col-lg-4">
					<button type="button" name="import" class="btn btn-primary" id="btnImportRiskInformation" disabled="disabled">
						<spring:message code="label.action.import" text="Import" />
					</button>
					<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.cancel" text="Cancel" />
					</button>
				</div>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
