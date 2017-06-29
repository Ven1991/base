package com.webapp.framework.utils.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

public class JaxbMapper
{
  private JAXBContext jaxbContext;

  public JaxbMapper(Class<?>[] rootTypes)
    throws JAXBException
  {
    this.jaxbContext = JAXBContext.newInstance(rootTypes);
  }

  public String toXml(Object root)
  {
    return toXml(root, null);
  }

  public String toXml(Object root, String encoding)
  {
    try
    {
      StringWriter writer = new StringWriter();
      createMarshaller(encoding).marshal(root, writer);
      return writer.toString(); } catch (JAXBException e) {
    }
    return null;
  }

  public String toXml(Collection<?> root, String rootName)
  {
    return toXml(root, rootName, null);
  }

  public String toXml(Collection<?> root, String rootName, String encoding)
  {
    try
    {
      CollectionWrapper wrapper = new CollectionWrapper();
      wrapper.collection = root;

      JAXBElement wrapperElement = new JAXBElement(new QName(rootName), CollectionWrapper.class, wrapper);

      StringWriter writer = new StringWriter();
      createMarshaller(encoding).marshal(wrapperElement, writer);

      return writer.toString(); } catch (JAXBException e) {
    }
    return null;
  }

  public <T> T fromXml(String xml)
  {
    try
    {
      StringReader reader = new StringReader(xml);
      return (T) createUnmarshaller().unmarshal(reader); } catch (JAXBException e) {
    }
    return null;
  }

  public <T> T unmarshal(File file, Class clas)
  {
    return (T) JAXB.unmarshal(file, clas);
  }

  public Marshaller createMarshaller(String encoding)
  {
    try
    {
      Marshaller marshaller = this.jaxbContext.createMarshaller();

      marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

      if ((null != encoding) && (!encoding.trim().equals(""))) {
        marshaller.setProperty("jaxb.encoding", encoding);
      }

      return marshaller; } catch (JAXBException e) {
    }
    return null;
  }

  public void marshal(Object root, File file, String encoding)
  {
    try
    {
      FileOutputStream os = new FileOutputStream(file);
      Marshaller mu = createMarshaller(encoding);
      mu.marshal(root, os);
    }
    catch (Exception e)
    {
    }
  }

  public void marshalToFile(Object root, File file)
  {
    JAXB.marshal(root, file);
  }

  public Unmarshaller createUnmarshaller()
  {
    try
    {
      return this.jaxbContext.createUnmarshaller(); } catch (JAXBException e) {
    }
    return null;
  }

  public static class CollectionWrapper
  {

    @XmlAnyElement
    protected Collection<?> collection;
  }
}