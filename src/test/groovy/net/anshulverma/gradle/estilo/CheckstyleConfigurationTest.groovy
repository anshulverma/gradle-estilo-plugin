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
package net.anshulverma.gradle.estilo

import net.anshulverma.gradle.estilo.test.AbstractSpecification
import org.gradle.api.plugins.quality.CheckstyleExtension

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class CheckstyleConfigurationTest extends AbstractSpecification {

  def 'Checkstyle plugin default configuration test'() {
    given:
      def project = singleJavaProject()
      def buildDir = "${project.rootDir}/build/estilo" as String

    when:
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def extension = project.extensions.findByName('checkstyle') as CheckstyleExtension

    then:
      extension.showViolations
      !extension.ignoreFailures
      extension.sourceSets.size() == 2
      extension.toolVersion == '6.7'
      extension.configFile instanceof File
      extension.configFile.path == "$buildDir/checkstyle.xml"
      extension.configProperties == ['header.file': null, 'suppressions.file': null]
  }
}
