# Maven-SortPom-Plugin #
Maven plugin that helps the user sort pom.xml. The default sort order is taken from the [Maven3 pom documentation](http://maven.apache.org/ref/3.0.3/maven-model/maven.html).
The main advantages to have standardized sorted poms are that they become more readable and that comparisons between different module poms becomes much easier.

## News ##
  * 2015-02-04: Received an Open Source license for Structure101. Thank you [Structure101](http://structure101.com/)!
  * 2014-08-11: Received an Open Source license for Araxis Merge. Thank you [Araxis](http://www.araxis.com/)!
  * 2014-04-18: Released [Echo-maven-plugin](https://code.google.com/p/echo-maven-plugin/), another Maven plugin. Please check it out!
  * 2014-02-05: Renewed Open Source Licence for IntelliJ Ultimate. Once again, thank you [JetBrains](http://www.jetbrains.com/idea/)!!
  * 2014-01-28: Released version 2.3.0 which lets you [ignore sections](IgnoringSections.md) in pom.xml where SortPom should not sort elements.
  * 2013-11-23: Hurrah! Got a donation. Thank you Bastian!
  * 2013-10-13: Released version 2.2.1 which corrects line separators when using xml:space="preserve"
  * 2013-10-01: Hurrah! Got an donation. Thank you Khalid!
  * 2013-08-22: Released version 2.2 which contains functionality to use an url as sortOrderFile. This means that you can have a centralized, version controlled, sort order file for multiple projects.
    * 2012-12-22: Split the project into three modules: one for sorting, one for maven plugin support and one for test utils.
  * 2012-11-05: Hurrah! Got an donation. Thank you Michael!
  * 2012-09-23: Released version 2.1.0. The parameters sortDependencies and sortPlugins are remade to allow for much greater flexibility when sorting dependencies or plugins.
  * 2012-08-28: Released version 2.0.0. Added new goal to verify that the xml elements in the pom are sorted without looking at xml formatting.
  * 2012-06-12: Hurrah! Got an donation. Thank you Reuben!
  * 2011-12-10: The 1.0.0 plugin has changed the default sort order. To use the sort order for plugin 0.4.0 and earlier, please use parameter `<`predefinedSortOrder`>`default\_0\_4\_0`<`/predefinedSortOrder`>`
  * 2011-11-21: The 0.4.0 plugin is now in the Central repository. It is highly recommended to use the 0.4.0 version or later from Central from now on. There is no need to include an extra repository location.

## Goals Overview ##
The SortPom Plugin has two goals.

  * **mvn com.google.code.sortpom:maven-sortpom-plugin:sort** sorts the current pom.xml file. This goal will always sort the pom.xml file.

  * **mvn com.google.code.sortpom:maven-sortpom-plugin:verify** only sorts the current pom.xml file if the xml elements are unsorted. This goal ignores text formatting (such as indentation and line breaks) when it verifies if the pom is sorted or not.

## Command-line ##
If you want to run the SortPom plugin from commandline, it will be more convenient if you add a Plugin group to your general Maven Settings. To do this, open up  ~/.m2/settings.xml and add the following plugin group:

```
<settings>
	...
	<pluginGroups>
  		<pluginGroup>com.google.code.sortpom</pluginGroup>
	</pluginGroups>
	...
</settings>
```
You can then omit the full specification for the plugin and run

  * **mvn sortpom:sort**

  * **mvn sortpom:verify**

## Parameters ##

### For both the Sort and Verify goal ###

| **Commandline example** | **Configurationtag example** | **Default value** | **Description** |
|:------------------------|:-----------------------------|:------------------|:----------------|
| ` -Dsort.pomFile="myPom.xml" ` | ` <pomFile>myPom.xml</pomFile> ` | ` pom.xml ` | Location of the pomfile |
| ` -Dsort.encoding="ASCII" ` | ` <encoding>ASCII</encoding> ` | ` UTF-8 ` | Encoding for the files |
| ` -Dsort.predefinedSortOrder="custom_1" ` | ` <predefinedSortOrder>custom_1</predefinedSortOrder> ` |  [default\_1\_0\_0](http://code.google.com/p/sortpom/source/browse/sorter/src/main/resources/default_1_0_0.xml) | Select from [a number of predefined sort profiles](PredefinedSortOrderProfiles.md) if you cannot be bothered to use a sortOrderFile and the default sort order does not match. |
| ` -Dsort.sortOrderFile= "src/main/resources/customSortOrder.xml" ` | ` <sortOrderFile>src/main/resources/customSortOrder.xml</sortOrderFile> or <sortOrderFile>https://sortpom.googlecode.com/git/custom_1.xml</sortOrderFile> ` | none | Custom sort order file read from either executing path, classpath or as URL. [File example](http://code.google.com/p/sortpom/source/browse/src/main/resources/default_1_0_0.xml) |
| ` -Dsort.sortDependencies=scope,artifactId ` | ` <sortDependencies>scope,artifactId</sortDependencies> ` | ` none ` | Comma-separated ordered list how dependencies should be sorted. [Sort mechanism](SortDependencies.md) [Warning](SortDependenciesWarning.md) |
| ` -Dsort.sortPlugins=groupId,artifactId ` | ` <sortPlugins>groupId,artifactId</sortPlugins> ` | ` none ` | Comma-separated ordered list how plugins should be sorted. [Sort mechanism](SortPlugins.md) [Warning](SortPluginsWarning.md) |
| ` -Dsort.sortProperties=true ` | ` <sortProperties>true</sortProperties> ` | ` false ` | Should the Maven pom properties be sorted alphabetically. Affects both project/properties and project/profiles/profile/properties |
| ` -Dsort.createBackupFile=false ` | ` <createBackupFile>false</createBackupFile> ` | ` true ` | Should a backup copy be created before sorting the pom |
| ` -Dsort.backupFileExtension=".old" ` | ` <backupFileExtension>.old</backupFileExtension> ` | ` .bak ` | Name of the file extension for the backup file |
| ` -Dsort.lineSeparator="\n" ` | ` <lineSeparator>\n</lineSeparator> ` | [line.separator ](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#getProperties%28%29) | Line separator for sorted pom. Can be either \n, \r or \r\n |
| ` -Dsort.expandEmptyElements=false ` | ` <expandEmptyElements>false</expandEmptyElements> ` | ` true ` | Should empty xml elements be expanded. `<configuration></configuration>` or `<configuration/>` |
| ` -Dsort.keepBlankLines=true ` | ` <keepBlankLines>true</keepBlankLines> ` | ` false ` | Should blank lines in the pom-file be perserved. A maximum of one line is preserved between each tag. |
| ` -Dsort.nrOfIndentSpace=4 ` | ` <nrOfIndentSpace>4</nrOfIndentSpace> ` | ` 2 ` | Number of space characters to use as indentation. A value of -1 indicates that a tab character should be used instead |
| ` -Dsort.indentBlankLines=true ` | ` <indentBlankLines>true</indentBlankLines> ` | ` false ` | Should blank lines (if preserved) have indentation. |
| ` -Dsort.skip=true ` | ` <skip>true</skip> ` | ` false ` | Set this to 'true' to bypass sorting of the pom.xml completely. |

### For the Verify goal ###

| **Commandline example** | **Configurationtag example** | **Default value** | **Description** |
|:------------------------|:-----------------------------|:------------------|:----------------|
| ` -Dsort.verifyFail="Stop" ` | ` <verifyFail>Stop</verifyFail> ` | ` Sort ` | Can be either [Sort, Stop or Warn](verifyFail.md) |

## Example ##
Maven users can add this plugin with the following addition to their pom.xml file. This will make sure that you pom file is sorted every time you compile the project.

```
...
<build>
  <plugins>
    <plugin>
      <groupId>com.google.code.sortpom</groupId>
      <artifactId>maven-sortpom-plugin</artifactId>
      <version>2.3.0</version>
      <executions>
        <execution>
          <goals>
            <goal>sort</goal>
          </goals>
          <phase>verify</phase>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
...
```

For a more complex example, have a look at my [recommended configuration](recommended_configuration.md)

Note: [Sorting other xml](SortingOtherXml.md)

## Ignoring Sections ##
The pom.xml can be enhanced so that sections of the pom can be excluded from the sorting process.
[IgnoringSections](IgnoringSections.md)

## Comments ##
Comments in the pom.xml are also sorted. They are bound to follow the following xml element.
[Comments](Comments.md)

## Version History ##
[Versions](Versions.md)

## Download ##
The plugin is hosted i [Maven Central](http://mvnrepository.com/artifact/com.google.code.sortpom/maven-sortpom-plugin) and will be downloaded automatically if you include it as a plugin in your pom file.

## Donations ##
If you use it, then please consider some encouragement.

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=JB25X84DDG5JW&lc=SE&item_name=Encourage%20the%20development&item_number=sortpom&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

[![](http://api.flattr.com/button/flattr-badge-large.png)](http://flattr.com/thing/439567/maven-sortpom-plugin)