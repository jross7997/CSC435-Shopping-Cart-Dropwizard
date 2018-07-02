package shop.utils;

import shop.db.FoodDb;
import shop.model.Food;

import java.util.ArrayList;
import java.util.List;

public class Helper {

    public double calculateTotal(ArrayList<Food> f){
        double total = 0;
        for(Food fd: f ){
            double t = Double.parseDouble(fd.getPrice());
            total += t;
        }
        return total;
    }

    public int getMax(List<FoodDb> list){
        int max = 0;
        for(FoodDb db : list) {
            if(max < db.getId()){
                max = db.getId();
            }
        }
        max++;
        return  max;
    }
}
