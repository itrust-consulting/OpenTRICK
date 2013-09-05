<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title>Trick Service</title>
<style>
.error {
	color: #ff0000;
}
</style>
</head>
<body>
	<form:errors cssClass="error" element="div" />
	<table>
		<tr>
			<td><b>Commandes</b></td>
		</tr>
		<!--  
		<tr>
			<td><a href="user/${user.id}">My Profil</a></td>
		</tr>
		-->
		<c:if test="${user.isAutorise('ROLE_CONSULTANT')}">
			<tr>
				<td><a href="import/analysis">Import Analysis</a></td>
			</tr>
			<tr>
				<td><a href="analysis/all">All analysis</a></td>
			</tr>
			<tr>
				<td><a href="analysis/customers">Analysis by customer</a></td>
			</tr>
			<c:if test="${user.isAutorise('ROLE_ADMIN')}">
				<tr>
					<td><a href="customer/all">All customers</a></td>
				</tr>
				<tr>
					<td><a href="user/all">All users</a></td>
				</tr>
			</c:if>
		</c:if>
		<tr>
			<td><a href='<c:url value="/j_spring_security_logout" />'>Logout</a></td>
		</tr>
	</table>
</body>
</html>