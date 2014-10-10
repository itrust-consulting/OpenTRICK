<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widgets">
	<jsp:include page="./forms/asset.jsp" />
	<jsp:include page="./forms/scenario.jsp" />
	<jsp:include page="./forms/phase.jsp" />
	<jsp:include page="./forms/standard.jsp" />
	<jsp:include page="./forms/actionplan.jsp" />
</div>