# Parameter verifyFail #

  * **Sort** The plugin sorts the pom.xml as if the goal sort would have invoked. All parameters are utilized when sorting.
  * **Stop** The plugin will stop the build with an error message
  * **Warn** The plugin will only output a warning message in the Maven build log



The verify goal ignores xml formatting when it determines if a pom.xml is sorted or not.
The following parameters are not used when verifying pom.xml:
  * createBackupFile
  * backupFileExtension
  * lineSeparator
  * expandEmptyElements
  * keepBlankLines
  * nrOfIndentSpace
  * indentBlankLines
That means that they don't have to be specified at all if verifyFail is Stop or Warn.