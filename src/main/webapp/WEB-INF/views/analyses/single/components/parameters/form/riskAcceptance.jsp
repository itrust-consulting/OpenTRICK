<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modalRiskAcceptanceForm" tabindex="-1" role="dialog" data-aria-labelledby="riskAcceptanceForm" style="z-index: 1042" data-aria-hidden="true" data-backdrop="static" data-keyboard="true" >
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.tile.manage.risk-acceptance" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 5px;">
				<form name="risk_acceptance_form" class="form-horizontal" id="risk_acceptance_form" method="post" style="height: 400px;overflow-y:auto;overflow-x:hide;">
					<table class='table table-hover table-condensed' data-trick-size='${fn:length(parameters)}'>
						<thead>
							<tr>
								<th width="15%"><spring:message code="label.action" /></th>
								<th width="60%"><spring:message code="label.importance.threshold" /></th>
								<th><spring:message code="label.color" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${parameters}" var="parameter">
								<tr data-trick-id='${parameter.id}'>
									<td><button class='btn btn-danger outline' name="delete"><i class='fa fa-remove'></i></button></td>
									<td>
										<div class="range-group">
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" var="value"/>
											<span class='range-text'>${value}</span>
											<input class="range-input" name="value" type="range" value="${value}" min='1' max='${maxImportance}'>
										</div>
										
									</td>
									<td><input type="color" class='form-control' name="description" value="${parameter.description}"></td>
								</tr>
							</c:forEach>
							<tr data-trick-max-value='${maxImportance}' class='panel-footer'>
								<td colspan="3" align="center"><button class="btn btn-xs btn-primary" type="button" name="add"><i class='fa fa-plus'></i></button></td>
							</tr>
						</tbody>
					</table>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
