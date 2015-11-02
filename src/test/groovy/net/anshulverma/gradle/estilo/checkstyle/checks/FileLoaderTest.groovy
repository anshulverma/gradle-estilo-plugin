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

import net.anshulverma.gradle.estilo.test.AbstractSpecification
import org.apache.commons.io.IOUtils
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
      def loader = new ConfigFilesLoader(checkType, "$project.rootDir/build/estilo")

    when:
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def loaded = loader.load()

    then:
      loaded.path == "$project.rootDir/build/estilo/checkstyle.xml"
      fileHash(loaded) == hash

    where:
      checkType        | hash
      CheckType.GOOGLE | [-19, -71, -37, 74, -78, -55, -7, 70, -95, -31, -6, -60, -78, -98, 97, 35]
      CheckType.SUN    | [-63, -29, -29, 26, 47, -103, 47, -66, -69, -101, -32, 25, 75, 28, -79, -76]
  }

  def fileHash(File file) {
    MessageDigest messageDigest = MessageDigest.getInstance('MD5')
    DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), messageDigest)
    IOUtils.toByteArray(inputStream)
    messageDigest.digest()
  }
}
