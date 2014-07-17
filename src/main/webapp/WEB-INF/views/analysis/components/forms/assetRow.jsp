<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<tr trick-id="${asset.id}" trick-selected="${asset.selected}" ondblclick="return editAsset('${asset.id}');">
	<c:set var="cssClass">
								${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}
							</c:set>
	<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_asset','#menu_asset');"></td>
	<td>${status.index+1}</td>
	<td class="${cssClass}" colspan="8"><spring:message text="${asset.name}" /></td>
	<td class="${cssClass}" colspan="2"><spring:message code="label.asset_type.${fn:toLowerCase(asset.assetType.type)}" text="${asset.assetType.type}" /></td>
	<td class="${cssClass}" colspan="2" title='<fmt:formatNumber value="${asset.value}"/>&euro;'><fmt:formatNumber value="${asset.value*0.001}" maxFractionDigits="1" /></td>
	<td class="${cssClass}" colspan="10"><pre><spring:message text="${asset.comment}" /></pre></td>
	<td class="${cssClass}" colspan="10"><pre><spring:message text="${asset.hiddenComment}" /></pre></td>
</tr>
