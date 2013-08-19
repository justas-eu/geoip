/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.justas.geoip.rest;

import au.com.bytecode.opencsv.CSVReader;
import eu.justas.geoip.model.Location;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author justas
 */
@Stateless
@Path("location")
public class LocationFacadeREST extends AbstractFacade<Location> {

    @PersistenceContext(unitName = "geoip-PU")
    private EntityManager em;
    @EJB
    private NetworkEntryFacadeREST networkEntryFacadeREST;

    public LocationFacadeREST() {
        super(Location.class);
    }

    @GET
    @Path("import")
    public void doImport() {
        networkEntryFacadeREST.doImport();

        long startTime = System.currentTimeMillis();
        CSVReader reader = null;
        try {
            String fName = "/Users/justas/Desktop/tmp/GeoLiteCity_20130806/GeoLiteCity-Location.csv";
            reader = new CSVReader(new FileReader(fName));
            String[] string = reader.readNext();
            System.out.println("Started Location import");

            long count = 0;
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
                string = reader.readNext();

            }

            System.out.println("Count ======= " + count);
        } catch (Exception ex) {
            Logger.getLogger(NetworkEntryFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(NetworkEntryFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        System.out.println("Import took: " + seconds);


    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Location entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Location entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    public Location find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Location> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Location> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
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
