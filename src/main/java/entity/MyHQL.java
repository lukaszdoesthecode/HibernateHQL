package entity;

import jakarta.persistence.Query;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class MyHQL {
    public static void main(String[] args) {
        EntityManager entityManager = Persistence.createEntityManagerFactory("default").createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        Session session = entityManager.unwrap(Session.class);

        transaction.begin();

        // TASK ONE - Show the percentage of foods that provide more than 50% of the sum of iron and calcium.
        // I will be honest, I do not understand the task, but I will try my best
        System.out.println("TASK 1");
        Query totalProducts = session.createQuery("SELECT COUNT(p) FROM ProductsEntity p", Long.class);
        long totalNumberOfProducts = (long) totalProducts.getSingleResult();

        Query ironCalciumProducts = session.createQuery(
                "SELECT COUNT(p) FROM ProductsEntity p WHERE (p.iron + p.calcium) > 50", Long.class);
        long ironCalciumRichProducts = (long) ironCalciumProducts.getSingleResult();

        double percentageOfIronCalciumRichProducts = (double)ironCalciumRichProducts/(double)totalNumberOfProducts * 100;
        System.out.printf("The percentage of food with amount of calcium and iron >=50: %.3f%%", percentageOfIronCalciumRichProducts);
        System.out.println();
        System.out.println("--------------------------------");

        // TASK TWO - Show the average caloric value of products with bacon in the name.
        System.out.println("TASK 2");
        Query baconAverage = session.createQuery(
                "SELECT AVG(p.calories) FROM ProductsEntity p WHERE LOWER(p.itemName) LIKE '%bacon%'", Double.class);
        double baconAverageCalories = (double) baconAverage.getSingleResult();
        System.out.printf("The average number of calories of bacon products: %.3f", baconAverageCalories);
        System.out.println();
        System.out.println("--------------------------------");

        // TASK THREE - Show the product with the highest cholesterol for each of the categories.
        System.out.println("TASK 3");
        Query highCholesterolProducts = session.createQuery(
                "FROM ProductsEntity p WHERE p.cholesterole = " +
                        "(SELECT MAX(p1.cholesterole) FROM ProductsEntity p1 WHERE p1.category = p.category)",
                ProductsEntity.class);
        highCholesterolProducts.getResultList().forEach(product -> {
            ProductsEntity p = (ProductsEntity) product;
            System.out.printf("Highest cholesterol in %s category is in %s, with the value %s\n",
                    p.getCategory(), p.getItemName(), p.getCholesterole());
        });
        System.out.println("--------------------------------");

        // TASK FOUR - Show the number of coffees (Mocha or Coffee in name) without fibre.
        System.out.println("TASK 4");
        Query coffeesWithoutFiber = session.createQuery(
                "SELECT count(p) FROM ProductsEntity p " +
                        "WHERE (LOWER(p.itemName) like '%coffee%' OR LOWER(p.itemName) like '%mocha%') " +
                        "AND p.fiber=0", Long.class);
        Long numberOfCoffeesWithoutFiber = (Long)coffeesWithoutFiber.getSingleResult();
        System.out.println("Number of coffees without fiber: " + numberOfCoffeesWithoutFiber);
        System.out.println("--------------------------------");

        //TASK FIVE - Show the caloric value of all McMuffins. Display the results in kilojoules (one calorie is 4184 joules) in ascending order.
        System.out.println("TASK 5");
        Query mcMuffins = session.createQuery(
                "FROM ProductsEntity p WHERE LOWER(p.itemName) LIKE '%mcmuffin%' ORDER BY p.calories ASC",
                ProductsEntity.class);
        List<ProductsEntity> resultList = mcMuffins.getResultList();
        for (ProductsEntity muffin : resultList) {
            double joules = muffin.getCalories() * 4.184;
            System.out.printf("%s - %.2fkJ \n", muffin.getItemName(), joules);
        }
        System.out.println("--------------------------------");

        //TASK SIX - Display the number of distinct carbohydrates values.
        System.out.println("TASK 6");
        Query numberOfCarbValues = session.createQuery(
                "SELECT COUNT(DISTINCT p.carbs) FROM ProductsEntity p", Long.class);
        long distinctNumberOfCarbValues = (long)numberOfCarbValues.getSingleResult();
        System.out.println("Number of distinct carbohydrates values: " + distinctNumberOfCarbValues);
    }
}
