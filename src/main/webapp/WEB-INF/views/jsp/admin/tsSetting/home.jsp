<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="tab-pane" id="tab-ts-setting">
	<div class="section" id="section_ts_setting">
		<spring:message code="label.yes_no.yes" text="Yes" var="yes" />
		<spring:message code="label.yes_no.no" text="No" var="no" />
		<fieldset>
			<legend>
				<spring:message code='label.setting.static' />
			</legend>
			<div class='form-horizontal col-lg-12'>
				<div class='form-group'>
					<label class="control-label col-sm-3"><spring:message code='label.setting.static.allow.otp' /></label>
					<div class='col-sm-offset-5 col-sm-4'>
						<div class="btn-group" data-toggle="buttons">
							<label class="btn btn-sm btn-default ${enabledOTP?'active':''} disabled">${yes}</label> <label class="btn btn-sm btn-default ${enabledOTP?'':'active'}  disabled">${no}</label>
						</div>
					</div>
				</div>

				<c:if test="${enabledOTP}">
					<div class='form-group'>
						<label class="control-label col-md-3"><spring:message code='label.setting.static.force.otp' /></label>
						<div class='col-sm-offset-5 col-sm-4'>
							<div class="btn-group" data-toggle="buttons">
								<label class="btn btn-sm btn-default ${forcedOTP?'active':''} disabled">${yes}</label> <label class="btn btn-sm btn-default ${forcedOTP?'':'active'}  disabled">${no}</label>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</fieldset>
		<fieldset>
			<legend>
				<spring:message code='label.setting.dynamic' />
			</legend>
			<c:set value="${tsSettings}" var="tsGenericSettings" scope="request"/>
			<jsp:include page="section.jsp" />
		</fieldset>
		<fieldset>
			<legend>
				<spring:message code='label.setting.dynamic.ticketing.system' />
			</legend>
			<c:set value="${ticketingSystems}" var="tsGenericSettings" scope="request"/>
			<jsp:include page="section.jsp" />
		</fieldset>
	</div>
</div>