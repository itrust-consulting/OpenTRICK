<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="manageAnalysisAccessModel" data-trick-id='${analysis.id}' data-trick-user-id='${myId}' tabindex="-1" role="dialog" data-aria-labelledby="manageAnalysisAccessModel" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="manageAnalysisAccessModel-title">
					<spring:message code="label.title.analysis.manage.access" text="Manage Analysis Access Rights" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 0;">

				<div style="padding: 5px 20px;">
					<h5 style="font-weight: bold;"><spring:message code="label.analysis.anage.access.info" arguments="${analysis.label} , ${analysis.version} , ${analysis.customer.organisation}"
							text="Analysis: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" /></h5>
					<div>
						<h4 class="col-xs-4 bordered-bottom" style="padding-left: 0">
							<spring:message code='label.name' />
						</h4>
						<h4 class="col-xs-8 bordered-bottom text-center">
							<spring:message code='label.analysis.rights' />
						</h4>
					</div>
				</div>
				<div class="form-horizontal" style="padding: 5px 20px; height: 380px; overflow-x: hidden; clear: both;">
					<c:forEach items="${userrights.keySet()}" var="user">
						<c:set var="userRight" value="${userrights[user]}" />
						<c:set var='name' value="right_${user.id}" />
						<div class="form-group" data-default-value='${userRight}' data-trick-id="${user.id}" data-name='${name}' >
							<div class="col-xs-4">
								<strong style="vertical-align: middle;"><spring:message text="${user.firstName} ${user.lastName}" /></strong>
							</div>
							<div class="col-xs-8 text-center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-sm btn-default ${userRight=='ALL'?'active':''}"><spring:message code='label.analysis.right.all' /><input ${userRight=='ALL'?'checked':''}
										name="${name}" type="radio" value="ALL"></label> <label class="btn btn-sm btn-default ${userRight=='EXPORT'?'active':''}"><spring:message
											code="label.analysis.right.export" /><input ${userRight=='EXPORT'?'checked':''} name="${name}" type="radio" value="EXPORT"></label> <label
										class="btn btn-sm btn-default ${userRight=='MODIFY'?'active':''}"><spring:message code="label.analysis.right.modify" /><input ${userRight=='MODIFY'?'checked':''}
										name="${name}" type="radio" value="MODIFY"></label> <label class="btn btn-sm btn-default ${empty userRight?'active':''}"><spring:message code="label.analysis.right.none" /><input
										${empty userRight?'checked':''} name="${name}" type="radio" value=""></label>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" name="save" data-dismiss="modal" class="btn btn-primary">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>