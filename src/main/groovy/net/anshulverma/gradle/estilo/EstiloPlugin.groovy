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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

/**
 * Registers the plugin's tasks.
 *
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class EstiloPlugin implements Plugin<Project> {

  @Override
  void apply(def Project project) {
    if (project.plugins.hasPlugin('java')) {
      def buildDir = "${project.rootDir}/build/estilo"

      project.extensions.create('estilo', EstiloExtension)
      project.tasks.create('estilo', EstiloTask) {
        checkstyleConfigDir buildDir
      }

      setupCheckstyle(project, buildDir)
    }
  }

  private void setupCheckstyle(Project project, buildDir) {
    project.apply plugin: 'checkstyle'

    project.tasks.withType(Checkstyle) { task ->
      dependsOn 'estilo'
      enabled = project.hasProperty('checkstyle') && 'disable' != (project.property('checkstyle'))
      group = 'Checkstyle'

      doLast {
        def input = task.name == 'checkstyleMain' ? 'main' : 'test'
        def inputPath = "${buildDir}/${input}.xml"
        ant.xslt(in: inputPath,
                 style: "//${buildDir}/checkstyle.xsl",
                 out: "${buildDir}/report-${input}.html")
      }
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
