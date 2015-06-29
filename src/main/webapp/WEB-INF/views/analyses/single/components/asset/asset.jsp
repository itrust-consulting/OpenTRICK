<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane"  id="tabAsset">
	<div class="section" id="section_asset">
		<ul class="nav nav-pills bordered-bottom" id="menu_asset">
			<c:if test="${isEditable}">
				<li><a href="#anchorAsset" onclick="return editAsset(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add.asset" /></a></li>

				<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="true"><a href="#anchorAsset" onclick="return editAsset();"><span
						class="glyphicon glyphicon-edit danger"></span> <fmt:message key="label.action.edit.asset" /> </a></li>
				<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined,'true')"><span
						class="glyphicon glyphicon-plus-sign"></span> <fmt:message key="label.action.select.asset" /> </a></li>
				<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined, 'false')"><span
						class="glyphicon glyphicon-minus-sign "></span> <fmt:message key="label.action.unselect.asset" /> </a></li>
			</c:if>
			<li  class="disabled" data-trick-selectable="true" data-trick-check="isSelected('asset')"><a href="#anchorAsset" onclick="return displayAssessmentByAsset()"><span
					class="glyphicon glyphicon-new-window"></span> <fmt:message key="label.action.show.asset.assessment" /> </a></li>
			<c:if test="${isEditable}">
				<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="multi"><a href="#anchorAsset" class="text-danger" onclick="return deleteAsset();"><span
						class="glyphicon glyphicon-remove"></span> <fmt:message key="label.action.delete.asset" /> </a></li>
			</c:if>
		</ul>
		<table class="table table-hover table-fixed-header-analysis" id="assetTable">
			<thead>
				<tr>
					<c:choose>
						<c:when test="${isEditable}">
						<th style="width:2%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'asset')"></th>
						</c:when>
						<c:otherwise>
							<th style="width:2%"></th>
						</c:otherwise>
					</c:choose>
					<th style="width:3%"><fmt:message key="label.row.index" /></th>
					<th style="width:25%"><fmt:message key="label.asset.name" /></th>
					<th style="width:5%"><fmt:message key="label.asset.type" /></th>
					<th style="width:6%"><fmt:message key="label.asset.value" /></th>
					<c:choose>
						<c:when test="${show_uncertainty}">
							<th style="width:5%"><fmt:message key="label.asset.aleo" /></th>
							<th style="width:5%"><fmt:message key="label.asset.ale" /></th>
							<th style="width:5%"><fmt:message key="label.asset.alep" /></th>
						</c:when>
						<c:otherwise>
							<th style="width:5%"><fmt:message key="label.asset.ale" /></th>
						</c:otherwise>
					</c:choose>
					<th><fmt:message key="label.asset.comment" /></th>
					<th><fmt:message key="label.asset.hidden_comment" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:set var="totalAssetValue" value="0" />
				<c:forEach items="${assets}" var="asset" varStatus="status">
					<tr data-trick-id="${asset.id}" data-trick-selected="${asset.selected}" data-trick-class="Asset" ${asset.selected? asset.value < 1 ? 'class="warning"' : 'class="success"' : ''} ondblclick="return editAsset('${asset.id}');">
						<c:set var="ale" value="${assetALE[asset.id]}" />
						<c:set var="totalAssetValue" value="${totalAssetValue + asset.value}" />
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_asset','#menu_asset');"></td>
						<td>${status.index+1}</td>
						<td><spring:message text="${asset.name}" /></td>
						<td><spring:message text="${asset.assetType.type}" /></td>
						<fmt:setLocale value="fr" scope="session" />
						<td title='<fmt:formatNumber value="${fct:round(asset.value,0)}" /> &euro;'><fmt:formatNumber
								value="${fct:round(asset.value*0.001,0)}" /></td>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[0].value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}"  /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[2].value*0.001,1)}"/></td>
							</c:when>
							<c:otherwise>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}" /></td>
							</c:otherwise>
						</c:choose>
						<fmt:setLocale value="${language}" scope="session" />
						<td onclick="editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.comment}" /></pre></td>
						<td onclick="editField(this.firstElementChild);"><pre data-trick-field="hiddenComment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.hiddenComment}" /></pre></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="panel-footer" style="font-weight: bold;">
					<spring:eval expression="T(lu.itrust.business.TS.model.assessment.helper.AssessmentManager).ComputeTotalALE(assetALE)" var="ale" />
					<td colspan="4"><fmt:message key="label.total.ale" /></td>
					<fmt:setLocale value="fr" scope="session" />
					<td title='<fmt:formatNumber value="${fct:round(totalAssetValue,0)}" /> &euro;'><fmt:formatNumber value="${fct:round(totalAssetValue*0.001,0)}" /></td>
					<c:choose>
						<c:when test="${show_uncertainty}">
							<td title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[0].value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"/> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[2].value*0.001,1)}" /></td>
						</c:when>
						<c:otherwise>
							<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
						</c:otherwise>
					</c:choose>
					<fmt:setLocale value="${language}" scope="session" />
					<td colspan="2"></td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>
