package com.vn.smartdata.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * The Class InvalidateSession.
 */
@Controller
public class InvalidateSession
{
	
	/**
	 * This url gets invoked when spring security invalidates session (ie timeout).
	 * Specific content indicates ui layer that session has been invalidated and page should be redirected to logout.
	 *
	 * @return the string
	 */
	@RequestMapping(value = "invalidate.html", method = RequestMethod.GET)
	@ResponseBody
	public String invalidateSession() {
		return "invalidSession";
	}
}