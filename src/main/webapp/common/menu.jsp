<%@ include file="/common/taglibs.jsp"%>

<menu:useMenuDisplayer name="Velocity" config="navbarMenu.vm" permissions="rolesAdapter">
<div class="collapse navbar-collapse" id="navbar">
<ul class="nav navbar-nav">
    <c:if test="${empty pageContext.request.remoteUser}">
        <li class="active">
            <a href="<c:url value="/login"/>"><fmt:message key="login.title"/></a>
        </li>
    </c:if>
    <menu:displayMenu name="Home"/>
    <menu:displayMenu name="UserMenu"/>
    <c:choose>
    	<c:when test="${isAdmin}">
			<menu:displayMenu name="OntologyMenu"/>    
	    </c:when>
	    <c:otherwise>
	    	<menu:displayMenu name="UserOntologyMenu"/>
	    </c:otherwise>
    </c:choose>
    
    <%-- <menu:displayMenu name="ActiveOntologyMenu"/> --%>
    <menu:displayMenu name="AdminMenu"/>
    <menu:displayMenu name="Logout"/>
</ul>
</div>
</menu:useMenuDisplayer>
