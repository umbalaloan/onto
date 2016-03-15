<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="home.title"/></title>
    <meta name="menu" content="Home"/>
</head>
<body class="home col-sm-12">

<h2><fmt:message key="home.heading"/></h2>
<p><fmt:message key="home.message"/></p>

<ul class="glassList">
	<c:choose>
		<c:when test="${isAdmin}">
			<li>
		        <a href="<c:url value='/userform'/>"><fmt:message key="menu.user"/></a>
		    </li>
		    <li>
		        <a href="<c:url value='/ontology/create'/>"><fmt:message key="menu.ontology.create"/></a>
		    </li>
		    <li>
				Join collaborative activities with others
		    </li>
		    <li>
				A right to edit ontology's original version		        
		    </li>
		    <li>
		        Tool <fmt:message key="menu.admin"/> (<a href="<c:url value="/#"/>">user management</a>, <a href="<c:url value="/#"/>">ontology management</a>)
		    </li>
		</c:when>
		<c:otherwise>
			<li>
		        <a href="<c:url value='/userform'/>"><fmt:message key="menu.user"/></a>
		    </li>
		    <li>
		        <a href="<c:url value="/ontology/list"/>">View <fmt:message key="menu.ontology.list"/></a>
		    </li>
		    <li>
				Join collaborative activities with others      
		    </li>
		</c:otherwise>
	</c:choose>
</ul>

</body>
