package shop.resources;

import io.dropwizard.jersey.sessions.Session;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import shop.db.FoodDb;
import shop.jdbi.FoodDAO;
import shop.messaging.Message;
import shop.model.Cart;
import shop.model.Food;
import shop.utils.Helper;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
public class CartResource {

    Jdbi jdbi;
    public CartResource(Jdbi j){
        jdbi = j;
    }

    @GET
    public Response getCart(@Session HttpSession session){
        ArrayList<Food> food = new ArrayList<Food>();

        try {
            if (session.getAttribute("cart") != null) {
                food = (ArrayList<Food>) session.getAttribute("cart");
            }
        }catch(JdbiException e){
            Message mess = null;
            if(e instanceof JdbiException){
                mess = new Message("Jdbi Exception", e.toString());
            }else{
                mess = new Message("Unknown Exception", e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        Cart c =new Cart(new Helper().calculateTotal(food),food);
        return Response.ok(c).build();
    }

    @PUT
    @Path("/{id}")
    public Response addToCart(@Session HttpSession session, @PathParam("id") String prodId) {
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

    @DELETE
    @Path("/{id}")
    public Response removeFromCart(@Session HttpSession session, @PathParam("id") String prodId){
        Message mess = null;
        try{
            int id = Integer.parseInt(prodId);
            if(session.getAttribute("cart") != null){
                Food food = null;
                ArrayList<Food> cart = (ArrayList<Food>) session.getAttribute("cart");
                for(Food f: cart){
                    if(f.getID() == id){
                        food = f;
                        break;
                    }
                }
                if(food != null) {
                    cart.remove(food);
                    mess = new Message("Item was Removed from Cart", food);
                }else{
                    mess = new Message("Item not in cart");
                }

            }else{
                synchronized (session) {
                    session.setAttribute("cart", new ArrayList<Food>());
                    mess = new Message("Empty Cart");
                }
            }

        }catch (JdbiException | NumberFormatException e){
            if(e instanceof JdbiException){
                mess = new Message("There was a JDBI Exception", e);
            }else if (e instanceof NumberFormatException) {
                mess = new Message("An id must be an integer", ((NumberFormatException) e).toString());
            }else{
                mess=new Message("Unknown Exception",e.toString());
            }
            return Response.status(400).entity(mess).build();
        }
        return Response.noContent().entity(mess).build();
    }

    @DELETE
    @Path("/logout")
    public Response logout(@Session HttpSession session){
        synchronized (session){
            session.invalidate();
        }
        return Response.status(200).entity(new Message("You've logged out")).build();
    }

}
