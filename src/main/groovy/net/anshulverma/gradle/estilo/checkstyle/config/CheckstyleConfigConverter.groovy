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

import static net.anshulverma.gradle.estilo.checkstyle.config.CheckstyleConfig.CheckstyleModule
import static net.anshulverma.gradle.estilo.checkstyle.config.CheckstyleConfig.CheckstyleProperty
import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.Module
import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.Property
import static net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.RootModule

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class CheckstyleConfigConverter {

  private CheckstyleConfigConverter() { }

  static CheckstyleModule convert(Module module) {
    CheckstyleModule.builder()
                    .name(module.name)
                    .propertyMap(buildPropertiesMap(module.properties))
                    .moduleMap(buildModuleMap(module.modules))
                    .build()
  }

  private static buildPropertiesMap(properties) {
    if (!properties) {
      return [:]
    }
    properties.collect { property ->
      CheckstyleProperty.builder()
                        .name(property.name)
                        .value(property.value)
                        .build()
    }.collectEntries { property ->
      [property.name, property]
    }
  }

  private static buildModuleMap(modules) {
    if (!modules) {
      return [:]
    }
    modules.inject([:]) { result, module ->
      CheckstyleModule checkstyleModule = convert(module)
      def moduleList = result.get(checkstyleModule.id)
      if (!moduleList) {
        moduleList = []
        result.put(checkstyleModule.id, moduleList)
      }
      moduleList << checkstyleModule
      result
    }
  }

  static RootModule convert(CheckstyleModule checkstyleModule) {
    def module = buildModule(checkstyleModule)
    new RootModule(name: module.name,
                   properties: module.properties,
                   modules: module.modules)
  }

  private static buildModule(CheckstyleModule checkstyleModule) {
    Module.builder()
          .name(checkstyleModule.name)
          .modules(checkstyleModule.moduleMap.values().flatten().collect { buildModule(it) })
          .properties(checkstyleModule.propertyMap.values().collect { buildProperty(it) })
          .build()
  }

  private static buildProperty(CheckstyleProperty checkstyleProperty) {
    Property.builder()
            .name(checkstyleProperty.name)
            .value(checkstyleProperty.value)
            .build()
  }
}
