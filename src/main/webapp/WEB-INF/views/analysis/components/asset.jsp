<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_asset">
	<div class="page-header">
		<h3 id="Asset">
			<spring:message code="label.asset" text="Asset" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<button class="btn btn-default" data-toggle="modal"
				onclick="findAllAssetType('asset_assettype_id');"
				data-target="#addAssetModel">
				<spring:message code="label.asset.add" text="Add new asset" />
			</button>
		</div>
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table">
				<thead>
					<tr>
						<th><spring:message code="label.row.index" text="#"
								htmlEscape="true" /></th>
						<th><spring:message code="label.asset.name" text="Name"
								htmlEscape="true" /></th>
						<th><spring:message code="label.asset.type" text="Type"
								htmlEscape="true" /></th>
						<th><spring:message code="label.asset.value" text="Value"
								htmlEscape="true" /></th>
						<th colspan="3"><spring:message code="label.asset.comment" text="Comment"
								htmlEscape="true" /></th>
						<th colspan="3"><spring:message code="label.asset.comment"
								text="Hidden comment" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${assets}" var="asset" varStatus="status">
						<tr  trick-id="${asset.id}" trick-selected="${asset.selected}" ondblclick="return editAssetRow('${asset.id}');">
							<c:set var="cssClass">
								${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}
							</c:set>
							<td>${status.index+1}</td>
							<td class="${cssClass}">${asset.name}</td>
							<td class="${cssClass}">${ asset.assetType.type}</td>
							<td class="${cssClass}">${asset.value}</td>
							<td class="${cssClass}" colspan="3">${asset.comment}</td>
							<td class="${cssClass}" colspan="3">${asset.hiddenComment}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>