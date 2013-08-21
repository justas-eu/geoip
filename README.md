geoip
===========

Java EE 6 web application.
The application tells ip address geo location.
The app is capable of importing maxmind data files in csv format.
The location table contains ~0.5 million rows and network ~2 millions

Technologies
===========
- Java EE 6
- CDI
- JPA
- OpenCSV

Prerequisites
===========
- Java 6 or 7
- Maven 3
- TomEE or GlassFish  (tested on TomEE Plus 1.5.2 and GlassFish 4)
- In use with http://dev.maxmind.com/geoip/legacy/geolite/

Install
===========
<pre>
mvn install
</pre>

Adding DB resource with id JNDI/localmysql on TomEE or GlassFish
