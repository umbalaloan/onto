package com.vn.smartdata.model.jaxb;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "individual")
@XmlAccessorType(XmlAccessType.FIELD)
public class IndividualJAXB {
	@XmlElement(name = "types")
	private List<TypeJAXB> types;

	@XmlElement(name = "sameIndividuals")
	private List<SameIndividualJAXB> sameIndividuals;

	@XmlElement(name = "differentIndividuals")
	private List<DifferentIndividualJAXB> differentIndividuals;

	@XmlElement(name = "objectPropertyAssertions")
	private List<ObjectPropertyAssertionJAXB> objectPropertyAssertions;

	@XmlElement(name = "dataPropertyAssertions")
	private List<DataPropertyAssertionJAXB> dataPropertyAssertions;

	@XmlElement(name = "createdDate")
	private Date createdDate;

	@XmlElement(name = "modifiedDate")
	private Date modifiedDate;
}
