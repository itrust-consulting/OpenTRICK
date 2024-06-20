<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty languages}">
	<div class="form-group">
		<label for="measurelanguageselect" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.language" />' > <spring:message code="label.language" text="Language" /></label>
		<div class="col-sm-9 text-center">
			<select id="measurelanguageselect" class="form-control">
				<c:forEach items="${languages}" var="language">
					<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}">
						<spring:message text="${language.name}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>
	<c:forEach items="${languages}" var="language">
		<div data-trick-id="${language.id}" ${language.id != selectedLanguage.id?'hidden="true"':""}>
			<div class="form-group">
				<label for="domain_${language.id}" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.domain" />' > <spring:message code="label.measure.domain" text="Domain" /></label>
				<div class="col-sm-9">
					<input name="domain_${language.id}" id="measure_domain_${language.id}" class="form-control" type="text" />
				</div>
			</div>
			<div class="form-group">
				<label for="description_${language.id}" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.description" />' > <spring:message code="label.measure.description" text="Description" /></label>
				<div class="col-sm-9">
					<textarea name="description_${language.id}" rows="15" id="measure_description_${language.id}" class="form-control"></textarea>
				</div>
			</div>
		</div>
	</c:forEach>
</c:if>