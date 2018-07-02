package shop.resources;

import shop.model.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/home")
@Produces(MediaType.APPLICATION_JSON)
public class HomeResource {

    @GET
    public Response getAPI(){
        ArrayList<ApiInfo[]> list = new ArrayList<>();

        //HomePage
        ApiInfo home[] = new ApiInfo[1];
        home[0] = new ApiInfo("GET", "/home", "Shows the applications Functionality");
        list.add(home);
        //Database
        ApiInfo[] data = new ApiInfo[6];
        data[0] = new ApiInfo("GET", "/data", "Shows the entire database");
        data[1] = new ApiInfo("GET","/data/{id}","Get an item by Id");
        data[2] = new ApiInfo("POST", "/data/post20", "Add 20 items from the external database");
        data[3] = new ApiInfo("POST", "/data?name=:name&calories=:calories&fat=:fat&sodium=:sodium", "Add a custom item to the database");
        data[4] = new ApiInfo("DELETE", "/data", "Delete the Entire Database");
        data[5] = new ApiInfo("DELETE", "/data/{id}", "Delete an item from the database");
        list.add(data);
        //Shop
        ApiInfo[] shop = new ApiInfo[2];
        shop[0] = new ApiInfo("GET", "/shop", "Get a list of the items for sale");
        shop[1] = new ApiInfo("PUT", "/shop/{id}", "Add an Item to the cart");
        list.add(shop);
        //Cart
        ApiInfo[] cart = new ApiInfo[4];
        cart[0] = new ApiInfo("GET", "/cart", "Get the User's cart, if logged in the price should be lower");
        cart[1] = new ApiInfo("PUT", "/cart/{id}", "Add an item to the cart");
        cart[2] = new ApiInfo("DELETE", "/cart/{id}", "Remove an Item from the cart");
        cart[3] = new ApiInfo("DELETE","/cart/logout", "Delete entire cart and invalidate Session");
        list.add(cart);
        return Response.ok(list).build();
    }

}
