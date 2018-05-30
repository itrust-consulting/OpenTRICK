<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="import-measure-data-modal" tabindex="-1" role="dialog" data-aria-labelledby="import-measure-data-modal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.modal.import.measure.data" text="Import measures data" />
				</h4>
			</div>
			<div class="modal-body">
				<div class='alert alert-sm alert-danger' style="margin-bottom: 10px"><spring:message code="info.import.measure.data"/></div>
				<form name="importMeasureData" method="post" action="${pageContext.request.contextPath}/Analysis/Standard/${idStandard}/Import/Measure/?${_csrf.parameterName}=${_csrf.token}" class="form-inline"
					id="importMeasureDataForm" enctype="multipart/form-data">
					<input type="hidden" value="${idStandard}" name="idStandard">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.import.measure.data.choose_file" text="Choose the file containing measures data to import" /></label>
						<div class="col-lg-12">
							<div class="input-group-btn">
								<input id="file-measure-data" type="file" accept=".xls,.xlsx,.xlsm"
									onchange='{$("#upload-file-info-measure").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info-measure").val(),"xls,xlsx,xlsm","#btnImportMeasureData");}'
									name="file" style="display: none;" /> <input id="upload-file-info-measure" class="form-control" readonly="readonly" required="required" style="width: 88%;"/>
								<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file-measure-data]').click();" style="margin-left: -5px;">
									<spring:message code="label.action.browse" text="Browse" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<div class="col-lg-8" style="color: #d9534f;" align="left" id="riskEstimationDataNotification"></div>
				<div class="col-lg-4">
					<button type="button" name="import" class="btn btn-primary" id="btnImportRiskEstimation" disabled="disabled">
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
