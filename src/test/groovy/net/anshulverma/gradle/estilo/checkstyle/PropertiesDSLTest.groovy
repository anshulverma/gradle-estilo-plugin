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
package net.anshulverma.gradle.estilo.checkstyle

import net.anshulverma.gradle.estilo.test.AbstractSpecification
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class PropertiesDSLTest extends AbstractSpecification {

  def 'Should not be able to get unknown property form collection'() {
    given:
      def properties = new PropertyCollection()

    when:
      properties.unknown

    then:
      thrown MissingPropertyException
  }

  @Unroll('calling unknown method with arguments #args')
  def 'Should not be able to call unknown method on collection'() {
    given:
      def properties = new PropertyCollection()

    when:
      properties.unknown(*args)

    then:
      thrown MissingMethodException

    where:
      args << [
          ['xyz', [1, 2, 3]],
          ['abcd', 'xyz', [1, 2, 3]]
      ]
  }
}
