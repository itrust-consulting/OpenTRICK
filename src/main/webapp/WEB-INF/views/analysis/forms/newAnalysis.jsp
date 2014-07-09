<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<jsp:include page="../../successErrors.jsp" />
<div id="form_add_analysis">
	<input type="hidden" name="id" value="-1" id="analysis_id">
	<div class="form-group">
		<label for="customer" class="col-sm-2 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
		</label>
		<div class="col-sm-10" id="analysiscustomercontainer">
			<select class="form-control" name="analysiscustomer">
				<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose ..." />
					<c:forEach items="${customers}" var="customer">
						<option value="${customer.id}">${customer.organisation}</option>
					</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-group">
		<label for="language" class="col-sm-2 control-label"> <spring:message code="label.analysis.language" text="Language" />
		</label>
		<div class="col-sm-10" id="analysislanguagecontainer">
			<select name="analysislanguage" class="form-control">
				<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose ..." />
					<c:forEach items="${languages}" var="language">
						<option value="${language.id}">${language.name}</option>
					</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-group">
		<label for="profile" class="col-sm-2 control-label"> <spring:message code="label.analysis.profileAnalysis" text="Profile" />
		</label>
		<div class="col-sm-10">
			<select name="profile" id="analysis_profile" class="form-control">
				<option value="-1"><spring:message code="label.select.analysisProfile" text="Select a profile" /></option>
				<c:forEach items="${profiles}" var="profile">
					<option value="${profile.id }"><spring:message text="${profile.identifier}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-group">
		<label for="label" class="col-sm-2 control-label"> <spring:message code="label.analysis.description" text="Description" />
		</label>
		<div class="col-sm-10">
			<textarea name="label" id="analysis_label" class="form-control resize_vectical_only"></textarea>
		</div>
	</div>
	<div class="form-group">
		<label for="author" class="col-sm-2 control-label"> <spring:message code="label.analysis.author" text="Author" />
		</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" name="author" value="${author}" />
		</div>
	</div>
	<div class="form-group">
		<label for="version" class="col-sm-2 control-label"> <spring:message code="label.analysis.version" text="Version" />
		</label>
		<div class="col-sm-10">
			<input name="version" id="analysis_version" class="form-control" type="text" value="0.0.1" />
		</div>
	</div>
</div>