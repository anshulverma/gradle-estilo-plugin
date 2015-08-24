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
import groovy.transform.TypeChecked
import groovy.transform.builder.Builder

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@TupleConstructor
class CheckstyleConfig {

  CheckstyleModule checkstyleModule

  static CheckstyleConfig buildFrom(ConfigMarshaller.RootModule rootModule) {
    def checkstyleModule = CheckstyleConfigConverter.convert(rootModule)
    new CheckstyleConfig(checkstyleModule)
  }

  ConfigMarshaller.RootModule toRootModule() {
    CheckstyleConfigConverter.convert(checkstyleModule)
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

  }

  @Builder
  static class CheckstyleProperty {

    String name

    String value

  }
}
