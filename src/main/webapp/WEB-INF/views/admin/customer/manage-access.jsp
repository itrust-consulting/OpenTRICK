<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="manageCustomerUserModel" tabindex="-1" role="dialog" data-aria-labelledby="manageCustomerUserModel" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="width: 705px;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.customer.manage.users" text="Manage customer users" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 0;">
				<div style="padding: 5px 20px;">
					<h5 style="font-weight: bold;">
						<spring:message code="label.customer.manage.access.info" arguments="${customer.organisation}"
							text="Organisation: ${customer.organisation}, Contact person: ${customer.contactPerson}" />
					</h5>
					<div>
						<h4 class="col-xs-7 bordered-bottom" style="padding-left: 0">
							<spring:message code='label.name' />
						</h4>
						<h4 class="col-xs-5 bordered-bottom text-center">
							<spring:message code='label.customer.access' />
						</h4>
					</div>
				</div>
				<spring:message code='label.customer.acess.yes' var="hasAccess" />
				<spring:message code="label.customer.acess.no" var="noAccess" />
				<div class="form-horizontal" style="padding: 5px 20px; height: 500px; overflow-x: hidden; clear: both;">
					<c:forEach items="${users}" var="user">
						<c:set var="userRight" value="${customerUsers.containsKey(user.login)}" />
						<c:set var='name' value="user_${user.id}" />
						<div class="form-group" data-default-value='${userRight}' data-trick-id="${user.id}" data-name='${name}'>
							<div class="col-xs-7">
								<strong style="vertical-align: middle; text-transform: capitalize;"><spring:message text="${fn:toLowerCase(user.firstName)} ${fn:toLowerCase(user.lastName)}" /></strong>
							</div>
							<div class="col-xs-5 text-center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-sm btn-default ${userRight?'active':''}">${hasAccess}<input ${userRight?'checked':''} name="${name}" type="radio" value="true"></label> <label
										class="btn btn-sm btn-default ${not userRight?'active':''}">${noAccess}<input ${not userRight?'checked':''} name="${name}" type="radio" value="false"></label>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save" data-dismiss="modal" >
					<spring:message code="label.action.save" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>