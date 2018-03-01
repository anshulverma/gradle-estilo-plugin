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
package net.anshulverma.gradle.estilo.checkstyle.checks

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
public class CheckType {

  public static GOOGLE = new CheckType('google_checks.xml')
  public static SUN = new CheckType('sun_checks.xml')
  public static EMPTY = new CheckType('empty_checks.xml')
  public static CUSTOM = new CheckType("config/checkstyle.xml")

  final String filename

  private CheckType(def filename) {
    this.filename = filename
  }

  static setCustom(String filename){
    CUSTOM = new CheckType(filename)
  }

  static CheckType valueOf(String name){
    CheckType.class.getField(name).get(null) as CheckType
  }
}
