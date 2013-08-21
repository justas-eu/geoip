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
- Java EE 6 application server (tested on TomEE Plus 1.5.2, GlassFish 4, JBoss EAP 6.1)
- In use with http://dev.maxmind.com/geoip/legacy/geolite/

Install
===========
<pre>
mvn install
</pre>

Adding DB resource with id JNDI/localmysql on TomEE or GlassFish, for JBoss read below

Running on JBoss
===========
Add data source java:jboss/datasources/geoipDS according to your JBoss version

persistence.xml 

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence          http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
    <persistence-unit name="geoip-PU">
        <jta-data-source>java:jboss/datasources/geoipDS</jta-data-source>
        <class>eu.justas.geoip.model.Country</class>
        <class>eu.justas.geoip.model.NetworkEntry</class>
        <class>eu.justas.geoip.model.Location</class>
        <properties>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLInnoDBDialect" />
            <property name="hibernate.hbm2ddl.auto" value="validate" />
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />    
        </properties>
    </persistence-unit>
</persistence>
```



