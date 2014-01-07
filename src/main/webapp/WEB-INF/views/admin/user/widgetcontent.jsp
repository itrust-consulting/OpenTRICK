<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addUserModel" tabindex="-1" role="dialog" aria-labelledby="addNewUser" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="min-width:50%;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addUserModel-title">
					<spring:message code="label.user.add.menu" text="Add new User" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="user" action="User/Save" class="form-horizontal" id="user_form" commandName="user">
					<input type="hidden" name="id" value="-1" id="user_id">
					<div class="form-group">
						<label for="login" class="col-sm-2 control-label">
							<spring:message code="label.user.login" />
						</label>
						<div class="col-sm-10">
							<input id="user_login" name="login" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="password" class="col-sm-2 control-label">
							<spring:message code="label.user.password" />
						</label>
						<div class="col-sm-10">
							<input id="user_password" name="password" class="form-control" type="password" />
						</div>
					</div>
					<div class="form-group">
						<label for="firstName" class="col-sm-2 control-label">
							<spring:message code="label.user.firstName" />
						</label>
						<div class="col-sm-10">
							<input id="user_firstName" name="firstName" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-2 control-label">
							<spring:message code="label.user.lastName" />
						</label>
						<div class="col-sm-10">
							<input id="user_lastName" name="lastName" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-2 control-label">
							<spring:message code="label.user.email" />
						</label>
						<div class="col-sm-10">
							<input id="user_email" name="email" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="roles" class="col-sm-2 control-label">
							<spring:message code="label.role" />
						</label>
						<div class="col-sm-10" id="rolescontainer">
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addUserbutton" type="button" class="btn btn-primary" onclick="saveUser('user_form')">
					<spring:message code="label.user.add.form" text="Add" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteUserModel" tabindex="-1" aria-hidden="true" aria-labelledby="deleteUser" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteUserModel-title">
					<spring:message code="title.user.delete" text="Delete a user" />
				</h4>
			</div>
			<div id="deleteUserBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deleteuserbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.answer.yes" text="Yes" />
				</button>
				<button id="deleteuserbuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.answer.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<spring:url value="js/user.js" />"></script>