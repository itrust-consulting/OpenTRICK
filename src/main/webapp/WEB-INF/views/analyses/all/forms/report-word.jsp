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
			<!-- dialog body -->
			<div class="modal-body" style="padding-top: 5px;">
				<form class="form form-inline" action="${pageContext.request.contextPath}/Analysis/Export/Report/{analysis.id}" method="post">

					<input type="hidden" name="analysis" id="exportWord.analysis" value="${analysis.id}">

					<fieldset>
						<legend>
							<spring:message code="label.template.source" />
						</legend>
						<div class='col-sm-12 text-center' data-trick-info="internal">
							<div class="btn-group" data-toggle="buttons">
								<label class="btn btn-sm btn-default active"><spring:message code='label.internal' /><input checked="checked" name="internal" type="radio" value="true"></label> <label
									class="btn btn-sm btn-default"><spring:message code='label.external' /><input name="internal" type="radio" value="false"></label>
							</div>
						</div>
					</fieldset>

					<c:if test="${not empty  types}">
						<fieldset>
							<legend>
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



					<fieldset>
						<legend>
							<spring:message code='label.internal.template' text='Internal template' />
						</legend>

						<div class='col-sm-12 text-center' data-trick-info="template">
							<select name='template' class="form-control" id="exportWord.template" required="required" style="width: 100%">
								<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
								<c:forEach items="${templates}" var="template">
									<option value="${template.id}" class='${template.outToDate?"text-danger":""}' data-trick-type='${template.type}'>
										<spring:message text="${template.label}" /> - V<spring:message text="${template.version}" />
									</option>
								</c:forEach>
							</select>
						</div>
					</fieldset>

					<fieldset data-trick-info='file'>
						<legend class='text-danger'>
							<spring:message code="label.external.template" />
						</legend>
						<div class="col-sm-12">
							<div class="input-group-btn">
								<input id="exportWord.file" type="file" accept=".docx" name="file" style="display: none;" /> <input id="exportWord.file.info" name="filename" class="form-control"
									readonly="readonly" required="required" style="width: 83%" />
								<button class="btn btn-primary" type="button" id="exportWord.file.browse.button" name="browse" style="margin-left: -5px;">
									<spring:message code="label.action.browse" text="Browse" />
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