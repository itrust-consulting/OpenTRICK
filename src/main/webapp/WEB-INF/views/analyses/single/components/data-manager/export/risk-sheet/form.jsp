<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:message code='label.nil' var="nil" />
<spring:message code='label.all' var="all" />
<spring:message code='label.compliant' var="compliant" />
<fieldset>
	<spring:message text="${fn:replace(item.name,'-','_')}" var="viewName" />
	<legend>
		<spring:message code="label.title.data_manager.export.${viewName}" />
	</legend>
	<div class='alert alert-sm alert-info' style="margin-bottom: 15px">
		<spring:message code="info.data_manager.export.${viewName}" />
	</div>
	<form class='form form-horizontal' name='filter'>
		<input value="${type}" name="type" hidden="hidden">
		<c:if test="${type == 'REPORT'}">
			<div class="form-group">
				<label for="cssf" class="control-label col-xs-4"> <spring:message code="label.risk_sheet.report.type" /></label>
				<div class="col-xs-5">
					<div class="btn-group btn-group-justified" data-toggle="buttons">
						<label class="btn btn-default active"><spring:message code="label.type.normal" /><input name="cssf" checked="checked" type="radio" value="false"></label> <label
							class="btn btn-default"><spring:message code="label.type.cssf" /><input name="cssf" type="radio" value="true"></label>
					</div>
				</div>
			</div>
		</c:if>
		<div class='form-group'>
			<c:set value="${parameters['cssfImpactThreshold']}" var="cssfImpactThreshold" />
			<label class='control-label col-xs-4'><spring:message code="label.parameter.simple.cssf.impact_threshold" /></label>
			<div class='col-xs-5'>
				<select name="filter.impact" class="form-control">
					<c:forEach items="${impacts}" var="impact">
						<option value="${impact.level}" ${impact.level==cssfImpactThreshold.value?'selected':''}><spring:message code='label.level.index' arguments="${impact.level}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class='form-group'>
			<c:set value="${parameters['cssfProbabilityThreshold']}" var="cssfProbabilityThreshold" />
			<label class='control-label col-xs-4'><spring:message code="label.parameter.simple.cssf.probability_threshold" /></label>
			<div class='col-xs-5'>
				<select name="filter.probability" class="form-control">
					<c:forEach items="${probabilities}" var="probability">
						<option value="${probability.level}" ${probability.level==cssfProbabilityThreshold.value?'selected':''}><spring:message code='label.level.index'
								arguments="${probability.level}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class='form-group'>
			<label class='control-label col-xs-4'><spring:message code="label.parameter.simple.cssf.direct_size" /></label>
			<c:set value="${parameters['cssfDirectSize']}" var="cssfDirectSize" />
			<div class='col-xs-5'>
				<select name="filter.direct" class="form-control">
					<option value="-2" ${cssfDirectSize.value==-2?'selected':''}>${nil}</option>
					<option value="-1" ${cssfDirectSize.value==-1?'selected':''}>${all}</option>
					<option value="0" ${cssfDirectSize.value==0?'selected':''}>${compliant}</option>
					<c:forEach begin="5" end="1000" step="5" var="cssfCount">
						<option value="${cssfCount}" ${cssfCount==cssfDirectSize.value?'selected':''}>${cssfCount}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class='form-group'>
			<c:set value="${parameters['cssfIndirectSize']}" var="cssfIndirectSize" />
			<label class='control-label col-xs-4'><spring:message code="label.parameter.simple.cssf.indirect_size" /></label>
			<div class='col-xs-5'>
				<select name="filter.indirect" class="form-control">
					<option value="-2" ${cssfIndirectSize.value==-2?'selected':''}>${nil}</option>
					<option value="-1" ${cssfIndirectSize.value==-1?'selected':''}>${all}</option>
					<option value="0" ${cssfIndirectSize.value==0?'selected':''}>${compliant}</option>
					<c:forEach begin="5" end="1000" step="5" var="cssfCount">
						<option value="${cssfCount}" ${cssfIndirectSize.value==cssfCount?'selected':''}>${cssfCount}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class='form-group'>
			<c:set value="${parameters['cssfCIASize']}" var="cssfCIASize" />
			<label class='control-label col-xs-4'><spring:message code="label.parameter.simple.cssf.cia_size" /></label>
			<div class='col-xs-5'>
				<select name="filter.cia" class="form-control">
					<option value="-2" ${cssfCIASize.value==-2?'selected':''}>${nil}</option>
					<option value="-1" ${cssfCIASize.value==-1?'selected':''}>${all}</option>
					<option value="0" ${cssfCIASize.value==0?'selected':''}>${compliant}</option>
					<c:forEach begin="5" end="1000" step="5" var="cssfCount">
						<option value="${cssfCount}" ${cssfCIASize.value==cssfCount?'selected':''}>${cssfCount}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class='form-group'>
			<label class='control-label col-xs-4'><spring:message code="label.title.owner" /></label>
			<div class='col-xs-5'>
				<select name="owner" class="form-control">
					<option value="">${all}</option>
					<c:forEach items="${owners}" var="owner">
						<spring:message var="ownerValue" text="${owner}" />
						<option value="${ownerValue}">${ownerValue}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	</form>
</fieldset>