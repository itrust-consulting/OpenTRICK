<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="section" id="section_roles">
	<table class="table" style="text-align: center;">
		<thead>
			<tr>
				<c:forEach items="${roles}" var="role">
					<c:set var="role_value" value="${fn:replace(role,'ROLE_','')}" />
					<th><spring:message code="label.role.${fn:toLowerCase(role_value)}" text="${role_value}"/></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<tr>
				<c:forEach items="${roles}" var="role">
					<td align="center"><input id="${role}" name="${role}" class="checkbox" type="checkbox" ${!empty userRoles && userRoles.contains(role)?"checked":""} /></td>
				</c:forEach>
			</tr>
		</tbody>
	</table>
</div>