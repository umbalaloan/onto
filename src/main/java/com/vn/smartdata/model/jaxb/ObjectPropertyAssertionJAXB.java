package com.vn.smartdata.model.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectPropertyAssertionJAXB {
	@XmlElement(name = "objectPropertyIRI")
	private String objectPropertyIRI;

	@XmlElement(name = "objectPropertyShortForm")
	private String objectPropertyShortForm;

	@XmlElement(name = "individualIRI")
	private String individualIRI;

	@XmlElement(name = "individualShortForm")
	private String individualShortForm;

	@XmlElement(name = "annotationLanguage", defaultValue = "false")
	private boolean isNegative;

	@XmlElement(name = "createdDate")
	private Date createdDate;
}
