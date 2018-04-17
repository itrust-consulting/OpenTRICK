<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="user-modal-form" tabindex="-1" role="dialog" data-aria-labelledby="user-modal-form" data-aria-hidden="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<c:choose>
						<c:when test="${user.id>0}">
							<spring:message code="label.title.edit.user" text="Edit user" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.title.add.user" text="Add new user" />
						</c:otherwise>
					</c:choose>

				</h4>
			</div>
			<div class="modal-body">
				<span id="success" hidden="hidden"></span>
				<form name="user" action="User/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="user_form" name="user">
					<input type="hidden" name="id" value="${user.id}" id="user_id" ${user.id>0? 'readonly':''}>
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code="label.user.title.login_information" text="Login Information" />
						</legend>
						<div class="form-group">
							<label for="login" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.login" />'> <spring:message code="label.user.login"
									text="Username" />
							</label>
							<div class="col-sm-8">
								<input id="user_login" name="login" class="form-control" type="text" value="${user.login}" required="required" ${user.id>0? 'readonly':''} />
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.password" />'> <spring:message
									code="label.user.password" text="Password" />
							</label>
							<div class="col-sm-8">
								<input id="user_password" name="password" class="form-control" type="password" ${user.id>0? '':'required'} />
							</div>
						</div>
						<div class="form-group">
							<label for="roles" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.role" />'> <spring:message
									code="label.user.account.access.level" text="Access level" /></label>
							<div class="col-sm-8">
								<div class="btn-group" data-toggle="buttons">
									<c:forEach items="${roles}" var="role">
										<c:set var="role_value" value="${fn:replace(role,'ROLE_','')}" />
										<label class="btn btn-default ${user.hasRole(role)? 'active' : ''}"><spring:message code="label.role.${fn:toLowerCase(role_value)}" text="${role_value}" /><input
											name="${role}" type="checkbox" ${user.hasRole(role)? 'checked' : ''}></label>
									</c:forEach>
								</div>
							</div>
						</div>
					</fieldset>
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code="label.user.title.personal_information" text="Personal Information" />
						</legend>
						<div class="form-group">
							<label for="firstName" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.first_name" />'> <spring:message
									code="label.user.first_name" text="Firstname" />
							</label>
							<div class="col-sm-8">
								<input id="user_firstName" name="firstName" class="form-control" type="text" value="${user.firstName}" required="required" />
							</div>
						</div>
						<div class="form-group">
							<label for="lastName" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.last_name" />'> <spring:message
									code="label.user.last_name" text="Lastname" />
							</label>
							<div class="col-sm-8">
								<input id="user_lastName" name="lastName" class="form-control" type="text" value="${user.lastName}" required="required" />
							</div>
						</div>
						<div class="form-group">
							<label for="email" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.email" />'> <spring:message code="label.user.email"
									text="Email address" />
							</label>
							<div class="col-sm-8">
								<input id="user_email" name="email" class="form-control" type="text" value="${user.email}" required="required" />
							</div>
						</div>
					</fieldset>
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code='label.user.title.account.authentication' text='Authentication information' />
						</legend>
						<div class="form-group">
							<label for="connexionType" class="col-sm-4 control-label" data-helper-content='<spring:message code="help.user.admin.connexion.type" />'><spring:message
									code="label.user.connexion.type" text="Authentication type" /></label>
							<div class="col-sm-8">
								<div class="btn-group" data-toggle="buttons" id="radioConnexionType">
									<label class="btn btn-default ${user.connexionType==-1?'active':''}"><spring:message code="label.user.connexion.standard" text="Standard" /><input
										name="connexionType" type="radio" value="-1" ${user.connexionType==-1?'checked':''}></label> <label class="btn btn-default ${user.connexionType==0?'active':''}"><spring:message
											code="label.user.connexion.both" text="Both" /><input name="connexionType" type="radio" value="0" ${user.connexionType==0?'checked':''}></label> <label
										class="btn btn-default ${user.connexionType==1?'active':''}"><spring:message code="label.user.connexion.ldap" text="LDAP" /><input name="connexionType"
										type="radio" ${user.connexionType==1?'checked':''} value="1"></label>
								</div>
							</div>
						</div>
						<c:if test="${enabledOTP}">
							<c:set var="using2FA" value="${user.using2FA}" />
							<spring:message code="label.action.enable" text="Enable" var="enable" />
							<spring:message code="label.action.disable" text="Disable" var="disable" />
							<div class='form-group' data-index='0'>
								<label class="control-label col-sm-4" data-helper-content='<spring:message code="help.user.admin.otp" />'><spring:message code='label.user.account.otp' /></label>
								<div class='col-sm-8'>
									<div class="btn-group" data-toggle="buttons">
										<label class="btn btn-default ${using2FA?'active':''}">${enable}<input ${using2FA?'checked':''} name="using2FA" type="radio" value="true"></label> <label
											class="btn btn-default ${using2FA?'':'active'}">${disable}<input ${using2FA?'':'checked'} name="using2FA" type="radio" value="false"></label>
									</div>
								</div>
							</div>
						</c:if>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button name="save" type="button" class="btn btn-primary">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
