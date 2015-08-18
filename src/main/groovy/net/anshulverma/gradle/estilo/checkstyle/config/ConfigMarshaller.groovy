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

import net.anshulverma.gradle.estilo.checkstyle.config.CheckstyleConfig.RootModule
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ConfigMarshaller {

  private static final Marshaller MARSHALLER =
      JAXBContext.newInstance(RootModule).createMarshaller()
  private static final Unmarshaller UNMARSHALLER =
      JAXBContext.newInstance(RootModule).createUnmarshaller()

  static final ConfigMarshaller INSTANCE = new ConfigMarshaller()

  static {
    MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
  }

  private ConfigMarshaller() { }

  def marshal(RootModule module) {
    def writer = new StringWriter()
    MARSHALLER.marshal(module, writer)
    return writer.toString()
  }

  def unmarshal(File file) {
    unmarshal(new FileInputStream(file))
  }

  def unmarshal(InputStream inputStream) {
    new CheckstyleConfig((RootModule) UNMARSHALLER.unmarshal(inputStream))
  }
}
