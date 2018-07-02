package shop.db;

public class FoodPost {
    private String name;
    private String calories;
    private String fat;
    private String sodium;

    public FoodPost(){

    }

    public FoodPost(String i,String c,String f,String s){
        name = i;
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

    public String getName() {
        return name;
    }

    public void setName(String item_name) {
        name = item_name;
    }
}

