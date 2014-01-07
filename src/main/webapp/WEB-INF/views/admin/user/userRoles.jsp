<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_roles">
	<table class="table" style="text-align: center;">
		<tbody>
			<tr>
				<c:forEach items="${roles}" var="role">
					<th><spring:message code="label.role.${role}" /></th>
				</c:forEach>
			</tr>
			<c:forEach items="${roles}" var="role">
				<td><input id="${role}" name="${role}" class="form-control" type="checkbox" ${userRoles.contains(role)?"checked":""}/></td>
			</c:forEach>
		</tbody>
	</table>
</div>