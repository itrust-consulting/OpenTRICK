<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="import-modal" tabindex="-1" role="dialog" data-aria-labelledby="import-modal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.modal.import" />
				</h4>
			</div>
			<div class="modal-body">
				<div class='col-xs-3'>
					<ul class="nav nav-tabs tabs-left">
						<c:forEach items="${items}" var="item" varStatus="status">
							<li ${status.index==0?"class='active'":""}><a href="#import-${item.name}" data-toggle="tab"><spring:message
										code="label.action.import.${fn:replace(item.name, '-','_')}" /></a></li>
						</c:forEach>
					</ul>
					<div class="clearfix"></div>
				</div>
				<div class="col-xs-9">
					<div id="import-view-container" class="tab-content">
						<c:forEach items="${items}" var="item" varStatus="status">
							<div class='tab-pane ${status.index==0?"active":""}' id="import-${item.name}" data-view-name='${item.name}' data-view-url='${item.viewURL}'
								data-view-process-url='${item.processURL}' data-view-extentions='${item.extensions}'>
								<c:if test="${empty item.viewURL}">
									<div class='alert alert-sm alert-danger' style="margin-bottom: 10px">
										<spring:message code="info.import.${fn:replace(item.name,'-','_')}" />
									</div>
									<form name="${item.name}" method="post" action="${pageContext.request.contextPath}${item.processURL}?${_csrf.parameterName}=${_csrf.token}" class="form-inline"
										id="form-${item.name}" enctype="multipart/form-data">
										<div class="row">
											<label class="col-lg-12" for="name"> <spring:message code="label.import.${fn:replace(item.name,'-','_')}.choose_file" text="Choose the file containing data to import" /></label>
											<div class="col-lg-12">
												<div class="input-group-btn">
													<spring:message text="${fn:replace(item.extensions,'.','')}" var="extension"/>
													<input id="file-${item.name}" type="file" accept="${item.extensions}"
														onchange='{$("#upload-file-info-${item.name}").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info-${item.name}").val(),"${extension}","#btn-import");}'
														name="file" style="display: none;" /> <input id="upload-file-info-${item.name}" class="form-control" readonly="readonly" required="required" style="width: 88%;" />
													<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=file-${item.name}]').click();" style="margin-left: -5px;">
														<spring:message code="label.action.browse" text="Browse" />
													</button>
												</div>
											</div>
										</div>
									</form>
									
									<div class="col-lg-8" style="color: #d9534f;" align="left" id="import${item.name}Notification"></div>

								</c:if>
							</div>
						</c:forEach>
					</div>
					<div class="clearfix"></div>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" id="btn-import" name="import" class="btn btn-primary" disabled="disabled">
					<spring:message code="label.action.import" text="Import" />
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