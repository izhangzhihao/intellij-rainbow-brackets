
<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets">
        <img src="./src/main/resources/META-INF/pluginIcon.svg" width="320" height="320" alt="logo"/>
    </a>
</div>
<h1 align="center">Intellij Rainbow Brackets</h1>
<p align="center">🌈Rainbow Brackets for IntelliJ-based IDEs/Android Studio/HUAWEI DevEco Studio And <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/blob/2022.3/Fleet.md">Fleet</a></p>

<p align="center">
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/r/stars/10080?style=flat-square" style="height: 30px;"></a>
<a href="https://plugins.jetbrains.com/embeddable/install/10080"><img src="https://img.shields.io/jetbrains/plugin/d/10080-rainbow-brackets.svg?style=flat-square" style="height: 30px;"></a>
<a href="https://plugins.jetbrains.com/plugin/10080-rainbow-brackets"><img src="https://img.shields.io/jetbrains/plugin/v/10080-rainbow-brackets.svg?style=flat-square&label=IntelliJ-based%20IDE" style="height: 30px;"></a>
<a href="https://plugins.jetbrains.com/plugin/23759-rainbow-brackets-for-fleet"><img src="https://img.shields.io/jetbrains/plugin/v/23759?style=flat-square&label=Fleet" style="height: 30px;"></a>
<br/>
<a href="https://www.buymeacoffee.com/rainbowbrackets" target="_blank" title="BuyMeACoffee">
    <img src="https://iili.io/JoQ1MeS.md.png" style="height: 35px;"/>
</a>
<a target="_blank" title="Join our Discord channel" href="https://discord.gg/upKwA9S3zH">
    <img src="https://dcbadge.limes.pink/api/server/upKwA9S3zH" style="height: 35px;"/>
</a>
</p>

<br>

# Table of contents

- [Change log](https://github.com/izhangzhihao/intellij-rainbow-brackets/blob/2022.3/CHANGELOG.md#change-log)
- [Features Matrix](#features-matrix)
- [Compatibility](#compatibility)
- [Install](#install)
- [Screenshots](#screenshots)
- [Config file path](#config-file-path)
- [Fleet Support](https://github.com/izhangzhihao/intellij-rainbow-brackets/blob/2022.3/Fleet.md)
- [Rainbow Brackets Lite](#rainbow-brackets-lite)

## Features Matrix


<table border="1" cellpadding="5" cellspacing="0" >
    <thead>
      <tr>
        <th>Language</th>
        <th>Rainbowify Brackets</th>
        <th>Indent Guidelines Highlighting</br>(<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#indent-highlighting">Free</a> + <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/380#issuecomment-1296938427">Premium options)</a></th>  
        <th>Scope Highlighting</br>(<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#scope-highlighting">Free</a> + <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/discussions/2644">Premium options)</a></th>  
        <th><a href="https://www.youtube.com/watch?v=8WRH59PQ5Dk">Rainbowify Variables</a></th>
        <th>Language-Specific Features</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Java</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#java">✅</a></td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td></td>
      </tr>  
      <tr>  
        <td>Scala</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#scala">✅</a></td>
        <td>✅ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2808">✨Premium for braceless style</a></td>
        <td>✅</td>
        <td>✅</td>
        <td></td>
      </tr>  
      <tr>  
        <td>Kotlin</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#kotlin">✅</a></td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#kotlin-function-literal-braces-and-arrow">Function literal braces and arrow highlighting</a></td>
      </tr>  
      <tr>  
        <td>Python</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#python">✅</a></td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#python">Premium</a></td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/discussions/2643">Premium</a></td>
        <td>✅</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#python">Keyword highlighting</a></td>
      </tr>  
      <tr>  
        <td>JavaScript/TypeScript</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#javascript--typescript">✅</a></td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#javascript--typescript">JSX support</a></td>
      </tr>  
      <tr>  
        <td>HTML/XML</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#htmlxml">✅</a></td>
        <td>✅</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2712">Premium</a></td>
        <td>✅ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#htmlxml">Tag name highlighting</a></td>
        <td></td>
      </tr>
      <tr>  
        <td>Vue.js</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#htmlxml">✅</a></td>
        <td>✅</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2712">Premium</a></td>
        <td>-</td>
        <td></td>
      </tr>
      <tr>  
        <td>C#</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#c">Premium</a></td>
        <td>✅</td>
        <td>✨ Premium</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2570">Premium</a></td>
        <td>Support .shader .cginc .hlsl .cshtml GDScript etc.</td>
      </tr>  
      <tr>  
        <td>F#</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/186">✨</a> <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/186">Premium</a></td>
        <td>✨ Premium</td>
        <td>-</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/186">Premium</a></td>
        <td></td>
      </tr>  
      <tr>  
        <td>C++ (CLion Classic)</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td></td>
      </tr>
      <tr>  
        <td>C++ (CLion Nova/<br>IntelliJ 2025.3+)</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/424#issuecomment-1377176193">✨</a> <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/424#issuecomment-1377176193">Premium in Rider</a></td>
        <td>✨<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2762">Premium</a></td>
        <td>✨<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2846">Premium</a></td>
        <td>✨<a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2846">Premium</a></td>
        <td></td>
      </tr>  
      <tr>  
        <td>YAML</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#yaml">✅</a></td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#yaml">Premium</a></td>
        <td>✅</td>
        <td>✅ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#yaml">Tag name highlighting</a></td>
        <td></td>
      </tr>
      <tr> 
        <td>Jinja2</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2677">✨</a> <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2677">Premium</a></td>
        <td>✨ Premium</td>
        <td>-</td>
        <td>-</td>
        <td></td>
      </tr>
      <tr>  
        <td>Pug/Jade</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#pugjade">✅</a></td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#pugjade">Premium</a></td>
        <td>-</td>
        <td>-</td>
        <td></td>
      </tr>  
      <tr>  
        <td>Dart</td>
        <td><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#dart">✅</a></td>
        <td>✅</td>
        <td>✅</td>
        <td>✨ <a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2741">Premium</a></td>
        <td></td>
      </tr>  
      <tr>
        <td>Rust</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td></td>
      </tr>
      <tr>
        <td>Go</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td>✅</td>
        <td></td>
      </tr>
    </tbody>  
</table>

Other Premium features:

<ul dir="auto">
    <li><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#use-the-color-generator">Color generator advanced options</a></li>
    <li><a href="https://user-images.githubusercontent.com/12044174/202852094-2da6945b-598e-4def-ab0c-331abdd6d3f8.png">Color generator for rainbow variables</a></li>
    <li><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/2526">Show and save generated colors</a></li>
    <li><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#customize-colors">Config up to 10 colors in config panel</a></li>
</ul>

<small>Other supported languages: Haskell, Agda, Erlang, Groovy, Ruby, Elixir, Objective-C, PHP, SQL, Apex language, Bash, C# Razor Pages, GLSL (the OpenGL Shading Language), Go Template, Solidity, MDX, FreeMarker, GDScript, Svelte and more.</small>

## Compatibility

IntelliJ IDEA(Ultimate/Community/Educational), PhpStorm, WebStorm, PyCharm(Professional/Community/Educational), RubyMine, AppCode, CLion, CLion(Nova), Gogland, DataGrip, Rider, MPS, Android Studio, HUAWEI DevEco Studio, DataSpell, Code With Me(Host), RustRover, Aqua, Fleet, WriterSide

## Install

<a href="https://plugins.jetbrains.com/embeddable/install/10080">
    <img src="https://user-images.githubusercontent.com/12044174/123105697-94066100-d46a-11eb-9832-338cdf4e0612.png" width="300"/>
</a>

### Install it inside your IDE:

For Windows & Linux - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd> > <kbd>Restart IntelliJ IDEA</kbd>

For Mac - <kbd>IntelliJ IDEA</kbd> > <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Rainbow Brackets"</kbd> > <kbd>Install Plugin</kbd>  > <kbd>Restart IntelliJ IDEA</kbd>

## Screenshots

### Java

<img width="640" alt="java" src="https://user-images.githubusercontent.com/12044174/218252098-d73f3be1-849f-4193-a095-105b639d1955.png">


### Scala

<img width="640" alt="scala" src="https://user-images.githubusercontent.com/12044174/218252110-409c8dc0-3893-42c0-97f8-5855f88728bf.png">


### Clojure

<img width="640" alt="Clojure" src="https://user-images.githubusercontent.com/12044174/218252123-b2c2ae55-0501-46d5-81c0-4208f19958b3.png">

### Kotlin

<img width="640" alt="kotlin" src="https://user-images.githubusercontent.com/12044174/218252132-f6b0d17a-9d3b-47bc-be0c-918531ffb050.png">

### HTML/XML

**NOTE: need to turn on the 'Rainbowify tag name' option**

<img width="640" alt="HTML" src="https://user-images.githubusercontent.com/12044174/218252140-926fb1cc-5836-4e9e-9a23-5d0a4d956c3c.png">

### Javascript & Typescript

**NOTE: need to turn on the 'Rainbowify tag name' option**

<img width="640" alt="js" src="https://user-images.githubusercontent.com/12044174/218252159-06337dbb-ee7a-47a6-92f7-64e29f7419da.png">
<img width="640" alt="ts" src="https://user-images.githubusercontent.com/12044174/218252164-1a0547a6-a423-4a07-aa80-5200a5660aa6.png">

### C#

<img width="640" alt="CSharp" src="https://user-images.githubusercontent.com/12044174/218252203-24057a01-79ba-44e5-834d-ac4414472583.png">

### Dart

<img width="640" alt="dart" src="https://user-images.githubusercontent.com/12044174/218252217-e526703d-d644-4e51-bb17-1273f3591dc4.png">

### Python

<img width="623" alt="image" src="https://user-images.githubusercontent.com/12044174/220900164-db6476ed-e4e8-4547-966a-2db89fe82651.png">

<img width="640" alt="python" src="https://user-images.githubusercontent.com/12044174/218252230-5e21ef7c-acc4-49be-80ff-8c8ed11538ad.png">

<img width="640" alt="image" src="https://github.com/izhangzhihao/intellij-rainbow-brackets/assets/12044174/e6fc7fe6-7cb5-448f-87f2-5411038fe32c">


### Pug/Jade

<img width="640" alt="pug" src="https://user-images.githubusercontent.com/12044174/218252187-1d581616-1b4a-4654-8d97-5fe9e9a505cf.png">

### YAML

To disable rainbowify tags for yaml, please follow: https://github.com/izhangzhihao/intellij-rainbow-brackets/discussions/2639#discussioncomment-6106439
<img width="640" alt="pug" src="https://github.com/izhangzhihao/intellij-rainbow-brackets/assets/12044174/b484249a-57a8-4335-9681-1e024199681f">


### Indent highlighting

options:

<img width="800" alt="image" src="https://github.com/izhangzhihao/intellij-rainbow-brackets/assets/12044174/08b2a707-176f-437d-852c-1052d5652d7f">

* Focus mode(premium)

<img width="800" alt="image" src="https://github.com/izhangzhihao/intellij-rainbow-brackets/assets/12044174/68f829c4-7c86-4b6b-b7f6-b68911c37097">

* Only selected indent guide(free)

highlighting indent guideline only when you select this indent line
<img width="800" alt="image" src="https://user-images.githubusercontent.com/12044174/229687079-6b66e842-7d60-426e-9741-9685f8d078aa.png">

* Only current indent guide(premium)

highlighting indent guide lines for current lines
<img width="800" alt="image" src="https://user-images.githubusercontent.com/12044174/198995710-a7956ae3-b008-41f0-a391-caf5a39cd8dd.png">

* All indent guide related(premium)

highlighting **all** indent guide lines for current lines
<img width="800" alt="image" src="https://user-images.githubusercontent.com/12044174/198984655-aedcec69-58c3-465f-b06a-279fd35068bd.png">

### Scope highlighting

**The highlight effects will not remove after releasing the shortcuts, but press the `ESC` key can do this. You could also config `Press any key to remove the highlighting effect` on the setting page.**

<kbd>Ctrl + Button3</kbd>(Windows & Linux) or <kbd>Meta + Button3</kbd>(Mac):

NOTE: <kbd>Button3</kbd> means "Secondary Click (click or tap with two fingers)" on Mac os, "Right click" for Windows or Linux.
NOTE: <kbd>Meta</kbd> means <kbd>command</kbd> on Mac os.

<img width="1000" alt="current-scope" src="https://user-images.githubusercontent.com/12044174/218252269-52373432-4054-4449-91f9-a968337b81ab.png">

![](https://user-images.githubusercontent.com/10737066/40234968-46593fe2-5adb-11e8-8ea8-0026fad86ca9.gif)

<kbd>Alt + Button3</kbd>(Windows & Linux) or <kbd>option + Button3</kbd>(Mac):

NOTE: <kbd>Button3</kbd> means "Secondary Click (click or tap with two fingers)" on Mac os, "Right click" for Windows or Linux.

<img width="1000" alt="rh" src="https://user-images.githubusercontent.com/12044174/218252282-305ece2c-e78a-453a-8558-e500d8d35c7d.png">

![](https://user-images.githubusercontent.com/10737066/40235004-642dfe54-5adb-11e8-9fd7-648b92fab8f5.gif)

## Customize colors

<kbd>Settings/Preferences</kbd> > <kbd>Editor</kbd> > <kbd>Color Scheme</kbd> > <kbd>Rainbow Brackets</kbd>:

<img width="849" alt="scheme" src="https://user-images.githubusercontent.com/12044174/218252304-5e22918f-5ab9-4bc2-8bfa-65d4614fc303.png">

## Config the number of colors?

<kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > <kbd>Color</kbd> > `Number of colors`: 5 or more

And you can use the color generator and configure your number of colors at the same time.

NOTE: For the default and Darcula color scheme(`Editor -> Color Scheme -> Rainbow Brackets -> Scheme`) the color number is 10, for the other scheme the number is 5, if your number is bigger than the number, you can config them in the config file.
If the color does not exist, we will use the color generator to generate it for you.

## Use the color generator

If you didn't like the built-in colors or want some new colors but don't want to try out and pick colors. Please use the color generator.

<kbd>Settings/Preferences</kbd> > <kbd>Rainbow Brackets</kbd> > <kbd>Color</kbd> > <kbd>Use color generator</kbd>

If you turn on this option, we will auto-generate some colors for you.

### Advanced options for color generator

<img width="749" alt="image" src="https://user-images.githubusercontent.com/12044174/202852094-2da6945b-598e-4def-ab0c-331abdd6d3f8.png">

```hue``` – Controls the hue of the generated color. You can pass a string representing a color name: ```red```, ```orange```, ```yellow```, ```green```, ```blue```, ```purple```, ```pink``` and ```monochrome``` are currently supported. If you pass a  hexidecimal color string such as ```#00FFFF```, the color generator will extract its hue value and use that to generate colors.

```luminosity``` – Controls the luminosity of the generated color. You can specify a string containing ```bright```, ```light``` or ```dark```.

## Config file path

If you want to customize the advanced configuration, you could edit the config file and then restart your IDE. 
The config file path is in `APP_CONFIG/rainbow_brackets.xml`. 

In MAC OS env maybe like `~/Library/Preferences/IntelliJIdea2020.2/options/rainbow_brackets.xml`.

If you are using the ToolBox, then it will be like `~/Library/ApplicationSupport/JetBrains/IntelliJIdea2020.2/options/rainbow_brackets.xml`

In Linux env maybe like `~/.IntelliJIdea/config/options/rainbow_brackets.xml`.

In Windows env maybe like `C:\Users\izhangzhihao\.IntelliJIdea2020.2\config\options\rainbow_brackets.xml`.

## JSX support

To enable rainbow brackets for JSX like this:

```javascript
var html = '<div><div><div>Hello</div></div></div>';
```

This plugin will automatically override color scheme property "HTML_CODE" [cause our rainbow color been covered by intellij built-in functionality](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000117450-My-HighlightVisitor-been-covered-by-intellij-built-in-functionality).
You still could set <kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > <kbd>Language Specific</kbd> > <kbd>Rainbowify JSX</kbd> in the config file to disable.

## Kotlin function literal braces and arrow

To enable rainbow brackets for multiple-level lambda Kotlin code like this:

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

This plugin will automatically override the color scheme property "KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW" cause our rainbow color is being covered by the Kotlin plugin's built-in functionality.
You still could set <kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > <kbd>Language Specific</kbd> > <kbd>Rainbowify Kotlin function literal braces and arrow</kbd> in the config page to disable.

## Disable rainbow brackets for specific languages

<kbd>Settings/Preferences</kbd> > <kbd>Other Settings</kbd> > <kbd>Rainbow Brackets</kbd> > `Do NOT rainbowify these languages (name or extension, comma separated)`: 

NOTE: You can use **name** of language or **extension** of file name(The names should be **lowercase**).

## Support Us

You can support us by the following actions:

* [Buy a license](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets/pricing#tabs)
* Star this project
* Share this plugin with your friends
* Rate this plugin on [JetBrains plugin repository](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets)
* Report bugs
* Tell us your ideas
* [Buy Me a Coffee](https://buymeacoffee.com/rainbowbrackets)

## Rainbow Brackets Lite

This repo is used to compile a fully free version of our product for the community to use. This is because some users do not need the paid features and do not want to constantly update their software. An open-source, entirely free, and stable version is a good option for these users.

I believe this will benefit both the community and our team. The community will have access to a free version of our software, and our team will continue to receive support from those who appreciate and value the paid features.

Please download the Lite version [here](https://plugins.jetbrains.com/plugin/20710).

**NOTE: If you need C# or C++ support in Rider IDE, these features are only available in the paid version. The Lite version does not include them. Upgrade to the paid version for full functionality**

## Acknowledgements

Intellij-rainbow-brackets is heavily inspired by [Rainbow Brackets for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=2gua.rainbow-brackets)
