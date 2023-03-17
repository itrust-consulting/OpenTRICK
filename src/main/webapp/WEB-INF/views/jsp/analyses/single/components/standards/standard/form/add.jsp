<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<form name="standard" action="/Create?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="standard_form" method="post">
	<input type="hidden" value="-1" name="id" id="standard_formId">
	<div class="form-group">
		<label class="control-label col-sm-3" data-helper-content='<spring:message code="help.norm.type" />' ><spring:message code="label.norm.standard_type" /></label>
		<div class="col-sm-9 text-center">
			<label class="radio-inline col-sm-offset-2 col-sm-4"> <input type="radio" name="type" value="NORMAL"> <spring:message code="label.norm.standard_type.normal" /></label>
			<label class="radio-inline col-sm-4"> <input type="radio" name="type" value="ASSET"> <spring:message code="label.norm.standard_type.asset" />
			</label>
		</div>
	</div>
	<div class="form-group">
		<label for="name" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.norm.name" />' ><spring:message code="label.norm.name" /> </label>
		<div class="col-sm-9">
			<input name="name" id="standard_name" class="form-control" type="text" />
		</div>
	</div>
	<div class="form-group">
		<label for="label" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.norm.label" />' ><spring:message code="label.norm.label" /> </label>
		<div class="col-sm-9">
			<input name="label" id="standard_label" class="form-control" type="text" />
		</div>
	</div>
	<div class="form-group">
		<label for="computable" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.norm.computable" />' > <spring:message code="label.norm.computable" />
		</label>
		<div class="col-sm-9" align="center">
			<input name="computable" id="standard_computable" class="checkbox" type="checkbox" checked />
		</div>
	</div>
	<div class="form-group">
		<label for="description" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.norm.description" />' > <spring:message code="label.norm.description" />
		</label>
		<div class="col-sm-9">
			<textarea name="description" id="standard_description" class="form-control resize_vectical_only" rows="16"></textarea>
		</div>
	</div>
</form>