<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="tab-otp" class="tab-pane">
	<div class="section" id="section_user_otp">
		<div class="page-header tab-content-header">
			<h3>
				<spring:message code="label.user.title.otp.options" text="Sign-in settings" />
			</h3>
		</div>
		<div style="margin: 0 auto; max-width: 650px;">
			<form id="user-otp-form" name="user-otp-form" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/Account/OTP/Update">
				<c:set var="using2FA" value="${user.using2FA}" />
				<spring:message code="label.action.enable" text="Enable" var="enable" />
				<spring:message code="label.action.disable" text="Disable" var="disable" />

				<div class='form-group' data-index='0'>
					<label class="control-label col-md-6"><spring:message code='label.user.otp.setting' /></label>
					<div class='col-md-6 text-center'>
						<div class="btn-group" data-toggle="buttons">
							<c:choose>
								<c:when test="${forcedOTP}">
								<spring:message code="label.user.opt.forced" var="titleForce"/>
									<label class="btn btn-sm btn-default active disabled" title='${titleForce}' >${enable}</label>
									<label class="btn btn-sm btn-default disabled" title='${titleForce}' >${disable}</label>
								</c:when>
								<c:otherwise>
									<label class="btn btn-sm btn-default ${using2FA?'active':''}">${enable}<input ${using2FA?'checked':''} name="using2FA" type="radio" value="true"
										onchange="updateUserOtp()"></label>
									<label class="btn btn-sm btn-default ${using2FA?'':'active'}">${disable}<input ${using2FA?'':'checked'} name="using2FA" type="radio" value="false"
										onchange="updateUserOtp()"></label>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<c:if test="${using2FA || forcedOTP}">
					<c:set value="${not empty user.secret}" var="hasSecret" />
					<div class='form-group' data-index='1'>
						<label class="control-label col-md-6"><spring:message code='label.user.otp.use.application' text='Use mobile application' /></label>
						<div class='col-md-6 text-center'>
							<div class="btn-group" data-toggle="buttons">
								<label class="btn btn-sm btn-default ${hasSecret?'active':''}">${enable}<input ${hasSecret?'checked':''} name="useApplication" type="radio" value="true"
									onchange="updateUserOtp()"></label> <label class="btn btn-sm btn-default ${hasSecret?'':'active'}">${disable}<input ${hasSecret?'':'checked'} name="useApplication"
									type="radio" value="false" onchange="updateUserOtp()"></label>
							</div>
						</div>
					</div>
					<div class='form-group' id='application-qrcode' data-index='2'>
						<c:if test="${not empty qrcode}">
							<label class="control-label col-md-6"><spring:message code='label.user.otp.use.qrcode' text='Please scan this following qrcode' /></label>
							<div class='col-md-6 text-center'>
								<img alt="<spring:message code='label.user.qrcode' text='Qrcode'/>" src="data:image/png;base64,${qrcode}">
							</div>
						</c:if>
					</div>
				</c:if>
			</form>
		</div>
	</div>
</div>