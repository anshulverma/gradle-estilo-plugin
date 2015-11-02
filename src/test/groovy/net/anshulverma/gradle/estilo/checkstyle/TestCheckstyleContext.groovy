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

import com.puppycrawl.tools.checkstyle.api.AuditListener
import com.puppycrawl.tools.checkstyle.api.Check
import com.puppycrawl.tools.checkstyle.api.FileSetCheck
import com.puppycrawl.tools.checkstyle.api.Filter
import groovy.util.logging.Slf4j
import org.eclipse.jdt.core.dom.Modifier
import org.reflections.Reflections

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class TestCheckstyleContext extends AbstractCheckstyleContext {

  private static final Reflections REFLECTIONS = new Reflections('com.puppycrawl.tools.checkstyle')

  TestCheckstyleContext() {
    findChecksUsingReflection()
  }

  private def findChecksUsingReflection() {
    [FileSetCheck, Filter, AuditListener].each {
      def checks = findChecks(it)
      log.info("found ${checks.size()} checks for root checker based on $it")
      rootCheckerChecks.addAll(checks)
    }
    treeWalkerChecks.addAll(findChecks(Check))
    log.info("found ${treeWalkerChecks.size()} checks tree walker")
  }

  private findChecks(Class clazz) {
    Set checks = []
    REFLECTIONS.getSubTypesOf(clazz).each { checkClass ->
      if (!Modifier.isAbstract(checkClass.modifiers)
          && (checkClass.name.endsWith('Check') || checkClass.name.endsWith('Filter'))) {
        checks.add(getCheckName(checkClass))
      }
      checks.addAll(findChecks(checkClass))
    }
    checks
  }

  private getCheckName(Class checkClass) {
    checkClass.simpleName - 'Check'
  }
}
