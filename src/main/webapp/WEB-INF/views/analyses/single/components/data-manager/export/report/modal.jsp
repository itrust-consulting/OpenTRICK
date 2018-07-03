<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="analysis-export-dialog" class="modal fade" role="dialog" tabindex="-1" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-sml">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.export.report" text="Exporting analysis word report" />
				</h4>
			</div>
			<c:set var="nameControl" value="${fn:toLowerCase(analysis.type)}" />
			<!-- dialog body -->
			<div class="modal-body">
				<form class="form form-inline" action="${pageContext.request.contextPath}/Analysis/Report/Export-process" method="post" enctype="multipart/form-data" data-type-control='${nameControl}'>
					
					<input type="hidden" name="analysis" id="exportWord.analysis.${nameControl}" value="${analysis.id}">
					
					<c:if test="${not empty  types}">
						<fieldset>
							<legend style="font-size: 15px" data-helper-content='<spring:message code="help.export.report.type" />'>
								<spring:message code="label.analysis.type" text="Type" />
							</legend>
							<div class="col-sm-12" align="center" data-trick-info="type">
								<div class="btn-group" data-toggle="buttons">
									<c:forEach items="${types}" var="type" varStatus="status">
										<c:set var="typeValue" value="${fn:toLowerCase(type)}" />
										<label class="btn btn-sm btn-default ${status.index==0?'active':''}"><spring:message code="label.analysis.type.${typeValue}" text="${typeValue}" /><input
											${status.index==0 ? 'checked' :''} name="type" type="radio" value="${type}"></label>
									</c:forEach>
								</div>
							</div>
						</fieldset>
					</c:if>

					<fieldset style="margin-bottom: 10px">
						<legend style="font-size: 15px" data-helper-content='<spring:message code="help.export.report.internal" />'>
							<spring:message code='label.internal.template' text='Internal template' />
						</legend>

						<div class='col-sm-12 text-center' data-trick-info="template">
							<select name='template' class="form-control" id="exportWord.template.${nameControl}" required="required" style="width: 100%">
								<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
								<c:forEach items="${templates}" var="template">
									<option value="${template.id}" class='${template.outToDate?"text-danger":""}' data-trick-type='${template.type}'>
										<spring:message text="${template.label}" /> - V
										<spring:message text="${template.version}" />
									</option>
								</c:forEach>
							</select>
						</div>
					</fieldset>

					<fieldset data-trick-info='file'>
						<legend class='text-warning' style="font-size: 15px" data-helper-content='<spring:message code="help.export.report.external" />'>
							<spring:message code="label.external.template" />
						</legend>

						<c:choose>
							<c:when test="${ maxFileSize< 1024}">
								<spring:message code="label.max.unit.data.byte" arguments="${maxFileSize}" var="maxSizeInfo" />
							</c:when>
							<c:when test="${ maxFileSize < 1048576}">
								<spring:message code="label.max.unit.data.kilo.byte" arguments="${maxFileSize / 1024}" var="maxSizeInfo" />
							</c:when>
							<c:otherwise>
								<spring:message code="label.max.unit.data.mega.byte" arguments="${maxFileSize / 1048576}" var="maxSizeInfo" />
							</c:otherwise>
						</c:choose>

						<div class="col-sm-12">
							<div class="input-group-btn">
								<input id="exportWord.file.${nameControl}" type="file" accept=".docx" name="file" style="display: none;" maxlength="${maxFileSize}" /> <input
									id="exportWord.file.info.${nameControl}" name="filename" placeholder="${maxSizeInfo}" class="form-control" readonly="readonly" required="required" style="width: 83%" />
								<button class="btn btn-primary" type="button" id="exportWord.file.browse.button.${nameControl}" name="browse" style="margin-left: -5px;">
									<spring:message code="label.action.browse.${nameControl}" text="Browse" />
								</button>
							</div>
						</div>
						
					</fieldset>

					<button name='submit' type="submit" style="display: none"></button>

				</form>
			</div>
			<!-- dialog buttons -->
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="export">
					<spring:message code="label.action.export" text="Export" />
				</button>
				<button type="button" class="btn btn-default" name="cancel" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>