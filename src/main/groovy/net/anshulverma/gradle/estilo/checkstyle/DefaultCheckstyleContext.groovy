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
import org.apache.commons.io.IOUtils

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class DefaultCheckstyleContext extends AbstractCheckstyleContext {

  DefaultCheckstyleContext() {
    loadChecksFromDump()
  }

  private def loadChecksFromDump() {
    rootCheckerChecks.addAll(loadChecksFromResource('/config/root-checker-checks.csv'))
    treeWalkerChecks.addAll(loadChecksFromResource('/config/tree-walker-checks.csv'))
  }

  private def loadChecksFromResource(String path) {
    def checksStream = getClass().getResourceAsStream(path)
    IOUtils.toString(checksStream).split(',').inject([]) { result, check ->
      result << check.trim()
    }
  }
}
