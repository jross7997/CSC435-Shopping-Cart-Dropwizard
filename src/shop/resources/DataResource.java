package shop.resources;

import shop.db.FoodDb;
import shop.db.FoodPost;
import shop.jdbi.FoodDAO;
import shop.messaging.Message;
import shop.model.CallInfo;
import shop.model.Fields;
import shop.model.Food;
import shop.model.FoodInfo;
import shop.utils.Helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.validator.constraints.NotEmpty;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {

    Jdbi jdbi;
    public DataResource(Jdbi d){
        jdbi = d;
    }

    @GET
    public Response getData(){
        ArrayList<Food> food  = new ArrayList<>();
        try {
            List<FoodDb> db = jdbi.withExtension(FoodDAO.class, dao -> {
                return dao.getAll();
            });
            for (FoodDb f : db) {
                food.add(new Food(f.getId(), f.getItem_name(), f.getCalories(), f.getFat(), f.getSodium()));
            }
        }catch (JdbiException e){
            Message mess;
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        return Response.ok(food).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String prodId) {
        ArrayList<Food> food = new ArrayList<>();
        try {
            int id = Integer.parseInt(prodId);
            List<FoodDb> db = jdbi.withExtension(FoodDAO.class, dao -> {
                return dao.getById(id);
            });
            for (FoodDb f : db) {
                food.add(new Food(f.getId(), f.getItem_name(), f.getCalories(), f.getFat(), f.getSodium()));
            }

        }catch(NumberFormatException | JdbiException e){
            Message mess;
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            }else if (e instanceof NumberFormatException) {
                mess = new Message("An id must be an integer", ((NumberFormatException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        return Response.ok(food).build();
    }

    @POST
    @Path("/post20")
    public Response save20Items(){
        ArrayList<Food> food = new ArrayList<>();
        String myURL = "https://api.nutritionix.com/v1_1/search/?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cnf_total_fat%2Cnf_calories%2Cnf_sodium&appId=88cf0044&appKey=90cbe0b7c7beeb26b938584e189be6fd";

        try {
            HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
            HttpGet request = new HttpGet(myURL);
            HttpResponse response = client.execute(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            CallInfo temp = new CallInfo();
            temp = mapper.readValue(new URL(myURL), CallInfo.class);
            ArrayList<FoodInfo> hits = temp.getHitList();
            Fields t = new Fields();

            List<FoodDb> db = jdbi.withExtension(FoodDAO.class, dao -> {
                return dao.getAll();
            });

            int max = new Helper().getMax(db);
            //Put all of the food in an ArrayList
            for (FoodInfo f : hits) {
                t = f.getFields();
                String itemName = t.getItemName();
                String calories = t.getCalories();
                String fat = t.getFat();
                String sodium = t.getSodium();
                Food tempFood = new Food(max,itemName,calories,fat,sodium);
                food.add(tempFood);
                boolean finish = jdbi.withExtension(FoodDAO.class, dao -> {
                    dao.addFood(tempFood.getID(),itemName,calories,fat,sodium);
                    return true;
                });
                max++;
            }

        } catch (IOException | JdbiException e) {
            Message mess;
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            }else if (e instanceof IOException) {
                mess = new Message("There was IO Exception", ((IOException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        Message m = new Message("Twenty Items have been Added", food);
        return Response.status(201).entity(m).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOne(FoodPost body){
        System.out.println(body.getName());
        List<FoodDb> ret = null;
        try {
            Helper helper = new Helper();

            List<FoodDb> newFood= jdbi.withExtension(FoodDAO.class, dao -> {
                List<FoodDb> db = dao.getAll();
                int max = helper.getMax(db);
                dao.addFood(max,body.getName(),body.getCalories(),body.getFat(),body.getSodium());
                return dao.getById(max);
            });
            ret = newFood;


        }catch ( JdbiException e) {
            Message mess;
            if (e instanceof JdbiException) {
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            } else {
                mess = new Message("Unknown Exception", e.toString());

            }
            return Response.status(400).entity(mess).build();
        }
        Message m = new Message("Food added",ret);
        return Response.status(201).entity(m).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateItem(@PathParam("id") String prodId, FoodPost body){
        List<FoodDb> food;
        try {
            int id = Integer.parseInt(prodId);
            food = jdbi.withExtension(FoodDAO.class, dao -> {
                dao.update(body.getName(),body.getCalories(),body.getFat(),body.getSodium(),id);
                return dao.getById(id);
            });

        }catch (NumberFormatException | JdbiException e){
            Message mess;
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            }else if (e instanceof NumberFormatException) {
                mess = new Message("An id must be an integer", ((NumberFormatException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        Message m = new Message("Item " + prodId + " was updated",food);
        return Response.accepted().entity(m).build();
    }

    @DELETE
    public Response deleteDatabase(){
        try {
            boolean bool = jdbi.withExtension(FoodDAO.class, dao -> {
                dao.deleteAll();
                return true;
            });
        }catch (JdbiException e){
            Message mess = null;
            if(e instanceof JdbiException){
                mess = new Message("Jdbi Exception", e.toString());
            }else{
                mess = new Message("Unknown Exception", e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        Message m = new Message("Database Deleted");
        return Response.noContent().entity(m).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteById(@PathParam("id") String prodId) {
        Food food = new Food();
        try{
            int id = Integer.parseInt(prodId);
            List<FoodDb> fdb = jdbi.withExtension(FoodDAO.class, dao -> {
                List<FoodDb> db = dao.getById(id);
                dao.deleteById(id);
                return db;
            });
            for(FoodDb f: fdb){
                food = new Food(f.getId(),f.getItem_name(),f.getCalories(),f.getFat(),f.getSodium());
            }

        }catch(NumberFormatException | JdbiException e){
            Message mess = null;
            if(e instanceof JdbiException){
                mess = new Message("Jdbi Exception", e.toString());
            }else if(e instanceof NumberFormatException) {
                mess = new Message("Number Format Exception", e.toString());
            }else{
                mess = new Message("Unknown Exception", e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        Message m = new Message("Item Deleted", food);
        return Response.noContent().entity(m).build();
    }
}
