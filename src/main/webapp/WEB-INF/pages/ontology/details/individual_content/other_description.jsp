<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- <%@ page trimDirectiveWhitespaces="true" %> --%>

<%
	try {
%>

<c:choose>
	<c:when test="${not empty individualList}">
		<c:forEach items="${individualList}" var="individual">
			<c:set var="individualId" value="${individual.id}"></c:set>
			<c:set var="annotations" value="${individual.annotations}"></c:set>
			
			<c:set var="types" value="${individual.types}"></c:set>
			
			<c:set var="sameIndividuals" value="${individual.sameIndividuals}"></c:set>
			<c:set var="differentIndividuals" value="${individual.differentIndividuals}"></c:set>
			
			<c:set var="opas" value="${individual.opas}"></c:set>
			<c:set var="nopas" value="${individual.nopas}"></c:set>
			
			<c:set var="dpas" value="${individual.dpas}"></c:set>
			<c:set var="ndpas" value="${individual.ndpas}"></c:set>
			
			<div class="col-sm-12 margin-top-15px">
				<div class="alert alert-success user-title">
					<strong><a href="mailto:${individual.editor.email}" title="${individual.editor.email}" class="user-title-anchor">${individual.editor.fullName}</a></strong>
				</div>
			</div>
			
			<!-- Annotations -->
			<div class="form-group form-group-sm">
				<div class="col-sm-12">
					<label class="control-label full-width">
						<fmt:message key="ontology.details.annotations" />
						&nbsp;
						<span class="badge pull-right annotation-badge" id="otherUser_IDannotationBadge${individualId}" title='<fmt:message key="ontology.details.individual.annotation.badge.title"/>'>${fn:length(annotations)}</span>
					</label>
					<div id="otherUser_IDannotations${individualId}">
					
						<c:forEach items="${annotations}" var="annotation">
							<c:set var="annotationId" value="${annotation.id}"/>
							<div class="panel-group" id="annotationAccordion${annotationId}">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<span>
												<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#annotationAccordion${annotationId}" href="#annotationAccordionBody${annotationId}">
													<span class="owl-entity-short-form" title="${annotation.annotationPropertyIRI}" style="font: bold;">
														<c:out value="${annotation.annotationPropertyShortForm}" escapeXml="false"/> 
													</span>
													<c:if test="${not empty annotation.annotationLanguage}">
														&nbsp;
														[language: ${annotation.annotationLanguage}]
													</c:if>
													<c:if test="${not empty annotation.annotationDataTypeIRI}">
														&nbsp;
														[type: <span href="javascript:;" class="owl-entity-short-form" title="${annotation.annotationDataTypeIRI}">${annotation.annotationDataType}</span>]
													</c:if>
												</a>
											</span>
										</h4>
									</div>
									<div class="panel-collapse collapse in" id="annotationAccordionBody${annotationId}">
										<div class="list-group">
											<span class="list-group-item list-group-item-span word-break-all">
												${annotation.annotationValue}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
						
					</div>
				</div>
			</div>
			
			<div id="descriptionOfIndividual${individualId}" class="form-group form-group-sm">
				<div class="col-sm-6">
					<label class="control-label">
						<fmt:message key="ontology.details.description" />
					</label>
					<div id="otherUser_IDdescription${individualId}">
						
						<!-- Types -->
						<div class="panel-group" id="otherUser_typesAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_typesAccordion${individualId}" href="#otherUser_typesBody${individualId}">
												<fmt:message key="ontology.details.description.types" />
												<span class="badge" id="otherUser_typesBadge${individualId}">${fn:length(types)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_typesBody${individualId}">
									<div class="list-group">
										<c:forEach items="${types}" var="type">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-circle class-color"></i>&nbsp;&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${type.owlClassIRI}" style="font: bold;">
													<c:out value="${type.owlClassShortForm}"></c:out>
												</a>
											</span>								
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- Same individual as -->
						<div class="panel-group" id="otherUser_sameAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_sameAccordion${individualId}" href="#otherUser_sameBody${individualId}">
												<fmt:message key="ontology.details.description.same" />
												<span class="badge" id="otherUser_sameBadge${individualId}">${fn:length(sameIndividuals)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_sameBody${individualId}">
									<div class="list-group">
										<c:forEach items="${sameIndividuals}" var="sameIndividual">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-circle class-color"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${sameIndividual.individualIRI}">${sameIndividual.individualShortForm}</a>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- different individuals -->
						<div class="panel-group" id="otherUser_differentAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_differentAccordion${individualId}" href="#otherUser_differentBody${individualId}">
												<fmt:message key="ontology.details.description.different" />
												<span class="badge" id="otherUser_differentBadge${individualId}">${fn:length(differentIndividuals)}</span>
											</a>
										</span>
										
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_differentBody${individualId}">
									<div class="list-group">
										<c:forEach items="${differentIndividuals}" var="differentIndividual">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-circle class-color"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${differentIndividual.individualIRI}">${differentIndividual.individualShortForm}</a>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- End Description-->
					</div>
				</div>
				
				<div class="col-sm-6">
					<label class="control-label">
						<fmt:message key="ontology.details.property.assertions" />
					</label>
					<div id="otherUser_propertyAssertions">
						
						<!-- object property assertions -->
						<div class="panel-group" id="otherUser_objectPropAssertionAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_objectPropAssertionAccordion${individualId}" href="#otherUser_objectPropAssertionBody${individualId}">
												<fmt:message key="ontology.details.object.property.assertions" />
												<span class="badge" id="otherUser_objectPropAssertionBadge">${fn:length(opas)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_objectPropAssertionBody${individualId}">
									<div class="list-group">
										<c:forEach items="${opas}" var="opa">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-square op-class"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${opa.objectPropertyIRI}">
													${opa.objectPropertyShortForm}
												</a>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${opa.individualIRI}">
													${opa.individualShortForm}
												</a>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- data property assertions -->
						<div class="panel-group" id="otherUser_dataPropAssertionAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_dataPropAssertionAccordion${individualId}" href="#otherUser_dataPropAssertionBody${individualId}">
												<fmt:message key="ontology.details.data.property.assertions" />
												<span class="badge" id="otherUser_dataPropAssertionBadge${individualId}">${fn:length(dpas)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_dataPropAssertionBody${individualId}">
									<div class="list-group">
										<c:forEach items="${dpas}" var="dpa">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-square dp-class"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${dpa.dataPropertyIRI}">${dpa.dataPropertyShortForm}</a>&nbsp;
												<span class="owl-literal">
													<c:choose>
														<c:when test="${empty dpa.dataPropertyTypeIRI}">
															${dpa.dataPropertyValue}<c:if test="${not empty dpa.dataPropertyLanguage}">@${dpa.dataPropertyLanguage}</c:if>
														</c:when>
														<c:otherwise>
															"${dpa.dataPropertyValue}"^^<a href="javascript:;" class="owl-entity-short-form" title="${dpa.dataPropertyTypeIRI}">${dpa.dataPropertyType}</a>
														</c:otherwise>
													</c:choose>
												</span>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- negative object property assertions -->
						<div class="panel-group" id="otherUser_negativeObjectPropAssertionAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_negativeObjectPropAssertionAccordion${individualId}" href="#otherUser_negativeObjectPropAssertionBody${individualId}">
												<fmt:message key="ontology.details.negative.object.property.assertions" />
												<span class="badge" id="otherUser_negativeObjectPropAssertionBadge${individualId}">${fn:length(nopas)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_negativeObjectPropAssertionBody${individualId}">
									<div class="list-group">
										<c:forEach items="${nopas}" var="nopa">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-square op-class"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${nopa.objectPropertyIRI}">
													${nopa.objectPropertyShortForm}
												</a>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${nopa.individualIRI}">
													${nopa.individualShortForm}
												</a>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- negative data property assertions -->
						<div class="panel-group" id="otherUser_negativeDataPropAssertionAccordion${individualId}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<span>
											<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#otherUser_negativeDataPropAssertionAccordion${individualId}" href="#otherUser_negativeDataPropAssertionBody${individualId}">
												<fmt:message key="ontology.details.negative.data.property.assertions" />
												<span class="badge" id="otherUser_negativeDataPropAssertionBadge${individualId}">${fn:length(ndpas)}</span>
											</a>
										</span>
									</h4>
								</div>
								<div class="panel-collapse collapse in" id="otherUser_negativeDataPropAssertionBody${individualId}">
									<div class="list-group">
										<c:forEach items="${ndpas}" var="ndpa">
											<span class="list-group-item list-group-item-span word-break-all">
												<i class="fa fa-square dp-class"></i>&nbsp;
												<a href="javascript:;" class="owl-entity-short-form" title="${ndpa.dataPropertyIRI}">${ndpa.dataPropertyShortForm}</a>&nbsp;
												<span class="owl-literal">
													<c:choose>
														<c:when test="${empty ndpa.dataPropertyTypeIRI}">
															${ndpa.dataPropertyValue}<c:if test="${not empty ndpa.dataPropertyLanguage}">@${ndpa.dataPropertyLanguage}</c:if>
														</c:when>
														<c:otherwise>
															"${ndpa.dataPropertyValue}"^^<a href="javascript:;" class="owl-entity-short-form" title="${ndpa.dataPropertyTypeIRI}">${ndpa.dataPropertyType}</a>
														</c:otherwise>
													</c:choose>
												</span>
											</span>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
						
						<!-- End Description-->
					</div>
				</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="col-sm-12 margin-top-15px">
			<div class="alert alert-danger user-title">
				<strong class="user-title-anchor">
					<fmt:message key="ontology.details.individuals.other.users.versions.no.record.found"/>
				</strong>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<%
	} catch (Exception e) {
		e.printStackTrace();
	} 
%>

<script type="text/javascript">
	var otherUser_startIndex = '${startIndex}';
	var otherUser_endIndex = '${endIndex}';
	var otherUser_totalPages = '${totalPages}';
	var otherUser_totalRecords = '${totalRecords}';
</script>
