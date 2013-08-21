/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.justas.geoip.rest;

import au.com.bytecode.opencsv.CSVReader;
import eu.justas.geoip.model.Location;
import eu.justas.geoip.utils.InjectedConfiguration;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author justas
 */
@Stateless
@Path("location")
public class LocationFacadeREST extends AbstractFacade<Location> {

    private static final Logger LOG = Logger.getLogger(LocationFacadeREST.class.getName());
    @PersistenceContext(unitName = "geoip-PU")
    private EntityManager em;
    @Inject
    @InjectedConfiguration(key = "import.location.file", defaultValue = "/tmp/GeoLiteCity-Location.csv")
    String importFile;

    public LocationFacadeREST() {
        super(Location.class);
    }

    @GET
    @Path("import")
    public String doImport() {
        long startTime = System.currentTimeMillis();
        CSVReader reader = null;
        long count = 0;

        try {
            reader = new CSVReader(new InputStreamReader(new FileInputStream(importFile), "Windows-1252"));

            String[] string;
            reader.readNext();
            LOG.log(Level.INFO, "Started Location import");

            reader.readNext();
            string = reader.readNext();
            while (string != null && string.length > 0) {
                count++;
                Long extId = Long.valueOf(string[0]);
                String countryCode = string[1];
                String region = string[2];
                String city = string[3];
                String postalCode = string[4];
                Double latitude = (string[5].equals("")) ? null : Double.valueOf(string[5]);
                Double longtitude = (string[6].equals("")) ? null : Double.valueOf(string[6]);
                Integer metroCode = (string[7].equals("")) ? null : Integer.valueOf(string[7]);
                Integer areaCode = (string[8].equals("")) ? null : Integer.valueOf(string[8]);
                Location location = new Location(extId, countryCode, region, city, postalCode, latitude, longtitude, metroCode, areaCode);
                super.create(location);

                if ((count % 50000) == 0) {
                    LOG.log(Level.INFO, "Location import in progress: {0}", count);
                }
                if ((count % 1000) == 0) {
                    em.flush();
                    em.clear();
                }

                string = reader.readNext();
            }
            LOG.log(Level.INFO, "Location insert count: {0}", count);
        } catch (Exception ex) {
            Logger.getLogger(NetworkEntryFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }

        em.flush();
        em.clear();
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        LOG.log(Level.INFO, "Location import took: {0} s", seconds);
        return "Done location entries import. Rows inserted: " + count + ". Duration: " + seconds;

    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Location findByExtId(Long locationId) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Location> cq = cb.createQuery(Location.class);

        Root<Location> rootLocationEntry = cq.from(Location.class);

        ParameterExpression<Long> extIdParam = cb.parameter(Long.class, "extIdParam");

        Predicate predicate1 = cb.equal(rootLocationEntry.<Long>get("extId"), extIdParam);

        cq.where(predicate1);

        Query q = getEntityManager().createQuery(cq);
        q.setParameter("extIdParam", (Long) locationId);
        return (Location) q.getSingleResult();
    }
}
