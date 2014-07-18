<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorAsset"></span>
<div class="section" id="section_asset">
	<div class="page-header">
		<h3 id="Asset">
			<spring:message code="label.asset" text="Asset" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_asset">
				<li><a href="#" onclick="return editAsset(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add.asset"
							text="Add" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editAsset();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.action.edit.asset" text="Edit" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectAsset(undefined,'true')"><span class="glyphicon glyphicon-plus-sign"></span> <spring:message
							code="label.action.select.asset" text="Select" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectAsset(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <spring:message
							code="label.action.unselect.asset" text="Unselect" /> </a></li>
				<li class="disabled" trick-selectable="true" trick-check="isSelected('asset')"><a href="#" onclick="return displayAssessmentByAsset()"><span
						class="glyphicon glyphicon-new-window"></span> <spring:message code="label.action.show.asset.assessment" text="Assessment" /> </a></li>
				<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteAsset();"><span class="glyphicon glyphicon-remove"></span>
						<spring:message code="label.action.delete.asset" text="Delete" /> </a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<table class="table table-hover table-fixed-header" id="assetTable">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'asset')"></th>
						<th><spring:message code="label.row.index" text="#" htmlEscape="true" /></th>
						<th colspan="8"><spring:message code="label.asset.name" text="Name" htmlEscape="true" /></th>
						<th colspan="2"><spring:message code="label.asset.type" text="Type" htmlEscape="true" /></th>
						<th colspan="2"><spring:message code="label.asset.value" text="Value" htmlEscape="true" /> (k&euro;)</th>
						<th colspan="2"><spring:message code="label.asset.aleo" text="ALEO" /> (k&euro;)</th>
						<th colspan="2"><spring:message code="label.asset.ale" text="ALE" /> (k&euro;)</th>
						<th colspan="2"><spring:message code="label.asset.alep" text="ALEP" /> (k&euro;)</th>
						<th colspan="10"><spring:message code="label.asset.comment" text="Comment" htmlEscape="true" /></th>
						<th colspan="10"><spring:message code="label.asset.hidden_comment" text="Hidden comment" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tfoot></tfoot>
				<tbody>
					<c:forEach items="${assets}" var="asset" varStatus="status">
						<tr trick-id="${asset.id}" trick-selected="${asset.selected}" ondblclick="return editAsset('${asset.id}');">
							<c:set var="ale" value="${assetALE[asset.id]}"/>
							<c:set var="cssClass">
								${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}
							</c:set>
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_asset','#menu_asset');"></td>
							<td>${status.index+1}</td>
							<td class="${cssClass}" colspan="8"><spring:message text="${asset.name}" /></td>
							<td class="${cssClass}" colspan="2"><spring:message text="${ asset.assetType.type}" /></td>
							<td class="${cssClass}" colspan="2" title='<fmt:formatNumber value="${asset.value}"/>&euro;'><fmt:formatNumber value="${asset.value*0.001}" maxFractionDigits="1" /></td>
							<td colspan="2" title="<fmt:formatNumber value="${ale[0].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[0].value*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td colspan="2" title="<fmt:formatNumber value="${ale[1].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[1].value*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td colspan="2" title="<fmt:formatNumber value="${ale[2].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[2].value*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td class="${cssClass}" colspan="10"><pre>
									<spring:message text="${asset.comment}" />
								</pre></td>
							<td class="${cssClass}" colspan="10"><pre>
									<spring:message text="${asset.hiddenComment}" />
								</pre></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
