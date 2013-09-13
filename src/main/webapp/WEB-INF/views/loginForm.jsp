<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<c:set var="title">label.title.login</c:set>
<jsp:include page="header.jsp" />
<body>
	<div class="container">
		<div class="form login" id="login">
			<h1>
				<spring:message code="label.login.title" text="Sign in" />
			</h1>
			<a class="right" href="${pageContext.request.contextPath}/user/add">
				<spring:message	code="label.signup" text="Sign up" />
			</a>
			<jsp:include page="successErrors.jsp" />
			<form method="post"
				  action="<c:url value='${pageContext.request.contextPath}/j_spring_security_check'/>">
				<label><spring:message code="label.signin.login" /></label> 
				<input name="j_username" value="${(!empty (j_username))? j_username : ''}" />
				<br/>
				<label><spring:message code="label.signin.password" /> </label>
				<input name="j_password" value="${(!empty (j_password))? j_password : ''}"
					   type="password" />
				<br/>
				<label><spring:message code="label.signin.rememberMe" text="Remember me" /></label>
				<input type='checkbox'
					   name='_spring_security_remember_me'
					   style="border: none;" class="center" />
				<button>
					<spring:message code="label.signin.connect" text="Sign in" />
				</button>
			</form>
		</div>
		<div class="footer"><jsp:include page="footer.jsp" /></div>
	</div>
</body>
</html>