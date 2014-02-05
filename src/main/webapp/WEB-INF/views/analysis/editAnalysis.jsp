<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<jsp:include page="../successErrors.jsp" />
<c:if test="${analysis != null}">
	<div id="form_edit_analysis">
		<input type="hidden" name="id" value="${analysis.id}" id="analysis_id">
		<div class="form-group">
			<label for="identifier" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.identifier" text="Identifier" />
			</label>
			<div class="col-sm-10">
				<input name="identifier" id="analysis_identifier"
					class="form-control" type="text" value="${analysis.identifier}"
					readonly />
			</div>
		</div>
		<div class="form-group">
			<label for="version" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.version" text="Version" />
			</label>
			<div class="col-sm-10">
				<input name="version" id="analysis_version" class="form-control"
					type="text" value="${analysis.version}" readonly />
			</div>
		</div>
		<div class="form-group">
			<label for="creationDate" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.creationdate" text="Creation Date" />
			</label>
			<div class="col-sm-10">
				<input name="creationDate" id="analysis_creationDate"
					class="form-control" type="text" value="${analysis.creationDate}"
					readonly />
			</div>
		</div>
		<div class="form-group">
			<label for="basedOnAnalysis" class="col-sm-2 control-label">
				<spring:message code="label.analysis.basedOnAnalysis"
					text="Based On Analysis Version" />
			</label>
			<div class="col-sm-10">
				<input name="basedOnAnalysis" id="analysis_basedOnAnalysis"
					class="form-control" type="text"
					value="${analysis.basedOnAnalysis!=null?analysis.basedOnAnalysis.version:'None'}"
					readonly />
			</div>
		</div>
		<div class="form-group">
			<label for="owner" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.owner" text="Owner" />
			</label>
			<div class="col-sm-10">
				<input name="owner" id="analysis_owner" class="form-control"
					type="text" value="${analysis.owner.login}" readonly />
			</div>
		</div>
		<div class="form-group">
			<label for="hasData" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.hasData" text="Has Data" />
			</label>
			<div class="col-sm-10">
				<input name="hasData" id="analysis_hasData" class="form-control"
					type="checkbox" ${analysis.hasData()?"checked='checked'":""}
					disabled="disabled" />
			</div>
		</div>
		<div class="form-group">
			<label for="customer" class="col-sm-2 control-label"> <spring:message
					code="label.customer.organisation" text="Customer" />
			</label>
			<div class="col-sm-10" id="analysiscustomercontainer">
				<select name="analysiscustomer">
					<c:forEach items="${customers}" var="customer">
						<option value="${customer.id}"
							${customer.equals(analysis.customer)?"selected='selected'":""}>${customer.organisation}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="language" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.language" text="Language" />
			</label>
			<div class="col-sm-10" id="analysislanguagecontainer">
				<select name="analysislanguage">
					<c:forEach items="${languages}" var="language">
						<option value="${language.id}"
							${language.equals(analysis.language)?"selected='selected'":""}>
							${language.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="label" class="col-sm-2 control-label"> <spring:message
					code="label.analysis.description" text="Description" />
			</label>
			<div class="col-sm-10">
				<textarea name="label" id="analysis_label" class="form-control">${analysis.label}</textarea>
			</div>
		</div>
	</div>
</c:if>