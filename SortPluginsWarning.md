## The parameter sortPlugins can affect compilation ##

If two plugins executes in the same phase, the order in pom-file will determine which plugin to execute first. Sorting the plugins may cause the compilation to fail if the result of one plugin is dependent on another.

If you have replaced an ant-script with the antrun plugin tasks, you know what I mean.