# Pom #

Place the following in you pom-file.

```
...
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    ...
  </properties>

...
  <build>
    <plugins>
      <plugin>
        <groupId>com.google.code.sortpom</groupId>
        <artifactId>maven-sortpom-plugin</artifactId>
        <version>2.2.1</version>
        <configuration>
          <predefinedSortOrder>custom_1</predefinedSortOrder>
          <lineSeparator>\n</lineSeparator>
          <encoding>${project.build.sourceEncoding}</encoding>
          <sortProperties>true</sortProperties>
          <keepBlankLines>true</keepBlankLines>
          <sortDependencies>scope</sortDependencies>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>sort</goal>
            </goals>
            <phase>verify</phase>
          </execution>
        </executions>
      </plugin>

        ...
    </plugins>
   
    ...
</build>
...
```

# What will it do? #

  * Keep your pom-file sorted every time you compile with maven
  * Use a predefined sort order which I prefer myself (parent, basic info and properties on top; dependencies and plugins in the middle; other info and profiles last)
  * Use unix-like line endings in the pom-file
  * Use the explicit encoding UTF-8
  * Sort the pom properties
  * Keep blank lines (but not indent them)
  * Sort dependencies by scope (but not by groupId or artifactId)