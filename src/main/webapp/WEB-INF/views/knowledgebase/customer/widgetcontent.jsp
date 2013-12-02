<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addCustomerModel" tabindex="-1" role="dialog" aria-labelledby="addNewCustomer" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addCustomerModel-title">
					<spring:message code="label.customer.add.menu" text="Add new customer" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="customer" action="Customer/Create" class="form-horizontal" id="customer_form">
					<input type="hidden" name="id" value="-1" id="customer_id">
					<div class="form-group">
						<label for="organisation" class="col-sm-2 control-label"> <spring:message code="label.customer.organisation" text="Company" />
						</label>
						<div class="col-sm-10">
							<input name="organisation" id="customer_organisation" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="contactPerson" class="col-sm-2 control-label"> <spring:message code="label.customer.contactPerson" text="Contact Person" />
						</label>
						<div class="col-sm-10">
							<input name="contactPerson" id="customer_contactPerson" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="telephoneNumber" class="col-sm-2 control-label"> <spring:message code="label.customer.telephoneNumber" text="Telephone Number" />
						</label>
						<div class="col-sm-10">
							<input name="telephoneNumber" id="customer_telephoneNumber" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-2 control-label"> <spring:message code="label.customer.email" text="Email" />
						</label>
						<div class="col-sm-10">
							<input name="email" id="customer_email" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="address" class="col-sm-2 control-label"> <spring:message code="label.customer.address" text="Address" />
						</label>
						<div class="col-sm-10">
							<input name="address" id="customer_address" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="city" class="col-sm-2 control-label"> <spring:message code="label.customer.city" text="City" />
						</label>
						<div class="col-sm-10">
							<input name="city" id="customer_city" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="ZIPCode" class="col-sm-2 control-label"> <spring:message code="label.customer.ZIPCode" text="ZIP Code" />
						</label>
						<div class="col-sm-10">
							<input name="ZIPCode" id="customer_ZIPCode" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="country" class="col-sm-2 control-label"> <spring:message code="label.customer.country" text="Country" />
						</label>
						<div class="col-sm-10">
							<input name="country" id="customer_country" class="form-control" type="text" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addcustomerbutton" type="button" class="btn btn-primary" onclick="saveCustomer('customer_form')">
					<spring:message code="label.customer.add.form" text="Add Customer" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteCustomerModel" tabindex="-1" aria-hidden="true" aria-labelledby="deleteCustomer" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteCustomerModel-title">
					<spring:message code="title.customer.delete" text="Delete a customer" />
				</h4>
			</div>
			<div id="deleteCustomerBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletecustomerbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.answer.yes" text="Yes" />
				</button>
				<button id="deletecustomerbuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.answer.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<spring:url value="js/customer.js" />"></script>