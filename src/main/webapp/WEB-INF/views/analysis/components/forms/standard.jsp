<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="addStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="addStandardForm" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<spring:message code="label.title.analysis.manage.standard" text="Manage standards" />
				</h4>
			</div>
			<div class="modal-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.norm.label" text="Name" /></th>
							<th><spring:message code="label.norm.version" text="Version" /></th>
							<th colspan="3"><spring:message code="label.norm.description" text="Description" /></th>
							<th colspan="2"><spring:message code="label.norm.computable" text="Computable" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${currentNorms}" var="norm">
							<tr>
								<td><spring:message code="label.standard.${norm.label}" text="${norm.label}" /></td>
								<td><spring:message text="${norm.version}" /></td>
								<td colspan="3"><spring:message text="${norm.description}" /></td>
								<td><spring:message code="label.yes_no.${fn:toLowerCase(norm.computable)}" text="${norm.computable? 'Yes' : 'No'}" /></td>
								<td><a href="#" role="remove-standard" trick-class="norm" trick-id="${norm.id}" style="font-size:20px" class="text-danger" title='<spring:message code="label.action.delete" text="Delete" />'> <span class="glyphicon glyphicon-remove-circle"></span></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<hr />
				<c:if test="${!empty(norms)}">
					<form name="standard" action="${pageContext.request.contextPath}/Analysis/Save/Standard" class="form" id="addStandardForm">
						<div class="form-group">
							<label> <spring:message code="label.analysis.add.standard.select.choose" text="Select your standard" />
							</label>
							<div>
								<div class="col-lg-11">
									<select name="idNorm" class="form-control" onchange="$('#selectedStandardDescription').html($('#addStandardForm select option:selected').attr('title'));">
										<c:forEach items="${norms}" var="norm">
											<option title='<spring:message text="${norm.description}"/>' value="${norm.id}">
												<spring:message text="${norm.label}" /> -
												<spring:message text="${norm.version}" />
											</option>
										</c:forEach>
									</select>
								</div>
								<a href="#" onclick="saveStandard('addStandardForm')" style="font-size:20px" class="col-lg-1" id="btn_save_standard" title='<spring:message code="label.action.add" text="Add" />'><span
									class="glyphicon glyphicon-plus"></span></a>
							</div>

						</div>
					</form>
					<fieldset style="margin-top: 40px;">
						<legend style="font-size: 12; font-weight: bold">
							<spring:message code="label.norm.description" text="Description" />
						</legend>
						<label id="selectedStandardDescription"> <spring:message text="${norms.get(0).description}" />
						</label>
					</fieldset>
				</c:if>
				<div id="add_standard_progressbar" class="progress progress-striped active" hidden="true">
					<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer"></div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->