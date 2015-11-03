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
package net.anshulverma.gradle.estilo.checkstyle

import groovy.transform.builder.Builder
import groovy.util.logging.Slf4j
import net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller
import net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller.SubPackage

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class ImportControlCollection {

  final String basePackage
  final List<ImportControl> importControlList = []
  final List<ImportControlCollection> subPackages = []

  ImportControlCollection(String basePackage) {
    this.basePackage = basePackage
  }

  ConfigMarshaller.ImportControlDTO toImportControlDTO() {
    ConfigMarshaller.ImportControlDTO importControlDTO = new ConfigMarshaller.ImportControlDTO()
    importControlDTO.pkg = basePackage
    toSubpackageDTO(importControlDTO)
  }

  SubPackage toSubpackageDTO(SubPackage subPackageDTO = SubPackage.builder().name(basePackage).build()) {
    subPackageDTO.allowedImports = []
    subPackageDTO.disallowedImports = []
    subPackageDTO.subPackages = []

    // subdivide import control into allowed and disallowed imports
    importControlList.each {
      if (it.type == 'allow') {
        subPackageDTO.allowedImports.push(it.toBaseImportControl())
      } else {
        subPackageDTO.disallowedImports.push(it.toBaseImportControl())
      }
    }

    // do recursive transformation
    subPackages.each { subPackageDTO.subPackages.push(it.toSubpackageDTO()) }

    subPackageDTO
  }

  def getLength() {
    importControlList.size() + subPackages.size()
  }

  protected subpackage(String files, Closure closure) {
    def subPackage = new ImportControlCollection(files)
    closure.@owner = subPackage
    closure()
    subPackages.push(subPackage)
  }

  def allow(Map importControlOptions) {
    handle('allow', importControlOptions)
  }

  def disallow(Map importControlOptions) {
    handle('disallow', importControlOptions)
  }

  protected handle(String controlType, Map importControlOptions) {
    importControlOptions.each { key, value ->
      def importControl = buildImportControl(controlType, key, value)
      importControlList.push(importControl)
    }
  }

  private buildImportControl(controlType, scope, value) {
    ImportControl.builder()
                 .type(controlType)
                 .scope(scope == 'clazz' ? 'class' : scope)
                 .value(value)
                 .build()
  }

  @Builder
  static class ImportControl {

    String type

    String scope

    String value

    ConfigMarshaller.BaseImportControl toBaseImportControl() {
      if (scope == 'class') {
        return ConfigMarshaller.BaseImportControl.builder().clazz(value).build()
      } else if (scope == 'pkg') {
        return ConfigMarshaller.BaseImportControl.builder().pkg(value).build()
      }
      throw new IllegalStateException("unknown scope $scope")
    }
  }
}
