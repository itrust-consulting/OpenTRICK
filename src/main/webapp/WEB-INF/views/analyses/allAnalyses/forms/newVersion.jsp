<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addHistoryModal" tabindex="-1" role="dialog" data-aria-labelledby="addhistoryLabel" data-backdrop="static" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="historyModal-title">
					<spring:message code="label.title.add.analysis.version" text="Add new version" />
				</h4>
			</div>
			<div class="modal-body">
				<jsp:include page="../../../template/successErrors.jsp" />
				<form name="history" action="${pageContext.request.contextPath}/History/Analysis/${analysisId}/NewVersion/Save" class="form-horizontal" id="history_form" method="post">
					<div class="form-group">
						<label for="author" class="col-sm-2 control-label"> <spring:message code="label.analysis.author" text="Author" />
						</label>
						<div class="col-sm-10">
							<input name="author" id="history_author" class="form-control" value="<spring:message text="${author}"/>" />
						</div>
					</div>
					<div class="form-group">
						<label for=basedOnVersion class="col-sm-2 control-label"> <spring:message code="label.analysis.based_on" text="Based on version" />
						</label>
						<div class="col-sm-10">
							<input name="oldVersion" id="history_oldVersion" class="form-control" value=<spring:message text="${oldVersion}"/> readonly />
						</div>
					</div>
					<div class="form-group">
						<label for=version class="col-sm-2 control-label"> <spring:message code="label.analysis.version" text="Version" />
						</label>
						<div class="col-sm-10">
							<input name="version" id="history_version" class="form-control" value="" />
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label"> <spring:message code="label.analysis.comment" text="Comment" />
						</label>
						<div class="col-sm-10">
							<textarea name="comment" class="form-control" id="history_comment"></textarea>
						</div>
					</div>
				</form>
				
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="history_submit_button" onclick="return duplicateAnalysis('history_form', '${analysisId}')">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>