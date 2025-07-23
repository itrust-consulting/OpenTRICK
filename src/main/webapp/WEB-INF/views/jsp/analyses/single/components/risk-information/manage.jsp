<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<c:set var="chapterRegex">^\d(\.0)*$</c:set>
<div class="modal fade" id="modal-manage-brainstorming" tabindex="-1" role="dialog" data-aria-labelledby="modal-manage-brainstorming" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<div class="modal-title">
					<h4 class="col-xs-5" style="padding: 0; margin: 0">
						<spring:message code="label.title.risk_information.${fn:toLowerCase(type)}.management" />
					</h4>
					<div class="col-xs-7" id="error-risk-information-modal" style="padding: 0"></div>
				</div>
			</div>
			<div class="modal-body" style="padding-top: 5px">
				<spring:message var="deleteBelow" code="label.action.delete.all.below" />
				<spring:message var="deleteChatper" code="label.action.delete.chapter" />
				<form action="#" method="post" class='tab-content' style="height: 500px; overflow-y: auto;">
					<c:forEach items="${riskInformationMap.keySet()}" var="category">
						<table class='table'>
							<thead>
								<tr>
									<th width="15%"><spring:message code="label.risk_information.id" /></th>
									<th><spring:message code="label.name" /></th>
									<th width="12%"><spring:message code="label.action" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${riskInformationMap[category]}" var="risk_information">
									<spring:message text='${risk_information.label}' var="label" />
									<spring:message text='${risk_information.chapter}' var="chapter" />
									<tr data-trick-id='${risk_information.id}'>
										<td><input type="hidden" name="id" value="${risk_information.id}" /> <input type="hidden" name="custom" value="${risk_information.custom}" /><input
											class='form-control' name="chapter" value="${chapter}" required="required" placeholder="${chapter}" /></td>
										<td><input class="form-control" type="text" name="label" value="${label}" placeholder="${label}" required></td>
										<td><button class='btn btn-xs btn-warning' name="delete" title="<spring:message code='label.action.delete'/>">
												<i class='fa fa-times-circle'></i>
											</button>
											<button class='btn btn-xs btn-danger' name="clear" title="<spring:message code='label.action.empty.chapter'/>">
												<i class='fa fa-trash-o'></i>
											</button>
											<button class='btn btn-xs btn-primary' name="add" title="<spring:message code='label.action.add'/>">
												<i class='fa fa-plus-circle' aria-hidden="true"></i>
											</button></td>
									</tr>
								</c:forEach>
								<tr data-role='add-btn'>
									<td colspan="3" class='text-center'><button class='btn btn-primary' data-error-full-message="<spring:message code='error.risk_information.too_many.chapter'/>"
											name="add-chapter" title="<spring:message code='label.action.add'/>">
											<i class='fa fw fa-plus-circle' aria-hidden="true"></i>
										</button></td>
								</tr>
							</tbody>
						</table>
					</c:forEach>
					<input hidden="hidden" type="submit">
				</form>
			</div>
			<div class="modal-footer">
				<button class="btn btn-primary" name="save">
					<spring:message code="label.action.save" />
				</button>
				<button class="btn btn-default" type="button" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.close" />
				</button>
			</div>
		</div>
	</div>
	<div id='risk-information-btn' style="display: none;">
		<button class='btn btn-xs btn-warning' name="delete" title="<spring:message code='label.action.delete'/>">
			<i class='fa fa-times-circle'></i>
		</button>
		<button class='btn btn-xs btn-danger' name="clear" title="<spring:message code='label.action.empty.chapter'/>">
			<i class='fa fa-trash-o'></i>
		</button>
		<button class='btn btn-xs btn-primary' name="add" title="<spring:message code='label.action.add'/>">
			<i class='fa fa-plus-circle' aria-hidden="true"></i>
		</button>
	</div>
</div>