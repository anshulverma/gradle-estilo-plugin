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

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import net.anshulverma.gradle.estilo.checkstyle.CheckstyleContext
import net.anshulverma.gradle.estilo.checkstyle.CustomOptions
import net.anshulverma.gradle.estilo.checkstyle.DefaultCheckstyleContext

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TupleConstructor
class CheckstyleConfig {

  private final CheckstyleContext checkstyleContext = new DefaultCheckstyleContext()

  CheckstyleModule checkstyleModule

  static CheckstyleConfig buildFrom(ConfigMarshaller.RootModule rootModule) {
    def checkstyleModule = CheckstyleConfigConverter.convert(rootModule)
    new CheckstyleConfig(checkstyleModule)
  }

  ConfigMarshaller.RootModule toRootModule() {
    CheckstyleConfigConverter.convert(checkstyleModule)
  }

  def extend(String name, CustomOptions options, Map<String, String> properties) {
    def parentModule = getParentModule(name)
    def id = properties['id'] ? properties['id'] : name
    if (options.remove) {
      return parentModule.remove(id)
    }

    CheckstyleModule subModule = parentModule.getOrCreate(id, name)
    if (options.override) {
      subModule.propertyMap = [:]
    }
    properties.each { key, value ->
      subModule.propertyMap[key] = CheckstyleProperty.from(key, "$value")
    }
    subModule.name = name
    subModule
  }

  private getParentModule(String name) {
    if (checkstyleContext.isRootCheck(name)) {
      checkstyleModule
    } else if (checkstyleContext.isTreeWalkerCheck(name)) {
      checkstyleModule.getOrCreate('TreeWalker')
    } else {
      throw new IllegalArgumentException("no parent module exists for $name type check")
    }
  }

  @Builder
  static class CheckstyleModule {

    String name

    Map<String, CheckstyleProperty> propertyMap = [:]

    Map<String, List<CheckstyleModule>> moduleMap = [:]

    def getId() {
      if (propertyMap.containsKey('id')) {
        propertyMap.'id'
      }
      name
    }

    CheckstyleModule getOrCreate(String id, String name = id) {
      if (!moduleMap.containsKey(id)) {
        moduleMap[id] = []
      }
      if (moduleMap[id].isEmpty()) {
        def module = new CheckstyleModule()
        module.name = name
        moduleMap[id] << module
        return module
      }
      moduleMap[id].first()
    }

    List<CheckstyleModule> remove(String id) {
      moduleMap.remove(id)
    }
  }

  @Builder
  @TupleConstructor
  static class CheckstyleProperty {

    String name

    String value

    static from(String name, String value) {
      new CheckstyleProperty(name, value)
    }
  }
}
