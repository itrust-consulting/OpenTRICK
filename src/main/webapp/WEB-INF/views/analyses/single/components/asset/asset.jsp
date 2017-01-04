<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane"  id="tab-asset">
	<div class="section" id="section_asset" >
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.asset" />
					</h3>
				</div>
			</div>
		</div>

		<ul class="nav nav-pills bordered-bottom" id="menu_asset">
			<c:if test="${isEditable}">
				<li><a href="#anchorAsset" onclick="return editAsset(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message
							code="label.action.add.asset" /></a></li>
				<li class="disabled" data-trick-selectable="true"><a href="#anchorAsset" onclick="return editAsset();"><span
						class="glyphicon glyphicon-edit danger"></span> <spring:message code="label.action.edit.asset" /> </a></li>
			</c:if>
			<c:if test="${isEditable}">
				<li data-trick-single-check="!isSelected('asset')" class="disabled" data-trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined,'true')"><span
						class="glyphicon glyphicon-plus-sign"></span> <spring:message code="label.action.select.asset" /> </a></li>
				<li data-trick-single-check="isSelected('asset')" class="disabled" data-trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined, 'false')"><span
						class="glyphicon glyphicon-minus-sign "></span> <spring:message code="label.action.unselect.asset" /> </a></li>
				<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="multi"><a href="#anchorAsset" class="text-danger" onclick="return deleteAsset();"><span
						class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete.asset" /> </a></li>
			</c:if>
		</ul>
		
		<table class="table table-hover table-fixed-header-analysis table-condensed" id="assetTable">
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
					<th style="width:3%"><spring:message code="label.row.index" /></th>
					<th style="width:25%"><spring:message code="label.asset.name" /></th>
					<th style="width:5%"><spring:message code="label.asset.type" /></th>
					<th style="width:6%"><spring:message code="label.asset.value" /></th>
					<c:if test="${type == 'QUANTITATIVE'}">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width:5%"><spring:message code="label.asset.aleo" /></th>
								<th style="width:5%"><spring:message code="label.asset.ale" /></th>
								<th style="width:5%"><spring:message code="label.asset.alep" /></th>
							</c:when>
							<c:otherwise>
								<th style="width:5%"><spring:message code="label.asset.ale" /></th>
							</c:otherwise>
						</c:choose>
					</c:if>
					<th><spring:message code="label.asset.comment" /></th>
					<th><spring:message code="label.asset.hidden_comment" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:set var="totalAssetValue" value="0" />
				<c:forEach items="${assets}" var="asset" varStatus="status">
					<tr data-trick-id="${asset.id}" onclick="selectElement(this)" data-trick-selected="${asset.selected}" data-trick-class="Asset" ${asset.selected? asset.value < 1 ? 'class="warning"' : 'class="success"' : ''} ondblclick="return editAsset('${asset.id}');">
						<c:set var="ale" value="${assetALE[asset.id]}" />
						<c:set var="totalAssetValue" value="${totalAssetValue + asset.value}" />
						<c:set var="selectClass" value="${asset.selected?'selected':'unselected'}" />
						<td class='${selectClass}'><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_asset','#menu_asset');"></td>
						<td>${status.index+1}</td>
						<td><spring:message text="${asset.name}" /></td>
						<td><spring:message text="${asset.assetType.type}" /></td>
						<td title='<fmt:formatNumber value="${fct:round(asset.value,0)}" /> &euro;'><fmt:formatNumber
								value="${fct:round(asset.value*0.001,0)}" /></td>
						<c:if test="${type == 'QUANTITATIVE'}">
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
						</c:if>
						<td onclick="editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.comment}" /></pre></td>
						<td onclick="editField(this.firstElementChild);"><pre data-trick-field="hiddenComment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.hiddenComment}" /></pre></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="panel-footer" style="font-weight: bold;">
					<td colspan="4"><spring:message code="label.total.ale" /></td>
					<td title='<fmt:formatNumber value="${fct:round(totalAssetValue,0)}" /> &euro;'><fmt:formatNumber value="${fct:round(totalAssetValue*0.001,0)}" /></td>
					<c:if test="${type == 'QUANTITATIVE'}">
						<spring:eval expression="T(lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager).ComputeTotalALE(assetALE)" var="ale" />
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
					</c:if>
					<td colspan="2"></td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>
