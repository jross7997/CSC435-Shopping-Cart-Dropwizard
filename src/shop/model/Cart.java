package shop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Cart {
    @JsonProperty
    String total;
    @JsonProperty
    ArrayList<Food> cart;

    public Cart(double t,ArrayList<Food> f){
        DecimalFormat df = new DecimalFormat( "#.00" );
        total = df.format(t);
        cart = f;
    }

    public ArrayList<Food> getCart() {
        return cart;
    }

    public String getTotal() {
        return total;
    }
}
