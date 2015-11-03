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

  def 'test suppressions should add a suppression filter module'() {
    given:
      def project = singleJavaProject()
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def extension = new EstiloExtension()
      def closure = {
        source 'google'

        checks {
          DescendantToken {
            id 'switchNoDefault'
            tokens 'LITERAL_SWITCH'
            limitedTokens 'LITERAL_DEFAULT'
            maximumNumber 2
            maximumDepth 1
          }
        }

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
      EstiloTask estiloTask = project.getTasksByName('estilo', true)[0]
      estiloTask.execute(extension)

    then:
      def expectedCheckstylePath = "${project.rootDir}/build/estilo/checkstyle.xml"
      def expectedSuppressionsPath = "${project.rootDir}/build/estilo/suppressions.xml"
      FileUtils.fileExists(expectedCheckstylePath)
      new File(expectedCheckstylePath).text.contains """<module name="SuppressionFilter">
        <property name="file" value="$expectedSuppressionsPath"/>
    </module>
"""
      FileUtils.fileExists(expectedSuppressionsPath)
      new File(expectedSuppressionsPath).text == '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suppressions PUBLIC
    "-//Puppy Crawl//DTD Suppressions 1.1//EN"
    "http://www.puppycrawl.com/dtds/suppressions_1_1.dtd">
<suppressions>
    <suppress columns="34,56,67" lines="23, 34, 45" files=".*\\QTest.java\\E" checks="LineLength"/>
    <suppress files=".*(?&lt;!\\QTest.java\\E)$" id="javadoc"/>
    <suppress files="\\QDraft\\E.*" checks="Indentation"/>
    <suppress files="^(?!\\QDraft\\E).*$" id="draftHeader"/>
    <suppress files=".*\\QAlgorithm\\E.*" checks="LineLength"/>
    <suppress files="^((?!\\QTest.java\\E).)*$" id="classJavaDoc"/>
</suppressions>
'''
  }

  def 'test import control should add a ImportControl check module'() {
    given:
      def project = singleJavaProject()
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def extension = new EstiloExtension()
      def closure = {
        source 'google'

        checks {
          DescendantToken {
            id 'switchNoDefault'
            tokens 'LITERAL_SWITCH'
            limitedTokens 'LITERAL_DEFAULT'
            maximumNumber 2
            maximumDepth 1
          }
        }

        suppressions {
          suffix 'Test.java', {
            checks 'LineLength'
            lines '23, 34, 45'
            columns([34, 56, 67])
          }
        }

        importControl 'com', {
          allow pkg: 'java'
          allow pkg: 'javax'
          allow pkg: 'com'
          allow clazz: 'net.anshulverma.gradle.estilo.EstiloTask'

          disallow pkg: 'org'
          disallow clazz: 'org.Unknown'

          subpackage 'com.test.first', {
            allow pkg: 'com'
          }

          subpackage 'com.test.second', {
            allow pkg: 'com'
            allow clazz: 'com.test.Testable'
            disallow pkg: 'com'
            disallow clazz: 'com.unknown.Weird'
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
      def expectedSuppressionsPath = "${project.rootDir}/build/estilo/suppressions.xml"
      def expectedImportControlPath = "${project.rootDir}/build/estilo/import-control.xml"
      FileUtils.fileExists(expectedCheckstylePath)
      new File(expectedCheckstylePath).text.contains """<module name="SuppressionFilter">
        <property name="file" value="$expectedSuppressionsPath"/>
    </module>
"""
      new File(expectedCheckstylePath).text.contains """<module name="ImportControl">
            <property name="file" value="${expectedImportControlPath}"/>
        </module>
"""

      FileUtils.fileExists(expectedImportControlPath)
      new File(expectedImportControlPath).text == '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE import-control PUBLIC "-//Puppy Crawl//DTD Import Control 1.0//EN"
  "http://www.puppycrawl.com/dtds/import_control_1_0.dtd">
<import-control pkg="com">
    <allow pkg="java"/>
    <allow pkg="javax"/>
    <allow pkg="com"/>
    <allow class="net.anshulverma.gradle.estilo.EstiloTask"/>
    <disallow pkg="org"/>
    <disallow class="org.Unknown"/>
    <subpackage name="com.test.first">
        <allow pkg="com"/>
    </subpackage>
    <subpackage name="com.test.second">
        <allow pkg="com"/>
        <allow class="com.test.Testable"/>
        <disallow pkg="com"/>
        <disallow class="com.unknown.Weird"/>
    </subpackage>
</import-control>
'''
  }

  def 'test can add header from estilo'() {
    given:
      def project = singleJavaProject()
      project.apply plugin: 'net.anshulverma.gradle.estilo'
      def extension = new EstiloExtension()
      def closure = {
        header regexp: true, multiLines: [22, 23, 24, 25, 27], template: """^/\\*\\*
^ \\* Copyright © 2015 Anshul Verma. All Rights Reserved.
^ \\*
^ \\* <p>
^ \\* Licensed under the Apache License, Version 2.0 \\(the "License"\\);
^ \\* you may not use this file except in compliance with the License.
^ \\* You may obtain a copy of the License at
^ \\*
^ \\* <p>
^ \\* http://www.apache.org/licenses/LICENSE-2\\.0
^ \\*
^ \\* <p>
^ \\* Unless required by applicable law or agreed to in writing, software
^ \\* distributed under the License is distributed on an "AS IS" BASIS,
^ \\* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
^ \\* See the License for the specific language governing permissions and
^ \\* limitations under the License.
^ \\*/
^\$
^package
^\$
^import
^\$
^import static
^\$
^/\\*\\*
^ \\*((?! @author )|\$)
^ \\* @author anshul\\.verma86@gmail\\.com \\(Anshul Verma\\)
^ \\*/
"""
      }
      closure.delegate = extension

    when:
      closure()
      EstiloTask estiloTask = project.getTasksByName('estilo', true)[0]
      estiloTask.execute(extension)

    then:
      def expectedCheckstylePath = "${project.rootDir}/build/estilo/checkstyle.xml"
      def expectedHeaderFilePath = "${project.rootDir}/build/estilo/header.txt"
      FileUtils.fileExists(expectedCheckstylePath)
      new File(expectedCheckstylePath).text.contains """<module name="RegexpHeader">
        <property name="headerFile" value="$expectedHeaderFilePath"/>
        <property name="multiLines" value="22,23,24,25,27"/>
    </module>
"""

      FileUtils.fileExists(expectedHeaderFilePath)
      new File(expectedHeaderFilePath).text ==
          IOUtils.toString(getClass().getResourceAsStream('/fixtures/test-header.txt'))
  }

  def fileHash(File file) {
    MessageDigest messageDigest = MessageDigest.getInstance('MD5')
    DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), messageDigest)
    IOUtils.toByteArray(inputStream)
    messageDigest.digest()
  }
}
