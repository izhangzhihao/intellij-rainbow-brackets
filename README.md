# Rainbow Brackets
[![Build Status](https://travis-ci.org/izhangzhihao/intellij-rainbow-brackets.svg?branch=master)](https://travis-ci.org/izhangzhihao/intellij-rainbow-brackets) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/1c72f2de07a5452da479565883d3ab74)](https://www.codacy.com/app/izhangzhihao/intellij-rainbow-brackets?utm_source=github.com&utm_medium=referral&utm_content=izhangzhihao/intellij-rainbow-brackets&utm_campaign=badger) [![Rainbow Brackets Release](https://img.shields.io/github/release/izhangzhihao/intellij-rainbow-brackets.svg)](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets) [![Plugin downloads](https://img.shields.io/jetbrains/plugin/d/10080-rainbow-brackets.svg)](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets) [![Plugin version](https://img.shields.io/jetbrains/plugin/v/10080-rainbow-brackets.svg)](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/izhangzhihao/intellij-rainbow-brackets/issues)

## Rainbow Brackets / Rainbow Parentheses for IntelliJ IDEA-based IDEs

## Compatible with

IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, Gogland, DataGrip, Rider, MPS, Android Studio

## Supported languages

Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, C#, HTML, XML, SQL, Apex language ...

## Screenshots

* with material-theme-jetbrains-eap

![](./screenshots/with-material-theme-ui.png)

* with scala

![](./screenshots/with-scala.png)

* with kotlin

![](./screenshots/with-kotlin.png)

* with HTML/XML

![](./screenshots/with-HTML.png)

* and more...

## Install

https://plugins.jetbrains.com/plugin/10080-rainbow-brackets

## Implement for some language you would like

1. Check [this commit](https://github.com/izhangzhihao/intellij-rainbow-brackets/commit/729eff116b5eb7dd93261d1476d4db305accffb1) for implementation for scala or check out [this commit](https://github.com/izhangzhihao/intellij-rainbow-brackets/commit/95489f599de1e4f536cfc4caf2e730fb63b765eb) for implementation for javascript.
2. Found the **language ID** such as `Scala`
3. Implement `RainbowScala` which is a class implement `Annotator`, you should know the `TokenTypes` of your language, such as [ScalaTokenTypes](https://github.com/JetBrains/intellij-scala/blob/idea173.x/scala/scala-impl/src/org/jetbrains/plugins/scala/lang/lexer/ScalaTokenTypes.java). 
And find out the `myDebugName` of your `IElementType`, in scala, we have:

|    IElementType    | bracket | myDebugName |
| :----------: | :---: | :---: |
| tLSQBRACKET | [ | [ |
| tRSQBRACKET | ] | ] |
| tLBRACE | { | { |
| tRBRACE | } | } |
| tLPARENTHESIS | ( | ( |
| tRPARENTHESIS | ) | ) |
| tLESS | < | < |
| tGREATER | > | > |

So you could use `myDebugName` as parameter pass to `annotateUtil`

4. Add your language id to `specLangList` so we can ignore common annotator.
5. Remove old annotator implement in `plugin.xml` and add your new !
6. `gradle runIde` check your awesome work !
7. Sent PR.

## Contribute

* `gradle runIde`
* `gradle buildPlugin`

## Acknowledgements

Intellij-rainbow-brackets is heavily inspired by [intellij-rainbow](https://github.com/zjhmale/intellij-rainbow) and [Rainbow Brackets for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=2gua.rainbow-brackets)
