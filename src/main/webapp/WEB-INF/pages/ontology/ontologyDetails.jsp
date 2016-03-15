<%@page import="com.vn.smartdata.constants.SystemRole"%>
<%@page import="com.vn.smartdata.webapp.util.RequestUtil"%>
<%@ include file="/common/taglibs.jsp" %>

<c:set var="isAdmin" value="<%=RequestUtil.hasRole(SystemRole.ROLE_ADMIN) %>"></c:set>

<head>
   <title><fmt:message key="ontology.details"/></title>
   <meta name="menu" content="${menu}"/>
</head>

<div class="col-sm-12 margin-top-minus-1point5">
    <h2>${ontology.ontologyName}</h2> 
</div>

<body>
	<ul id="ontologyTabs" class="nav nav-tabs">
  		<li class="active" id="classesLI"><a data-toggle="tab" href="#classes"><fmt:message key="ontology.details.classes"/></a></li>
	  	<li><a data-toggle="tab" href="#objectProperties"><fmt:message key="ontology.details.properties.object"/></a></li>
	  	<li><a data-toggle="tab" href="#dataProperties"><fmt:message key="ontology.details.properties.data"/></a></li>
	  	<li><a data-toggle="tab" href="#annotationProperties"><fmt:message key="ontology.details.properties.annotation"/></a></li>
	  	<li><a data-toggle="tab" href="#individuals"><fmt:message key="ontology.details.individuals"/></a></li>
	  	
	  	<!-- Option button -->
	  	<c:if test="${isAdmin}">
	  		<div class="btn-group pull-right margin-bottom-none margin-right-15px">
				<li id="" class="btn btn-danger dropdown-toggle"
					data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><fmt:message
						key="button.options" />&nbsp;<span class="caret"></span></li>
				<ul class="dropdown-menu">
					<li><a id="discardChangesBtn" href="javascript:;"><i class="fa fa-undo"></i>&nbsp;<fmt:message key="button.discard.changes"/></a></li>
					<li><a id="saveChangesBtn" href="javascript:;"><i class="fa fa-floppy-o"></i>&nbsp;<fmt:message key="button.save.changes"/></a></li>
					<li><a id="downloadBtn" href="javascript:;"><i class="fa fa-download"></i>&nbsp;<fmt:message key="button.download"/></a></li>
					<!-- <li role="separator" class="divider"></li>
					<li><a href="#">Separated link</a></li> -->
				</ul>
			</div>
	  	</c:if>
	</ul>
	
	<div class="tab-content">
		<div class="tab-pane fade in active" id="classes">
			<div class="parent-content">
				<%@ include file="/WEB-INF/pages/ontology/details/classes.jspf" %>
			</div>
		</div>
		<div class="tab-pane fade" id="objectProperties">
			<div class="parent-content">
				<%@ include file="/WEB-INF/pages/ontology/details/objectProperties.jspf" %>
			</div>
		</div>
		<div class="tab-pane fade" id="dataProperties">
			<div class="parent-content">
				<%@ include file="/WEB-INF/pages/ontology/details/dataProperties.jspf" %>
			</div>
		</div>
		<div class="tab-pane fade" id="annotationProperties">
			<div class="parent-content">
				<%@ include file="/WEB-INF/pages/ontology/details/annotationProperties.jspf" %>
			</div>
		</div>
		<div class="tab-pane fade" id="individuals">
			<div class="parent-content">
				<%-- <%@ include file="/WEB-INF/pages/ontology/details/individuals.jspf" %> --%>
				<%@ include file="/WEB-INF/pages/ontology/details/individuals_lazy-loading.jspf" %>
			</div>
		</div>
	</div>
	
	<!-- This context menu is used to show a delete menu when an admin right click an individual -->
	<ul id="deleteIndividualContextMenu" class="dropdown-menu display-none" role="menu">
	    <li><a tabindex="-1" href="#"><i class="fa fa-remove"></i>&nbsp;<fmt:message key="button.delete"/></a></li>
	    <!-- <li><a tabindex="-1" href="#">Another action</a></li>
	    <li><a tabindex="-1" href="#">Something else here</a></li>
	    <li class="divider"></li>
	    <li><a tabindex="-1" href="#">Separated link</a></li> -->
	</ul>
	
	<script type="text/javascript">
		var isAdmin = ${isAdmin};
		var refreshDataConfirmMessage = '<fmt:message key="ontology.details.refresh.message"/>';
		var reloadSuccessMessage = '<fmt:message key="ontology.details.refresh.success"/>';
		var confirmDialog = '<fmt:message key="label.confirm"/>';
		var ontologyId = '${ontologyId}';
		
		var objectPropLoaded = false;
	  	var dataPropLoaded = false;
	  	var annoPropLoaded = false;
	  	var individualsLoaded = false;
	  	var otherVersionsLoaded = false;

	  	var currentSelectedTab;
	  	
	  	// Classes hierarchy URL
		var classesTabURL = '//' + window.location.host + '${ctx}' + '/ontology/getClassDetails';
		
		// Object Properties URL
		var opJstreeRootURL = '//' + window.location.host + '${ctx}' + '/ontology/getOPRootContent';
		var opJstreeChildrenURL = '//' + window.location.host + '${ctx}' + '/ontology/getOPChildrenContent';
	
		// Data Properties URL
		var dpJstreeRootURL = '//' + window.location.host + '${ctx}' + '/ontology/getDPRootContent';
		var dpJstreeChildrenURL = '//' + window.location.host + '${ctx}' + '/ontology/getDPChildrenContent';
	
		// Data Properties URL
		var apJstreeRootURL = '//' + window.location.host + '${ctx}' + '/ontology/getAPRootContent';
		var apJstreeChildrenURL = '//' + window.location.host + '${ctx}' + '/ontology/getAPChildrenContent';
	
		// Individuals URL
		var individualsTabURL = '//' + window.location.host + '${ctx}' + '/ontology/getClassIndividuals';

		// Class hierarchy URL
		var classHierarchyURL = '//' + window.location.host + '${ctx}' + '/ontology/getClassHierarchy';
		// Annotation hierarchy URL
		var annotationHierarchyURL = '//' + window.location.host + '${ctx}' + '/ontology/getAnnotationHierarchy';
		// OP hierarchy URL
		var opHierarchyURL = '//' + window.location.host + '${ctx}' + '/ontology/getOPHierarchy';
		// DP hierarchy URL
		var dpHierarchyURL = '//' + window.location.host + '${ctx}' + '/ontology/getDPHierarchy';
		// individual hierarchy URL
		var individualHierarchyURL = '//' + window.location.host + '${ctx}' + '/ontology/getIndividualHierarchy';

		// Solution to parent will collapse if its children are floating: http://stackoverflow.com/questions/218760/how-do-you-keep-parents-of-floated-elements-from-collapsing
		var listGroupItemTemplate = '<span class="list-group-item list-group-item-span white-space-pre-wrap word-break-all overflow-auto"></span>';
	
		// Classes variables
		var subClassOfListGroup = $('div#subClassOfBody').find('.list-group');
		var equivalentToListGroup = $('div#equivalentToBody').find('.list-group');
		var membersListGroup = $('div#membersBody').find('.list-group');
		var targetForKeyListGroup = $('div#targetForKeyBody').find('.list-group');
		var disjointWithListGroup = $('div#disjointWithBody').find('.list-group');
		var disjointUnionOfListGroup = $('div#disjointUnionOfBody').find('.list-group');
	
		// Object Properties variables
		var subPropertyOfListGroup = $('div#subPropertyOfBody').find('.list-group');
		var OPequivalentToListGroup = $('div#opEquivalentToBody').find('.list-group');
		var domainsListGroup = $('div#domainsBody').find('.list-group');
		var rangesListGroup = $('div#rangesBody').find('.list-group');
		var inverseOfListGroup = $('div#inverseOfBody').find('.list-group');
		var OPdisjointWithListGroup = $('div#opDisjointWithBody').find('.list-group');
	
		// Data Properties variables
		var DPsubPropertyOfListGroup = $('div#dpSubPropertyOfBody').find('.list-group');
		var DPequivalentToListGroup = $('div#dpEquivalentToBody').find('.list-group');
		var DPdomainsListGroup = $('div#dpDomainsBody').find('.list-group');
		var DPrangesListGroup = $('div#dpRangesBody').find('.list-group');
		var DPdisjointWithListGroup = $('div#dpDisjointWithBody').find('.list-group');
	
		// Annotation Properties variables
		var APrangesListGroup = $('div#apRangesBody').find('.list-group');
		var APdomainsListGroup = $('div#apDomainsBody').find('.list-group');
		var superPropertiesListGroup = $('div#superPropertiesBody').find('.list-group');

		// Individuals variables
		var typesListGroup = $('div#typesBody').find('.list-group');
		var sameListGroup = $('div#sameBody').find('.list-group');
		var differentListGroup = $('div#differentBody').find('.list-group');
		var opaListGroup = $('div#objectPropAssertionBody').find('.list-group');
		var dpaListGroup = $('div#dataPropAssertionBody').find('.list-group');
		var nopaListGroup = $('div#negativeObjectPropAssertionBody').find('.list-group');
		var ndpaListGroup = $('div#negativeDataPropAssertionBody').find('.list-group');
		
		var annotationTemplate = '<div class="panel-group" id="annotationAccordion">' +
									'<div class="panel panel-default">' +
										'<div class="panel-heading">' +
											'<h4 class="panel-title">' +
												'<span>' +
													'<a class="accordion-toggle color-black" data-toggle="collapse" data-parent="#annotationAccordion" href="#annotationAccordionBody">' +
														'<span class="annotationName"/>' +
														'&nbsp;' +
														'<span class="annotationLanguage"/>' +
														'&nbsp;' +
														'<span class="annotationDatatype"/>' +
														'&nbsp;' +
														'<a href="javascript:;" class="btn btn-sm btn-danger btn-circle pull-right margin-top-10px">' +
															'<i class="fa fa-remove"></i>' +
														'</a>' +
													'</a>' +
												'</span>' +
											'</h4>' +
										'</div>' +
										'<div class="panel-collapse collapse in" id="annotationAccordionBody">' +
											'<div class="list-group"/>' +
										'</div>' +
									'</div>' +
								'</div>';

		// Individual template, used to display individual as a tree when searching
		// On-Clicked: jstree-clicked / On-Hovered: jstree-hovered / Otherwise: only jstree-anchor
		var individualLiTemplate = '<li role="treeitem" id="individualId" class="jstree-node jstree-leaf">' +
								   		'<i class="jstree-icon jstree-ocl" role="presentation"></i>' +
									    '<a class="jstree-anchor" href="javascript:;" tabindex="-1" id="individualId_anchor">' +
											'<i class="jstree-icon jstree-themeicon fa diamond-icon individual-color jstree-themeicon-custom" role="presentation"></i>' +
										'</a>' +
								   '</li>';

		var deleteBtn = '<div><a href="javascript:;" class="btn btn-sm btn-danger btn-circle pull-right margin-top-10px"><i class="fa fa-remove"></i></a></div>';
								
		$(function () {
			// Navigation tab click action
		   	$('.nav-tabs a').on('shown.bs.tab', function(e){
		    	var aElem = e.target;

		    	if(aElem.hash == '#objectProperties' && !objectPropLoaded) {
		    		$('#objectProperties').trigger('click');
		    	} else if (aElem.hash == '#dataProperties' && !dataPropLoaded) {
		    		$('#dataProperties').trigger('click');
		    	} else if (aElem.hash == '#annotationProperties' && !annoPropLoaded) {
		    		$('#annotationProperties').trigger('click');
		    	} else if (aElem.hash == '#individuals' && !individualsLoaded) {
		    		$('#individuals').trigger('click');
		    	}
	    	});

	    	$('.nav.nav-pills a').on('show.bs.tab', function(e){
		    	var aElem = e.target;

		    	currentSelectedTab = aElem.hash;

		    	// if other version tab is clicked, display modal footer, set css height of the panel to be smaller
		    	if(currentSelectedTab == '#otherUsersVersions') {
		    		
		    		// also load other users versions
		    		// and show pagination div
		    		// block the description div
		    		if(!otherVersionsLoaded){
		    			// This function is located at other_users_versions.jspf
		    			loadOtherUsersIndvididualVersions();
		    		} else {
		    			var totalPages = otherUser_totalPages;
		    	        var startIndex = otherUser_startIndex;
		    	        var endIndex = otherUser_endIndex;
			    		
		    			if(totalPages != 0 || startIndex <= endIndex){
		    				$('#otherUser_paginationDiv').show();
			    			$('#individualDescriptionPanel').removeClass('panel-height-custom');
		    			} else {
		    				$('#individualDescriptionPanel').addClass('panel-height-custom');
				    		$('#otherUser_paginationDiv').hide();
		    			}
		    		}

	    		// if other version tab is clicked, do not display modal footer (use display:none, not visibility hidden), and set height of the panel back normal
		    	} else {
			    	$('#individualDescriptionPanel').addClass('panel-height-custom');
		    		$('#otherUser_paginationDiv').hide();
		    	}
	    	});

		 	// discard changes click action
			$('#discardChangesBtn').click(function(e){
				UTIL.showPopupConfirm(confirmDialog, '<fmt:message key="ontology.details.discard.changes.confirm.message"/>', ['Yes', 'No'], function(index){
					if(index == 0){
						UTIL.showLoadingMaskWithMessage('<fmt:message key="ontology.details.discard.changes.message"/>');
						
						$.ajax({
							type: "POST",
					        url: '${ctx}' + "/ontology/discardChanges",
					        data: {
					        	ontologyId: ontologyId
					        },
					        error: function () {
					        	UTIL.showMessage('<fmt:message key="errorPage.title" />', 'error');
					        	UTIL.hideLoadingMask();
					        },
					        success: function(data){
						        UTIL.hideLoadingMask();
						        
						        if(data == true){
						        	UTIL.showMessage('<fmt:message key="request.succeeded" />', 'success');
						        	UTIL.showMessage('<fmt:message key="ontology.details.reloading.data.message" />', 'info');
						        	reloadIndvTab();
						        } else {
						        	UTIL.showMessage('<fmt:message key="ontology.details.discard.changes.error.message" />', 'error');
						        }
					        }
						});
					}
				}, ['btn-primary', 'btn-default']);
			});
			
			// Save changes click action
			$('#saveChangesBtn').click(function(e){
				UTIL.showPopupConfirm(confirmDialog, '<fmt:message key="ontology.details.save.changes.confirm.message"/>', ['Yes', 'No'], function(index){
					if(index == 0){
						UTIL.showLoadingMaskWithMessage('<fmt:message key="ontology.details.save.changes.message"/>');
						
						$.ajax({
							type: "POST",
					        url: '${ctx}' + "/ontology/saveChanges",
					        data: {
					        	ontologyId: ontologyId
					        },
					        error: function () {
					        	UTIL.showMessage('<fmt:message key="errorPage.title" />', 'error');
					        	UTIL.hideLoadingMask();
					        },
					        success: function(data){
						        UTIL.hideLoadingMask();
						        
						        if(data == true){
						        	UTIL.showMessage('<fmt:message key="request.succeeded" />', 'success');
						        } else {
						        	UTIL.showMessage('<fmt:message key="ontology.details.save.changes.error.message" />', 'error');
						        }
					        }
						});
					}
				}, ['btn-primary', 'btn-default']);
			});
			
			// download btn click action
			$('#downloadBtn').click(function(e){
				UTIL.showPopupConfirm('<fmt:message key="ontology.details.download.dialog.message"/>', '<fmt:message key="ontology.details.download.info.message"/>', ['<fmt:message key="button.download.changes"/>', '<fmt:message key="button.download.wo.changes"/>', 'Cancel'], function(index){
					// Download with changes					
					if(index == 0){
						window.location.href = '${ctx}' + "/download/downloadChanges?ontologyId=" + ontologyId;
					// Download without changes
					} else if(index == 1){
						window.location.href = '${ctx}' + "/download/downloadWOChanges?ontologyId=" + ontologyId;
					}
				}, ['btn-info', 'btn-info', 'btn-default']);
			});
		});
	</script>
</body>