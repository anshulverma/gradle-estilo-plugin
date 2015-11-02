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

      def check = extension.checkCollection.collection.pop()
      check.name == 'RegexpHeader'
      check.headerFile == '/tmp/config/checkstyle/java.header.txt'
      check.multiLines == '23,24,25'
  }

  def 'Allow adding multiple check of same name'() {
    given:
      def extension = new EstiloExtension()
      def closure = {
        checks {
          DescendantToken(override: true, remove: true) {
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

      def check2 = extension.checkCollection.collection.pop()
      def check1 = extension.checkCollection.collection.pop()

      check1.name == 'DescendantToken'
      check1.id == 'stringEqual'
      check1.tokens == 'EQUAL,NOT_EQUAL'
      check1.limitedTokens == 'STRING_LITERAL'
      check1.maximumNumber == '0'
      check1.maximumDepth == '1'
      check1.customOptions.override == true
      check1.customOptions.remove == true

      check2.name == 'DescendantToken'
      check2.id == 'switchNoDefault'
      check2.tokens == 'LITERAL_SWITCH'
      check2.limitedTokens == 'LITERAL_DEFAULT'
      check2.maximumNumber == '2'
      check2.maximumDepth == '1'
      check2.customOptions.override == false
      check2.customOptions.remove == false
  }

  def 'No suppressions and checks are added by default'() {
    when:
      def extension = new EstiloExtension()

    then:
      extension.checkCollection == null
      extension.suppressionCollection == null
  }

  def 'Allow adding suppressions'() {
    given:
      def extension = new EstiloExtension()
      def closure = {
        suppressions {
          suffix 'Test.java', {
            checks 'LineLength'
            lines '23, 34, 45'
            columns([34, 56, 67])
          }
          suffix.not 'Test.java', {
            id 'javadoc'
          }
          prefix 'Draft', {
            checks 'Indentation'
          }
          prefix.not 'Draft', {
            id 'draftHeader'
          }
          contains 'Algorithm', {
            checks 'LineLength'
          }
          contains.not 'Test.java', {
            id 'classJavaDoc'
          }
        }
      }
      closure.delegate = extension

    when:
      closure()

    then:
      extension.suppressionCollection.length == 6

      extension.suppressionCollection.collection[0].files == '.*\\QTest.java\\E'
      extension.suppressionCollection.collection[0].checks == 'LineLength'
      extension.suppressionCollection.collection[0].lines == '23, 34, 45'
      extension.suppressionCollection.collection[0].columns == '34,56,67'
      extension.suppressionCollection.collection[0].properties.size() == 4

      extension.suppressionCollection.collection[1].files == '.*(?&lt;!\\QTest.java\\E)$'
      extension.suppressionCollection.collection[1].id == 'javadoc'
      extension.suppressionCollection.collection[1].properties.size() == 2

      extension.suppressionCollection.collection[2].files == '\\QDraft\\E.*'
      extension.suppressionCollection.collection[2].checks == 'Indentation'
      extension.suppressionCollection.collection[2].properties.size() == 2

      extension.suppressionCollection.collection[3].files == '^(?!\\QDraft\\E).*$'
      extension.suppressionCollection.collection[3].id == 'draftHeader'
      extension.suppressionCollection.collection[3].properties.size() == 2

      extension.suppressionCollection.collection[4].files == '.*\\QAlgorithm\\E.*'
      extension.suppressionCollection.collection[4].checks == 'LineLength'
      extension.suppressionCollection.collection[4].properties.size() == 2

      extension.suppressionCollection.collection[5].files == '^((?!\\QTest.java\\E).)*$'
      extension.suppressionCollection.collection[5].id == 'classJavaDoc'
      extension.suppressionCollection.collection[5].properties.size() == 2
  }
}
