<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab_impact_kb_scale">
	<div class='section row' id='section_impact_kb_scale'>
		<ul class="nav nav-pills bordered-bottom" id="menu_impact_kb_scale" style="margin-bottom: 10px;">
			<li><a href="#"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a><span class="glyphicon glyphicon-edit danger"></span> <spring:message
						code="label.action.edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="multi"><a class="text-danger"><span class="glyphicon glyphicon-remove"></span> <spring:message
						code="label.action.delete" /> </a></li>
		</ul>
	</div>
</div>