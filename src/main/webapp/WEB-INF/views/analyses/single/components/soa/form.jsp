<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="manageSAOModel" tabindex="-1" role="dialog" data-aria-labelledby="manageSAOModel" data-aria-hidden="true">
	<div class="modal-dialog" style="width: 705px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="manageSAOModel-title">
					<spring:message code="label.title.manage.soa" text="Manage SOA" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 0;">
				<div style="padding: 5px 20px;">
					<div>
						<h4 class="col-xs-3 bordered-bottom" style="padding-left: 0">
							<spring:message code="label.norm.label" text="Name" />
						</h4>
						<h4 class="col-xs-6 bordered-bottom" style="padding-left: 0">
							<spring:message code="label.description"/>
						</h4>
						<h4 class="col-xs-3 bordered-bottom text-center">
							<spring:message code='label.status' />
						</h4>
					</div>
				</div>
				<spring:message code="label.status.enabled" var="enable" />
				<spring:message code="label.status.disabled" var="disable" />
				<div class="form-horizontal" style="padding: 5px 20px; height: 500px; overflow-x: hidden; clear: both;">
					<c:forEach items="${analysisStandards}" var="analysisStandard">
						<div class="form-group" data-default-value='${analysisStandard.soaEnabled}' data-trick-id="${analysisStandard.id}">
							<div class="col-xs-3">
								<strong style="vertical-align: middle; text-transform: capitalize;"><spring:message text="${analysisStandard.standard.name}" /></strong>
							</div>
							<div class="col-xs-6">
								<p style="padding-left: 0; font-size: 12px;text-transform: capitalize;">
									<spring:message code="label.norm.standard_type.${fn:toLowerCase(analysisStandard.standard.type)}" var="type"/>
									<spring:message code="label.${analysisStandard.standard.computable?'yes':'no'}" var="computable"/>
									<spring:message code="label.standard.info" arguments="${analysisStandard.standard.description},${type},${computable}" />
								</p>
							</div>
							<div class="col-xs-3 text-center">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-sm btn-default ${analysisStandard.soaEnabled?'active':''}">${enable}<input ${analysisStandard.soaEnabled?'checked':''}
										name="${analysisStandard.id}" type="radio" value="true"></label> <label class="btn btn-sm btn-default ${not analysisStandard.soaEnabled?'active':''}">${disable}<input
										${not analysisStandard.soaEnabled?'checked':''} name="${analysisStandard.id}" type="radio" value="false"></label>
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