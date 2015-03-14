# How does it work #

The plugins in a pom file can be sorted alphabetically and the parameter sortPlugins decides how it should do that.

Example:
> sortPlugins=groupId,artifactId will sort the plugins first by groupId and then by artifactId (if the groupId is the same).

The argument list is the same as the child elements of the plugin element. The list is separated by ; : or , and it is not recommended to contain space.

# Default groupId #

If groupId is omitted when specifying a plugin in a pom file, then it is presumed that the groupId is 'org.apache.maven.plugins' just as Maven would.

# Plugin Management #

Small problem: The plugin does not know anything about Maven infrastructure, so if plugin management is used, it can ruin the sorting. As long as you only use groupId and artifactId this should not be a problem. I will extend this page if somebody actually has a problem with this.

The problem affects sortDependencies more but that is [another](SortDependencies.md) wiki page.

# ReportPlugins #

Report plugins can be sorted, but unfortunately the element maven-site-plugin->configuration->reportPlugins is not part of the standard maven pom specification. You need to use a custom sort order file to sort the report plugins. The sort order file must contain the following snippet to sort report plugins.

Download the source code. Pick a default sort order file that you like and save it as your own sort order file. Replace the element plugins->plugin->configuration with
```
        <configuration>
          <reportPlugins>
            <plugin>
              <groupId/>
              <artifactId/>
              <version/>
              <configuration>
              </configuration>
              <reports>
                <report/>
              </reports>
            </plugin>
          </reportPlugins>            
        </configuration>
```

Then run the plugin with mvn sortpom:sort -Dsort.sortOrderFile=<your own file here>

There is a unit test in the source code that uses a custom sort order file that I have made. Use it for inspiration: custom\_report\_plugins.xml

# Version 2.0.0 and earlier #

Earlier versions of the plugin had a boolean argument:
> true means 'groupId,artifactId'

> false means empty list.

# The parameter sortPlugins can affect compilation #

If two plugins executes in the same phase, the order in pom-file will determine which plugin to execute first. Sorting the plugins may cause the compilation to fail if the result of one plugin is dependent on another.

If you have replaced an ant-script with the antrun plugin tasks, you know what I mean.