<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="uploadNormModal" tabindex="-1" role="dialog" data-aria-labelledby="uploadNorm" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="uploadNorm-title">
					<spring:message code="label.norm.import" text="Import of a new norm" />
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="../../../successErrors.jsp" />
				<form name="importNorm" method="post" action="${pageContext.request.contextPath}/KnowledgeBase/Norm/Import" class="form-inline" id="uploadNorm_form"
					enctype="multipart/form-data">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.norm.import" text="Choose the file containing new norm to import" /></label>
						<div class="col-lg-10">
							<div class="input-group-btn">
								<input id="file" type="file" onchange="return onSelectFile(this)" name="file" style="display: none;" /> <input id="upload-file-info" class="form-control"
									readonly="readonly" />
								<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file]').click();" style="margin-left: -5px;">
									<spring:message code="label.upload.file.browse" text="Browse" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="importNewNorm()">
					<spring:message code="label.action.norm.import" text="Import" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->