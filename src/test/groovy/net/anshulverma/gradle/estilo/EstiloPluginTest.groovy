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

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class EstiloPluginTest extends Specification {

  def 'Add task to project test'() {
    given:
    def project = ProjectBuilder.builder().build()
    def task = project.task('estilo', type: EstiloTask)

    expect:
    task instanceof EstiloTask
  }

  def 'Plugin adds estilo task to project'() {
    given:
    def project = ProjectBuilder.builder().build()

    when:
    project.apply plugin: 'net.anshulverma.gradle.estilo'

    then:
    project.tasks.estilo instanceof EstiloTask
  }

  def singleProject() {
    new ProjectBuilder().withName('single').build()
  }
}
