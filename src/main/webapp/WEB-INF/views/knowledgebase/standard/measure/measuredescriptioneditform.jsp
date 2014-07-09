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
					<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}">${language.name}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<c:forEach items="${measuredescriptionTexts}" var="measureDescriptionText">
		<div trick-id="${measureDescriptionText.language.id}" ${measureDescriptionText.language.id != selectedLanguage.id?'hidden="true"':""}>
			<div class="form-group">
				<label for="domain_${measureDescriptionText.language.id}" class="col-sm-2 control-label"> <spring:message code="label.measure.domain" text="Domain" /></label>
				<div class="col-sm-10">
					<input name="domain_${measureDescriptionText.language.id}" id="measure_domain_${measureDescriptionText.language.id}" class="form-control" type="text" value="${measureDescriptionText.domain}" />
				</div>
			</div>
			<div class="form-group">
				<label for="description_${measureDescriptionText.language.id}" class="col-sm-2 control-label"> <spring:message code="label.measure.description" text="Description" /></label>
				<div class="col-sm-10">
					<input name="description_${measureDescriptionText.language.id}" id="measure_description_${measureDescriptionText.language.id}" class="form-control" type="text"
						value="${measureDescriptionText.description}" />
				</div>
			</div>
		</div>
	</c:forEach>
</c:if>