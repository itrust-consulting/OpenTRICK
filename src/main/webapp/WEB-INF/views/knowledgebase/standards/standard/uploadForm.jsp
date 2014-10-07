<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="uploadStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="uploadStandard" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="uploadStandard-title">
					<spring:message code="label.title.import.norm" text="Import of a new standard" />
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="../../../successErrors.jsp" />
				<form name="importStandard" method="post" action="${pageContext.request.contextPath}/KnowledgeBase/Standard/Import" class="form-inline" id="uploadStandard_form"
					enctype="multipart/form-data">
					<div class="row">
						<label class="col-lg-12" for="name"> <spring:message code="label.norm.import.choose_file" text="Choose the file containing new standard to import" /></label>
						<div class="col-lg-10">
							<div class="input-group-btn">
								<input id="file" type="file" onchange="return onSelectFile(this)" name="file" style="display: none;" /> <input id="upload-file-info" class="form-control"
									readonly="readonly" />
								<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file]').click();" style="margin-left: -5px;">
									<spring:message code="label.action.browse" text="Browse" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="importNewStandard()">
					<spring:message code="label.action.import.norm" text="Import" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->