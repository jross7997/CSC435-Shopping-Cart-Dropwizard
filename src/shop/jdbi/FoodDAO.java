package shop.jdbi;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import shop.db.FoodDb;
import java.util.List;

public interface FoodDAO {

    @SqlUpdate("delete from food")
    void deleteAll();

    @SqlUpdate("insert into food values (:id,:name,:calories,:fat,:sodium)")
    void addFood(@Bind("id") int id, @Bind("name") String name, @Bind("calories") String calories,
                 @Bind("fat") String fat, @Bind("sodium") String sodium);

    @SqlQuery("select * from food")
    @RegisterBeanMapper(FoodDb.class)
    List<FoodDb> getAll();

    @SqlQuery("select * from food where id=:id")
    @RegisterBeanMapper(FoodDb.class)
    List<FoodDb> getById(@Bind("id") int id);

    @SqlUpdate("delete from food where id=:id")
    void deleteById(@Bind("id") int id);

    @SqlUpdate("update food set Item_name=:name,calories=:calories, fat=:fat,sodium=:sodium where id=:id")
    void update( @Bind("name") String name, @Bind("calories") String calories,
                 @Bind("fat") String fat, @Bind("sodium") String sodium, @Bind("id") int id);

}
