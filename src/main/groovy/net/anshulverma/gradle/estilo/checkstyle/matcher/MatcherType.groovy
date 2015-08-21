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
package net.anshulverma.gradle.estilo.checkstyle.matcher

import groovy.text.SimpleTemplateEngine

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
public enum MatcherType {

  SUFFIX('.*$param', '.*(?&lt;!${param})\\$'),
  PREFIX('$param.*', '^(?!${param}).*\\$'),
  CONTAINS('.*$param.*', '^((?!${param}).)*\\$')

  private final regexpTemplate
  private final negativeRegexpTemplate

  private MatcherType(String regexpTemplate, String negativeRegexpTemplate) {
    def templateEngine = new SimpleTemplateEngine()
    this.regexpTemplate = templateEngine.createTemplate(regexpTemplate)
    this.negativeRegexpTemplate = templateEngine.createTemplate(negativeRegexpTemplate)
  }

  def makeRegexp(String param, boolean negative) {
    def template = negative ? negativeRegexpTemplate : regexpTemplate
    template.make(['param': param])
  }

  static MatcherType fromName(String name) {
    valueOf(name.toUpperCase())
  }
}
