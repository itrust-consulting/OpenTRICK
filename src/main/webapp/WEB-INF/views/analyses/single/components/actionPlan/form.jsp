<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="actionplancomputeoptions" tabindex="-1" role="dialog" data-aria-labelledby="actionplancomputeoptions" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<fmt:message key="label.title.compute.action_plan" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<fmt:message key="label.title.options.select_norm" />
				</p>
				<p>
					<fmt:message key="label.title.options.select_norm.info" />
				</p>
				<form action="${pageContext.request.contextPath}/ActionPlan/Compute" method="post" class="form-horizontal" id="actionplancomputationoptionsform">
					<c:if test="${!empty(id)}">
						<input name="id" value="${id}" type="hidden">
					</c:if>
					<table class="table text-center">
						<c:choose>
							<c:when test="${!empty(standards)}">
								<thead>
									<tr>
										<c:forEach items="${standards}" var="analysisStandard">
											<td><b><spring:message text="${analysisStandard.standard.label}" /></b></td>
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
					<fmt:message key="label.action.compute" />
				</button>
				<button id="cancelcomputeActionPlanButton" type="button" class="btn btn-default" data-dismiss="modal">
					<fmt:message key="label.action.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>