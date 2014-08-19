#Developer Guide

 * install the intellij sdk
 * install necessary plugins Grammar-kit, Plugin-Devkit, PsiViewer
  http://confluence.jetbrains.com/display/IntelliJIDEA/Prerequisites
 * Create a run configuration (Plugin -> Use Classpath of module -> Choose the current module), hit run
 * A new instance of intellij will be launched with the plugin installed  

##Lexing and parsing

 * The lexing rules are defined in _SpecLexer.flex and _SpecLexer.java is generated using JFlex
 * The grammar rules are defined in specification.bnf and SpecParser.java is generated using grammar kit
   SpecParser builds an structure with PsiElements and we can add custom behavior for each element (eg step has get specstep has getStepValue)   
 
##Available Features

 * Project Creation
 * Syntax Highlighting
 * Auto completion
 * Navigation from step to implementation
 * Quick Fix for unimplemented steps


##Interacting with the core

Auto completion,step value extraction and other api calls are made from the plugin. Each module is associated with an api connection(GaugeConnection). 
This Gauge connection is created during the module initialization phase after the gauge process is started in background. Gauge connection talks to the core using protobuff. 


##Debugging specs

* Create a remote run configuration which attaches on port 50005
* Right click on the spec -> Choose Debug specification (this will wait for a debugger to get attached, currently waits for 30 seconds)
* Choose the previously created remote run configuration and run it (this will attach to the gauge process)


##Some useful links

[Antlr guy's tips](https://theantlrguy.atlassian.net/wiki/display/~admin/Intellij+plugin+development+notes)

[Psi Cook book](https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook)
     