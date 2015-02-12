<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<jsp:include page="../../../template/successErrors.jsp" />
<c:if test="${analysis != null}">
	<div id="form_edit_analysis">
		<input type="hidden" name="id" value="${analysis.id}" id="analysis_id">
		<div class="form-group">
			<label for="identifier" class="col-sm-2 control-label"> <spring:message code="label.analysis.identifier" text="Identifier" />
			</label>
			<div class="col-sm-10">
				<input name="identifier" id="analysis_identifier" class="form-control" type="text" value="${analysis.identifier}" readonly />
			</div>
		</div>
		<c:if test="${analysis.profile == false }">
			<div class="form-group">
				<label for="version" class="col-sm-2 control-label"> <spring:message code="label.analysis.version" text="Version" />
				</label>
				<div class="col-sm-10">
					<input name="version" id="analysis_version" class="form-control" type="text" value="${analysis.version}" readonly />
				</div>
			</div>
		</c:if>
		<div class="form-group">
			<label for="creationDate" class="col-sm-2 control-label"> <spring:message code="label.analysis.creation_date" text="Creation date" />
			</label>
			<div class="col-sm-10">
				<input name="creationDate" id="analysis_creationDate" class="form-control" type="text" value="${analysis.creationDate}" readonly />
			</div>
		</div>
		<c:if test="${not(analysis.profile || empty analysis.basedOnAnalysis)}">
			<div class="form-group">
				<label for="basedOnAnalysis" class="col-sm-2 control-label"> <spring:message code="label.analysis.based_on_analysis" text="Based on analysis version" />
				</label>
				<div class="col-sm-10">
					<input name="basedOnAnalysis" id="analysis_basedOnAnalysis" class="form-control" type="text" value="${analysis.basedOnAnalysis.version}" readonly />
				</div>
			</div>
		</c:if>
		<div class="form-group">
			<label for="owner" class="col-sm-2 control-label"> <spring:message code="label.analysis.owner" text="Owner" />
			</label>
			<div class="col-sm-10">
				<input name="owner" id="analysis_owner" class="form-control" type="text" value="${analysis.owner.firstName} ${analysis.owner.lastName}" readonly />
			</div>
		</div>
		<c:if test="${analysis.profile == false }">
			<div class="form-group">
				<label for="hasData" class="col-sm-2 control-label"> <spring:message code="label.analysis.has_data" text="Has Data" />
				</label>
				<div class="col-sm-10">
					<input name="hasData" id="analysis_hasData" class="form-control" type="checkbox" ${analysis.hasData()?"checked='checked'":""} disabled="disabled" />
				</div>
			</div>
		</c:if>
		<c:if test="${analysis.profile == false }">
			<div class="form-group">
				<label for="customer" class="col-sm-2 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
				</label>
				<div class="col-sm-10" id="analysiscustomercontainer">
					<select name="analysiscustomer" class="form-control">
						<c:forEach items="${customers}" var="customer">
							<option value="${customer.id}" ${customer.equals(analysis.customer)?"selected='selected'":""}>${customer.organisation}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</c:if>
		<div class="form-group">
			<label for="language" class="col-sm-2 control-label"> <spring:message code="label.analysis.language" text="Language" />
			</label>
			<div class="col-sm-10" id="analysislanguagecontainer">
				<select name="analysislanguage" class="form-control">
					<c:forEach items="${languages}" var="language">
						<option value="${language.id}" ${language.equals(analysis.language)?"selected='selected'":""}>${language.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="comment" class="col-sm-2 control-label"> <spring:message code="label.analysis.description" text="Description" />
			</label>
			<div class="col-sm-10">
				<textarea name="comment" class="form-control resize_vectical_only" rows="5" required="required"><spring:message text="${analysis.label}"/></textarea>
			</div>
		</div>
		<div class="form-group">
			<label for="uncertainty" class="col-sm-2 control-label"> <spring:message code="label.analysis.uncertainty" text="Uncertainty" />
			</label>
			<div class="col-sm-10">
				<input type="checkbox" name="uncertainty" class="form-control" ${analysis.uncertainty?"checked='checked'":''}>
			</div>
		</div>
		<div class="form-group">
			<label for="cssf" class="col-sm-2 control-label"> <spring:message code="label.analysis.cssf" text="CSSF" /></label>
			<div class="col-sm-10">
				<input type="checkbox" name="cssf" class="form-control" ${analysis.cssf?"checked='checked'":''}>
			</div>
		</div>
	</div>
</c:if>