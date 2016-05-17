<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<form name="standard" action="/Create?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="standard_form" method="post">
	<input type="hidden" value="-1" name="id" id="id">
	<div class="form-group">
		<label for="label" class="col-sm-2 control-label"><spring:message code="label.norm.label" />
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