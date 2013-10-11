<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">label.signin.login</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
<div class="container">

<!-- #################################################################### Content ################################################################### -->

	<div class="content" id="content">
	
	
		<div class="form login" id="login">
			<h1>
				<spring:message code="label.login.title" text="Sign in" />
			</h1>
			<a class="right" href="${pageContext.request.contextPath}/user/add">
				<spring:message	code="label.signup" text="Sign up" />
			</a>
			<jsp:include page="successErrors.jsp" />
			<form method="post" action="<c:url value='${pageContext.request.contextPath}/j_spring_security_check'/>">
				<table>
					<tr>
						<td>
							<label><spring:message code="label.signin.login" /></label> 
						</td>
						<td>
							<input name="j_username" value="${(!empty (j_username))? j_username : ''}" />
						</td>
					</tr>
					<tr>
						<td>
							<label><spring:message code="label.signin.password" /></label>
						</td>
						<td>
							<input name="j_password" value="${(!empty (j_password))? j_password : ''}" type="password" />
						</td>
					</tr>
					<tr>
						<td>
							<label><spring:message code="label.signin.rememberMe" text="Remember me" /></label>
						</td>
						<td>
							<input type='checkbox' name='_spring_security_remember_me' style="border: none;" class="center" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<button><spring:message code="label.signin.connect" text="Sign in" /></button>
						</td>
					</tr>
				</table>
			</form>			
		</div>
		
		
	</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>