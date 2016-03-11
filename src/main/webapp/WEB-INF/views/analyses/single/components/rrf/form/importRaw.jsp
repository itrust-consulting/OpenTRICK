<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="import_raw_rrf_modal" tabindex="-1" role="dialog" data-aria-labelledby="import_raw_rrf" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="title_raw_rrf">
					<spring:message code="label.title.import.raw.rrf" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="importRawRRF" method="post" action="${pageContext.request.contextPath}/Analysis/RRF/Import/Raw/${idAnalysis}" class="form-inline" id="raw_rrf_form"
					enctype="multipart/form-data">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.raw.rrf.choose.import.file" /></label>
						<div class="col-lg-12">
							<div class="input-group-btn">
								<input id="file" type="file" accept=".xls,.xlsx" onchange='{$("#upload-file-info").prop("value", $(this).prop("value")); checkExtention($("#upload-file-info").val(),"xls,xlsx","#raw_rrf_import_button");}' name="file" style="display: none;" /> <input id="upload-file-info" class="form-control" readonly="readonly" required="required" style="width: 88%;" />
								<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file]').click();" style="margin-left: -5px;">
									<spring:message code="label.action.browse" text="Browse" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="return importDataRawRRF(${idAnalysis});" disabled="disabled" id="raw_rrf_import_button">
					<spring:message code="label.action.import" text="Import" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
	<script type="text/javascript">
		
	</script>
</div>
<!-- /.modal -->