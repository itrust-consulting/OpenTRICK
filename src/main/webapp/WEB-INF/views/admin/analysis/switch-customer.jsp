<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="switchCustomerModal" tabindex="-1" role="dialog" data-aria-labelledby="switchCustomerModal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.switch.customer" text="Switch customer" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="info.analysis.switch.customer" text="All analyses with the same identifier will be switched to the new customer." />
				</p>
				<form id="formSwitchCustomer" class="form-horizontal">
					<input name="idAnalysis" hidden="hidden" value="${idAnalysis}">
					<div class="form-group">
						<label class="col-xs-4"> <spring:message code="label.current.customers" text="Current customers" />
						</label>
						<ul class="list-group col-xs-8">
							<c:forEach items="${currentCustomers}" var="customer">
								<li class="list-group-item"><spring:message text="${customer.organisation} - ${customer.country}" /></li>
							</c:forEach>
						</ul>
					</div>
					<div class="form-group">
						<label class="col-xs-4"> <spring:message code="label.new.customer" text="New customer" />
						</label>
						<div class="list-group col-xs-8">
							<select name="customer" class="form-control">
								<c:forEach items="${customers}" var="customer">
									<option value="${customer.id}" ${currentCustomers.size() == 1 && currentCustomers[0] == customer? 'selected="selected"' : ''}><spring:message
											text="${customer.organisation} - ${customer.country}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button  type="button" name="save" class="btn btn-primary">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" name="cancel"  class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>