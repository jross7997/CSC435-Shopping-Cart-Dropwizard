package shop.resources;

import io.dropwizard.jersey.sessions.Session;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import shop.db.FoodDb;
import shop.jdbi.FoodDAO;
import shop.messaging.Message;
import shop.model.Food;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/shop")
@Produces(MediaType.APPLICATION_JSON)
public class ShopResource {

    Jdbi jdbi;

    public ShopResource(Jdbi j){
        jdbi = j;
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
        }catch (JdbiException e) {
            Message mess = null;
            if (e instanceof JdbiException) {
                mess = new Message("Jdbi Exception", e.toString());
            } else {
                mess = new Message("Unknown Exception", e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        return Response.ok(food).build();
    }

    @PUT
    @Path("/{id}")
        public Response addToCart(@Session HttpSession session, @PathParam("id") String prodId){
        Message mess = null;
        try{
            int id = Integer.parseInt(prodId);
            List<FoodDb> db = jdbi.withExtension(FoodDAO.class, dao -> {
                return dao.getById(id);
            });
            Food food = null;
            for(FoodDb f: db){
                food = new Food(f.getId(),f.getItem_name(),f.getCalories(),f.getFat(),f.getSodium());
            }
            synchronized (session) {
                if (session.getAttribute("cart") != null) {
                    if(food != null) {
                        ArrayList<Food> cart = (ArrayList<Food>) session.getAttribute("cart");
                        cart.add(food);
                        session.setAttribute("cart", cart);
                        mess = new Message("Item added to your cart",food);
                    }else{
                        mess = new Message("Item was null", food);
                    }
                } else {
                    if(food != null) {
                        ArrayList<Food> cart = new ArrayList<>();
                        cart.add(food);
                        session.setAttribute("cart",cart);
                        mess = new Message("Item was added to your cart",food);
                    }else{
                        mess = new Message("Item was null", food);
                    }
                }
            }
        }catch(NumberFormatException|JdbiException e){
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", ((JdbiException) e).toString());
            }else if (e instanceof NumberFormatException) {
                mess = new Message("An id must be an integer", ((NumberFormatException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        return Response.status(201).entity(mess).build();
    }
}
