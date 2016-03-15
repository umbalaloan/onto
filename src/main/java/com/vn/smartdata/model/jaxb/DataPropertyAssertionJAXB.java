package com.vn.smartdata.model.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPropertyAssertionJAXB {

	@XmlElement(name = "dataPropertyIRI")
	private String dataPropertyIRI;

	@XmlElement(name = "dataPropertyShortForm")
	private String dataPropertyShortForm;

	@XmlElement(name = "annotationValue")
	private String annotationValue;

	@XmlElement(name = "annotationType")
	private String annotationType;

	@XmlElement(name = "annotationTypeIRI")
	private String annotationTypeIRI;

	@XmlElement(name = "annotationLanguage")
	private String annotationLanguage;

	@XmlElement(name = "annotationLanguage", defaultValue = "false")
	private boolean isNegative;

	@XmlElement(name = "createdDate")
	private Date createdDate;

}
