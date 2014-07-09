<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widgets">
	<jsp:include page="./forms/addOrEditAsset.jsp" />
	<jsp:include page="./forms/addOrEditScenario.jsp" />
	<jsp:include page="./forms/addPhase.jsp" />
	<jsp:include page="./forms/addStandard.jsp" />
</div>