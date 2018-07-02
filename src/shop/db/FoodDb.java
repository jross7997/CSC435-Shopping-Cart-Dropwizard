package shop.db;

public class FoodDb {

    private int id;
    private String Item_name;
    private String calories;
    private String fat;
    private String sodium;

    public FoodDb(){

    }

    public FoodDb(int ie, String i,String c,String f,String s){
        id= ie;
        Item_name = i;
        calories = c;
        fat = f;
        sodium = s;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setSodium(String sodium) {
        this.sodium = sodium;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getFat() {
        return fat;
    }

    public String getSodium() {
        return sodium;
    }

    public String getCalories() {
        return calories;
    }

    public int getId() {
        return id;
    }

    public String getItem_name() {
        return Item_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItem_name(String item_name) {
        Item_name = item_name;
    }
}
