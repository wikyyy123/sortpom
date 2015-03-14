# How does it work #

The dependencies in a pom file can be sorted alphabetically and the parameter sortDependencies decides how it should do that.

Example:
> sortDependencies=groupId,artifactId will sort the dependencies first by groupId and then by artifactId (if the groupId is the same).

The argument list is the same as the child elements of the dependency element. The list is separated by ; : or , and it is not recommended to contain space.

# Child element: Scope #

If scope is specified in the list, then scope is not sorted alphabetically. It has the following sort order: COMPILE (or empty), PROVIDED, SYSTEM, RUNTIME, IMPORT and TEST

# Dependency Management #

The plugin does not know anything about Maven infrastructure, so if dependency management is used, it can ruin the sorting.

Example:
> Dependency management has been used to specify scope for the most common dependencies and some dependencies do not have scope specified.
```
   <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.10</version>
        <scope>test</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-all</artifactId>
      <version>1.1</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
  </dependencies>
```
> If the plugin has the parameter sortDependencies=scope,artifactId then the junit dependency will be sorted before hamcrest because it is treated as having no scope (default: compile scope). The plugin does not know that the scope is provided by dependency management.

The solution to this is either not to sort by scope or to omit scope in dependency management.

# Version 2.0.0 and earlier #

Earlier versions of the plugin had a boolean argument:
> true means 'groupId,artifactId'

> false means empty list.

# The parameter sortDependencies can affect compilation #
Please use this option with caution. Maven reads dependencies according to the order in the pom-file when compiling. Rearranging the order may affect the compilation output.