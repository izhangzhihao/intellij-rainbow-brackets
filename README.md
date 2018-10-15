<h1 align="center">Intellij rainbow brackets</h1>
<p align="center">Rainbow Brackets / Rainbow Parentheses for IntelliJ based IDEs.</p>

<p align="center"> 
<a href="https://gitter.im/izhangzhihao/intellij-rainbow-brackets"><img src="https://img.shields.io/gitter/room/izhangzhihao/intellij-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://circleci.com/gh/izhangzhihao/intellij-rainbow-brackets"><img src="https://img.shields.io/circleci/project/github/izhangzhihao/intellij-rainbow-brackets/IC-2017.2.svg?style=flat-square"></a>
<a href="https://codecov.io/gh/izhangzhihao/intellij-rainbow-brackets"><img src="https://img.shields.io/codecov/c/github/izhangzhihao/intellij-rainbow-brackets/IC-2017.2.svg?style=flat-square"></a>
<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/releases"><img src="https://img.shields.io/github/release/izhangzhihao/intellij-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/d/10080-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/v/10080-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/"><img src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat-square"></a>
</p>

<br>

# Table of Contents

- [Table of Contents](#table-of-contents)
    - [Compatibility](#compatibility)
    - [Supported languages](#supported-languages)
    - [Screenshots](#screenshots)
    - [Install](#install)
    - [Customize colors](#customize-colors)
    - [HTML code in js](#html-code-in-js)
    - [Kotlin function literal braces and arrow](#kotlin-function-literal-braces-and-arrow)
    - [Contribute](#contribute)
    - [Acknowledgements](#acknowledgements)

## Compatibility

IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, Gogland, DataGrip, Rider, MPS, Android Studio

## Supported languages

Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, HTML, XML, SQL, Apex language ...

## Screenshots

* with Java

![](./screenshots/with-java.png)

![](./screenshots/with-material-theme-ui.png)

* with Scala

![](./screenshots/with-scala.png)

* with Clojure

![](./screenshots/with-Clojure.png)

* with Kotlin

![](./screenshots/with-kotlin.png)

* with HTML/XML

![](./screenshots/with-HTML.png)

* Scope highlighting

<kbd>Ctrl + Button3</kbd>(Windows & Linux) or <kbd>Meta+ Button3</kbd>(Mac):

![](https://user-images.githubusercontent.com/10737066/40234968-46593fe2-5adb-11e8-8ea8-0026fad86ca9.gif)

<kbd>Alt + Button3</kbd>:

![](https://user-images.githubusercontent.com/10737066/40235004-642dfe54-5adb-11e8-9fd7-648b92fab8f5.gif)

* [night-owl-jetbrains](https://github.com/xdrop/night-owl-jetbrains)

![](https://github.com/xdrop/night-owl-jetbrains/raw/2018.2/screenshot.png)

* looking forward to your screenshots(PR welcome!)

## Install

For Windows - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd> > <kbd>Restart IntelliJ IDEA</kbd>

For Mac - <kbd>IntelliJ IDEA</kbd> > <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd>  > <kbd>Restart IntelliJ IDEA</kbd>

## Customize colors

<kbd>Settings/Preferences</kbd> > <kbd>Editor</kbd> > <kbd>Color Scheme</kbd> > <kbd>Rainbow Brackets</kbd>:

![Customize colors](./screenshots/customize-colors.png)

## HTML code in js

To enable rainbow brackets for HTML inside js code like this:

```javascript
var html = '<div><div><div>Hello</div></div></div>';
```

This plugin will automatically override color scheme propertie "HTML_CODE" [cause our rainbow color been covered by intellij built-in functionality](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000117450-My-HighlightVisitor-been-covered-by-intellij-built-in-functionality).
You still could set `<option name="rainbowifyHTMLInsideJS" value="false" />` in config file to disable just like [Config brackets colors](#config-brackets-colors).

## Kotlin function literal braces and arrow

To enable rainbow brackets for multiple level lambda Kotlin code like this:

```kotlin
event.throwable?.let { throwable ->
    IdeErrorsDialog.findPluginId(throwable)?.let { pluginId ->
        PluginManager.getPlugin(pluginId)?.let { ideaPluginDescriptor ->
            if (!ideaPluginDescriptor.isBundled) {
                bean.pluginName = ideaPluginDescriptor.name
                bean.pluginVersion = ideaPluginDescriptor.version
            }
        }
    }
}
```

This plugin will automatically override color scheme propertie "KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW" cause our rainbow color been covered by kotlin plugin built-in functionality.
You still could set `<option name="rainbowifyKotlinFunctionLiteralBracesAndArrow" value="false" />` in config file to disable just like [Config brackets colors](#config-brackets-colors).

## Contribute

* `gradle test`
* `gradle runIde`
* `gradle buildPlugin`

## Acknowledgements

Intellij-rainbow-brackets is heavily inspired by [Rainbow Brackets for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=2gua.rainbow-brackets)
