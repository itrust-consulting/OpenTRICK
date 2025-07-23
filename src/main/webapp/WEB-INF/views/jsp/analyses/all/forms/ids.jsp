<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="manageAnalysisIDSAccessModel" data-trick-id='${analysis.id}' tabindex="-1" role="dialog" data-aria-labelledby="manageAnalysisIDSAccessModel"
	data-aria-hidden="true">
	<div class="modal-dialog" style="width: 705px;">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.manage.ids.access" text="Manage subscriptions to IDSs" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 0;">

				<div style="padding: 5px 20px;">
					<h5 style="font-weight: bold;">
						<spring:message code="label.analysis.manage.access.info" arguments="${analysis.label} , ${analysis.version} , ${analysis.customer.organisation}"
							text="Analysis: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
					</h5> 
					<div>
						<h4 class="col-xs-8 bordered-bottom" style="padding-left: 0">
							<spring:message code='label.name' />
						</h4>
						<h4 class="col-xs-4 bordered-bottom text-center">
							<spring:message code='label.action' text="Action"/>
						</h4>
					</div>
				</div>
				<spring:message code="label.action.subscribe" text="Subscribe" var="subscribe"/>
				<spring:message code="label.action.unsubscribe" text="Unsubscribe" var="unsubscribe"/>
				<div class="form-horizontal" style="padding: 5px 20px; height: 500px; overflow-x: hidden; clear: both;">
					<c:forEach items="${IDSs}" var="ids">
						<c:set var="hasAccess" value="${not empty subscriptionsStates[ids.id]}"/>
						<div class="form-group" data-trick-id="${ids.id}" data-default-value='${hasAccess}'>
							<div class="col-xs-8">
								<h4 style="vertical-align: middle; text-transform: capitalize;"><spring:message text="${fn:toLowerCase(ids.prefix)}" /></h4>
							</div>
							<div class="col-xs-4 text-center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-sm btn-default ${hasAccess?'active':''}">${subscribe}<input ${hasAccess?'checked':''} name="${ids.id}" type="radio" value="true"></label>
									<label class="btn btn-sm btn-default ${not hasAccess?'active':''}">${unsubscribe}<input ${not hasAccess?'checked':''} name="${ids.id}" type="radio" value="false"></label>
								</div>
							</div>
							<p class="col-xs-12"><spring:message text="${ids.description}"/></p>
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