<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div id="tab-profile" class="tab-pane active">
	<div class="section" id="section_profile">
		<div class="page-header tab-content-header">
			<h3>
				<spring:message code="label.user.title.profile" text="User profile" />
			</h3>
		</div>
		<div style="margin: 0 auto; max-width: 800px;">
			<form id="updateprofileform" name="updateprofileform" onsubmit="return updateProfile('updateprofileform');" class="form-horizontal" method="post"
				action="${pageContext.request.contextPath}/Account/Update">
				<fieldset>
					<legend>
						<spring:message code="label.user.title.login_information" text="Login Information" />
					</legend>
					<div class="form-group">
						<label for="login" class="col-sm-3 control-label"> <spring:message code="label.user.login" text="Username" />
						</label>
						<div class="col-sm-9">
							<input class="form-control" disabled="disabled" readonly="readonly" value="${user.login}" />
						</div>
					</div>
					<c:if test="${user.connexionType!=1}">
						<div class="form-group">
							<label for="oldPassword" class="col-sm-3 control-label"> <spring:message code="label.user.current_password" text="Current Password" />
							</label>
							<div class="col-sm-9">
								<input type="password" id="currentPassword" name="currentPassword" class="form-control" required="required" />
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-sm-3 control-label"> <spring:message code="label.user.password" text="Password" />
							</label>
							<div class="col-sm-9">
								<input type="password" id="password" name="password" class="form-control" />
							</div>
						</div>
						<div class="form-group">
							<label for="repeatPassword" class="col-sm-3 control-label"> <spring:message code="label.user.repeat_password" text="Repeat password" />
							</label>
							<div class="col-sm-9">
								<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" />
							</div>
						</div>
					</c:if>
				</fieldset>
				<c:if test="${allowedTicketing}">
					<fieldset>
						<legend>
							<spring:message code="label.user.title.ticketing.system.credential" text="Ticketing system credential" />
						</legend>
						<div class="form-group">
							<label for="ticketingUsername" class="col-sm-3 control-label"> <spring:message code="label.user.login" text="Username" />
							</label>
							<div class="col-sm-9">
								<input class="form-control" id='ticketingUsername' name="ticketingUsername" value="${user.userSettings['user-titcketing-credential-username']}" />
							</div>
						</div>
						<div class="form-group">
							<label for="ticketingPassword" class="col-sm-3 control-label">
								<c:choose>
									<c:when test="${isTokenAuthentication}">
										<spring:message code="label.user.api.token" text="Token" />
									</c:when>
									<c:otherwise>
										<spring:message code="label.user.password" text="Password" />
									</c:otherwise>
								</c:choose>
							</label>
							<div class="col-sm-9">
								<input type="password" id="ticketingPassword" name="ticketingPassword" class="form-control" />
							</div>
						</div>
					</fieldset>
				</c:if>
				<fieldset>
					<legend>
						<spring:message code="label.user.title.personal_information" text="Personal Information" />
					</legend>
					<div class="form-group">
						<label for="firstName" class="col-sm-3 control-label"> <spring:message code="label.user.first_name" text="Firstname" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="firstName" name="firstName" class="form-control" required value="${user.firstName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-3 control-label"> <spring:message code="label.user.last_name" text="Lastname" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="lastName" name="lastName" class="form-control" required value="${user.lastName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-3 control-label"> <spring:message code="label.user.email" text="Email address" />
						</label>
						<span class="visible-xs clearfix"></span>
						<div class="${user.emailValidated? 'col-sm-9' : 'col-xs-10 col-sm-7' }">
							<input type="email" id="email" name="email" class="form-control" ${user.connexionType==1?'disabled="disabled" readonly="readonly"':''} required value="${user.email}" />
						</div>
						<c:if test="${not user.emailValidated }">
							<div class='col-xs-2 col-sm-2 text-right'>
								<button type="button" class='btn btn-primary' onclick="validateUserEmail()">
									<spring:message code="label.action.check"/>
								</button>
							</div>
						</c:if>
					</div>
				</fieldset>
				<fieldset>
					<legend>
						<spring:message code='label.user.title.account.access.info' text='Account access information' />
					</legend>
					<div class="form-group">
						<label for="connexionType" class="col-sm-3 control-label"><spring:message code="label.user.connexion.type" text="Authentication type" /></label>
						<div class="col-sm-9 text-center">
							<div class="btn-group" data-toggle="buttons" id="radioConnexionType">
								<label class="btn ${user.connexionType==-1?'btn-primary':'btn-default'} disabled"><spring:message code="label.user.connexion.standard" text="Standard" /><input
									name="connexionType" type="radio" readonly="readonly" value="-1"></label> <label class="btn ${user.connexionType==0?'btn-primary':'btn-default'} disabled"><spring:message
										code="label.user.connexion.both" text="Both" /><input name="connexionType" type="radio" readonly="readonly" value="0" checked="checked"></label> <label
									class="btn ${user.connexionType==1?'btn-primary':'btn-default'} disabled"><spring:message code="label.user.connexion.ldap" text="LDAP" /><input
									name="connexionType" type="radio" readonly="readonly" value="1"></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="roles" class="col-sm-3 control-label"> <spring:message code="label.user.account.access.level" text="Access level" /></label>
						<div class="col-sm-9 text-center">
							<div class="btn-group" data-toggle="buttons">
								<c:forEach items="${roles}" var="role">
									<c:set var="role_value" value="${fn:replace(role,'ROLE_','')}" />
									<label class="btn disabled ${user.isAutorised(role)? 'btn-primary' : 'btn-default'}"><spring:message code="label.role.${fn:toLowerCase(role_value)}"
											text="${role_value}" /></label>
								</c:forEach>
							</div>
						</div>
					</div>
				</fieldset>
				<fieldset>
					<legend>
						<spring:message code="label.user.title.application_settings" text="Application Settings" />
					</legend>
					<div class="form-group">
						<label for="locale" class="col-sm-3 control-label"> <spring:message code="label.user.default_ui_language" text="Default User Interface Language" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="locale" id="locale">
								<option value="en" ${user.locale.equals("en")?"selected='selected'":"" } class="list-group-item pull-left"
									style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/en.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;">English</option>
								<option value="fr" ${user.locale.equals("fr")?"selected='selected'":"" } class="list-group-item pull-left"
									style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/fr.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;">Français</option>
							</select>
						</div>
						<input type="hidden" value="${user.locale}" id="perviouslanguage" />
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-9 text-center">
							<button class="btn btn-primary" type="submit">
								<spring:message code="label.action.save" text="Save" />
							</button>
						</div>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
</div>