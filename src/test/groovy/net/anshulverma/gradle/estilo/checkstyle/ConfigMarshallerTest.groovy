/**
 * Copyright 2015 Anshul Verma. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.anshulverma.gradle.estilo.checkstyle

import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.Module
import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.Property
import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.RootModule
import net.anshulverma.gradle.estilo.checkstyle.config.CheckstyleConfig
import net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller
import net.anshulverma.gradle.estilo.test.AbstractSpecification

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ConfigMarshallerTest extends AbstractSpecification {

  def 'Parse checkstyle file'() {
    given:
      def file = this.class.getResource('/fixtures/checkstyle_test.xml').path as File

    when:
      CheckstyleConfig checkstyleConfig = ConfigMarshaller.INSTANCE.unmarshal(file)

    then:
      checkstyleConfig.checkstyleModule.name == 'Checker'
      checkstyleConfig.checkstyleModule.propertyMap.size() == 2
      checkstyleConfig.checkstyleModule.moduleMap.size() == 1
      checkstyleConfig.checkstyleModule.moduleMap.entrySet().first().key == 'SuppressionFilter'
      checkstyleConfig.checkstyleModule.moduleMap.entrySet().first().value.size() == 1
      checkstyleConfig.checkstyleModule.moduleMap.entrySet().first().value.propertyMap.size() == 1
  }

  def 'Convert module to to checkstyle config then into xml string'() {
    given:
      Module module = Module.builder()
                            .name('root')
                            .properties(
          [
              Property.builder()
                      .name('prop1')
                      .value('val1')
                      .build(),
              Property.builder()
                      .name('prop2')
                      .value('val2')
                      .build()
          ])
                            .modules(
          [
              Module.builder()
                    .name('child1')
                    .build(),
              Module.builder()
                    .name('child1')
                    .properties(
                  [
                      Property.builder()
                              .name('prop3')
                              .value('val3')
                              .build(),
                      Property.builder()
                              .name('prop4')
                              .value('val4')
                              .build()
                  ])
                    .build()
          ])
                            .build()
      def rootModule = new RootModule()
      rootModule.name = module.name
      rootModule.properties = module.properties
      rootModule.modules = module.modules

    when:
      CheckstyleConfig checkstyleConfig = CheckstyleConfig.buildFrom(rootModule)
      def marshalled = ConfigMarshaller.INSTANCE.marshal(checkstyleConfig)

    then:
      marshalled == '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<module name="root">
    <property value="val1" name="prop1"/>
    <property value="val2" name="prop2"/>
    <module name="child1"/>
    <module name="child1">
        <property value="val3" name="prop3"/>
        <property value="val4" name="prop4"/>
    </module>
</module>
'''
  }
}
