modgwt-maven-plugin
===================

A gwt pluging that depends on the http://mojo.codehaus.org/gwt-maven-plugin/ making hot code replacement easier for multi module projects.

See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/productivity.html

Usage:

just pass a root folder under witch your needed modules can be found, and the modules artifact ids comma separated.

ex:

mvn modgwt:gwt-run -Dmodgwt.search=ROOT_SEARCH_FOLDER -Dmodgwt.includes=GWT-MODULE1,GWT-MODULE2

mvn modgwt:gwt-debug -Dmodgwt.search=ROOT_SEARCH_FOLDER -Dmodgwt.includes=GWT-MODULE1,GWT-MODULE2


contact:
code@xptdev.com
