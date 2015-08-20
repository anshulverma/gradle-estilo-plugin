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
package net.anshulverma.gradle.estilo.util

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
abstract class AbstractDSL<T> {

  protected AbstractDSL() {
    def mc = new ExpandoMetaClass(getClass(), false, true)
    mc.initialize()
    this.metaClass = mc
  }

  def methodMissing(String name, args) {
    if (args.length == 1) {
      handle(name, convertArg(args[0]))
    } else {
      throw new MissingMethodException(name, this.class, args)
    }
  }

  def propertyMissing(String name) {
    handle(name)
  }

  protected def convertArg(def arg) {
    arg as T
  }

  protected abstract handle(String name, T value)

  protected abstract handle(String name)
}
