<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modalRiskAcceptanceForm" tabindex="-1" role="dialog" data-aria-labelledby="riskAcceptanceForm" style="z-index: 1042" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.tile.manage.risk-acceptance" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 5px;">
				<form name="risk_acceptance_form" class="form-horizontal" id="risk_acceptance_form" method="post">
					<table class='table table-hover table-condensed'>
						<thead>
							<tr>
								<th><spring:message code="label.action" /></th>
								<th><spring:message code="label.parameter.level" /></th>
								<th><spring:message code="label.color" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${parameters}" var="parameter">
								<tr data-trick-id='${parameter.id}'>
									<td><button class='btn btn-xs btn-danger'><i class='fa fa-remove'></i></button></td>
									<td>
										<select name="value" class="form-control">
											<c:forEach begin="1" end="${maxImportance}" var="i">
												<option value="${i}" ${parameter.value==i?'selected':''}>${i}</option>
											</c:forEach>
										</select>
									</td>
									<td><input type="color" value="${parameter.description}" class="form-control"></td>
								</tr>
							</c:forEach>
							<tr data-trick-id='-2' data-trick-max-value='${maxImportance}'>
								<td><button class="btn btn-xs btn-primary" type="button"><i class='fa fa-plus'></i></button></td>
								<td colspan="2" class="default disabled"></td>
							</tr>
						</tbody>
					</table>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
