package com.vn.smartdata.model.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SameIndividualJAXB {
	@XmlElement(name = "iri")
	private String iri;

	@XmlElement(name = "shortForm")
	private String shortForm;

	@XmlElement(name = "createdDate")
	private Date createdDate;
}
