<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="form_customer_template" class="tab-pane" style="height: 500px; overflow-y: auto; overflow-x: hidden; margin-top: -10px">
	<form name="reportTemplate" class="form form-horizontal" id="reportTemplate-form" method="post" enctype="multipart/form-data">
		<input type="hidden" value="-1" name="id" id="reportTemplate.id"> <input type="hidden" value="${customer.id}" name="customer" id="reportTemplate.customer.id">
		<div class="form-group">
			<label class="control-label col-sm-3" data-helper-content='<spring:message code="help.report.template.type" />'><spring:message code="label.type" /></label>
			<div class="col-sm-9 text-center" data-trick-info='type'>
				<div class="btn-group" data-toggle="buttons">
					<c:forEach items="${types}" var="type" varStatus="status">
						<c:set var="typeValue" value="${fn:toLowerCase(type)}" />
						<label class="btn btn-default ${status.index==0 ?'active':''}"><spring:message code="label.analysis.type.${typeValue}" text="${typeValue}" /><input
							${status.index==0? 'checked' :''} name="type" type="radio" value="${type}"></label>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label for="language" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.report.template.language" />'><spring:message code="label.language"
					text="Language" /></label>
			<div class="col-sm-9" data-trick-info='language'>
				<select name="language" class="form-control" required="required">
					<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
					<c:forEach items="${languages}" var="language">
						<option value="${language.id}"><spring:message text="${language.name}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label for="label" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.report.template.label" />'> <spring:message
					code="label.title" />
			</label>
			<div class="col-sm-9" data-trick-info='label'>
				<textarea name="label" id="reportTemplate.label" class="form-control resize_vectical_only" rows="6"  required="required"  maxlength="255"></textarea>
			</div>
		</div>

		<div class="form-group">
			<label for="version" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.report.template.version" />'> <spring:message
					code="label.version" />
			</label>
			<div class="col-sm-9" data-trick-info='version'>
				<input name="version" list="dataListVersions" id="reportTemplate.version" class="form-control" type="text" required="required" size="255" placeholder="0.0.1" pattern="((\d+\.*)*([A-Za-z]+\.*)*(\d+\.*)*)+"/>
			</div>
		</div>

		<div class="form-group">
			<label for="file" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.report.template.file" />'> <spring:message
					code="label.report.template.file" />
			</label>
			<div class="col-sm-9" data-trick-info='file'>
				<div class="form-inline">
					<div class="input-group-btn">
						<input id="reportTemplate.file" type="file" accept=".docx" name="file" style="display: none;" /> <input id="reportTemplate.file.info" style="width: 88%;" name="filename" class="form-control"
							readonly="readonly" required="required" />
						<button class="btn btn-primary" type="button" id="reportTemplate.file.browse.button" name="browse" style="margin-left: -5px;">
							<spring:message code="label.action.browse" text="Browse" />
						</button>
					</div>
				</div>
			</div>
		</div>
		
		<datalist style="display: none" id="dataListVersions">
			<c:forEach items="${versions}" var="version">
				<option value='<spring:message text="${version}"/>'/>
			</c:forEach>
		</datalist>
		
		<button type="submit" name="submit" style="display: none;"></button>
	</form>
</div>