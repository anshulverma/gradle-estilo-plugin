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

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.estilo.EstiloExtension
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class CheckstylePluginHelper {

  static setupCheckstyle(Project project, String buildDir) {
    project.apply plugin: 'checkstyle'

    project.tasks.withType(Checkstyle) { task ->
      dependsOn 'estilo'
      enabled = project.hasProperty('checkstyle') && 'disable' != (project.property('checkstyle'))
      group = 'Checkstyle'

      def input = task.name == 'checkstyleMain' ? 'main' : 'test'
      def inputPath = "${buildDir}/${input}.xml"
      def outputPath = "${buildDir}/report-${input}.html"

      doLast {
        ant.xslt(in: inputPath,
                 style: "//${buildDir}/checkstyle.xsl",
                 out: outputPath)
      }

      def finalizeTask = project.tasks.create("estiloFinalize${input[0].toUpperCase()}${input[1..-1]}") {
        doLast {
          def settings = (EstiloExtension) project.getExtensions().findByName('estilo')
          def warningsFile = inputPath as File
          if (!(warningsFile.exists() && warningsFile.text.contains('<error '))) {
            return
          }
          if (settings.ignoreCheckstyleWarnings) {
            log.warn("WARNING: There were checkstyle errors for '$input' source set. " +
                         'Build will continue since you have marked these errors to be ignored. ' +
                         "For a detailed report, check: file://$outputPath")
          } else {
            throw new IllegalStateException("Code style check for source set '$input' failed. " +
                                                "For detailed report, check: file://$outputPath")
          }
        }
      }

      task.finalizedBy finalizeTask
    }

    project.checkstyle {
      showViolations = true
      ignoreFailures = false
      sourceSets = [sourceSets.main, sourceSets.test]
      toolVersion = '6.7'
      configFile = "$buildDir/checkstyle.xml" as File
      reportsDir = buildDir as File
    }
  }
}
