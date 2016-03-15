<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="ontology.title"/></title>
    <meta name="menu" content="OntologyMenu"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="ontologyList.ontology"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="menu.ontology.create"/></h2>
    <p><fmt:message key="ontology.create.message"/></p>
</div>
<div class="col-sm-7">
    <spring:bind path="createOntology.*">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger alert-dismissable">
                <a href="#" data-dismiss="alert" class="close">&times;</a>
                <c:forEach var="error" items="${status.errorMessages}">
                    <c:out value="${error}" escapeXml="false"/><br/>
                </c:forEach>
            </div>
        </c:if>
    </spring:bind>

    <form:form commandName="createOntology" method="POST" action="create" id="ontologyForm" autocomplete="off" enctype="multipart/form-data"
               cssClass="well" name="ontologyForm">
        <%-- <form:hidden path="id"/>
        <form:hidden path="version"/>
        <input type="hidden" name="from" value="<c:out value="${param.from}"/>"/> --%>

        <spring:bind path="createOntology.ontologyName">
        	<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        </spring:bind>
            <appfuse:label styleClass="control-label" key="ontology.ontologyName"/>
            <form:input cssClass="form-control" path="ontologyName" id="ontologyName"/>
            <form:errors path="ontologyName" cssClass="help-block"/>
            <%-- <c:if test="${pageContext.request.remoteUser == user.username}">
                <span class="help-block">
                    <a href="<c:url value="/updatePassword" />"><fmt:message key='updatePassword.changePasswordLink'/></a>
                </span>
            </c:if> --%>
        </div>

        <spring:bind path="createOntology.description">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        </spring:bind>
            <appfuse:label styleClass="control-label" key="ontology.description"/>
            <form:textarea cssClass="form-control" path="description" id="description"/>
            <form:errors path="description" cssClass="help-block"/>
        </div>
        
        <spring:bind path="createOntology.uploadedFile">
  		<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        </spring:bind>
        
	        <appfuse:label key="uploadForm.file" styleClass="control-label"/>
	        <form:input path="uploadedFile" type="file" id="uploadedFile"/>
	        <form:errors path="uploadedFile" cssClass="help-block"/>
        </div>
        
        <div class="form-group">
            <button type="submit" class="btn btn-primary" name="save" onclick="bCancel=false">
                <i class="icon-ok icon-white"></i> <fmt:message key="button.save"/>
            </button>

            <%-- <c:if test="${param.from == 'list' and param.method != 'Add'}">
              <button type="submit" class="btn btn-default" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                  <i class="icon-trash"></i> <fmt:message key="button.delete"/>
              </button>
            </c:if> --%>

            <button type="submit" class="btn btn-default" name="cancel" onclick="bCancel=true">
                <i class="icon-remove"></i> <fmt:message key="button.cancel"/>
            </button>
        </div>
        
    </form:form>
</div>

<c:set var="scripts" scope="request">
<script type="text/javascript">
// This is here so we can exclude the selectAll call when roles is hidden
function onFormSubmit(theForm) {
    return validateOntology(theForm);
}
</script>
</c:set>

<v:javascript formName="ontology" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value="/scripts/validator.jsp"/>"></script>

