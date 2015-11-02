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

import net.anshulverma.gradle.estilo.checkstyle.checks.CheckType
import net.anshulverma.gradle.estilo.test.AbstractSpecification
import org.apache.commons.io.IOUtils
import org.codehaus.plexus.util.FileUtils
import spock.lang.Unroll
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class EstiloTaskTest extends AbstractSpecification {

  @Unroll
  def 'test loading and extending #checkType checks'() {
    given:
      def project = singleJavaProject()
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def extension = new EstiloExtension()
      def closure = {
        source checkType.name()

        checks {
          DescendantToken {
            id 'switchNoDefault'
            tokens 'LITERAL_SWITCH'
            limitedTokens 'LITERAL_DEFAULT'
            maximumNumber 2
            maximumDepth 1
          }
        }
      }
      closure.delegate = extension

    when:
      closure()
      EstiloTask estiloTask = project.getTasksByName('estilo', true)[0]
      estiloTask.execute(extension)

    then:
      def expectedCheckstylePath = "${project.rootDir}/build/estilo/checkstyle.xml"
      FileUtils.fileExists(expectedCheckstylePath)
      fileHash(expectedCheckstylePath as File) == hash

    where:
      checkType        | hash
      CheckType.GOOGLE | [-62, 6, -40, 36, -31, -5, -56, 11, 119, 5, 18, -89, -84, 39, -71, 75]
      CheckType.SUN    | [124, -99, 69, -22, 64, 85, 118, 17, -83, -38, -81, -93, -28, 121, 99, -34]
  }

  def fileHash(File file) {
    MessageDigest messageDigest = MessageDigest.getInstance('MD5')
    DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), messageDigest)
    IOUtils.toByteArray(inputStream)
    messageDigest.digest()
  }
}
