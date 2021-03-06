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
import net.anshulverma.gradle.estilo.checkstyle.ImportControlCollection
import net.anshulverma.gradle.estilo.checkstyle.SuppressionCollection
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

  private static final Map<Class, String> HEADER_BY_TYPE = [
      (RootModule): '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC
    "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
    "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
''',
      (Suppressions): '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suppressions PUBLIC
    "-//Puppy Crawl//DTD Suppressions 1.1//EN"
    "http://www.puppycrawl.com/dtds/suppressions_1_1.dtd">
''',
      (ImportControlDTO): '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE import-control PUBLIC "-//Puppy Crawl//DTD Import Control 1.0//EN"
  "http://www.puppycrawl.com/dtds/import_control_1_0.dtd">
'''
  ]
  private static final Unmarshaller UNMARSHALLER =
      JAXBContext.newInstance(RootModule).createUnmarshaller()
  private static final String JAXB_XML_HEADERS = 'com.sun.xml.bind.xmlHeaders'
  private static final UNWANTED_HEADER_LENGTH =
      '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'.length() + 1

  static final ConfigMarshaller INSTANCE = new ConfigMarshaller()

  private ConfigMarshaller() { }

  def marshal(CheckstyleConfig checkstyleConfig) {
    marshalInternal(checkstyleConfig.toRootModule())
  }

  def marshal(SuppressionCollection suppressionCollection) {
    String marshalled = marshalInternal(suppressionCollection.toSuppressions())

    // seems like there is no way to unescape &amp; easily
    unescapeAmpersand(marshalled)
  }

  def marshal(ImportControlCollection importControlCollection) {
    marshalInternal(importControlCollection.toImportControlDTO())
  }

  private marshalInternal(Object obj) {
    def writer = new StringWriter()
    def marshaller = setupMarshaller(obj)
    marshaller.marshal(obj, writer)
    removeUnwantedHeader(writer.toString())
  }

  private String unescapeAmpersand(String escaped) {
    escaped.replaceAll('\\(\\?\\&amp;lt;', '(?&lt;')
  }

  private String removeUnwantedHeader(String marshalled) {
    marshalled[UNWANTED_HEADER_LENGTH..-1]
  }

  CheckstyleConfig unmarshal(File file) {
    unmarshal(new FileInputStream(file))
  }

  CheckstyleConfig unmarshal(InputStream inputStream) {
    CheckstyleConfig.buildFrom(UNMARSHALLER.unmarshal(inputStream))
  }

  private Marshaller setupMarshaller(obj) {
    def marshaller = JAXBContext.newInstance(obj.class).createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
    if (HEADER_BY_TYPE.containsKey(obj.class)) {
      marshaller.setProperty(JAXB_XML_HEADERS, HEADER_BY_TYPE[obj.class])
    } else {
      throw new IllegalArgumentException("no header available for ${obj.class}. " +
                                             "Acceptable values: ${HEADER_BY_TYPE.keySet()}")
    }
    marshaller
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @XmlRootElement(name = 'module')
  static class RootModule extends Module { }

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

  @XmlAccessorType(XmlAccessType.NONE)
  @XmlRootElement(name = 'suppressions')
  @Builder
  static class Suppressions {

    @XmlElement(name = 'suppress')
    List<SuppressedCheck> suppressedChecks
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Builder
  static class SuppressedCheck {

    @XmlAttribute
    String id

    @XmlAttribute
    String checks

    @XmlAttribute
    String files

    @XmlAttribute
    String lines

    @XmlAttribute
    String columns
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @XmlRootElement(name = 'import-control')
  static class ImportControlDTO extends SubPackage {

    @XmlAttribute
    String pkg

  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Builder
  static class SubPackage {

    @XmlAttribute
    String name

    @XmlElement(name = 'allow')
    List<BaseImportControl> allowedImports

    @XmlElement(name = 'disallow')
    List<BaseImportControl> disallowedImports

    @XmlElement(name = 'subpackage')
    List<SubPackage> subPackages

  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Builder
  static class BaseImportControl {

    @XmlAttribute(name = 'class')
    String clazz

    @XmlAttribute
    String pkg

  }
}
