<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty languages}">
	<div class="form-group">
		<label for="measurelanguageselect" class="col-sm-2 control-label"> <spring:message code="label.language" text="Language" /></label>
		<div class="col-sm-10">
			<select id="measurelanguageselect" class="form-control" style="width: auto;">
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
				<label for="domain_${language.id}" class="col-sm-2 control-label"> <spring:message code="label.measure.domain" text="Domain" /></label>
				<div class="col-sm-10">
					<input name="domain_${language.id}" id="measure_domain_${language.id}" class="form-control" type="text" />
				</div>
			</div>
			<div class="form-group">
				<label for="description_${language.id}" class="col-sm-2 control-label"> <spring:message code="label.measure.description" text="Description" /></label>
				<div class="col-sm-10">
					<textarea name="description_${language.id}" rows="15" id="measure_description_${language.id}" class="form-control"></textarea>
				</div>
			</div>
		</div>
	</c:forEach>
</c:if>