<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="actionplancomputeoptions" tabindex="-1" role="dialog" data-aria-labelledby="actionplancomputeoptions" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.compute.action_plan" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="label.title.options.select_norm" />
				</p>
				<p>
					<spring:message code="label.title.options.select_norm.info" />
				</p>
				<form action="${pageContext.request.contextPath}/ActionPlan/Compute?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal" id="actionplancomputationoptionsform">
					<c:if test="${!empty(id)}">
						<input name="id" value="${id}" type="hidden">
					</c:if>
					<table class="table text-center">
						<c:choose>
							<c:when test="${!empty(standards)}">
								<thead>
									<tr>
										<c:forEach items="${standards}" var="analysisStandard">
											<td><b><spring:message text="${analysisStandard.standard.name}" /></b></td>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<c:forEach items="${standards}" var="analysisStandard">
											<td><input type="checkbox" name="standard_${analysisStandard.id}" value="1" /></td>
										</c:forEach>
									</tr>
								</tbody>
							</c:when>
						</c:choose>
					</table>
				</form>
			</div>
			<div class="modal-footer">
				<button id="computeActionPlanButton" type="button" class="btn btn-default" data-dismiss="modal" onclick="return calculateActionPlanWithOptions('actionplancomputationoptionsform')">
					<spring:message code="label.action.compute" />
				</button>
				<button id="cancelcomputeActionPlanButton" type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>