<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<tr trick-id="${asset.id}" trick-selected="${asset.selected}" ondblclick="return editAsset('${asset.id}');">
	<td><input type="checkbox" class="checkbox checkboxselectable" onchange="return updateMenu('#section_asset','#menu_asset');" /></td>
	<td>${status.index+1}</td>
	<td class="${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}"><spring:message text="${asset.name}" /></td>
	<td class="${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}"><spring:message text="${ asset.assetType.type}" /></td>
	<td class="${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}" title="${asset.value}"><fmt:formatNumber value="${asset.value*0.001}" maxFractionDigits="0" /></td>
	<td class="${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}" colspan="3"><spring:message text="${asset.comment}" /></td>
	<td class="${asset.selected? asset.value < 1 ? 'warning' : 'success' : ''}" colspan="3"><spring:message text="${asset.hiddenComment}" /></td>
</tr>
