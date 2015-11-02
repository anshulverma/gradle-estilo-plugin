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
package net.anshulverma.gradle.estilo.checkstyle.checks

import net.anshulverma.gradle.estilo.EstiloExtension
import net.anshulverma.gradle.estilo.EstiloTask
import net.anshulverma.gradle.estilo.test.AbstractSpecification
import org.apache.commons.io.IOUtils
import org.codehaus.plexus.util.FileUtils
import spock.lang.Unroll
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class FileLoaderTest extends AbstractSpecification {

  @Unroll('load #checkType checks')
  def 'Load checks file'() {
    given:
      def project = singleJavaProject()
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def settings = new EstiloExtension()
      settings.baseChecks = checkType

    when:
      EstiloTask estiloTask = project.getTasksByName('estilo', true)[0]
      estiloTask.execute(settings)

    then:
      def expectedCheckstylePath = "${project.rootDir}/build/estilo/checkstyle.xml"
      FileUtils.fileExists(expectedCheckstylePath)
      fileHash(expectedCheckstylePath as File) == hash

    where:
      checkType        | hash
      CheckType.GOOGLE | [-107, -45, 71, 124, 50, 103, -47, 32, -74, 58, -63, -23, 56, 8, -56, 122]
      CheckType.SUN    | [118, -45, -18, 87, 12, 27, -77, 123, -122, -14, -128, -107, 121, -53, 119, -29]
  }

  def fileHash(File file) {
    MessageDigest messageDigest = MessageDigest.getInstance('MD5')
    DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), messageDigest)
    IOUtils.toByteArray(inputStream)
    messageDigest.digest()
  }
}
