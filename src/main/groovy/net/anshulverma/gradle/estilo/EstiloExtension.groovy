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
package net.anshulverma.gradle.estilo

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.estilo.checkstyle.ImportControlCollection
import net.anshulverma.gradle.estilo.checkstyle.PropertyCollection
import net.anshulverma.gradle.estilo.checkstyle.SuppressionCollection
import net.anshulverma.gradle.estilo.checkstyle.checks.CheckType

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class EstiloExtension {

  CheckType baseChecks = CheckType.GOOGLE
  PropertyCollection checkCollection
  PropertyCollection suppressionCollection
  ImportControlCollection importControlCollection
  Map headerCheckOptions
  boolean ignoreCheckstyleWarnings = false

  def source(String type) {
    baseChecks = CheckType.valueOf(type.toUpperCase())
  }

  def checks(Closure closure) {
    checkCollection = evaluate('checks', new PropertyCollection(), closure)
  }

  def suppressions(Closure closure) {
    suppressionCollection = evaluate('suppressions', new SuppressionCollection(), closure)
  }

  def importControl(String basePackage, Closure closure) {
    importControlCollection = evaluate('importControl', new ImportControlCollection(basePackage), closure)
  }

  def header(Map headerCheckOptions) {
    this.headerCheckOptions = headerCheckOptions
  }

  def ignoreWarnings(boolean ignoreCheckstyleWarnings) {
    this.ignoreCheckstyleWarnings = ignoreCheckstyleWarnings
  }

  boolean hasSuppressions() {
    suppressionCollection != null && suppressionCollection.length > 0
  }

  boolean hasImportControl() {
    importControlCollection != null && importControlCollection.length > 0
  }

  boolean hasHeader() {
    headerCheckOptions != null && !headerCheckOptions.isEmpty()
  }

  private def evaluate(name, collection, closure) {
    closure.delegate = collection
    closure()
    log.debug("added ${collection.length} $name")
    collection
  }
}
