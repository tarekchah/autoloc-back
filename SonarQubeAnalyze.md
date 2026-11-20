# Impact Faible
* Remove the 'ApiResponses' wrapper from this annotation group [java:S1710] 
* Remove this unused import 'java.util.List'. [java:S1128] 
* Rename this field "Repository" to match the regular expression '^[a-z][a-zA-Z0-9]*$'. [java:S116] 
* Rename this generic name to match the regular expression '^[A-Z][0-9]?$'. [java:S119] 
* Rename this bean to match the regular expression '^[a-z][a-zA-Z0-9]*$'. [règle non confirmée] 
* Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method. [java:S8688] 

# Impact Moyen
* ID is not used in the interface. [java:S2326] 
* Add a private constructor to hide the implicit public one. [java:S1118] 
* Replace this use of System.out by a logger. [java:S106]
# Impact Important
* Replace this persistent entity with a simple POJO or DTO object. [java:S4684] 
* Add at least one assertion to this test case. [java:S2699] 
* Make sure this database password gets changed and removed from the code. [règle non confirmée] 

