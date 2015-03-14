# Comments #

Comment in the pom-files are also sorted. The comments are bound to the following xml element and as the element moves, the comment follows. Do not place comments after xml elements as they will bound to the the next one.

Example of a nice comment:
```
  <!— This is a comment that is bound to the properties element —>
  <properties>
    <compileSource>1.6</compileSource>
    <my.property>Special value</my.property>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
```
Example of a comment that SortPom does not handle well:
```
  <plugin>
    <version>2.3</version>  <!— This comment is bound to the groupId element (not version) —>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jxr-plugin</artifactId>
  </plugin>
```
Comments will always be placed on their own line after sorting.