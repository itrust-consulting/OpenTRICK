<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="reportTemplateModal" tabindex="-1" role="dialog" data-aria-labelledby="reportTemplateModal" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.manage.customer.template" text="Manage customer template" />
				</h4>
			</div>
			<div class="modal-body tab-content">
				<jsp:include page="manage-template.jsp" />
				<jsp:include page="add-template.jsp" />
			</div>
			<div class="modal-footer">
				<button type="button" name="save" class="btn btn-primary" style="display: none;">
					<spring:message code="label.action.save" />
				</button>
				<a href="#section_manage_customer_template" class="btn btn-default" data-toggle="tab" style="display: none;"><spring:message code='label.action.back' /></a>
				<button type="button" class="btn btn-default" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.close" text="Close" />
				</button>
			</div>
		</div>
	</div>
</div>