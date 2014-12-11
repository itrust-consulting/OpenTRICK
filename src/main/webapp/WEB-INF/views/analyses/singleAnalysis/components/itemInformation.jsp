<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorScope"></span>
<div class="section" id="section_itemInformation">
	<div class="page-header">
		<h3 id="ItemInformation">
			<fmt:message key="label.title.scope" />
		</h3>
	</div>
	<c:set var="newline" value="(\r\n|\n\r|\r|\n)"/>
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-default">
				<div class="panel-heading">
				&nbsp;
				</div>
				<div class="panel-body autofitpanelbodydefinition">
					<table id="iteminformationtable" class="table table-condensed table-hover table-fixed-header">
						<thead>
							<tr>
								<th colspan="1"><fmt:message key="label.item_information.description" /></th>
								<th colspan="2"><fmt:message key="label.item_information.value" /></th>
								
							</tr>
						</thead>
						<tfoot></tfoot>
						<tbody>
							<c:forEach items="${itemInformations}" var="itemInformation">
								<tr trick-class="ItemInformation" trick-id="${itemInformation.id}">
									<td colspan="1">
									<fmt:message key="label.item_information.${itemInformation.description.trim()}" /></td>
									<td colspan="3" ondblclick="return editField(this.firstElementChild);" class="success"><pre trick-field="value" trick-content="text" trick-field-type="string"><spring:message text="${itemInformation.value}" /></pre></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>