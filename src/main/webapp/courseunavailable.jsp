<%-- 
    Document   : courseunavailable.jsp
    Created on : 21/07/2011, 11:39:19 AM
    Author     : Wiley Fuller <wfuller@swin.edu.au>

    Copyright Swinburne University of Technology, 2011.
  --%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="title" key="ssv.courseUnavailalablePage.title" />
<fmt:message var="unavailableMessage" key="ssv.courseUnavailalablePage.unavailableMessage" />

<bbNG:learningSystemPage title="${title}" ctxId="ctx" > 
    <bbNG:cssBlock>
        <style type="text/css">
            .localList li {
                list-style: disc;
                margin-left: 2em;
            }
        </style>
    </bbNG:cssBlock>
    <bbNG:pageHeader >
        <bbNG:breadcrumbBar >
            <bbNG:breadcrumb title="${title}"/>
        </bbNG:breadcrumbBar>
        <bbNG:pageTitleBar title="${title}" />
    </bbNG:pageHeader>
    ${unavailableMessage}
</bbNG:learningSystemPage>