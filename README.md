<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets">
        <img src="./src/main/resources/META-INF/pluginIcon.svg" width="320" height="320" alt="logo"/>
    </a>
</div>
<h1 align="center">Intellij rainbow brackets</h1>
<p align="center">Rainbow Brackets / Rainbow Parentheses for IntelliJ based IDEs.</p>

<p align="center"> 
<a href="https://circleci.com/gh/izhangzhihao/intellij-rainbow-brackets"><img src="https://img.shields.io/circleci/project/github/izhangzhihao/intellij-rainbow-brackets/2020.1.svg?style=flat-square"></a>
<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/releases"><img src="https://img.shields.io/github/release/izhangzhihao/intellij-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/d/10080-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/v/10080-rainbow-brackets.svg?style=flat-square"></a>
<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/"><img src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat-square"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets#backer"><img src="https://img.shields.io/opencollective/backers/intellij-rainbow-brackets?style=flat-square"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets#sponsor"><img src="https://img.shields.io/opencollective/sponsors/intellij-rainbow-brackets?style=flat-square"></a>
</p>

<br>

- [Compatibility](#compatibility)
- [Supported languages](#supported-languages)
- [Author's choice](#authors-choice)
- [Screenshots](#screenshots)
- [Install](#install)
- [Customize colors](#customize-colors)
- [Use color generator](#use-color-generator)
- [Config file path](#config-file-path)
- [HTML code in js](#html-code-in-js)
- [Kotlin function literal braces and arrow](#kotlin-function-literal-braces-and-arrow)
- [Disable rainbow brackets for specific languages](#disable-rainbow-brackets-for-specific-languages)
- [Contribute](#contribute)
- [Support Us](#support-us)
- [Backers](#backers)
- [Sponsors](#sponsors)
- [Acknowledgements](#acknowledgements)

## Compatibility

IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, Gogland, DataGrip, Rider, MPS, Android Studio

## Supported languages

Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, HTML, XML, SQL, Apex language, C#, Dart, ...

## Author's choice

Rainbow Brackets + Material Theme UI(Oceanic theme) + Nyan Progress Bar + [Fira Code](https://github.com/tonsky/FiraCode) (Font)

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

**The highlight effects will not remove after release the shortcuts, but press `ESC` key can do this. You could also config `Press any key to remove the highlighting effect` in setting page.**

<kbd>Ctrl + Button3</kbd>(Windows & Linux) or <kbd>Meta+ Button3</kbd>(Mac):

NOTE: <kbd>Button3</kbd> means "Secondary Click (click or tap with two fingers)" on Mac os, "Right click" for Windows or Linux.

![](https://user-images.githubusercontent.com/10737066/40234968-46593fe2-5adb-11e8-8ea8-0026fad86ca9.gif)

<kbd>Alt + Button3</kbd>:

NOTE: <kbd>Button3</kbd> means "Secondary Click (click or tap with two fingers)" on Mac os, "Right click" for Windows or Linux.

![](https://user-images.githubusercontent.com/10737066/40235004-642dfe54-5adb-11e8-9fd7-648b92fab8f5.gif)

* Rainbow indent guide lines

![](https://user-images.githubusercontent.com/10737066/65765792-c41cb500-e15b-11e9-8877-2239c6afa7bf.gif)


* [night-owl-jetbrains](https://github.com/xdrop/night-owl-jetbrains)

![](https://github.com/xdrop/night-owl-jetbrains/raw/2018.2/screenshot.png)

* looking forward to your screenshots(PR welcome!)

## Install

For Windows & Linux - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd> > <kbd>Restart IntelliJ IDEA</kbd>

For Mac - <kbd>IntelliJ IDEA</kbd> > <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd>  > <kbd>Restart IntelliJ IDEA</kbd>

### Install snapshot build

You can download the latest snapshot build from [here](https://circleci.com/gh/izhangzhihao/intellij-rainbow-brackets), just click in the latest build and click the 'Artifacts' tab.(You must logged in via github)

## Customize colors

<kbd>Settings/Preferences</kbd> > <kbd>Editor</kbd> > <kbd>Color Scheme</kbd> > <kbd>Rainbow Brackets</kbd>:

![Customize colors](./screenshots/customize-colors.png)

## Use color generator

I didn't like the build-in colors, I want some new color but does want to try out and pick colors. Please use color generator.

<kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > <kbd>Use color generator</kbd>

If you turn on this option, we will auto generate some colors for you.

If you need more control, you can decide the options of color generator by edit [the config file](#config-file-path), 

The value of the option `customColorGeneratorOption` is a json object, available options are:

```hue``` – Controls the hue of the generated color. You can pass a string representing a color name: ```red```, ```orange```, ```yellow```, ```green```, ```blue```, ```purple```, ```pink``` and ```monochrome``` are currently supported. If you pass a  hexidecimal color string such as ```#00FFFF```, color generator will extract its hue value and use that to generate colors.

```luminosity``` – Controls the luminosity of the generated color. You can specify a string containing ```bright```, ```light``` or ```dark```.

Please make sure the value of the option `customColorGeneratorOption` is escaped in xml format,
you can use [this tool](https://www.freeformatter.com/xml-escape.html#ad-output) to do this. Please don't forget to restart your IDE after change the config file.

```xml
<application>
  <component name="RainbowSettings">
    <option name="useColorGenerator" value="true" />
    <option name="customColorGeneratorOption" value="{&quot;luminosity&quot;: &quot;light&quot;,&quot;hue&quot;: &quot;blue&quot;}"/>
  </component>
</application>
```

### Want to config the number of colors?

<kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > `Number of colors`: 5 or more

NOTE: For default and darcula color scheme(`Editor -> Color Scheme -> Rainbow Brackets -> Scheme`) the color number is 10, for the other scheme the number is 5, if your number is bigger than the number, you can config them in the config file.
If the color is not exist, we will use color generator to generator it for you.

Please follow [the official guide](https://www.jetbrains.com/help/idea/configuring-colors-and-fonts.html#share-color-scheme):
* `Export a color scheme as XML`
* Edit the xml file, put `ROUND_BRACKETS_RAINBOW_COLOR5` to `ROUND_BRACKETS_RAINBOW_COLOR100` to match your number just like [the default color scheme](./src/main/resources/colorSchemes/rainbow-color-default-darcula.xml).
* `Import a color scheme`

## Config file path

If you want to customize the advanced configuration, you could edit the config file then restart your IDE. 
Config file path in `APP_CONFIG/rainbow_brackets.xml`. 

In MAC OS env maybe like `~/Library/Preferences/IntelliJIdea2020.1/options/rainbow_brackets.xml`.

If you are using the ToolBox, then it will be like `~/Library/ApplicationSupport/JetBrains/IntelliJIdea2020.1/options/rainbow_brackets.xml`

In Linux env maybe like `~/.IntelliJIdea/config/options/rainbow_brackets.xml`.

In Windows env maybe like `C:\Users\izhangzhihao\.IntelliJIdea2020.1\config\options\rainbow_brackets.xml`.

## HTML code in js

To enable rainbow brackets for HTML inside js code like this:

```javascript
var html = '<div><div><div>Hello</div></div></div>';
```

This plugin will automatically override color scheme property "HTML_CODE" [cause our rainbow color been covered by intellij built-in functionality](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000117450-My-HighlightVisitor-been-covered-by-intellij-built-in-functionality).
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

This plugin will automatically override color scheme property "KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW" cause our rainbow color is being covered by kotlin plugin built-in functionality.
You still could set `<option name="rainbowifyKotlinFunctionLiteralBracesAndArrow" value="false" />` in config file to disable just like [Config brackets colors](#config-brackets-colors).

## Disable rainbow brackets for specific languages

If you want to disable rainbow brackets for javascript languages and java languages, you could set `languageBlacklist` property in config file just like: 

```xml
<application>
  <component name="RainbowSettings">
    <option name="languageBlacklist">
      <array>
        <option value="java" />
        <option value="javascript" />
      </array>
    </option>
  </component>
</application>
```

NOTE: You can use **name** of language or **extension** of file name(The names should be **lowercase**).

## Contribute

NOTE: To view the PSI tree and explore the internal structure of source code, you need to set up your IDE follow [this](https://www.jetbrains.com/help/idea/psi-viewer.html).
For the Rider IDE, please follow: [this](https://rider-support.jetbrains.com/hc/en-us/articles/207327910-How-to-run-JetBrains-Rider-in-Internal-Mode)

* `gradle test`
* `gradle runIde`
* `gradle buildPlugin`

## Support Us

You can support us by the following actions:

* Star this project on GitHub
* Share this plugin with your friends/colleagues
* Rate this plugin on [JetBrains plugin repository](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets)
* Make pull requests
* Report bugs
* Tell us your ideas
* Become a sponsor by donating on [Open Collective](https://opencollective.com/intellij-rainbow-brackets)
* Become a sponsor by donating with AliPay or WeChatPay
* Don't want to use AliPay's mobile App? If you could read Chinese, please use [the official website](https://shenghuo.alipay.com/send/payment/fill.htm) to transfer your donation to my Alipay account(izhangzhihao@hotmail.com). Otherwise, you can try [the English version of Alipay](https://global.alipay.com/) to transfer your donation.

<table>
  <tr>
    <th width="50%">AliPay</th>
    <th width="50%">WeChatPay</th>
  </tr>
  <tr></tr>
  <tr align="center">
    <td><img width="70%" src="https://user-images.githubusercontent.com/12044174/85197261-77dd5a80-b312-11ea-9630-51caf7d634f2.jpg"></td>
    <td><img width="70%" src="https://user-images.githubusercontent.com/12044174/85197263-7b70e180-b312-11ea-917a-35eab2ea08ae.jpg"></td>
  </tr>
</table>

## Backers

Thank you to all our backers! ❤️ [[Become a backer](https://opencollective.com/intellij-rainbow-brackets#backer)]

<a href="https://opencollective.com/intellij-rainbow-brackets#backers" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/backers.svg?width=890"></a>

## Sponsors

Support this project by becoming a sponsor! 🌈 Your logo will show up here with a link to your website. [[Become a sponsor](https://opencollective.com/intellij-rainbow-brackets#sponsor)]

<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/0/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/1/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/2/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/3/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/4/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/5/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/6/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/7/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/8/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/9/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/9/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/10/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/10/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/11/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/11/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/12/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/12/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/13/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/13/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/14/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/14/avatar.svg"></a>
<a href="https://opencollective.com/intellij-rainbow-brackets/sponsor/15/website" target="_blank"><img src="https://opencollective.com/intellij-rainbow-brackets/sponsor/15/avatar.svg"></a>

## Acknowledgements

Intellij-rainbow-brackets is heavily inspired by [Rainbow Brackets for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=2gua.rainbow-brackets)
