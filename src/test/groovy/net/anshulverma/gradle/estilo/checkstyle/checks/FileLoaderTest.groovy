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
      CheckType.GOOGLE | [44, -55, 53, -59, -34, -78, 95, -46, -92, 29, 109, -104, 126, -12, 120, -16]
      CheckType.SUN    | [123, -62, -28, 57, 21, -38, -118, -78, -54, -54, -44, 67, -21, 63, -66, -79]
  }

  def fileHash(File file) {
    MessageDigest messageDigest = MessageDigest.getInstance('MD5')
    DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), messageDigest)
    IOUtils.toByteArray(inputStream)
    messageDigest.digest()
  }
}
