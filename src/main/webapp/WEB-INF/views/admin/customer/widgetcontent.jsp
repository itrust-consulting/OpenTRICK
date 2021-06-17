<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="modal fade" id="addCustomerModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewCustomer" data-aria-hidden="true">
	<div class="modal-dialog customer-modal ${adminaAllowedTicketing? 'modal-mdl' : '' }">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addCustomerModel-title">
					<spring:message code="label.customer.add.menu" text="Add new customer" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="customer" action="Customer/Create?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="customer_form">
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code='label.contact' text='Contact' />
						</legend>
						<input type="hidden" name="id" value="-1" id="customer_id">
						<input type="hidden" name="canBeUsed" value="true" id="customer_canBeUsed">
						<div class="form-group">
							<label for="organisation" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.organisation" />'> <spring:message
									code="label.customer.organisation" text="Company" />
							</label>
							<div class="col-sm-9">
								<input name="organisation" id="customer_organisation" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="contactPerson" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.contact_person" />'> <spring:message
									code="label.customer.contact_person" text="Contact person" />
							</label>
							<div class="col-sm-9">
								<input name="contactPerson" id="customer_contactPerson" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="phoneNumber" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.phone_number" />'> <spring:message
									code="label.customer.phone_number" text="Phone number" />
							</label>
							<div class="col-sm-9">
								<input name="phoneNumber" id="customer_phoneNumber" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="email" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.email" />'> <spring:message code="label.customer.email"
									text="Email address" />
							</label>
							<div class="col-sm-9">
								<input name="email" id="customer_email" class="form-control" type="text" />
							</div>
						</div>
					</fieldset>
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code="label.address" text="Address" />
						</legend>
						<div class="form-group">
							<label for="address" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.address" />'> <spring:message
									code="label.customer.address" text="Address" />
							</label>
							<div class="col-sm-9">
								<input name="address" id="customer_address" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="zipCode" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.zip_code" />'> <spring:message
									code="label.customer.zip_code" text="Zip code" />
							</label>
							<div class="col-sm-9">
								<input name="zipCode" id="customer_ZIPCode" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="city" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.city" />'> <spring:message code="label.customer.city"
									text="City" />
							</label>
							<div class="col-sm-9">
								<input name="city" id="customer_city" class="form-control" type="text" />
							</div>
						</div>
						<div class="form-group">
							<label for="country" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.customer.country" />'> <spring:message
									code="label.customer.country" text="Country" />
							</label>
							<div class="col-sm-9">
								<input name="country" id="customer_country" class="form-control" type="text" />
							</div>
						</div>
					</fieldset>
					<c:if test="${adminaAllowedTicketing}">
						<fieldset>
							<legend style="font-size: 15px">
								<spring:message code="label.ticketing.system"/>
							</legend>
							<div class="form-group">
								<label for="ticketingSystem.enabled" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.ticketing.system.enabled" />'> <spring:message
										code="label.ticketing.system.enabled"/>
								</label>
								<div class="col-sm-9">
									<div class="btn-group btn-group-justified" id="customer_tickecting_system_enabled"  data-toggle="buttons">
											<label class="btn btn-default"><spring:message code="label.yes_no.yes"/> 
												<input name="ticketingSystem.enabled" type="radio" value="true">
											</label>
											<label class="btn btn-default active"><spring:message code="label.yes_no.no"/> 
												<input name="ticketingSystem.enabled" type="radio" value="false" checked="checked">
											</label>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="ticketingSystem.type" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.ticketing.system.type" />'> <spring:message
										code="label.ticketing.system.type"/>
								</label>
								<div class="col-sm-9">
									<div class="btn-group btn-group-justified"  id="customer_tickecting_system_type" data-toggle="buttons">
										<c:forEach items="${ticketingTypes}" var="type" varStatus="status">
											<label class="btn btn-default ${status.index==0?'active':''}"><spring:message code="label.ticketing.system.type.${fn:toLowerCase(type)}"/> 
												<input name="ticketingSystem.type" type="radio" value="${type}" ${status.index==0?'checked="checked"':''}>
											</label>
										</c:forEach>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="ticketingSystem.name" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.ticketing.system.name" />'> <spring:message
										code="label.ticketing.system.name" />
								</label>
								<div class="col-sm-9">
									<input name="ticketingSystem.name" id="customer_tickecting_system_name" class="form-control" type="text" />
								</div>
							</div>
							<div class="form-group">
								<label for="ticketingSystem.url" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.ticketing.system.url" />'> <spring:message code="label.ticketing.system.url" />
								</label>
								<div class="col-sm-9">
									<input name="ticketingSystem.url" id="customer_tickecting_system_url" class="form-control" type="url" />
								</div>
							</div>
							
						</fieldset>
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
			<div class="modal-body"></div>
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
					<spring:message code="title.action.delete" text="Delete" />
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