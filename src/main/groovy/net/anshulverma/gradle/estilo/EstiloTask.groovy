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
package net.anshulverma.gradle.estilo

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.estilo.checkstyle.CustomOptions
import net.anshulverma.gradle.estilo.checkstyle.ImportControlCollection
import net.anshulverma.gradle.estilo.checkstyle.PropertyCollection
import net.anshulverma.gradle.estilo.checkstyle.SuppressionCollection
import net.anshulverma.gradle.estilo.checkstyle.checks.ConfigFileLoader
import net.anshulverma.gradle.estilo.checkstyle.config.CheckstyleConfig
import net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller
import net.anshulverma.gradle.estilo.util.FileCopyUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class EstiloTask extends DefaultTask {

  private static final Object OVERRIDE_OPTIONS = CustomOptions.fromHash([override: true])

  String checkstyleConfigDir

  EstiloTask() {
    description = 'Manages checkstyle checkers for this project.'
    group = 'Checkstyle'
  }

  @TaskAction
  def run() {
    def settings = (EstiloExtension) project.getExtensions().findByName('estilo')
    execute(settings)
  }

  def execute(EstiloExtension settings) {
    CheckstyleConfig config = new ConfigFileLoader(settings.baseChecks).load()
    extendChecks(config, settings.checkCollection)
    addImplicitExtensions(settings, config)
    createCheckstyleConfig(config)
    createCheckstyleXSL()
  }

  private addImplicitExtensions(EstiloExtension settings, CheckstyleConfig config) {
    if (settings.hasSuppressions()) {
      createSupressionsConfig(settings.suppressionCollection)
      config.extend('SuppressionFilter',
                    OVERRIDE_OPTIONS,
                    [file: "$checkstyleConfigDir/suppressions.xml"])
    }

    if (settings.hasImportControl()) {
      createImportControlConfig(settings.importControlCollection)
      config.extend('ImportControl',
                    OVERRIDE_OPTIONS,
                    [file: "$checkstyleConfigDir/import-control.xml"])
    }

    if (settings.hasHeader()) {
      writeToFile(settings.headerCheckOptions.template, "$checkstyleConfigDir/header.txt")
      def headerProperties = [ headerFile: "$checkstyleConfigDir/header.txt" ]
      if (settings.headerCheckOptions.containsKey('multiLines')) {
        headerProperties.multiLines = settings.headerCheckOptions.multiLines.join(',')
      }
      config.extend('RegexpHeader',
                    OVERRIDE_OPTIONS,
                    headerProperties)
    }
  }

  private extendChecks(CheckstyleConfig config, PropertyCollection checks) {
    checks.each {
      def properties = it.properties
      def options = it.customOptions
      def name = properties.remove('name')
      config.extend(name, options, properties)
    }
  }

  private createCheckstyleConfig(CheckstyleConfig config) {
    createConfigFile(config, "$checkstyleConfigDir/checkstyle.xml")
  }

  private createSupressionsConfig(SuppressionCollection suppressionCollection) {
    createConfigFile(suppressionCollection, "$checkstyleConfigDir/suppressions.xml")
  }

  private createImportControlConfig(ImportControlCollection importControlCollection) {
    createConfigFile(importControlCollection, "$checkstyleConfigDir/import-control.xml")
  }

  private createConfigFile(Object config, String destination) {
    def source = ConfigMarshaller.INSTANCE.marshal(config)
    writeToFile(source, destination)
  }

  private writeToFile(String source, String destination) {
    FileCopyUtil.copyStringToFile(source, destination)
    log.debug("wrote contents to file $destination")
  }

  private createCheckstyleXSL() {
    FileUtils.copyURLToFile(getClass().getResource('/config/checkstyle.xsl'),
                            "$checkstyleConfigDir/checkstyle.xsl" as File)
    log.debug('checkstyle.xsl created')
  }
}
