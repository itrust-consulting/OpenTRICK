<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<spring:url value="/js/jquery-2.0.js" />"></script>
<script src="<spring:url value="/js/jquery-ui.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.min.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.widgets.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.pager.js" />"></script>
<script src="<spring:url value="/js/jquery.fileDownload.js" />"></script>
<script src="<spring:url value="/js/bootstrap.min.js" />"></script>
<script src="<spring:url value="/js/bootbox.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap-slider.js" />"></script>
<script src="<spring:url value="/js/typeahead.bundle.min.js" />"></script>
<script src="<spring:url value="/js/jquery.fixedheadertable.js" />"></script>
<script src="<spring:url value="/js/bootstrap-datepicker.js" />"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ar.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.az.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.bg.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ca.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.cs.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.cy.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.da.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.de.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.el.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.es.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.et.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.fa.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.fi.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.fr.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.gl.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.he.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.hr.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.hu.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.id.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.is.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.it.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ja.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ka.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.kk.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.kr.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.lt.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.lv.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.mk.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ms.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.nb.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.nl.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.nl-BE.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.no.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.pl.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.pt-BR.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.pt.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ro.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.rs.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.rs-latin.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ru.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.sk.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.sl.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.sq.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.sv.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.sw.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.th.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.tr.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.ua.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.vi.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.zh-CN.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/locales/bootstrap-datepicker.zh-TW.js" />" charset="UTF-8"></script>
<script src="<spring:url value="/js/bootstrap-tooltip.js" />"></script>
<script src="<spring:url value="/js/dom-parser.js" />"></script>
<script src="<spring:url value="/js/highcharts.js" />"></script>
<script src="<spring:url value="/js/highcharts-more.js" />"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>

<script src="<spring:url value="/js/main.js" />"></script>
<script type="text/javascript">
	<sec:authorize ifNotGranted="ROLE_ANONYMOUS" >
	<!--
	new TimeoutInterceptor().Start();
	new TaskManager().Start();
	-->
	</sec:authorize>
</script>
<jsp:include page="alertDialog.jsp" />
