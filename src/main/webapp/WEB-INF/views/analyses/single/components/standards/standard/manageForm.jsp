<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="section_manage_standards" class="tab-content" style="height: 500px; overflow-x: auto; margin-top: -10px">
	<div class="tab-pane active">
		<ul id="menu_manage_standards" class="nav nav-pills bordered-bottom">
			<li><a href="#addStandardTab" data-toggle="tab"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<spring:message code="label.action.create" /></a></li>
			<li><a onclick="return addStandard();" href="#"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<spring:message code="label.action.add" /></a></li>
			<li data-trick-selectable="true" data-trick-check="isAnalysisOnlyStandard('#section_manage_standards')" class="disabled"><a onclick="return editStandard();" href="#"><span
					class="glyphicon glyphicon-edit primary"></span>&nbsp;<spring:message code="label.action.edit" /></a></li>
			<li data-trick-selectable="true" class="disabled pull-right"><a onclick="return removeStandard();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<spring:message
						code="label.action.remove" /></a></li>
		</ul>
		<c:if test="${!empty(currentStandards)}">
			<table class="table table-hover">
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th><spring:message code="label.norm.label" /></th>
						<th><spring:message code="label.norm.version" /></th>
						<th colspan="3"><spring:message code="label.norm.description" /></th>
						<th><spring:message code="label.norm.computable" /></th>
						<th><spring:message code="label.norm.type" /></th>
						<th><spring:message code="label.norm.analysisOnly" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${currentStandards}" var="standard">
						<tr ondblclick="return editStandard(this);" data-trick-id="${standard.id}" data-trick-analysisOnly="${standard.analysisOnly}" data-trick-type="${standard.type}"
							data-trick-computable="${standard.computable}">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_manage_standards','#menu_manage_standards');"></td>
							<td><spring:message text="${standard.label}" /></td>
							<td><spring:message text="${standard.version}" /></td>
							<td colspan="3"><spring:message text="${standard.description}" /></td>
							<td style="text-align: center"><spring:message code="label.${standard.computable?'yes':'no'}" /></td>
							<td style="text-align: center"><spring:message code="label.norm.standard_type.${fn:toLowerCase(standard.type)}" /></td>
							<td style="text-align: center"><spring:message code="label.${standard.analysisOnly?'yes':'no'}" /></td>
						</tr>
					</c:forEach>
					<c:if test="${currentStandards!=null?currentStandards.size()==0:true}">
						<tr>
							<td colspan="6"><spring:message code="label.no_standards" /></td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</c:if>
		<c:if test="${empty(currentStandards)}">
			<spring:message code="label.no_standards" />
		</c:if>
	</div>
	<div id="addStandardTab" class="tab-pane">
		<form name="standard" action="/Create?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="standard_form" method="post">
			<input type="hidden" value="-1" name="id" id="id">
			<div class="form-group">
				<label for="label" class="col-sm-2 control-label"> <spring:message code="label.norm.label" />
				</label>
				<div class="col-sm-10">
					<input name="label" id="standard_label" class="form-control" type="text" />
				</div>
			</div>
			<div class="form-group">
				<label for="description" class="col-sm-2 control-label"> <spring:message code="label.norm.description" />
				</label>
				<div class="col-sm-10">
					<input name="description" id="standard_description" class="form-control" type="text" />
				</div>
			</div>
			<div class="form-group">
				<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.norm.computable" />
				</label>
				<div class="col-sm-10" align="center">
					<input name="computable" id="standard_computable" class="checkbox" type="checkbox" checked />
				</div>
			</div>
			<div class="panel panel-primary">
				<div class="panel-body" align="center">
					<label class="col-sm-12"><spring:message code="label.norm.standard_type" /></label> <label class="radio-inline col-sm-offset-2 col-sm-4"> <input type="radio"
						name="type" value="NORMAL"> <spring:message code="label.norm.standard_type.normal" /></label> <label class="radio-inline col-sm-4"> <input type="radio" name="type"
						value="ASSET"> <spring:message code="label.norm.standard_type.asset" />
					</label>
				</div>
			</div>

		</form>
	</div>
</div>