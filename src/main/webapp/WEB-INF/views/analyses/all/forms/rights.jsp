<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="manageAnalysisAccessModel" data-trick-id='${analysis.id}' data-trick-user-id='${myId}' tabindex="-1" role="dialog"
	data-aria-labelledby="manageAnalysisAccessModel" data-aria-hidden="true">
	<div class="modal-dialog" style="width: 705px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="manageAnalysisAccessModel-title">
					<spring:message code="label.title.analysis.manage.access" text="Manage analysis access rights" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 0;">

				<div style="padding: 5px 20px;">
					<h5 style="font-weight: bold;">
						<spring:message code="label.analysis.manage.access.info" arguments="${analysis.label} , ${analysis.version} , ${analysis.customer.organisation}"
							text="Analysis: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
					</h5>
					<div>
						<h4 class="col-xs-5 bordered-bottom" style="padding-left: 0">
							<spring:message code='label.name' />
						</h4>
						<h4 class="col-xs-7 bordered-bottom text-center">
							<spring:message code='label.analysis.rights' />
						</h4>
					</div>
				</div>
				<spring:message code='label.analysis.right.all' var="rightAll" />
				<spring:message code="label.analysis.right.export" var="rightExport" />
				<spring:message code="label.analysis.right.modify" var="rightModify" />
				<spring:message code="label.analysis.right.read" var="rightRead" />
				<spring:message code="label.analysis.right.none" var="rightNone" />
				<div class="form-horizontal" style="padding: 5px 20px; height: 500px; overflow-x: hidden; clear: both;">
					<c:forEach items="${userrights.keySet()}" var="user">
						<c:set var="userRight" value="${userrights[user]}" />
						<c:set var='name' value="right_${user.id}" />
						<div class="form-group" data-default-value='${userRight}' data-trick-id="${user.id}" data-name='${name}'>
							<div class="col-xs-5">
								<strong style="vertical-align: middle; text-transform: capitalize;"><spring:message text="${fn:toLowerCase(user.firstName)} ${fn:toLowerCase(user.lastName)}" /></strong>
							</div>
							<div class="col-xs-7 text-center">
								<c:choose>
									<c:when test="${user.id==ownerId}">
										<c:choose>
											<c:when test="${not isAdmin and user.id!=myId}">
												<div class="btn-group" data-toggle="buttons">
													<label class="btn btn-sm btn-default disabled ${userRight=='ALL'?'active':''}">${rightAll}</label><label class="btn btn-sm btn-default disabled ${userRight=='EXPORT'?'active':''}">${rightExport}</label>
													<label class="btn btn-sm btn-default disabled ${userRight=='MODIFY'?'active':''}">${rightModify}</label> <label class="btn btn-sm btn-default disabled ${userRight=='READ'?'active':''}">${rightRead}</label> <label class="btn btn-sm btn-default disabled">${rightNone}</label>
												</div>
											</c:when>
											<c:otherwise>
												<div class="btn-group" data-toggle="buttons">
													<label class="btn btn-sm btn-default ${userRight=='ALL'?'active':''}">${rightAll}<input ${userRight=='ALL'?'checked':''} name="${name}" type="radio"
														value="ALL"></label> <label class="btn btn-sm btn-default ${userRight=='EXPORT'?'active':''}">${rightExport}<input ${userRight=='EXPORT'?'checked':''}
														name="${name}" type="radio" value="EXPORT"></label> <label class="btn btn-sm btn-default ${userRight=='MODIFY'?'active':''}">${rightModify}<input
														${userRight=='MODIFY'?'checked':''} name="${name}" type="radio" value="MODIFY"></label> <label class="btn btn-sm btn-default ${userRight=='READ'?'active':''}">${rightRead}<input
														${userRight=='READ'?'checked':''} name="${name}" type="radio" value="READ"></label> <label class="btn btn-sm btn-default disabled">${rightNone}</label>
												</div>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<div class="btn-group" data-toggle="buttons">
											<label class="btn btn-sm btn-default ${userRight=='ALL'?'active':''}">${rightAll}<input ${userRight=='ALL'?'checked':''} name="${name}" type="radio" value="ALL"></label>
											<label class="btn btn-sm btn-default ${userRight=='EXPORT'?'active':''}">${rightExport}<input ${userRight=='EXPORT'?'checked':''} name="${name}" type="radio"
												value="EXPORT"></label> <label class="btn btn-sm btn-default ${userRight=='MODIFY'?'active':''}">${rightModify}<input ${userRight=='MODIFY'?'checked':''}
												name="${name}" type="radio" value="MODIFY"></label> <label class="btn btn-sm btn-default ${userRight=='READ'?'active':''}">${rightRead}<input ${userRight=='READ'?'checked':''}
												name="${name}" type="radio" value="READ"></label> <label class="btn btn-sm btn-default ${empty userRight?'active':''}">${rightNone}<input
												${empty userRight?'checked':''} name="${name}" type="radio" value="">
											</label>
										</div>
									</c:otherwise>
								</c:choose>
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