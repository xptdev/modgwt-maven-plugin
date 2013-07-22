modgwt-maven-plugin
===================

[![Build Status](https://buildhive.cloudbees.com/job/xptdev/job/modgwt-maven-plugin/badge/icon)](https://buildhive.cloudbees.com/job/xptdev/job/modgwt-maven-plugin/)

A gwt pluging that depends on the http://mojo.codehaus.org/gwt-maven-plugin/ making hot code replacement easier for multi module projects.

See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/productivity.html

Usage:

just pass a root folder under witch your needed modules can be found, and the modules artifact ids comma separated.

ex:

mvn modgwt:gwt-run -Dmodgwt.search=root_search_folder -Dmodgwt.includes=gwt-module1,gwt-module2

mvn modgwt:gwt-debug -Dmodgwt.search=root_search_folder -Dmodgwt.includes=gwt-module1,gwt-module2


contact:
code@xptdev.com
