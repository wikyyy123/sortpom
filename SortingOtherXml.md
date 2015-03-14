# Sorting Xml #

Theoretically it should be possible to sort other xml files with the sortpom plugin.

Very long time ago, I worked with an application server that resorted its own configuration file every time it started which meant it was very hard to have a library of setup files and compare them to each other. Modern application servers don't do this anymore, right?

Some time ago, I worked with spring configuration files (no autowire) for an enterprise system. It would have been nice to automatically place certain elements first in those files.
But now everything is autowired and modern frameworks are are designed with 'Convention over configuration', right?

Anyway, it you decide to sort other xml files then feel free to experiment with these parameters:
```
 <pomFile>myFile.xml</pomFile>
 <sortOrderFile>sortSchemaOrder.xml</sortOrderFile>
```
Let me know if you have tried the plugin with some other xml and how it worked out. Just add an [Issue](http://code.google.com/p/sortpom/issues/list)