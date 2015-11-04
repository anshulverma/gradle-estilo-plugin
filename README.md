# gradle-estilo-plugin

[![Build Status](https://travis-ci.org/anshulverma/gradle-estilo-plugin.svg)](https://travis-ci.org/anshulverma/gradle-estilo-plugin)
[![Coverage Status](https://coveralls.io/repos/anshulverma/gradle-estilo-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/anshulverma/gradle-estilo-plugin?branch=master)
[![Download](https://api.bintray.com/packages/anshulverma/gradle-plugins/gradle-estilo-plugin/images/download.svg)](https://bintray.com/anshulverma/gradle-plugins/gradle-estilo-plugin/_latestVersion)

This plugin allows you to setup checkstyle by providing a powerful DSL directly in your build
script. This means no more `checkstyle.xml`, `checkstyle.xsl`, `suppressions.xml`,
`import-control.xml` and `header.txt`. All of these files are auto generated based on your
configuration options that you specified in the build script.

## Usage

Add a `buildScript` dependency to enable this plugin:

``` groovy
buildscript {
  repositories {
    jcenter() // also available in mavenCentral()
  }

  dependencies {
    classpath 'net.anshulverma.gradle:gradle-estilo-plugin:0.4.3'
  }
}
```

Then apply the plugin:

``` groovy
apply plugin: 'net.anshulverma.gradle.estilo'
```

## Configuration

Here is a sample configuration with inline explanations. Some configuration options are not present
in this sample. These and others are explained in the next section.

``` groovy
estilo {
  source 'google' // starting point for checkstyle checks (other option: 'sun')

  ignoreWarnings true // ignore checkstyle warning and let the build continue

  // this block provides a DSL that allows you to
  // override, remove or extend checks of the source file
  checks {
    // this check will override the original check from 'google'.
    // in other words it says remove original check and create a new one
    // with the following properties
    CustomImportOrder(override: true) {
      specialImportsRegExp 'net.anshulverma'
      sortImportsInGroupAlphabetically true
      customImportOrderRules 'SPECIAL_IMPORTS' +
                                 '###THIRD_PARTY_PACKAGE' +
                                 '###STANDARD_JAVA_PACKAGE' +
                                 '###STATIC'
      separateLineBetweenGroups false
    }

    // extends the original 'EmptyLineSeparator' check (default behavior)
    // by adding/modifying properties from the original check
    EmptyLineSeparator {
      allowNoEmptyLineBetweenFields true
      allowMultipleEmptyLines false
      tokens 'PACKAGE_DEF, IMPORT, CLASS_DEF, ENUM_DEF, ' +
                 'INTERFACE_DEF, CTOR_DEF, METHOD_DEF, ' +
                 'STATIC_INIT, INSTANCE_INIT, VARIABLE_DEF'
    }

    // remove this check completely
    SingleLineJavadoc(remove: true)
  }

  // add checkstyle suppressions.
  // this automatically adds a 'SuppressionFilter' to checkstyle.xml
  suppressions {
    suffix('Test.java') {
      checks 'LineLength'
    }
    prefix('Client') {
      checks 'JavadocMethod'
    }
    prefix.not('Server') {
      checks 'FileTabCharacter'
    }
  }

  // setup import control.
  // this automatically adds a 'ImportControl' check to checkstyle.xml
  importControl('net.anshulverma.skydns') {
    // list packages to allow or disallow
    allow pkg: 'java'
    allow pkg: 'net'
    disallow pkg: 'lombok'

    // similarly you can allow or disallow a class
    allow class: 'sun.misc.Unsafe'

    // setup import control in a similar way for a subpackage
    subpackage('common.threads') {
      allow class: 'java.util.concurrent.Executors'
    }
  }

  // setup header check.
  // this automatically adds a 'RegexpHeader' check to checkstyle.xml.
  header regexp: true, multiLines: [7, 8, 9, 10, 12], template: '''^/\\*\\*
^ \\* Copyright © 2015 Anshul Verma. All Rights Reserved.
^ \\*/
^\$
^package
^\$
^import
^\$
^import static
^\$
^/\\*\\*
^ \\*((?! @author )|\$)
^ \\* @author anshul\\.verma86@gmail\\.com \\(Anshul Verma\\)
^ \\*/
'''
}
```

## Estilo DSL properties

The `DSL` provided by the `gradle-estilo-plugin` allows you to control several options and properties
so that you can have a fine grained control on your custom checkstyle setup. This section explins
each `DSL` block one by one.

### `source`

Control what flavor of checkstyle you prefer to use. This is just a starting point as you can still
override it in the following sections.

Available options: `google` and `sun` (more may be added later)

Default value: `google`

### `ignoreWarnings`

Control whether to fail the build or not if a checkstyle error is encountered.

Default value: `false`

### `checks`

This block allows you to override, remove or extend the checks provided in the source checkstyle
file. The basic syntax is:

``` groovy
checks {
  <CheckName>(<options>) {
    <propertyName> <propertyValueLiteral>
  }
}
```

`<options>` is a `hashmap` and is optional. It can be used to specify if a check should be
overridden, removed or extended. Default behavior is to extend a check.

Please refer to the example above on how to configure the checks.

### `suppressions`

This block allows you to add checkstyle suppression if you don't want to make some part of your code
conform to checkstyle rules.

Suppressions are specified using a regex on the class or package and it contains a comma separated
list of checks that are to be suppressed for classes matching that pattern.

The matching pattern can be specified in three ways:

- `suffix` -- this matches the suffix of a class name
- `contains` -- this matches that the class name contains a string
- `prefix` -- this matches the prefix of a class name

All of these checks can be negated by applying a `.not` operator. Please refer to the above example
for a sample use of this.

### `importControl`

Sometimes you want to control what sort of imports should be allowed or disallowed in your source
code. This is done using import control check.

### `header`

This property lets you specify a header template that all classes must contain. It will
automatically add a `RegexpHeader` check, indicating that presently, `gradle-estilo-plugin` can
only handle `regexp` headers.

## HTML reporting

The autogenerated files required for `checkstyle` task are stored in
`${project.rootDir}/build/estilo`. Along with this a `html` report is also generated for each
source set.

## Building

You are welcome to suggest changes or work on them yourself and issue a pull request. To make sure
your changes pass all requirements, please run the `check` task:

``` bash
$ ./gradlew check
```

## Contributing

1. Fork it ( https://github.com/anshulverma/gradle-estilo-plugin/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request

## Author

[Anshul Verma](http://anshulverma.net/) ::
[anshulverma](https://github.com/anshulverma) ::
[@anshulverma](http://twitter.com/anshulverma)

## License

Copyright (c) 2014 Anshul Verma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
