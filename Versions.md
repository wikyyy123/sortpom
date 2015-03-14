|2009-09-03|0.1.1| First working version. |
|:---------|:----|:-----------------------|
|2009-09-12|0.2.0| All custom parameters implemented. |
|2009-10-01|0.2.1| Solved strange dependency for qdox. Generated runtime exception under Linux. |
|2009-10-06|0.2.3| Read sortOrderFile from executing path or classpath. |
|2010-08-07|0.3.0| Added parameter to set indentation. |
|2010-08-20|0.3.1| Added parameters to sort dependencies and plugins. |
|2011-11-19|0.3.2| Removed non-Central dependencies. Preparing for hosting in Central. Added signed jars. |
|2011-11-21|0.4.0| The plugin is now hosted in Central. |
|2011-12-10|1.0.0| Changed the default sort order according to newer maven documentation. <br>Added predefined sort order profiles. <br>Added parameter to control handling of empty xml elements.  <br>
<tr><td>2011-12-14</td><td>1.1.0</td><td> Removed the additional blank row at the end a sorted pomfile. </td></tr>
<tr><td>2012-02-16</td><td>1.2.0</td><td> Added parameter to sort properties. </td></tr>
<tr><td>2012-02-22</td><td>1.2.1</td><td> The Mojo has been marked as @threadSafe </td></tr>
<tr><td>2012-05-24</td><td>1.3.0</td><td> Added parameter to keep blank lines. </td></tr>
<tr><td>2012-06-17</td><td>1.3.1</td><td> Added parameter to control indentation for blank lines. </td></tr>
<tr><td>2012-06-19</td><td>1.3.2</td><td> Fixed a bug where text was deleted if surrounding tags where not the on same line. </td></tr>
<tr><td>2012-08-28</td><td>2.0.0</td><td> Added goal to verify if xml elements are sorted regardless of formatting. </td></tr>
<tr><td>2012-09-23</td><td>2.1.0</td><td> The parameters sortDependencies and sortPlugins are remade for greater flexibility. </td></tr>
<tr><td>2013-08-22</td><td>2.2</td><td> URLs can now be used as sortOrderFile to allow a centralized file. </td></tr>
<tr><td>2013-10-13</td><td>2.2.1</td><td> Corrected strange line separators when using xml:space="preserve" </td></tr>