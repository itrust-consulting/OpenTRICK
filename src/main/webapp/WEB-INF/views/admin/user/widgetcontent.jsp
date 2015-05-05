<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addUserModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewUser" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="min-width: 50%;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addUserModel-title">
					<spring:message code="label.title.add.user" text="Add new user" />
				</h4>
			</div>
			<div class="modal-body">
				<span id="success" hidden="hidden"></span>
				<form name="user" action="User/Save" class="form-horizontal" id="user_form" name="user">
					<input type="hidden" name="id" value="-1" id="user_id">
					<div class="form-group">
						<label for="login" class="col-sm-2 control-label"> <spring:message code="label.user.login" text="Username"/>
						</label>
						<div class="col-sm-10">
							<input id="user_login" name="login" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="password" class="col-sm-2 control-label"> <spring:message code="label.user.password" text="Password"/>
						</label>
						<div class="col-sm-10">
							<input id="user_password" name="password" class="form-control" type="password" />
						</div>
					</div>
					<div class="form-group">
						<label for="firstName" class="col-sm-2 control-label"> <spring:message code="label.user.first_name" text="Firstname" />
						</label>
						<div class="col-sm-10">
							<input id="user_firstName" name="firstName" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-2 control-label"> <spring:message code="label.user.last_name" text="Lastname"/>
						</label>
						<div class="col-sm-10">
							<input id="user_lastName" name="lastName" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-2 control-label"> <spring:message code="label.user.email" text="Email address"/>
						</label>
						<div class="col-sm-10">
							<input id="user_email" name="email" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="roles" class="col-sm-2 control-label"> <spring:message code="label.user.account.role" text="Role"/>
						</label>
						<div class="col-sm-10" id="rolescontainer"></div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addUserbutton" type="button" class="btn btn-primary" onclick="saveUser('user_form')">
					<spring:message code="label.action.add.user" text="Add" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
