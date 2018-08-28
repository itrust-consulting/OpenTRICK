<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="export-modal" tabindex="-1" role="dialog" data-aria-labelledby="export-modal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.data_manager.export" />
				</h4>
			</div>
			<div class="modal-body">
				<div class='col-xs-3' data-view-part='nav'>
					<ul class="nav nav-pills nav-stacked">
						<c:forEach items="${items}" var="item" varStatus="status">
							<li ${status.index==0?"class='active'":""}><a href="#export-${item.name}" data-toggle="tab"><spring:message
										code="label.menu.data_manager.export.${fn:replace(item.name, '-','_')}" /></a></li>
						</c:forEach>
					</ul>
					<div class="clearfix"></div>
				</div>
				<div class="col-xs-9" data-view-part='content'>
					<div id="export-view-container" class="tab-content" data-view-tab='main'>
						<c:forEach items="${items}" var="item" varStatus="status">
							<div class='tab-pane ${status.index==0?"active":""} tab-pane-main' id="export-${item.name}" data-view-name='${item.name}' data-view-url='${item.viewURL}'
								data-view-process-url='${item.processURL}' data-view-extentions='${item.extensions}' data-view-token="${item.token}">
								<c:if test="${empty item.viewURL}">
									<fieldset>
										<spring:message text="${fn:replace(item.name,'-','_')}" var="viewName" />
										<legend>
											<spring:message code="label.title.data_manager.export.${viewName}" />
										</legend>
										<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
											<spring:message code="info.data_manager.export.${viewName}" />
										</div>
										<c:if test="${not item.background}">
											<div class="hidden">
												<a href='<spring:url value="${item.processURL}?token=${item.token}"/>' data-token='${item.token}' target="_ts_downloading" download class='btn btn-lg btn-primary'><spring:message code='label.action.download.click' />
													<i class="glyphicon glyphicon-download"></i></a>
											</div>
										</c:if>
									</fieldset>
								</c:if>
							</div>
						</c:forEach>
					</div>
					<div class="clearfix"></div>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" id="btn-export" name="export" class="btn btn-primary" disabled="disabled">
					<spring:message code="label.action.export" text="Export" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->