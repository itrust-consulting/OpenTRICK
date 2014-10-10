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
					<fmt:message key="label.title.analysis.manage_standard"  />
				</h4>
			</div>
			<div class="modal-body">
				<h3><fmt:message key="label.title.analysis.manage_standard.current" /></h3>
				<table class="table">
					<thead>
						<tr>
							<th><fmt:message key="label.norm.label"  /></th>
							<th><fmt:message key="label.norm.version"  /></th>
							<th colspan="3"><fmt:message key="label.norm.description"  /></th>
							<th><fmt:message key="label.norm.computable"  /></th>
							<th><fmt:message key="label.action.remove" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${currentStandards}" var="standard">
							<tr>
								<td><spring:message text="${standard.label}"  /></td>
								<td><spring:message text="${standard.version}" /></td>
								<td colspan="3"><spring:message text="${standard.description}" /></td>
								<td><fmt:message key="label.${standard.computable?'yes':'no'}"  /></td>
								<td><a href="#" role="remove-standard" trick-class="standard" trick-id="${standard.id}" style="font-size: 20px" class="text-danger"
									title='<fmt:message key="label.action.delete" />'> <span class="glyphicon glyphicon-remove-circle"></span></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<hr />
				<h3><fmt:message key="label.title.analysis.manage_standard.available" /></h3>
				<c:if test="${!empty(standards)}">
					<form name="standard" action="${pageContext.request.contextPath}/Analysis/Save/Standard" class="form" id="addStandardForm">
						<div class="form-group">
							<label> <fmt:message key="label.analysis.add.standard.select.choose"  />
							</label>
							<div>
								<div class="col-lg-11">
									<select name="idStandard" class="form-control" onchange="$('#selectedStandardDescription').html($('#addStandardForm select option:selected').attr('title'));">
										<c:forEach items="${standards}" var="standard">
											<option title='<spring:message text="${standard.description}"/>' value="${standard.id}">
												<spring:message text="${standard.label}" /> -
												<spring:message text="${standard.version}" />
											</option>
										</c:forEach>
									</select>
								</div>
								<a href="#" onclick="saveStandard('addStandardForm')" style="font-size: 20px" class="col-lg-1" id="btn_save_standard"
									title='<fmt:message key="label.action.add" />'><span class="glyphicon glyphicon-plus"></span></a>
							</div>
						</div>
					</form>
				</c:if>
				<c:if test="${empty(standards)}">
					<fmt:message key="label.no_standards" />
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