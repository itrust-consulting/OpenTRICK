<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="tab-message" class="tab-pane" data-update-required="true" data-trigger="loadNotification">
	<div id="section_notification">
		<ul class="nav nav-pills bordered-bottom" id="menu_customer">
			<li><a id='btn-add-notification' href="#"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
			<li id="btn-clear-notification" class="pull-right"><a href="#" class="text-danger"><span class="glyphicon glyphicon-remove"></span>
					<spring:message code="label.action.clear"/> </a></li>
		</ul>
		<div  class='row' id="notification-content" style="margin-top: 10px;">
		</div>
	</div>
</div>