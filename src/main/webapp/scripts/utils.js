/**
 * 
 */

var UTIL = function()
{
};

var loadingGifURL = contextPath + '/images/ajax_loader_blue.gif';

/**
 * Redirect to another page
 * @param url
 */
UTIL.redirectTo	= function(url,newTab)
{
	if (newTab == true){
		var win = window.open(url, '_blank');
        win.focus();
	}
	else{
		window.location = url;
	}
	
};

/**
 * Get parameter from current url
 * */
UTIL.getUrlParameter = function(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
};

UTIL.showLoadingMask = function(){
    $.blockUI({
        message: '<div class="alert alert-info" role="alert" style="font-size: 17px"><img src="' + loadingGifURL + '" alt="Loading..." style="height: 20px; width: 20px;"/>&nbsp;Loading ontology...</div>',
        css: { 
            border: 'none', 
            backgroundColor: 'transparent', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px',
            'z-index': '999999'
        }
    });
};

UTIL.hideLoadingMask= function(){
	$.unblockUI();
};

UTIL.showLoadingMaskWithMessage = function(message){
    $.blockUI({
        message: '<div class="alert alert-info" role="alert" style="font-size: 17px"><img src="' + loadingGifURL + '" alt="Loading..." style="height: 20px; width: 20px;"/>&nbsp;' + message + '</div>',
        css: { 
            border: 'none', 
            backgroundColor: 'transparent', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px',
            'z-index': '999999'
        }
    });
};

UTIL.blockElementWithMessage = function (elementId, message) {
	$(elementId).block({ 
        message: '<div class="alert alert-info" role="alert" style="font-size: 17px; width: 150px"><img src="' + loadingGifURL + '" alt="Loading..." style="height: 20px; width: 20px;"/>&nbsp;' + message + '</div>', 
        css: { 
            border: 'none', 
            backgroundColor: 'transparent', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px',
            'z-index': '999999'
        }
    }); 
}

UTIL.unblockElement = function (elementId) {
	$(elementId).unblock();
}

// Type: info, success, error, notice
UTIL.showMessage = function(message,type)
{
	new PNotify({
	    title: false,
	    text: message,
	    type: type,
	    delay:2000,
	    shadow: true,
	    buttons: {
	    	closer: true,
	    	closer_hover: true,
	    	sticker: true,
	    	sticker_hover: true
	    }
	});
};

//money formatting
UTIL.MONEY_STYLE = {
		symbol : "",
		decimal : ".",
		thousand: ",",
		precision : 2,
		format: "%s%v"
};
UTIL.NUMBER_STYLE={
		symbol : "",
		decimal : ".",
		thousand: ",",
		precision : 0,
		format: "%s%v"
};

UTIL.mouseOverCallback = function(selector, changedValue) {
	$(selector)
    .attr('data-original-title', changedValue)
    .tooltip('fixTitle')
    .tooltip('show');
};

UTIL.initializeMaterializeCSS = function () {
	$.material.init();
}

UTIL.getApplicationURL = function () {
	var applicationURL = window.location.protocol + '//' + window.location.host;
	
	return applicationURL;
}

UTIL.initializeModal = function (modalId) {
	$(modalId).modal({
		backdrop: 'static',
		keyboard: false,
		show: false
	});
}

UTIL.showElement = function (show, elementId) {
	if(show){
		$(elementId).show();
	} else {
		$(elementId).hide();
	}
}

UTIL.showPopupConfirm = function (titleMessage, bodyMessage, arrButtons, callBack, btnClasses) {
	var popupModal = $('#popupConfirmModal');
	
	// Add title message
	popupModal.find('.modal-title').html(titleMessage);
	
	// Add body message
	popupModal.find('.modal-body').html(bodyMessage);
	
	// clear block button
	popupModal.find('.modal-footer').html('');
	
	if (!arrButtons) {
		arrButtons = [];
		arrButtons.push('Ok');
	}
	
	for ( var i = 0; i < arrButtons.length; i++) {
		var btn;
		
		if(btnClasses){
			btn = $('<button index="' + i + '" class="btn ' + btnClasses[i] + '">' + arrButtons[i] + '</button>');
		} else {
			btn = $('<button index="' + i + '" class="btn btn-default">' + arrButtons[i] + '</button>');
		}
		popupModal.find('.modal-footer').append(btn);
		
		btn.click(function() {
			if (callBack) {
				var indx = $(this).attr('index');
				callBack(indx);
			}
			// hide popup when any button is clicked
			popupModal.modal('hide');
		});
	}
	
	popupModal.modal('show');
};

// initializing js tree instance
// jsTreeElementId: a DOM element's id that is used for loading js tree instance
// onLoaded: execute this function when js tree is loaded
// onSelectNode: execute this function when a node is selected
// data: a function to get a data from a node plus additional outside data, these are parameters that are sent to the servers
// plugins: an array that is used to declare used plugins
// multiple: is an boolean variable which allows user to select multiple nodes or not (true = allow multiple selection, false = only 1 node is selected at a time)
// offlineData: is an array of data that is used for this jstree ==> we dont need to specify the loading url
UTIL.initializeJsTree = function (jsTreeElementId, loadingURL, data, onLoaded, onSelectNode, plugins, multiple, offlineData){
	
	// if offline data is not null, switch to offline mode jstree
	if(offlineData){
		$(jsTreeElementId)
		.on('loaded.jstree', function(e, data){
			if(onLoaded){
				onLoaded(e, data);
			}
		})
		
		.on('select_node.jstree', function(node, selected, e){
			if(onSelectNode){
				onSelectNode(node, selected, e);
			}
		})
		
		.jstree({
			"core" : {
			  'data' : offlineData,
			  'dataType': "json",
			  multiple : multiple
			},
			"search": {
	  		  	"case_sensitive" : false,
	            "show_only_matches" : true
	        },
			"plugins": plugins
		});
		
	// Else, use URL with extra params to pass to the server
	} else {
		$(jsTreeElementId)
		.on('loaded.jstree', function(e, data){
			if(onLoaded){
				onLoaded(e, data);
			}
		})
		
		.on('select_node.jstree', function(node, selected, e){
			if(onSelectNode){
				onSelectNode(node, selected, e);
			}
		})
		
		.jstree({
			"core" : {
			  'data' : {
				'url' : function (node) {
					return loadingURL(node);
				},
			  "data" : function (node) {
				  	return data(node);
			  },
			   'dataType': "json",
			  },
			  multiple : multiple
			},
			"search": {
	  		  	"case_sensitive" : false,
	            "show_only_matches" : true
	        },
			"plugins": plugins
		});
	}
}

// Destroy a jstree instance
UTIL.destroyJsTree = function (loadingElem){
	$(loadingElem).jstree().destroy(false);
}

// jsTreeId: id of js tree element: #individualJsTree
// reloadURL: url to get data
// reloadType: POST, GET, etc.
// ontologyId: the ontology id
// classId: the IRI of OWL:Thing
// successMessage
UTIL.reloadJSTreeData = function (jsTreeId, reloadURL, reloadType, ontologyId, classId, successMessage, callback) {
	$.ajax({
        type: reloadType,
        url: reloadURL,        
        data: {
        	ontologyId: ontologyId,
        	id: classId
        },
        success: function(data){
        	$(jsTreeId).jstree({
                "xml_data": {
                    "ajax": {
                        cache: false,
                        data: data
                    }
                }
            });
        	
        	//$(jsTreeId).jstree(true).settings.core.data = data;
        	$(jsTreeId).jstree(true).refresh();
        	UTIL.showMessage(successMessage, 'success');
        	
        	if(callback){
        		callback();
        	}
        }
    });
}

//Clear selected state of all nodes in js tree 
UTIL.deselectAll = function (jsTreeElement) {
	$(jsTreeElement).jstree().deselect_all(false);
	//$(jsTreeElement).find('a').removeClass('jstree-clicked');
}