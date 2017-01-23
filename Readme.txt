Hello, its RefOrms framework.
Bad English - Sorry.

What the project does:
 Project help you write pretty full SQL and map result to your ORM data.
 Project contains SQL-92 parser (select query only), SelectQuery as AST tree
 Project contains api to contains SQL with your filters
 Project contains api to map SQL results to yout Orm classes

Why the project is useful:
 It's powerfull instrument help you to take all from SQL and put it to ORM.
 It's not hibernate, it's not DDL (like JOOQ) its only SQL to ORM

 How users can get started with the project
 Download reforms.jar and include it to your project.

 Example of usage (scheme of code)

 // OrmDao - is class from reforms framework (it's only ONE CLASS from framework, other is YOUR classes!)
 // ds  - DataSource, object that contain java.sql.Connection to BD
 // query, for example: SELECT id, name, description, price, articul, act_time FROM goods WHERE id = :id
 // Your_Orm, Pojo class, for example: public class GoodsOrm{...} with fields like id, name, description, price, articul, actTime and get/set methods
 // filters - FilterMap with key "id" and "1L" value, or 1L (use with loadSimpleOrms)
 // :id - is place holder for ? that be set with PreparedStatement like this -> ps.setLong(1, 1L);
 // Usage:
 OrmDao ormDao = new OrmDao(ds);
 List<Your_Orm> yourOrmList = ormDao.loadOrms(Your_Orm.class, query, filters);
 // after invoke loadOrms or loadSimpleOrms method you will get List of Your_Orm (GoodsOrm in comment example)

 Full example you can find in test of this project. See 'reforms/src/test/java/com/reforms/orm/UTestOrmDao.java' and other classes.

