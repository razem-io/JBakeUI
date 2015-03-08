# JBakeUI
A simple UI for JBake. Please note the software is still in the early stages. Expect thunderstorms and wild fire!

<div style="text-align:center"><img src ="http://razem.io/projects/JBakeUI/screens/20150308_JBakeUI.png" /></div>

# Requirements
* Java Runtime 1.8+
* [JBake](http://jbake.org/download.html) (tested and working with version 2.3.2)

# Usage
You can download a compiled version of JBakeUI [here](https://github.com/razem-io/JBakeUI/wiki/Download).

Once downloaded extract the archieve and navigate to the bin folder. Depending on your system start ```JBakeUI.bat``` (Windows) or ```JBakeUI``` (Linux and Mac).

After the initial configuration the information will be saved and you don't need to worry about it anymore. Multi 
project support is planned.

If everything is configured correctly, you may now bake your site and start the server. Once the server is started you can open a browser and navigate to http://localhost:8820 to have a quick preview of your blog/site.

#Build instructions
This project uses gradle. Open a terminal navigate to the root directory of the project and execute ```gradle distZip```. You then will find a brand new copy of JBakeUI in ```build/distributions/JBakeUI-*.zip```.

# Tools & Libraries Used

* [MapDB](http://mapdb.org/)
* [Guava](https://github.com/google/guava)
* [zt-zip](https://github.com/zeroturnaround/zt-zip)

# Copyright & License

Licensed under the MIT License, see the LICENSE file.
