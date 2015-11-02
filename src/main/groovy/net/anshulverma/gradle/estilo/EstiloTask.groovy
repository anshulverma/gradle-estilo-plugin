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

import net.anshulverma.gradle.estilo.checkstyle.PropertyCollection
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
class EstiloTask extends DefaultTask {

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
    createCheckstyleConfig(config)
    createCheckstyleXSL()
  }

  private extendChecks(CheckstyleConfig config, PropertyCollection checks) {
    checks.each {
      def properties = it.properties
      def name = properties.remove('name')
      config.extend(name, properties)
    }
  }

  private createCheckstyleConfig(CheckstyleConfig config) {
    def source = ConfigMarshaller.INSTANCE.marshal(config)
    def destination = "$checkstyleConfigDir/checkstyle.xml"
    FileCopyUtil.copyStringToFile(source, destination)
  }

  private createCheckstyleXSL() {
    FileUtils.copyURLToFile(getClass().getResource('/config/checkstyle.xsl'),
                            "$checkstyleConfigDir/checkstyle.xsl" as File)
  }
}
