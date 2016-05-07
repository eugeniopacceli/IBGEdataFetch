# IBGEdataFetch

Fetches data from IBGE's SIDRA API, with Java.

More information about SIDRA:
http://www.sidra.ibge.gov.br/

More information about the SIDRA API:
http://www.sidra.ibge.gov.br/ConsultaApiSidra.htm

# What's this?

An example of a Java program handling data from IBGE's SIDRA. The source code is distributed as a Maven project, or Netbeans project.

![Screenshot](http://i.imgur.com/UpiOi3l.png "Screenshot")

This example implements the following capabilities:

* User interface written in JavaFx 8;
* Reads an Excel spreadsheet with queries's parameters;
* Requests data (executes said queries) from IBGE's SIDRA, through their REST api;
* Saves the data to JSON files on a local directory;
* Does not request same data twice;
* Populates Java entities with data from those JSON files;
* Populates a MySQL data base with said entities. (TODO)

#Dependencies

Handled automatically by Maven, this project uses:

* Jackson JSON (for JSON reading)
* Apache POI (for Excel)
* Apache's Commons IO (for basic REST communication)

# The Queries folder

JSONs in the "queries" folder are results returned by IBGE's SIDRA api.
