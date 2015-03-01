# JBakeUI
A simple UI for JBake

<div style="text-align:center"><img src ="http://razem.io/projects/JBakeUI/screens/20150301_JBakeUI.png" /></div>

# Requirements
* Java Runtime 1.8+
* [JBake](http://jbake.org/download.html) (tested and working with version 2.3.2)
* You need to execute ```jbake -i``` once if you haven't a blog source folder already. This requirement will be removed soon.

# Usage
When you start the UI for the first time you will be asked for two folders:
* JBake distribution folder (where jbake-core.jar is located)
* Source folder of your blog (where your templates and content can be found)

After the initial configuration the information will be saved and you don't need to worry about it anymore. Multi 
project support is planned.

If everything is configured correctly, you may now bake your site, and start the server for a quick preview of your 
changes.

# Tools & Libraries Used

* [MapDB](http://mapdb.org/)

# Copyright & License

Licensed under the MIT License, see the LICENSE file.
