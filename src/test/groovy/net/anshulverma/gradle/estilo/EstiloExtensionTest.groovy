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
        checks {
          RegexpHeader {
            headerFile "$rootDir/config/checkstyle/java.header.txt"
            multiLines([23, 24, 25])
          }
        }
      }
      closure.delegate = extension

    when:
      closure()

    then:
      extension.checkCollection.length == 1

      def check = extension.checkCollection.checks.pop()
      check.name == 'RegexpHeader'
      check.headerFile == '/tmp/config/checkstyle/java.header.txt'
      check.multiLines == '23,24,25'
  }

  def 'Allow adding multiple check of same name'() {
    given:
      def extension = new EstiloExtension()
      def closure = {
        checks {
          DescendantToken {
            id 'stringEqual'
            tokens 'EQUAL,NOT_EQUAL'
            limitedTokens 'STRING_LITERAL'
            maximumNumber 0
            maximumDepth 1
          }
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

    then:
      extension.checkCollection.length == 2

      def check2 = extension.checkCollection.checks.pop()
      def check1 = extension.checkCollection.checks.pop()

      check1.name == 'DescendantToken'
      check1.id == 'stringEqual'
      check1.tokens == 'EQUAL,NOT_EQUAL'
      check1.limitedTokens == 'STRING_LITERAL'
      check1.maximumNumber == '0'
      check1.maximumDepth == '1'

      check2.name == 'DescendantToken'
      check2.id == 'switchNoDefault'
      check2.tokens == 'LITERAL_SWITCH'
      check2.limitedTokens == 'LITERAL_DEFAULT'
      check2.maximumNumber == '2'
      check2.maximumDepth == '1'
  }
}
