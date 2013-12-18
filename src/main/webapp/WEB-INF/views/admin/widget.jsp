<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widget">
	<jsp:include page="user/widgetcontent.jsp" />
	<div id="contextMenu" class="dropdown clearfix" trick-selected-id="1" style="position: absolute; display: none; left: 426px; top: 4215.35px;">
		<ul class="dropdown-menu" style="display: block; position: static; margin-bottom: 5px;" aria-labelledby="dropdownMenu" role="menu">
			<li name="edit_row"><a href="#" tabindex="-1" onclick=""><spring:message code="label.action.edit" /></a></li>
			<li class="divider"></li>
			<li name="delete"><a href="#" tabindex="-1" onclick=""><spring:message code="label.action.delete" /></a></li>
		</ul>
	</div>
</div>