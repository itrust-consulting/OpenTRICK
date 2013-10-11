<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
<div class="container">

<!-- ################################################################### Nav Menu ################################################################### -->

<jsp:include page="menu.jsp" />

<!-- #################################################################### Content ################################################################### -->

	<div class="content" id="content">
	
	<a href="Customer/Display"><spring:message code="menu.knowledgebase.customers"/></a>
	
	<a href="Language/Display"><spring:message code="menu.knowledgebase.languages"/></a>	

	<a href="Standard/Display"><spring:message code="menu.knowledgebase.standards"/></a>
		
	</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>