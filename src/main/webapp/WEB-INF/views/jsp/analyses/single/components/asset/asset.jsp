<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-asset">
	<div class="section" id="section_asset">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.assets" />
					</h3>
				</div>
			</div>
		</div>
		<c:if test="${isEditable}">
			<ul class="nav nav-pills bordered-bottom" id="menu_asset">
				<li data-trick-ignored="true" ><a href="#anchorAsset" onclick="return editAsset(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message
							code="label.action.add.asset" /></a></li>
				<li class="disabled" data-trick-selectable="true"><a href="#anchorAsset" onclick="return editAsset();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.action.edit.asset" /> </a></li>
				<li data-trick-single-check="!isSelected('asset')" data-trick-check="hasSelectedState('asset','false')" class="disabled" data-trick-selectable="multi"><a
					href="#anchorAsset" onclick="return selectAsset(undefined,'true')"><span class="glyphicon glyphicon-plus-sign"></span> <spring:message code="label.action.select.asset" />
				</a></li>
				<li data-trick-single-check="isSelected('asset')" data-trick-check="hasSelectedState('asset','true')" class="disabled" data-trick-selectable="multi"><a href="#anchorAsset"
					onclick="return selectAsset(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <spring:message code="label.action.unselect.asset" /> </a></li>
				<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="multi"><a href="#anchorAsset" class="text-danger" onclick="return deleteAsset();"><span
						class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete.asset" /> </a></li>
			</ul>
		</c:if>

		<table class="table table-hover table-fixed-header-analysis table-condensed" id="assetTable">
			<thead>
				<tr>
					<c:if test="${isEditable}">
						<th style="width: 2%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'asset')"></th>
					</c:if>
					<th style="width: 3%"><a href="#" onclick="return sortTable('index',this,true)" data-order='0'> <spring:message code="label.row.index" /></a></th>
					<th style="width: 20%"><a href="#" onclick="return sortTable('name',this)" data-order='1'><spring:message code="label.asset.name" /></a></th>
					<th style="width: 8%"><a href="#" onclick="return sortTable('type',this)" data-order='1'><spring:message code="label.asset.type" /></a></th>
					<th style="width: 6%"><a href="#" onclick="return sortTable('value',this,true)" data-order='1'><spring:message code="label.asset.value" /></a></th>
					<c:if test="${type.quantitative}">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 5%"><a href="#" onclick="return sortTable('aleo',this,true)" data-order='1'><spring:message code="label.asset.aleo" /></a></th>
								<th style="width: 5%"><a href="#" onclick="return sortTable('ale',this,true)" data-order='1'><spring:message code="label.asset.ale" /></a></th>
								<th style="width: 5%"><a href="#" onclick="return sortTable('alep',this,true)" data-order='1'><spring:message code="label.asset.alep" /></a></th>
							</c:when>
							<c:otherwise>
								<th style="width: 5%"><a href="#" onclick="return sortTable('ale',this,true)" data-order='1'><spring:message code="label.asset.ale" /></a></th>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test="${isILR}">
						<th style="width: 2%" title='<spring:message code="label.scenario.type.confidentiality" />' ><a href="#" onclick="return sortTable('ilrImpactC',this,true)" data-order='1'><spring:message code="label.asset.ilr.impact.c" /></a></th>
						<th style="width: 2%" title='<spring:message code="label.scenario.type.integrity" />'><a href="#" onclick="return sortTable('ilrImpactI',this,true)" data-order='1'><spring:message code="label.asset.ilr.impact.i" /></a></th>
						<th style="width: 2%" title='<spring:message code="label.scenario.type.availability" />'><a href="#" onclick="return sortTable('ilrImpactA',this,true)" data-order='1'><spring:message code="label.asset.ilr.impact.a" /></a></th>
					</c:if>
					<th><a href="#" onclick="return sortTable('comment',this)" data-order='1'> <spring:message code="label.asset.comment" /></a></th>
					<c:if test="${showHiddenComment}">
						<th><a href="#" onclick="return sortTable('hiddenComment',this)" data-order='1'><spring:message code="label.asset.hidden_comment" /></a></th>
					</c:if>
					<c:if test="${isILR}">
						<th style="width: 15%"><a href="#" onclick="return sortTable('relatedName',this)" data-order='1'> <spring:message code="label.asset.related_name" /></a></th>
					</c:if>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:set var="totalAssetValue" value="0" />
				<c:forEach items="${assets}" var="asset" varStatus="status">

					<tr data-trick-id="${asset.id}" onclick="selectElement(this)" data-trick-selected="${asset.selected}" data-trick-class="Asset"
						${asset.selected? asset.value < 1 ? 'class="warning"' : 'class="editable"' : ''} ondblclick="return editAsset('${asset.id}');">
						<c:set var="ale" value="${assetALE[asset.id]}" />
						<c:set var="totalAssetValue" value="${totalAssetValue + asset.value}" />
						<c:set var="selectClass" value="${asset.selected?'selected':'unselected'}" />
						<c:if test="${isEditable}">
							<td class='${selectClass}'><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_asset','#menu_asset');"></td>
						</c:if>
						<td data-trick-field="index">${status.index+1}</td>
						<td data-trick-field="name"><spring:message text="${asset.name}" /></td>
						<td data-trick-field="type"><spring:message code="label.asset_type.${fn:toLowerCase(asset.assetType.name)}" /></td>
						<td data-trick-field="value" title='<fmt:formatNumber value="${fct:round(asset.value,0)}" /> &euro;'><fmt:formatNumber value="${fct:round(asset.value*0.001,0)}" /></td>
						<c:if test="${type.quantitative}">
							<c:choose>
								<c:when test="${show_uncertainty}">
									<td data-trick-field="aleo" title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[0].value*0.001,1)}" /></td>
									<td data-trick-field="ale" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
									<td data-trick-field="alep" title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[2].value*0.001,1)}" /></td>
								</c:when>
								<c:otherwise>
									<td data-trick-field="ale" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}" /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${isILR}">
							<c:set var="assetNode" value="${assetNodes[asset.id]}" />
							<c:choose>
								<c:when test="${empty assetNode}">
									<td data-trick-field="ilrImpactC" title="-"></td>
									<td data-trick-field="ilrImpactI" title="-"></td>
									<td data-trick-field="ilrImpactA" title="-"></td>
								</c:when>
								<c:otherwise>
									<c:set var="ilrImpactC" value="${assetNode.confidentiality}" />
									<c:set var="ilrImpactI" value="${assetNode.integrity}" />
									<c:set var="ilrImpactA" value="${assetNode.availability}" />
									<td data-trick-field="ilrImpactC" title="${ilrImpactC == -1? '-' :  ilrImpactC}">${ilrImpactC == -1? '' :  ilrImpactC}</td>
									<td data-trick-field="ilrImpactI" title="${ilrImpactI == -1? '-' :  ilrImpactI}">${ilrImpactI == -1? '' :  ilrImpactI}</td>
									<td data-trick-field="ilrImpactA" title="${ilrImpactA == -1? '-' :  ilrImpactA}">${ilrImpactA == -1? '' :  ilrImpactA}</td>
								</c:otherwise>
							</c:choose>
						</c:if>
						<td onclick="editField(this);" data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.comment}" /></td>
						<c:if test="${showHiddenComment}">
							<td onclick="editField(this);" data-trick-field="hiddenComment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.hiddenComment}" /></td>
						</c:if>
						<c:if test="${isILR}">
							<td onclick="editField(this);" data-trick-field="relatedName" data-trick-field-type="string" data-trick-content="text"><spring:message text="${asset.relatedName}" /></td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="panel-footer" style="font-weight: bold;">
					<td colspan="${isEditable? '4': '3'}"><spring:message code="label.total.ale" /></td>
					<td title='<fmt:formatNumber value="${fct:round(totalAssetValue,0)}" /> &euro;'><fmt:formatNumber value="${fct:round(totalAssetValue*0.001,0)}" /></td>
					<c:if test="${type.quantitative}">
						<spring:eval expression="T(lu.itrust.business.ts.component.AssessmentAndRiskProfileManager).ComputeTotalALE(assetALE)" var="ale" />
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
					<td colspan="${(showHiddenComment? (isILR? 6: 2) : (isILR? 5: 1 ))}"></td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>
