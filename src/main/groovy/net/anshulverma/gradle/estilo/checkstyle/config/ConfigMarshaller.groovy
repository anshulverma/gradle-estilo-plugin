/**
 * Copyright 2015 Anshul Verma. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.anshulverma.gradle.estilo.checkstyle.config

import groovy.transform.builder.Builder
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ConfigMarshaller {

  private static final Marshaller MARSHALLER =
      JAXBContext.newInstance(RootModule).createMarshaller()
  private static final Unmarshaller UNMARSHALLER =
      JAXBContext.newInstance(RootModule).createUnmarshaller()
  private static final String JAXB_XML_HEADERS = 'com.sun.xml.bind.xmlHeaders'
  private static final UNWANTED_HEADER_LENGTH =
      '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'.length() + 1

  static final ConfigMarshaller INSTANCE = new ConfigMarshaller()

  static {
    MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
    MARSHALLER.setProperty(JAXB_XML_HEADERS,
                           '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC
    "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
    "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
''')
  }

  private ConfigMarshaller() { }

  def marshal(CheckstyleConfig checkstyleConfig) {
    def writer = new StringWriter()
    def rootModule = checkstyleConfig.toRootModule()
    MARSHALLER.marshal(rootModule, writer)
    return writer.toString()[UNWANTED_HEADER_LENGTH..-1]
  }

  CheckstyleConfig unmarshal(File file) {
    unmarshal(new FileInputStream(file))
  }

  CheckstyleConfig unmarshal(InputStream inputStream) {
    CheckstyleConfig.buildFrom(UNMARSHALLER.unmarshal(inputStream))
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @XmlRootElement(name = 'module')
  static class RootModule extends Module {

  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Builder
  static class Module {

    @XmlAttribute
    String name

    @XmlElement(name = 'property')
    List<Property> properties

    @XmlElement(name = 'module')
    List<Module> modules

  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Builder
  static class Property {

    @XmlAttribute
    String value

    @XmlAttribute
    String name
  }
}
