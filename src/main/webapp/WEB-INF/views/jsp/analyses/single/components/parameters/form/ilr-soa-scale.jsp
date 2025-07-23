<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modalIlrSoaScaleFormForm" tabindex="-1" role="dialog" data-aria-labelledby="ilrSoaScaleForm" style="z-index: 1042" data-aria-hidden="true"
	data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog modal-mdl">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.tile.manage.ilr_soa_scale" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 5px;">
				<form name="ilr_soa_scale_form" class="form-horizontal" id="ilr_soa_scale_form" method="post" style="height: 600px; overflow-y: auto; overflow-x: hide;">
					<table class='table table-hover table-condensed' data-trick-size='${fn:length(parameters)}'>
						<thead>
							<tr>
								<th><spring:message code="label.action" /></th>
								<th style="width: 27%" class="textaligncenter"><spring:message code="label.implementation.rate.threshold" /></th>
								<th style="width: 55%" class="textaligncenter"><spring:message code="label.description" /></th>
								<th class="textaligncenter" style="min-width: 50px;"><spring:message code="label.color" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${parameters}" var="parameter">
								<tr data-trick-id='${parameter.id}'>
									<td><button class='btn btn-danger outline' name="delete">
											<i class='fa fa-remove'></i>
										</button></td>
									<td>
										<div class="range-group">
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" var="value" />
											<span class='range-text'>${value}</span> <input class="range-input" name="value" type="range" value="${value}" min='-1' max='${100}'>
										</div>
									</td>
									<td><textarea rows="1" class="form-control resize_vectical_only" name="description"><spring:message text="${parameter.description}"/></textarea></td>
									<td><input type="color" class='form-control form-control-static' name="color" value="${parameter.color}"></td>
								</tr>
							</c:forEach>
							<tr data-trick-max-value='${100}' class='panel-footer'>
								<td colspan="5" align="center"><button class="btn btn-xs btn-primary" type="button" name="add">
										<i class='fa fa-plus'></i>
									</button></td>
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
