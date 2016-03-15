<%@ include file="/common/taglibs.jsp" %>

<head>
    <title><fmt:message key="menu.ontology.list"/></title>
    <meta name="menu" content="OntologyMenu"/>
</head>

<c:if test="${not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="${searchError}"/>
    </div>
</c:if>

<body>
	<div class="col-sm-12">
	    <h2><fmt:message key="menu.ontology.list"/></h2>
	    
	    <table id="ontologyList" class="display" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th class="table-bordered"><fmt:message
							key="ontology.id" /></th>
					<th class="table-bordered"><fmt:message
							key="ontology.name" /></th>
					<th class="table-bordered"><fmt:message
							key="ontology.description" /></th>
					<th class="table-bordered"><fmt:message
							key="ontology.createdDate" /></th>
					<th class="table-bordered"><fmt:message
							key="ontology.modifiedDate" /></th>
					<th class="table-bordered"><fmt:message
							key="ontology.creator" /></th>
					<th class="table-bordered"><fmt:message key="ontology.action" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${ontologyList}" var="ontology" varStatus="idx">					
					<tr>
						<td class="table-bordered">${ontology.id}</td>
						<td class="table-bordered">${ontology.ontologyName}</td>
						<td class="table-bordered">${ontology.description}</td>
						<td class="table-bordered">${ontology.createdDate}</td>
						<td class="table-bordered">${ontology.modifiedDate}</td>
						<td class="table-bordered">${ontology.creator.fullName}</td>
						<td class="table-bordered">
							<div align="center">
								<c:set var="ontologyDetailsURL">
									<c:url value="/ontology/details">
										<c:param name="ontologyId" value="${ontology.id}"/>
									</c:url>
								</c:set>
								<a href="${ontologyDetailsURL}" class="viewOntologyLink">
									<i class="fa fa-eye small-icon " title="<fmt:message key="button.view"/>"></i>
								</a>								
							</div>
						</td>
					</tr>					
				</c:forEach>
			</tbody>
		</table>
	
	    <%-- <form method="get" action="${ctx}/admin/users" id="searchForm" class="form-inline">
	    <div id="search" class="text-right">
	        <span class="col-sm-9">
	            <input type="text" size="20" name="q" id="query" value="${param.q}"
	                   placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm">
	        </span>
	        <button id="button.search" class="btn btn-default btn-sm" type="submit">
	            <i class="icon-search"></i> <fmt:message key="button.search"/>
	        </button>
	    </div>
	    </form>
	
	    <div id="actions" class="btn-group">
	        <a class="btn btn-primary" href="<c:url value='/userform?method=Add&from=list'/>">
	            <i class="icon-plus icon-white"></i> <fmt:message key="button.add"/></a>
	
	        <a class="btn btn-default" href="<c:url value='/home'/>">
	            <i class="icon-ok"></i> <fmt:message key="button.done"/></a>
	    </div>
	
	    <display:table name="userList" cellspacing="0" cellpadding="0" requestURI=""
	                   defaultsort="1" id="users" pagesize="25" class="table table-condensed table-striped table-hover" export="true">
	        <display:column property="username" escapeXml="true" sortable="true" titleKey="user.username" style="width: 25%"
	                        url="/userform?from=list" paramId="id" paramProperty="id"/>
	        <display:column property="fullName" escapeXml="true" sortable="true" titleKey="activeUsers.fullName"
	                        style="width: 34%"/>
	        <display:column property="email" sortable="true" titleKey="user.email" style="width: 25%" autolink="true"
	                        media="html"/>
	        <display:column property="email" titleKey="user.email" media="csv xml excel pdf"/>
	        <display:column sortProperty="enabled" sortable="true" titleKey="user.enabled"
	                        style="width: 16%; padding-left: 15px" media="html">
	            <input type="checkbox" disabled="disabled" <c:if test="${users.enabled}">checked="checked"</c:if>/>
	        </display:column>
	        <display:column property="enabled" titleKey="user.enabled" media="csv xml excel pdf"/>
	
	        <display:setProperty name="paging.banner.item_name"><fmt:message key="userList.user"/></display:setProperty>
	        <display:setProperty name="paging.banner.items_name"><fmt:message key="userList.users"/></display:setProperty>
	
	        <display:setProperty name="export.excel.filename" value="User List.xls"/>
	        <display:setProperty name="export.csv.filename" value="User List.csv"/>
	        <display:setProperty name="export.pdf.filename" value="User List.pdf"/>
	    </display:table> --%>
	</div>
</body>