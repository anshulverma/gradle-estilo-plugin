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
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class EstiloExtensionTest extends AbstractSpecification {

  @Unroll('convert #checkType check type')
  def 'Choose a valid base checks type'() {
    given:
      def extension = new EstiloExtension()
      def closure = {
        source name
      }
      closure.delegate = extension

    when:
      closure()

    then:
      extension.baseChecks == checkType

    where:
      checkType        | name
      CheckType.GOOGLE | 'google'
      CheckType.SUN    | 'sun'
  }

  def 'Allow adding RegexpHeader check'() {
    given:
      def extension = new EstiloExtension()
      def rootDir = '/tmp'
      def closure = {
        RegexpHeader {
          headerFile "$rootDir/config/checkstyle/java.header.txt"
          multiLines([23, 24, 25])
        }
      }
      closure.delegate = extension

    when:
      closure()

    then:
      extension.checks.RegexpHeader.properties.headerFile == '/tmp/config/checkstyle/java.header.txt'
      extension.checks.RegexpHeader.properties.multiLines == '23,24,25'
  }
}
