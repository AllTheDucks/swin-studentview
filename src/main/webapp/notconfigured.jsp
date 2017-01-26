<%-- 
    Document   : noaccess
    Created on : 21/07/2011, 11:36:53 AM
    Author     : Wiley Fuller <wfuller@swin.edu.au>

    Copyright Swinburne University of Technology, 2011.
  --%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="title" key="ssv.notConfiguredPage.title" />
<fmt:message var="notConfiguredMessage" key="ssv.notConfiguredPage.notConfiguredMessage" />


<bbNG:learningSystemPage title="${title}" ctxId="ctx" > 
    <bbNG:pageHeader >
        <bbNG:breadcrumbBar >
            <bbNG:breadcrumb title="${title}"/>
        </bbNG:breadcrumbBar>
        <bbNG:pageTitleBar title="${title}" />
    </bbNG:pageHeader>
    ${notConfiguredMessage}
</bbNG:learningSystemPage>