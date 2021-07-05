package org.jhipster.health.service;

import org.jhipster.health.domain.BloodPressure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.ZonedDateTime;

@Service
public class BloodPressureService {
    private final Logger log = LoggerFactory.getLogger(BloodPressureService.class);


    public BloodPressure parseWithDbf(String xmlPayload) {
        log.debug("Parse XML Payload with Document Builder Factory: {}", xmlPayload);

        try {
            //Parser that produces DOM object trees from XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            //Disallow dtd
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            //API to obtain DOM Document instance
            DocumentBuilder builder;

            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlPayload)));

            //Normalize the XML Structure
            doc.getDocumentElement().normalize();

            Element element = doc.getDocumentElement();
            Long bloodPressureId = Long.valueOf(doc.getElementsByTagName("bloodPressureId").item(0).getTextContent());
            ZonedDateTime bloodPressureTimestamp = ZonedDateTime.parse(doc.getElementsByTagName("bloodPressureTimestamp").item(0).getTextContent());
            Integer bloodPressureSystolic = Integer.valueOf(doc.getElementsByTagName("bloodPressureSystolic").item(0).getTextContent());
            Integer bloodPressureDiastolic = Integer.valueOf(doc.getElementsByTagName("bloodPressureDiastolic").item(0).getTextContent());

            BloodPressure bloodPressure = new BloodPressure();
            bloodPressure.setId(bloodPressureId);
            bloodPressure.setTimestamp(bloodPressureTimestamp);
            bloodPressure.setSystolic(bloodPressureSystolic);
            bloodPressure.setDiastolic(bloodPressureDiastolic);
            bloodPressure.setUser(null);

            return bloodPressure;

        } catch (ParserConfigurationException e) {
            log.error("ParserConfigurationException was thrown: " + e.getMessage());
        } catch (SAXException e) {
            log.error("SAXException was thrown: " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException was thrown: " + e.getMessage());
        }

        return null;
    }

    public BloodPressure parseWithXif(String xmlPayload) {

        log.debug("Parse XML Payload with XML Input Factory: {}", xmlPayload);

        Long bloodPressureId = null;
        ZonedDateTime bloodPressureTimestamp = null;
        Integer bloodPressureSystolic = null;
        Integer bloodPressureDiastolic = null;

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();

            XMLStreamReader streamReader = factory.createXMLStreamReader(new StringReader(xmlPayload));

            if (streamReader != null) {
                while (streamReader.hasNext()) {
                    //Move to next event
                    streamReader.next();

                    if (streamReader.isStartElement()) {
                        if (streamReader.getLocalName().equalsIgnoreCase("bloodPressureId")) {
                            bloodPressureId = Long.valueOf(streamReader.getElementText());
                            log.info(bloodPressureId + "");
                        } else if (streamReader.getLocalName().equalsIgnoreCase("bloodPressureTimestamp")) {
                            bloodPressureTimestamp = ZonedDateTime.parse(streamReader.getElementText());
                            log.info(bloodPressureTimestamp + "");
                        } else if (streamReader.getLocalName().equalsIgnoreCase("bloodPressureSystolic")) {
                            bloodPressureSystolic = Integer.valueOf(streamReader.getElementText());
                            log.info(bloodPressureSystolic + "");
                        } else if (streamReader.getLocalName().equalsIgnoreCase("bloodPressureDiastolic")) {
                            bloodPressureDiastolic = Integer.valueOf(streamReader.getElementText());
                            log.info(bloodPressureDiastolic + "");
                        }
                    }
                }
            }

            BloodPressure bloodPressure = new BloodPressure();
            bloodPressure.setId(bloodPressureId);
            bloodPressure.setTimestamp(bloodPressureTimestamp);
            bloodPressure.setSystolic(bloodPressureSystolic);
            bloodPressure.setDiastolic(bloodPressureDiastolic);
            bloodPressure.setUser(null);

            System.out.println(bloodPressure.toString());

            return bloodPressure;

        } catch (XMLStreamException e) {
            log.error("XMLStreamException was thrown: " + e.getMessage());
        }

        return null;
    }
}
