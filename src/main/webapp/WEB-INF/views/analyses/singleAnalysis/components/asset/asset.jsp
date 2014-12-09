<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorAsset"></span>
<div class="section" id="section_asset">
	<div class="page-header">
		<h3 id="Asset">
			<fmt:message key="label.title.assets" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_asset">
				<li><a href="#anchorAsset" onclick="return editAsset(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add.asset" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#anchorAsset" onclick="return editAsset();"><span class="glyphicon glyphicon-edit danger"></span> <fmt:message
							key="label.action.edit.asset" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined,'true')"><span class="glyphicon glyphicon-plus-sign"></span>
						<fmt:message key="label.action.select.asset" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#anchorAsset" onclick="return selectAsset(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span>
						<fmt:message key="label.action.unselect.asset" /> </a></li>
				<li class="disabled" trick-selectable="true" trick-check="isSelected('asset')"><a href="#anchorAsset" onclick="return displayAssessmentByAsset()"><span
						class="glyphicon glyphicon-new-window"></span> <fmt:message key="label.action.show.asset.assessment" /> </a></li>
				<li class="disabled pull-right" trick-selectable="multi"><a href="#anchorAsset" class="text-danger" onclick="return deleteAsset();"><span
						class="glyphicon glyphicon-remove"></span> <fmt:message key="label.action.delete.asset" /> </a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<table class="table table-hover table-fixed-header" id="assetTable">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'asset')"></th>
						<th><fmt:message key="label.row.index" /></th>
						<th colspan="8"><fmt:message key="label.asset.name" /></th>
						<th colspan="2"><fmt:message key="label.asset.type" /></th>
						<th colspan="2"><fmt:message key="label.asset.value" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th colspan="2"><fmt:message key="label.asset.aleo" /></th>
								<th colspan="2"><fmt:message key="label.asset.ale" /></th>
								<th colspan="2"><fmt:message key="label.asset.alep" /></th>
							</c:when>
							<c:otherwise>
								<th colspan="2"><fmt:message key="label.asset.ale" /></th>
							</c:otherwise>
						</c:choose>
						<th colspan="14"><fmt:message key="label.asset.comment" /></th>
						<th colspan="14"><fmt:message key="label.asset.hidden_comment" /></th>
					</tr>
				</thead>
				<tfoot></tfoot>
				<tbody>
					<c:forEach items="${assets}" var="asset" varStatus="status">
						<tr trick-id="${asset.id}" trick-selected="${asset.selected}" ondblclick="return editAsset('${asset.id}');">
							<c:set var="ale" value="${assetALE[asset.id]}" />
							<c:set var="cssClass">${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}</c:set>
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_asset','#menu_asset');"></td>
							<td>${status.index+1}</td>
							<td class="${cssClass}" colspan="8"><spring:message text="${asset.name}" /></td>
							<td class="${cssClass}" colspan="2"><spring:message text="${ asset.assetType.type}" /></td>
							<fmt:setLocale value="fr" scope="session" />
							<td class="${cssClass}" colspan="2" title='<fmt:formatNumber value="${fct:round(asset.value,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber
									value="${fct:round(asset.value*0.001,0)}" maxFractionDigits="0" /></td>
							<c:choose>
								<c:when test="${show_uncertainty}">
									<td colspan="2" title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
											value="${fct:round(ale[0].value*0.001,0)}" maxFractionDigits="0" /></td>
									<td colspan="2" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
											value="${fct:round(ale[1].value*0.001,0)}" maxFractionDigits="0" /></td>
									<td colspan="2" title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
											value="${fct:round(ale[2].value*0.001,0)}" maxFractionDigits="0" /></td>
								</c:when>
								<c:otherwise>
									<td colspan="2" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
											value="${fct:round(ale[1].value*0.001,0)}" maxFractionDigits="0" /></td>
								</c:otherwise>
							</c:choose>
							<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
							<td class="${cssClass}" colspan="14"><pre>
									<spring:message text="${asset.comment}" />
								</pre></td>
							<td class="${cssClass}" colspan="14"><pre>
									<spring:message text="${asset.hiddenComment}" />
								</pre></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr class="panel-footer" style="font-weight: bold;">
						<spring:eval expression="T(lu.itrust.business.TS.data.assessment.helper.AssessmentManager).ComputeTotalALE(assetALE)" var="ale" />
						<td colspan="14"><fmt:message key="label.total.ale" /></td>
						<fmt:setLocale value="fr" scope="session" />
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td name="ale" colspan="2" title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[0].value*0.001,0)}" maxFractionDigits="0" /></td>
								<td name="ale" colspan="2" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,0)}" maxFractionDigits="0" /></td>
								<td name="ale" colspan="2" title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[2].value*0.001,0)}" maxFractionDigits="0" /></td>
							</c:when>
							<c:otherwise>
								<td name="ale" colspan="2" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" maxFractionDigits="0" /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,0)}" maxFractionDigits="0" /></td>
							</c:otherwise>
						</c:choose>
						<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
						<td colspan="28"></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</div>
