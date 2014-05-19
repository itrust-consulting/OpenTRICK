<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="addStandardForm" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<spring:message code="label.analysis.add.standard" text="Add a standard" />
				</h4>
			</div>
			<div class="modal-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.norm.label" text="Name" /></th>
							<th><spring:message code="label.norm.version" text="Version" /></th>
							<th><spring:message code="label.norm.description" text="Description" /></th>
							<th><spring:message code="label.norm.computable" text="Computable" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${currentNorms}" var="norm">
							<tr>
								<td><spring:message code="label.standard.${norm.label}" text="${norm.label}" /></td>
								<td><spring:message text="${norm.version}" /></td>
								<td><spring:message text="${norm.description}" /></td>
								<td><spring:message code="label.yes_no.${norm.computable}" text="${norm.computable? 'Yes' : 'No'}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<hr />
				<form name="standard" action="${pageContext.request.contextPath}/Analysis/Save/Standard" class="form" id="addStandardForm">
					<div class="form-group">
						<label> <spring:message code="label.analysis.add.standard.select.choose" text="Select your standard" />
						</label> <select name="idNorm" class="form-control" onchange="$('#selectedStandardDescription').html($('#addStandardForm select option:selected').attr('title'));" >
							<c:forEach items="${norms}" var="norm">
								<option title='<spring:message text="${norm.description}"/>' value="${norm.id}">
									<spring:message code="label.standard.${norm.label}" text="${norm.label}" /> -
									<spring:message text="${norm.version}" />
								</option>
							</c:forEach>
						</select>
					</div>
				</form>
				<p id="selectedStandardDescription"> <spring:message text="${norms.get(0).description}"/></p>
				<div id="add_standard_progressbar" class="progress progress-striped active" hidden="true">
					<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveStandard('addStandardForm')" id="btn_save_standard">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->