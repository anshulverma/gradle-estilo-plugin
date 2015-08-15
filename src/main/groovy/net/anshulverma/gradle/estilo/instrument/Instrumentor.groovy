package net.anshulverma.gradle.estilo.instrument

import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@TupleConstructor
class Instrumentor {

  Project project
  String outputDir

  def Instrumented instrument() {
    return new Instrumented(project)
  }
}
