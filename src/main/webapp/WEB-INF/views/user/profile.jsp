<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="profile" class="tab-pane active">
	<div class="section" id="section_profile">
		<span id="profileInfo" hidden="hidden"></span>
		<div style="margin: 0 auto; max-width: 650px;">
			<form id="updateprofileform" name="updateprofileform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/Profile/Update">
				<div class="page-header">
					<h3>
						<spring:message code="label.user.title.login_information" text="Login Information" />
					</h3>
				</div>
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
							<input type="password" id="password" name="password" class="form-control" required="required" />
						</div>
					</div>
					<div class="form-group">
						<label for="repeatPassword" class="col-sm-3 control-label"> <spring:message code="label.user.repeat_password" text="Repeat password" />
						</label>
						<div class="col-sm-9">
							<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" required="required" />
						</div>
					</div>
				</c:if>
				<div class="page-header">
					<h3>
						<spring:message code="label.user.title.personal_information" text="Personal Information" />
					</h3>
				</div>
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
					<div class="col-sm-9">
						<input type="text" id="email" name="email" class="form-control" ${user.connexionType==1?'disabled="disabled" readonly="readonly"':''} required
							pattern='^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$' value="${user.email}" />
					</div>
				</div>
				<div class="form-group">
						<label for="connexionType" class="col-sm-3 control-label"><spring:message code="label.user.connexion.type" text="Authentication type" /></label>
						<div class="col-sm-9">
							<div class="btn-group" data-toggle="buttons" id="radioConnexionType">
								<label class="btn ${user.connexionType==-1?'btn-primary':'btn-default'} disabled"><spring:message code="label.user.connexion.standard" text="Standard" /><input name="connexionType" type="radio" readonly="readonly" value="-1"></label> <label
									class="btn ${user.connexionType==0?'btn-primary':'btn-default'} disabled"><spring:message code="label.user.connexion.both" text="Both" /><input name="connexionType" type="radio" readonly="readonly" value="0" checked="checked"></label>
								<label class="btn ${user.connexionType==1?'btn-primary':'btn-default'} disabled"><spring:message code="label.user.connexion.ldap" text="LDAP" /><input name="connexionType" type="radio" readonly="readonly" value="1"></label>
							</div>
						</div>
					</div>
				<div class="form-group">
					<c:if test="${user.roles.size()>1}">
						<label for="roles" class="col-sm-3 control-label"> <spring:message code="label.user.account.roles" text="Roles" />
						</label>
					</c:if>
					<c:if test="${user.roles.size()==1}">
						<label for="roles" class="col-sm-3 control-label"> <spring:message code="label.user.account.role" text="Role" />
						</label>
					</c:if>
					<div class="col-sm-9">
						<c:forEach items="${user.roles}" var="role">
							<c:set var="role_value" value="${fn:replace(role.type,'ROLE_','')}" />
							<div style="padding: 6px; border: 1px solid #dddddd; text-align: center; border-radius: 4px; background-color: #eeeeee;">
								<spring:message code="label.role.${fn:toLowerCase(role_value)}" text="${role_value}" />
							</div>
						</c:forEach>
					</div>
				</div>
				<div class="page-header">
					<h3>
						<spring:message code="label.user.title.application_settings" text="Application Settings" />
					</h3>
				</div>
				<div class="form-group">
					<label for="locale" class="col-sm-5 control-label"> <spring:message code="label.user.default_ui_language" text="Default User Interface Language" />
					</label>
					<div class="col-sm-7">
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
					<div class="col-sm-12" style="text-align: center;">
						<button class="btn btn-primary" onclick="return updateProfile('updateprofileform');" type="button">
							<spring:message code="label.user.update" text="Update" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>