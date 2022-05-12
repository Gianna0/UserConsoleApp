# Simple console application.


## Description

This app connects to Json and it gets data from it. In the menu you can choose to:
- display whole table,
- display sorted table by id DESC,
- display table with user-selected decimal id,
- exit from the program

## Technologies
- Java openjdk-17
- Java runtime 61.0 for app jar

## Package
```
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONArray;
```

## Compile 

To compile the program, you need a project with all classes(Program, TableGenerator, Address, Company, Geo, User) and packages(app, create, data). Then run the project to compile it using Java openjdk-17.

## Launch

You need a compiled jar archive to run the application. then using the cmd console you can run the application. To work properly, the application requires Java Runtime version 61.0 or newer.