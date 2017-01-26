<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--
    Document   : config.jsp
    Created on : 21/07/2011, 12:33:41 PM
    Author     : Wiley Fuller <wfuller@swin.edu.au>

    Copyright Swinburne University of Technology, 2011.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>


<fmt:message var="pluginPageStr" key="admin_plugin_manage.label" bundle="${bundles.navigation_item}"/>
<fmt:message var="title" key="ssv.configPage.title" />
<fmt:message var="pageHelp" key="ssv.configPage.pageHelp" />
<fmt:message var="userDetailsStepTitle" key="ssv.configPage.userDetailsStepTitle" />
<fmt:message var="userRolesStepTitle" key="ssv.configPage.userRolesStepTitle" />
<fmt:message var="userPrefixLabel" key="ssv.configPage.userPrefixlabel" />
<fmt:message var="userEmailLabel" key="ssv.configPage.userEmailLabel" />
<fmt:message var="userEmailInstructions" key="ssv.configPage.userEmailInstructions" />
<fmt:message var="userEmailPrefixInstructions" key="ssv.configPage.userEmailPrefixInstructions" />
<fmt:message var="userEmailPrefixLabel" key="ssv.configPage.userEmailPrefixLabel" />
<fmt:message var="instructorEmailText" key="ssv.configPage.instructorEmailText" />
<fmt:message var="userIdentifierLabel" key="ssv.configPage.userIdentifierlabel" />
<fmt:message var="userIdentifierInfoText" key="ssv.configPage.userIdentifierInfoText" />
<fmt:message var="dskLabel" key="ssv.configPage.dsklabel" />
<fmt:message var="portalRoleLabel" key="ssv.configPage.portalRolelabel" />
<fmt:message var="systemRoleLabel" key="ssv.configPage.systemRolelabel" />
<fmt:message var="userPrefixInstructions" key="ssv.configPage.userPrefixInstructions" />
<fmt:message var="userIdentifierInstructions" key="ssv.configPage.userIdentifierInstructions" />
<fmt:message var="dskInstructions" key="ssv.configPage.dskInstructions" />
<fmt:message var="portalRoleInstructions" key="ssv.configPage.portalRoleInstructions" />
<fmt:message var="systemRoleInstructions" key="ssv.configPage.systemRoleInstructions" />

<bbNG:genericPage title="${title}" ctxId="ctx" >
    <bbNG:pageHeader instructions="${pageHelp}">
        <bbNG:breadcrumbBar environment="SYS_ADMIN" >
            <bbNG:breadcrumb href="../blackboard/admin/manage_plugins.jsp" title="${pluginPageStr}" />
            <bbNG:breadcrumb title="${title}"/>
        </bbNG:breadcrumbBar>
        <bbNG:pageTitleBar title="${title}" />
    </bbNG:pageHeader>

    <style type="text/css">
        .infoText {
            border: 1px solid;
            margin: 0px 10px;
            padding: 3px 3px 3px 5px;
            /*            background-repeat: no-repeat;
                        background-position: 10px center;*/
            color: #00529B;
            background-color: #BDE5F8;
            /*            background-image: url('info.png');*/
            border-radius: 3px;
        }
        #instructorEmailInfoText {
            border: 1px solid #AAA;
            color: #888;
            padding: 2px;
        }
    </style>

    <stripes:form beanclass="au.edu.swinburne.bb.studentview.stripes.ConfigAction">
        <input type="hidden" name="saveConfigSettings" value=""/>
        <bbNG:dataCollection hasRequiredFields="true">
            <bbNG:step title="${userDetailsStepTitle}">

                <!-- user prefix -->
                <bbNG:stepInstructions text="${userPrefixInstructions}"/>
                <bbNG:dataElement label="${userPrefixLabel}">
                    <stripes:text name="userPrefix" id="userPrefix" value="${actionBean.userPrefix}" />
                </bbNG:dataElement>

                <!-- user identifier -->
                <bbNG:stepInstructions text="${userIdentifierInstructions}"/>
                <bbNG:dataElement label="${userIdentifierLabel}">
                    <stripes:select name="userIdentifier" value="${actionBean.userIdentifier}" id="userIdentifier">
                        <stripes:options-collection collection="${actionBean.userIdentifiers}" label="name" />
                    </stripes:select>
                    <span id="userIdInfoText" class="infoText" style="display:inline;">${userIdentifierInfoText}</span>
                </bbNG:dataElement>

                <!-- whether the username is prefixed to email address -->
                <bbNG:stepInstructions text="${userEmailPrefixInstructions}"/>
                <bbNG:dataElement label="${userEmailPrefixLabel}">
                    <stripes:checkbox id="prefixUsername" name="prefixUsername" />
                </bbNG:dataElement>

                <!-- user email address -->
                <bbNG:stepInstructions text="${userEmailInstructions}"/>
                <bbNG:dataElement label="${userEmailLabel}">
                    <stripes:text id="userEmail" name="userEmail" value="${actionBean.userEmail}" />
                    <span id="instructorEmailInfoText">${instructorEmailText}</span>
                </bbNG:dataElement>


                <!-- data source key -->
                <bbNG:stepInstructions text="${dskInstructions}"/>
                <bbNG:dataElement label="${dskLabel}">
                    <stripes:select name="dataSourceKey" value="${actionBean.dataSourceKey}">
                        <stripes:options-collection collection="${actionBean.dataSourceKeys}"
                                                    label="batchUid" value="batchUid" />
                    </stripes:select>
                </bbNG:dataElement>
            </bbNG:step>
            <bbNG:step title="${userRolesStepTitle}">
                <!-- portal role -->
                <bbNG:stepInstructions text="${portalRoleInstructions}"/>
                <bbNG:dataElement label="${portalRoleLabel}">
                    <stripes:select name="portalRole">
                        <stripes:options-collection collection="${actionBean.portalRoles}"
                                                    label="roleName" value="roleID"/>
                    </stripes:select>
                </bbNG:dataElement>

                <!-- system role -->
                <bbNG:stepInstructions text="${systemRoleInstructions}"/>
                <bbNG:dataElement label="${systemRoleLabel}">
                    <stripes:select name="systemRole">
                        <stripes:options-collection collection="${actionBean.systemRoles}"
                                                    label="name" value="identifier"/>
                    </stripes:select>
                </bbNG:dataElement>
            </bbNG:step>
            <bbNG:stepSubmit>
            </bbNG:stepSubmit>
        </bbNG:dataCollection>
    </stripes:form>

    <script type="text/javascript">
        function checkDemoUserPlurality() {
            var userIdType = $("userIdentifier").getValue();
            if (userIdType == "USER_ID" || userIdType == "USER_PKID") {
                $("userIdInfoText").show();
                $("instructorEmailInfoText").show();
                $("userEmail").hide();
            } else {
                $("userIdInfoText").hide();
                $("instructorEmailInfoText").hide();
                $("userEmail").show();
            }
        }

        function calculateEmailAddressExample() {
            var msgPrefix = "(e.g. ";
            var msgSuffix = ")";
            var userIdType = $("userIdentifier").getValue();
            if (userIdType == "USER_ID" || userIdType == "USER_PKID") {
                $("example-email").innerHTML = "";
            } else {
                if ($("prefixUsername").checked) {
                    $("example-email").innerHTML = msgPrefix + $("userPrefix").value
                        + "1234" + $("userEmail").value + msgSuffix;
                } else {
                    $("example-email").innerHTML = msgPrefix
                        + $("userEmail").value + msgSuffix;
                }
            }
        }

        $("userPrefix").observe("keyup", calculateEmailAddressExample);
        $("userEmail").observe("keyup", calculateEmailAddressExample);
        $("prefixUsername").observe("change", calculateEmailAddressExample);
        $("userIdentifier").observe("change", calculateEmailAddressExample);
        $("userIdentifier").observe("change", calculateEmailAddressExample);
        $("userIdentifier").observe("change", checkDemoUserPlurality);
        checkDemoUserPlurality();
        calculateEmailAddressExample();
    </script>

</bbNG:genericPage>