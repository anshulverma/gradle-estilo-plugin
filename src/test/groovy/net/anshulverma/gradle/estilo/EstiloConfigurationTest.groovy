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
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class EstiloConfigurationTest extends AbstractSpecification {

  def 'Add task to project test'() {
    given:
      def project = ProjectBuilder.builder().build()
      def task = project.task('estilo', type: EstiloTask)

    expect:
      task instanceof EstiloTask
  }

  def 'Plugin adds estilo task to project'() {
    given:
      def project = singleJavaProject()

    when:
      project.apply plugin: 'net.anshulverma.gradle.estilo'

    then:
      project.tasks.estilo instanceof EstiloTask
  }

  def 'Checkstyle plugin is not added to non-java projects'() {
    given:
      def project = singleProject()

    when:
      project.apply plugin: 'net.anshulverma.gradle.estilo'

    then:
      project.plugins.withType(CheckstylePlugin).size() == 0
  }

  def 'Checkstyle plugin is added to java projects'() {
    given:
      def project = singleJavaProject()

    when:
      project.apply plugin: 'net.anshulverma.gradle.estilo'

    then:
      project.plugins.withType(CheckstylePlugin).size() == 1
  }
}
