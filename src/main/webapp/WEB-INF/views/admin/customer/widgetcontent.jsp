<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addCustomerModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewCustomer" data-aria-hidden="true">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addCustomerModel-title">
					<spring:message code="label.customer.add.menu" text="Add new customer" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="customer" action="Customer/Create" class="form-horizontal" id="customer_form">
					<fieldset>
						<legend>
							<spring:message code='label.contact' text='Contact' />
						</legend>
						<input type="hidden" name="id" value="-1" id="customer_id">
						<div class="form-group">
							<label for="organisation" class="col-sm-3 control-label"> <spring:message code="label.customer.organisation" text="Company" />
							</label>
							<div class="col-sm-9">
								<input name="organisation" id="customer_organisation" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="contactPerson" class="col-sm-3 control-label"> <spring:message code="label.customer.contact_person" text="Contact person" />
							</label>
							<div class="col-sm-9">
								<input name="contactPerson" id="customer_contactPerson" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="phoneNumber" class="col-sm-3 control-label"> <spring:message code="label.customer.phone_number" text="Phone number" />
							</label>
							<div class="col-sm-9">
								<input name="phoneNumber" id="customer_phoneNumber" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="email" class="col-sm-3 control-label"> <spring:message code="label.customer.email" text="Email" />
							</label>
							<div class="col-sm-9">
								<input name="email" id="customer_email" class="form-control" type="text" />
							</div>
						</div>
					</fieldset>
					<fieldset>
						<legend>
							<spring:message code="label.address" text="Address" />
						</legend>
						<div class="form-group">
							<label for="address" class="col-sm-3 control-label"> <spring:message code="label.customer.address" text="Address" />
							</label>
							<div class="col-sm-9">
								<input name="address" id="customer_address" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="city" class="col-sm-3 control-label"> <spring:message code="label.customer.city" text="City" />
							</label>
							<div class="col-sm-9">
								<input name="city" id="customer_city" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="ZIPCode" class="col-sm-3 control-label"> <spring:message code="label.customer.zip_code" text="ZIP Code" />
							</label>
							<div class="col-sm-9">
								<input name="ZIPCode" id="customer_ZIPCode" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="country" class="col-sm-3 control-label"> <spring:message code="label.customer.country" text="Country" />
							</label>
							<div class="col-sm-9">
								<input name="country" id="customer_country" class="form-control" type="text" />
							</div>
						</div>
					</fieldset>
					<c:if test="${!empty(adminView)}">
						<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
							<fieldset>
								<legend>
									<spring:message code='label.status' text='Status' />
								</legend>
								<div class="form-group">
									<label for="country" class="col-sm-3 control-label"> <spring:message code="label.customer.can_be_used" text="Profile only" />
									</label>
									<div class="col-md-offset-7" style="margin-top: 10px;">
										<input name="canBeUsed" id="customer_canBeUsed" class="checkbox" type="checkbox" />
									</div>
								</div>
							</fieldset>
						</sec:authorize>
					</c:if>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addcustomerbutton" type="button" class="btn btn-primary" onclick="saveCustomer('customer_form')">
					<spring:message code="label.action.add.customer" text="Add Customer" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="manageCustomerUserModel" tabindex="-1" role="dialog" data-aria-labelledby="manageCustomerUserModel" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addCustomerModel-title">
					<spring:message code="label.customer.manage.users" text="Manage customer users" />
				</h4>
			</div>
			<div class="modal-body" id="customerusersbody" style="margin: 0px;"></div>
			<div class="modal-footer" style="margin-top: 0px;">
				<button id="customerusersbutton" type="button" class="btn btn-primary" onclick="">
					<spring:message code="label.action.update" text="Update" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteCustomerModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteCustomer" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteCustomerModel-title">
					<spring:message code="title.action.delete.customer" text="Delete customer" />
				</h4>
			</div>
			<div id="deleteCustomerBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletecustomerbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.yes_no.yes" text="Yes" />
				</button>
				<button id="deletecustomerbuttonCancel" type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>