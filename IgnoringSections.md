# Ignore Sections #

SortPom always sort the whole pom.xml by default and this is the recommended behavior. The plugin can be configured with parameters to fit most needs and if that fails custom sort order files can be written. But when everything else is unsuccessful, sections in the pom.xml can be excluded from the sorting process.

## How to ignore a section ##

Use the xml processing commands <?SORTPOM IGNORE?> and <?SORTPOM RESUME?>.

Example:
```
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <?SORTPOM IGNORE?>
            <version>2.0</version><!--$NO-MVN-MAN-VER$ -->
            <?SORTPOM RESUME?>
        </dependency>
```
Normally SortPom would place the comment on the next line.

If the comment absolutely need to stay on the same line then the <?SORTPOM IGNORE?> and <?SORTPOM RESUME?> instructions can be used to create an ignored section.

## How does it work ##

The ignored section will be completely removed before the sorting and then inserted again after the sorting is done. What are the consequences?

  * The ignored section must be on the same xml level. Otherwise SortPom will just see a broken xml.
  * The IGNORE instruction must always be followed by a RESUME instruction.
  * The ignored sections cannot be nested.
  * If the pom.xml is really unsorted, the ignored section can end up in unexpected places. Please sort the pom.xml once without ignored sections first.