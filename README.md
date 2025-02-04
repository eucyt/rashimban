# Rashimban

![Build](https://github.com/eucyt/rashimban/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26256.svg)](https://plugins.jetbrains.com/plugin/26256)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26256.svg)](https://plugins.jetbrains.com/plugin/26256)

> Never get lost in the jungle of code again.

<!-- Plugin description -->
This plugin visualizes your navigation between files when jumping through code.

With this tool, you can easily track which file you came from and where you are now, even in projects with complex dependencies.

- Display a diagram showing the filenames of the source and destination when go to a declaration or usage.
- Left-clicking a filename in diagram jumps to that file.
- Right-clicking a filename in diagram removes it.
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "rashimban"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/26256) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/26256/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/eucyt/rashimban/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Author

[@eucyt](https://euchi.jp)

## FAQ

### How do I build and run this project?

For development purpose, clone the project locally and start it with the command

`./gradlew runIde`

This will build the plugin and start an Instance of IntelliJ with the plugin already installed.
You can even start this in debug mode.