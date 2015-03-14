# Maven-SortPom-Plugin #
Maven plugin that helps the user sort pom.xml. The default sort order is taken from the [Maven3 pom documentation](http://maven.apache.org/ref/3.0.3/maven-model/maven.html).
The main advantages to have standardized sorted poms are that they become more readable and that comparisons between different module poms becomes much easier.

## News ##
  * 2012-08-28: Released version 2.0.0. Added new goal to verify that the xml elements in the pom are sorted without looking at xml formatting.
  * 2012-06-29: Started to work on a new goal 'verify'
  * 2012-06-19: Released version 1.3.2. Fixed a bug where text was deleted if surrounding tags was not the on same line.
  * 2012-06-17: Released version 1.3.1 with a new parameter to control indentation for blank lines
  * 2012-06-12: Hurrah! Got an donation. Thank you Reuben!
  * 2012-05-24: Released version 1.3.0 with a new parameter to keep blank lines in the pom-file.
  * 2012-02-29: Received an Open Source Licence for JRebel. Thank you [ZeroTurnaround](http://zeroturnaround.com/jrebel/)!
  * 2012-02-22: Released version 1.2.1. The plugin is now officially thread safe.
  * 2012-02-17: Received an Open Source Licence for IntelliJ Ultimate. Thank you [JetBrains](http://www.jetbrains.com/idea/)!
  * 2012-02-16: Released version 1.2.0 with a new parameter to sort properties.
  * 2011-12-10: The 1.0.0 plugin has changed the default sort order. To use the sort order for plugin 0.4.0 and earlier, please use parameter `<`predefinedSortOrder`>`default\_0\_4\_0`<`/predefinedSortOrder`>`
  * 2011-11-21: The 0.4.0 plugin is now in the Central repository. It is highly recommended to use the 0.4.0 version or later from Central from now on. There is no need to include an extra repository location.

## Goals Overview ##
The SortPom Plugin has two goals.

  * **mvn com.google.code.sortpom:maven-sortpom-plugin:sort** sorts the current pom.xml file. This goal will always sort the pom.xml file.

  * **mvn com.google.code.sortpom:maven-sortpom-plugin:verify** sorts the current pom.xml file if the xml elements are unsorted. This goal ignores text formatting (such as indentation and line breaks) when it verifies if the pom is sorted or not.

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
| ` -Dsort.predefinedSortOrder="custom_1" ` | ` <predefinedSortOrder>custom_1</predefinedSortOrder> ` |  [default\_1\_0\_0](http://code.google.com/p/sortpom/source/browse/trunk/src/main/resources/default_1_0_0.xml) | Select from [a number of predefined sort profiles](PredefinedSortOrderProfiles.md) if you cannot be bothered to use a sortOrderFile and the default sort order does not match. |
| ` -Dsort.sortOrderFile= "src/main/resources/customSortOrder.xml" ` | ` <sortOrderFile>src/main/resources/customSortOrder.xml</sortOrderFile> ` | none | Custom sort order file read from either executing path or classpath. [File example](http://code.google.com/p/sortpom/source/browse/trunk/src/main/resources/default_1_0_0.xml) |
| ` -Dsort.sortDependencies=true ` | ` <sortDependencies>true</sortDependencies> ` | ` false ` | Should all the dependencies be sorted, by groupId and artifactId, in alphabetic order. [Varning](SortDependenciesVarning.md) |
| ` -Dsort.sortPlugins=true ` | ` <sortPlugins>true</sortPlugins> ` | ` false ` | Should all the plugins be sorted, by groupId and artifactId, in alphabethic order. [Varning](SortPluginsVarning.md) |
| ` -Dsort.sortProperties=true ` | ` <sortProperties>true</sortProperties> ` | ` false ` | Should the Maven pom properties be sorted alphabetically. Affects both project/properties and project/profiles/profile/properties |
| ` -Dsort.createBackupFile=false ` | ` <createBackupFile>false</createBackupFile> ` | ` true ` | Should a backup copy be created before sorting the pom |
| ` -Dsort.backupFileExtension=".old" ` | ` <backupFileExtension>.old</backupFileExtension> ` | ` .bak ` | Name of the file extension for the backup file |
| ` -Dsort.lineSeparator="\n" ` | ` <lineSeparator>\n</lineSeparator> ` | [line.separator ](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#getProperties%28%29) | Line separator for sorted pom. Can be either \n, \r or \r\n |
| ` -Dsort.expandEmptyElements=false ` | ` <expandEmptyElements>false</expandEmptyElements> ` | ` true ` | Should empty xml elements be expanded. `<configuration></configuration>` or `<configuration/>` |
| ` -Dsort.keepBlankLines=true ` | ` <keepBlankLines>true</keepBlankLines> ` | ` false ` | Should blank lines in the pom-file be perserved. A maximum of one line is preserved between each tag. |
| ` -Dsort.nrOfIndentSpace=4 ` | ` <nrOfIndentSpace>4</nrOfIndentSpace> ` | ` 2 ` | Number of space characters to use as indentation. A value of -1 indicates that a tab character should be used instead |
| ` -Dsort.indentBlankLines=true ` | ` <indentBlankLines>true</indentBlankLines> ` | ` false ` | Should blank lines (if preserved) have indentation. |

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
      <version>2.0.0</version>
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

## Version History ##
[Versions](Versions.md)

## Donations ##
If you use it, then please consider some encouragement.

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=JB25X84DDG5JW&lc=SE&item_name=Encourage%20the%20development&item_number=sortpom&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

[![](http://api.flattr.com/button/flattr-badge-large.png)](http://flattr.com/thing/439567/maven-sortpom-plugin)