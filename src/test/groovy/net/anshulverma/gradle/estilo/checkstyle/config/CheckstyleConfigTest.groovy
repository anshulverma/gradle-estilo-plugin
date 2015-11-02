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
package net.anshulverma.gradle.estilo.checkstyle.config

import net.anshulverma.gradle.estilo.checkstyle.CustomOptions
import net.anshulverma.gradle.estilo.test.AbstractSpecification

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class CheckstyleConfigTest extends AbstractSpecification {

  def 'test ability to extend checkstyle config'() {
    given:
      def file = this.class.getResource('/fixtures/checkstyle_test.xml').path as File
      CheckstyleConfig checkstyleConfig = ConfigMarshaller.INSTANCE.unmarshal(file)

    when:
      checkstyleConfig.extend('RegexpHeader', CustomOptions.fromHash([:]), [p1: 'v1', p2: 'v2'])
      checkstyleConfig.extend('SeverityMatchFilter', CustomOptions.fromHash([:]), [p1: 'v1', p2: 'v2'])
      checkstyleConfig.extend('MethodCount', CustomOptions.fromHash([:]), [p1: 'v1', p2: 'v2'])
      checkstyleConfig.extend('FileTabCharacter', CustomOptions.fromHash([:]), [p1: 'v1', p2: 'v2', eachLine: 'false'])
      checkstyleConfig.extend('NewlineAtEndOfFile', CustomOptions.fromHash([override: true]), [p1: 'v1', p2: 'v2'])
      checkstyleConfig.extend('Translation', CustomOptions.fromHash([remove: true]), [p1: 'v1', p2: 'v2'])
      def marshalled = ConfigMarshaller.INSTANCE.marshal(checkstyleConfig)

    then:
      marshalled == '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC
    "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
    "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    <module name="SeverityMatchFilter">
        <property name="matcher" value="abcd"/>
        <property name="p1" value="v1"/>
        <property name="p2" value="v2"/>
    </module>
    <module name="FileTabCharacter">
        <property name="eachLine" value="false"/>
        <property name="p1" value="v1"/>
        <property name="p2" value="v2"/>
    </module>
    <module name="NewlineAtEndOfFile">
        <property name="p1" value="v1"/>
        <property name="p2" value="v2"/>
    </module>
    <module name="RegexpHeader">
        <property name="p1" value="v1"/>
        <property name="p2" value="v2"/>
    </module>
    <module name="TreeWalker">
        <module name="MethodCount">
            <property name="p1" value="v1"/>
            <property name="p2" value="v2"/>
        </module>
    </module>
</module>
'''
  }
}
