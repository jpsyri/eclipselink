/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases.MyStreamSchemaOutputResolver;
import org.xml.sax.InputSource;

public abstract class JAXBWithJSONTestCases extends JAXBTestCases {

    protected String controlJSONLocation;
    private String controlJSONWriteLocation;
    private String controlJSONWriteFormattedLocation;

    public JAXBWithJSONTestCases(String name) throws Exception {
        super(name);
    }

    public void setControlJSON(String location) {
        this.controlJSONLocation = location;        
    }
    
    public void setWriteControlJSON(String location) {
        this.controlJSONWriteLocation = location;        
    }

    public void setWriteControlFormattedJSON(String location) {
        this.controlJSONWriteFormattedLocation= location;        
    }
    
    public String getWriteControlJSON(){
    	if(controlJSONWriteLocation != null){
    		return controlJSONWriteLocation;
    	}else{
    		return controlJSONLocation;
    	}
    }

    public String getWriteControlJSONFormatted(){
    	if(controlJSONWriteFormattedLocation != null){
    		return controlJSONWriteFormattedLocation;
    	}else{    	
    	    return getWriteControlJSON();
    	}
    }
    
    protected Marshaller getJSONMarshaller() throws Exception{
    	return jaxbMarshaller;
    }
    
   protected Unmarshaller getJSONUnmarshaller() throws Exception{
	   return jaxbUnmarshaller;
    }
    
   public void jsonToObjectTest(Object testObject) throws Exception {
   	   jsonToObjectTest(testObject, getJSONReadControlObject());
   }
   
    public void jsonToObjectTest(Object testObject, Object controlObject) throws Exception {
    	if(controlObject == null){
    		assertNull(testObject);
    		return;
    	}
    	
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(controlObject.toString());
        log("Actual:");
        log(testObject.toString());

        if ((controlObject instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)controlObject;
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj, false);
        } else {
        	assertEquals(controlObject, testObject);
        }
    }
    
    public void testJSONUnmarshalFromInputStream() throws Exception {
    	if(isUnmarshalTest()){
    		getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        Object testObject = null;
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(inputStream);
	        }
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }
    public MediaType getJSONUnmarshalMediaType(){
    	return MediaType.APPLICATION_JSON;
    }
    
    public void testUnmarshalAutoDetect() throws Exception {
    	if(isUnmarshalTest()){
    	   	getJSONUnmarshaller().setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
    	    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        Object testObject = null;
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(inputStream);
	        }
	        inputStream.close();
	        jsonToObjectTest(testObject);
	        
	        assertTrue(getJSONUnmarshaller().getProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE) == Boolean.TRUE);
	        if(null != XML_INPUT_FACTORY) {
	            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
	            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
	            
	            if(getUnmarshalClass() != null){                            
	                testObject = getJAXBUnmarshaller().unmarshal(xmlStreamReader, getUnmarshalClass());
	            }else{
	            	testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
	            }
	            instream.close();
	            xmlToObjectTest(testObject);
	        }
	        assertTrue(getJSONUnmarshaller().getProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE)== Boolean.TRUE);

    	}
    }
    	
    public void testJSONUnmarshalFromInputSource() throws Exception {
    	if(isUnmarshalTest()){
    		getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());

	
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        InputSource inputSource = new InputSource(inputStream);
	        Object testObject = null;
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(inputSource);
	        }
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }
    
    

    public void testJSONUnmarshalFromReader() throws Exception {
    	if(isUnmarshalTest()){
    		getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());

	
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        Reader reader = new InputStreamReader(inputStream);
	        Object testObject = null;
	        
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(reader), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(reader);
	        }
	        
	        reader.close();
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }

    public void testJSONUnmarshalFromSource() throws Exception {
        if(isUnmarshalTest()){
    		getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());

    
            Source source = new StreamSource(controlJSONLocation);
            Object testObject = null;
            if(getUnmarshalClass() != null){
                testObject = getJSONUnmarshaller().unmarshal(source, getUnmarshalClass());
            }else{
                testObject = getJSONUnmarshaller().unmarshal(source);
            }
            jsonToObjectTest(testObject);
        }
    }

    public void testJSONUnmarshalFromURL() throws Exception {
    	if(isUnmarshalTest()){
    		getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
	        URL url = getJSONURL();
	        Object testObject= null;
	        if(getUnmarshalClass() == null){
	        	testObject = getJSONUnmarshaller().unmarshal(url);
	        }else{
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(url.openStream()), getUnmarshalClass());
	        }
	        	
	        jsonToObjectTest(testObject);
    	}
    }
    

    public void testJSONMarshalToOutputStream() throws Exception{
    	getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getJSONMarshaller().marshal(getWriteControlObject(), os);
        compareStringToControlFile("testJSONMarshalToOutputStream", new String(os.toByteArray()));
        os.close();
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
    	getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    	getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getJSONMarshaller().marshal(getWriteControlObject(), os);
        compareStringToControlFile("testJSONMarshalToOutputStream_FORMATTED", new String(os.toByteArray()), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
        os.close();
    }

    public void testJSONMarshalToStringWriter() throws Exception{
    	getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        StringWriter sw = new StringWriter();
        getJSONMarshaller().marshal(getWriteControlObject(), sw);
        log(sw.toString());
        compareStringToControlFile("**testJSONMarshalToStringWriter**", sw.toString());
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
    	getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    	getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        getJSONMarshaller().marshal(getWriteControlObject(), sw);
        log(sw.toString());
        compareStringToControlFile("testJSONMarshalToStringWriter_FORMATTED", sw.toString(), getWriteControlJSONFormatted(),shouldRemoveWhitespaceFromControlDocJSON());
    }

    protected void compareStringToControlFile(String test, String testString) {
    	compareStringToControlFile(test, testString, getWriteControlJSON());
    }
    
    protected void compareStringToControlFile(String test, String testString, String controlFileLocation) {
    	compareStringToControlFile(test, testString, controlFileLocation, shouldRemoveEmptyTextNodesFromControlDoc());
    }
    
    protected void compareStringToControlFile(String test, String testString, String controlFileLocation, boolean removeWhitespace) {
        String expectedString = loadFileToString(controlFileLocation);
        compareStrings(testString, testString, expectedString, removeWhitespace);
    }
    
    protected void compareStrings(String test, String testString, String expectedString, boolean removeWhitespace) {
        log(test);
        if(removeWhitespace){
            log("Expected (With All Whitespace Removed):");
        }else{
        	log("Expected");
        }
        
        if(removeWhitespace){
        	expectedString = expectedString.replaceAll("[ \b\t\n\r]", "");
        }
        log(expectedString);
        if(removeWhitespace){
            log("\nActual (With All Whitespace Removed):");
        }else{
        	log("\nActual");
        }
        
        if(removeWhitespace){
            testString = testString.replaceAll("[ \b\t\n\r]", "");
        }
        log(testString);
        assertEquals(expectedString, testString);
    }

    protected Object getJSONReadControlObject(){
    	return getReadControlObject();
    }

    private URL getJSONURL() {
        return Thread.currentThread().getContextClassLoader().getResource(controlJSONLocation);
    }
        
    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    	return true;
    }
    
    public void generateJSONSchema(InputStream controlSchema) throws Exception {
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();
    	controlSchemas.add(controlSchema);
    	generateJSONSchema(controlSchemas);
    }
    
    
    public void generateJSONSchema(List<InputStream> controlSchemas) throws Exception {
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();

        Class theClass = getWriteControlObject().getClass();
        if(theClass == JAXBElement.class){
        	 theClass = ((JAXBElement) getWriteControlObject()).getValue().getClass();
        }
        
        ((JAXBContext)getJAXBContext()).generateJsonSchema(outputResolver, theClass);
    	        
        List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
        
        
        assertEquals("Wrong Number of Schemas Generated", controlSchemas.size(), generatedSchemas.size());
        
        for(int i=0; i<controlSchemas.size(); i++){
        	InputStream controlInputstream = controlSchemas.get(i);
        	Writer generated = generatedSchemas.get(i);
            log(generated.toString());            
            String controlString =  loadInputStreamToString(controlInputstream);            
            compareStrings("generateJSONSchema", generated.toString(), controlString, true);
        }
    }
}