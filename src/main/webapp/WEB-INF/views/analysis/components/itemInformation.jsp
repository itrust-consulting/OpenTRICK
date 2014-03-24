<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorItemInformation"></span>
<div class="section" id="section_itemInformation">
	<div class="page-header">
		<h3 id="ItemInformation">
			<spring:message code="label.scope" text="Scope" />
		</h3>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-info">
				<div class="panel-heading">
				&nbsp;
				</div>
				<div class="panel-body" style="max-height: 700px; overflow: auto;">
					<table class="table table-hover">
						<thead>
							<tr>
								<th colspan="2"><spring:message code="label.itemInformation.description" text="Description" /></th>
								<th colspan="1"><spring:message code="label.itemInformation.value" text="Value" /></th>
								
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${itemInformations}" var="itemInformation">
								<tr trick-class="ItemInformation" trick-id="${itemInformation.id}">
									<td colspan="2"><spring:message code="label.itemInformation.${itemInformation.description}" text="${itemInformation.description}" /></td>
									<td colspan="1" trick-field="value" trick-content="text" trick-field-type="string" class="success" ondblclick="return editField(this);"><spring:message text="${itemInformation.value}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>