<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter">
	<div class='section row' id='section_qualitative_parameter'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.parameter' />
					</h3>
				</div>
			</div>
		</div>
		<jsp:include page="../other.jsp" />
	</div>
</div>