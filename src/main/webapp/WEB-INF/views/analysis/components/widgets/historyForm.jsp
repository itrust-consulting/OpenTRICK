<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addHistoryModal" tabindex="-1" role="dialog"
	aria-labelledby="addhistoryLabel" data-backdrop="static"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="historyModal-title">
					<spring:message code="label.history.add" text="Add new version" />
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="./../../../successErrors.jsp" />
				<spring:hasBindErrors name="history">
					<div class="alert alert-danger">
						<spring:bind path="history.*" ignoreNestedPath="true"
							htmlEscape="true">
							<c:forEach items="${status.errorMessages}" var="error">
								<c:out value="${error}" />
								<br>
							</c:forEach>
						</spring:bind>
					</div>
				</spring:hasBindErrors>
				<form name="history"
					action="${pageContext.request.contextPath}/History/Analysis/${analysisId}/NewVersion/Save"
					class="form-horizontal" id="history_form" method="post">
					<div class="form-group">
						<label for="author" class="col-sm-2 control-label"> <spring:message
								code="label.history.author" text="Author" />
						</label>
						<div class="col-sm-10">
							<input name="author" id="history_author" class="form-control"
								value="${empty(history)? '':history.author}" />
						</div>
					</div>
					<div class="form-group">
						<label for=basedOnVersion class="col-sm-2 control-label">
							<spring:message code="label.history.basedOn"
								text="Based on Version" />
						</label>
						<div class="col-sm-10">
							<input class="form-control" value="${oldVersion}" readonly />
						</div>
					</div>
					<div class="form-group">
						<label for=version class="col-sm-2 control-label"> <spring:message
								code="label.history.version" text="Version" />
						</label>
						<div class="col-sm-10">
							<input name="version" id="history_version" class="form-control"
								value="${empty(history)? '':history.version}" />
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label"> <spring:message
								code="label.history.comment" text="Comment" />
						</label>
						<div class="col-sm-10">
							<textarea name="comment" class="form-control"
								id="history_comment">${empty(history)? '': history.comment}</textarea>
						</div>
					</div>
				</form>
				<div class="progress progress-striped active" hidden="true">
					<div class="progress-bar" role="progressbar" aria-valuenow="100"
						aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary"
					onclick="return duplicateAnalysis('history_form', '${analysisId}')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->