package net.anshulverma.gradle.estilo.instrument

import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@TupleConstructor
class Instrumented {

  Project project

  def run() {
    println 'not implemented yet'
  }
}
