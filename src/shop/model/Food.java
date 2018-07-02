package shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import shop.db.FoodDb;

import java.text.DecimalFormat;
import java.util.List;

public class Food {
    @JsonIgnore
    public  DecimalFormat df = new DecimalFormat( "#.00" );

    @JsonProperty
    private String name;
    @JsonProperty
    private String calories;
    @JsonProperty
    private String fat;
    @JsonProperty
    private String sodium;
    @JsonProperty
    private String price;
    @JsonIgnore
    private String salesPrice;
    @JsonProperty
    private int id = 0;

    public Food(){};

    public Food(int id, String n, String c, String f, String s) {
        name = n;
        if(c == null){
            c = "0";
        }else {
            calories = c;
        }

        if (f == null) {
            fat = "0";
        } else {
            fat = f;
        }

        if(s == null){
            s = "0";
        }else {
            sodium = s;
        }
        double pr = (Double.parseDouble(calories)+ Double.parseDouble(sodium) + Double.parseDouble(fat))/100;
        price =df.format(pr);
        salesPrice = df.format(pr/2);
        this.id = id;
    }




    public String getName() {
        return name;
    }

    public String getCalories() {
        return calories;
    }

    public String getFat() {
        return fat;
    }

    public String getSodium() {
        return sodium;
    }

    public String getPrice() {
        return price;
    }
    public String getSalesPrice() {
        return salesPrice;
    }
    public int getID() {
        return id;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public void setSodium(String sodium) {
        this.sodium = sodium;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }
}
