/**
 * Copyright 2015 Anshul Verma. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.anshulverma.gradle.estilo.checkstyle

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.estilo.checkstyle.config.ConfigMarshaller
import net.anshulverma.gradle.estilo.checkstyle.matcher.MatcherRegexp
import net.anshulverma.gradle.estilo.checkstyle.matcher.MatcherType

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class SuppressionCollection extends PropertyCollection {

  @Override
  protected handle(String name, String files, Closure closure) {
    def matcher = newMatcherBuilder(MatcherType.fromName(name)).matchParam(files).build()
    addSuppression(matcher, closure)
  }

  @Override
  def handle(String name) {
    def builder = newMatcherBuilder(MatcherType.fromName(name))
    builder.metaClass.not = { files, closure ->
      MatcherRegexp matcher = builder.matchParam(files)
                                     .negative(true)
                                     .build()
      addSuppression(matcher, closure)
    }
    builder
  }

  private newMatcherBuilder(matcherType) {
    MatcherRegexp.builder()
                 .matcherType(matcherType)
  }

  private addSuppression(matcher, closure) {
    def properties = new Properties([:])
    properties.files(matcher.regexp.toString())
    evaluate(properties, closure)
    properties
  }

  ConfigMarshaller.Suppressions toSuppressions() {
    List<ConfigMarshaller.SuppressedCheck> suppressedChecks = []
    each {
      def properties = it.properties
      ConfigMarshaller.SuppressedCheck suppressedCheck = ConfigMarshaller.SuppressedCheck.builder()
                                                                         .id(properties.id)
                                                                         .checks(properties.checks)
                                                                         .files(properties.files)
                                                                         .lines(properties.lines)
                                                                         .columns(properties.columns)
                                                                         .build()
      suppressedChecks.add(suppressedCheck)
    }
    ConfigMarshaller.Suppressions.builder()
                    .suppressedChecks(suppressedChecks)
                    .build()
  }
}
