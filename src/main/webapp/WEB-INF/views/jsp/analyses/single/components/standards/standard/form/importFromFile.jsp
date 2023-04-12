<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<form name="importStandardFromFile" method="post" action="/Import?${_csrf.parameterName}=${_csrf.token}" class="form-inline" id="importStandardFromFile" enctype="multipart/form-data">
	<input type="hidden" value="-1" name="id" id="importStandardFromFileId">
	<fieldset style="margin-bottom: 10px">
		<legend data-helper-content='<spring:message code="help.norm.type" />' ><spring:message code="label.norm.standard_type" /></legend>
		<div class='col-sm-12' data-trick-info="type">
			<select name="type" class="form-control" style="width: 97%">
				<option value="NORMAL"> <spring:message code="label.norm.standard_type.normal" /></option>
				<option value="ASSET"> <spring:message code="label.norm.standard_type.asset" /></option>
			</select>
		</div>
	</fieldset>
	<fieldset style="margin-bottom: 10px">
		<legend data-helper-content='<spring:message code="help.norm.name" />' ><spring:message code="label.norm.name" /></legend>
		<div class='col-sm-12' data-trick-info="name">
			<input name="name" id="import_standard_name" class="form-control" style="width: 97%" type="text" />
		</div>
	</fieldset>
	<fieldset >
		<legend for="name" data-helper-content='<spring:message code="label.norm.import.choose_file" />' > <spring:message code="label.file" text="Choose the file" /></legend>
		<div class="col-lg-12">
			<div class="input-group-btn">
				<input id="importStandardFromFileInputFile" type="file" accept=".xls,.xlsx,.xlsm"
					onchange='{$("#upload-file-info").prop("value",$(this).prop("value")); checkExtention($("#upload-file-info").val(),"xls,xlsx,xlsm","#standardModalImportBtn");}'
					name="file" style="display: none;" /> <input id="upload-file-info" class="form-control" readonly="readonly" required="required" style="width: 88%;"/>
				<button class="btn btn-primary" type="button" id="browse-button" onclick="$('input[id=importStandardFromFileInputFile]').click();" style="margin-left: -5px;">
					<spring:message code="label.action.browse" text="Browse" />
				</button>
			</div>
		</div>
	</fieldset>
</form>