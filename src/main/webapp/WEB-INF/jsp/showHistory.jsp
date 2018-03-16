<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<c:set var="n"><portlet:namespace/></c:set>

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<div id="${n}">

    <div class="container-fluid bootstrap-styles announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 class="title" role="heading"><spring:message code="showHistory.history"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ topic.id }"/></portlet:renderURL>"><i class="fa fa-arrow-left" aria-hidden="true"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL></portlet:renderURL>"><i class="fa fa-home" aria-hidden="true"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <c:choose>
            <c:when test="${not empty announcements}">
                <div class="row">
                    <div class="col-md-12">
                        <table id="historyTable" class="table table-condensed announcements-table tablesorter">
                            <caption class="sr-only"><spring:message code="showHistory.table"/></caption>
                            <thead>
                                <tr>
                                    <th scope="col" width="15%"><spring:message code="showHistory.header.topic"/></th>
                                    <th scope="col"><spring:message code="showHistory.header.ann"/></th>
                                    <th scope="col"><spring:message code="showHistory.header.start"/></th>
                                    <th scope="col"><spring:message code="showHistory.header.end"/></th>
                                    <th scope="col"><spring:message code="showHistory.header.repost"/></th>
                                    <th scope="col"><spring:message code="showHistory.header.delete"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${announcements}" var="ann">
                                    <tr>
                                        <td><c:out value="${ann.parent.title}"/></td>
                                        <td>
                                            <a title="<spring:message code="showHistory.preview"/>"  href="<portlet:renderURL><portlet:param name="action" value="previewAnnouncement"/><portlet:param name="annId" value="${ ann.id }"/></portlet:renderURL>">
                                                <c:out value="${ann.title}"/>
                                            </a>
                                            <br/><c:out value="${ann.abstractText}"/>
                                        </td>
                                        <td><fmt:formatDate value="${ann.startDisplay}" dateStyle="short"/></td>
                                        <td><fmt:formatDate value="${ann.endDisplay}" dateStyle="short"/></td>
                                        <td>
                                            <a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${ann.id}"/></portlet:renderURL>" title="<spring:message code="showHistory.viewedit"/>">
                                                <span class="pull-right">
                                                    <spring:message code="showHistory.viewedit"/> <i class="fa fa-edit" aria-hidden="true"></i>
                                                </span>
                                            </a>
                                        </td>
                                        <td>
                                            <a href="#" onclick="${n}_delete('<portlet:actionURL escapeXml="false"><portlet:param name="action" value="deleteAnnouncement"/><portlet:param name="annId" value="${ann.id}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="showHistory.delete"/>">
                                                <span class="pull-right">
                                                    <spring:message code="showHistory.delete"/> <i class="fa fa-trash-o" aria-hidden="true"></i>
                                                </span>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="col-lg-12 alert alert-warning"><spring:message code="display.no.history"/></div>
            </c:otherwise>
        </c:choose>
    </div>


<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.11.0/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.tablesorter.min.js"/>"></script>
<script type="text/javascript">
var ${n} = ${n} || {};
${n}.jQuery  = jQuery.noConflict(true);
${n}.jQuery(document).ready(function(){
		${n}.jQuery("#historyTable").tablesorter( {sortList: [[1,0], [2,0]]} );
	}
);
</script>

</div>
