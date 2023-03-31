<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="uploadStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="uploadStandard" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="uploadStandard-title">
					<spring:message code="label.title.import.norm" text="Import a new standard" />
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="../../../template/successErrors.jsp" />
				<form name="importStandard" method="post" action="${pageContext.request.contextPath}/KnowledgeBase/Standard/Import?${_csrf.parameterName}=${_csrf.token}" class="form-inline"
					id="uploadStandard_form" enctype="multipart/form-data">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.norm.import.choose_file" text="Choose the file containing new standard to import" /></label>
						<div class="col-lg-12">
							<div class="input-group-btn">
								<input id="file" type="file" accept=".xls,.xlsx,.xlsm"
									onchange='{$("#upload-file-info").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info").val(),"xls,xlsx,xlsm","#btnImportStandard");}'
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
				<div class="col-lg-8" style="color: #d9534f;" align="left" id="updateStandardNotification"></div>
				<div class="col-lg-4">
					<button type="button" class="btn btn-primary" onclick="return importNewStandard()" id="btnImportStandard" disabled="disabled">
						<spring:message code="label.action.import" text="Import" />
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
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
