package OOP.Tests;

import OOP.Provided.PizzaLover;
import OOP.Provided.PizzaLover.*;
import OOP.Provided.PizzaPlace;
import OOP.Provided.PizzaPlace.PizzaPlaceAlreadyInSystemException;
import OOP.Provided.PizzaPlace.PizzaPlaceNotInSystemException;
import OOP.Provided.PizzaPlace.RateRangeException;
import OOP.Provided.PizzaWorld;
import OOP.Provided.PizzaWorld.ImpossibleConnectionException;
import OOP.Solution.PizzaLoverImpl;
import OOP.Solution.PizzaPlaceImpl;
import OOP.Solution.PizzaWorldImpl;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class PizzaPlaceTest {
    // when changing those - update ToStringTest accordingly
    private static final Set<String> traditional_pizzas = new HashSet<>(Arrays.asList(
            "Margarita", "Hawaiian", "Greek", "Napoli", "Sicilian", "Marinara"
    ));
    private static final Set<String> meat_topping_pizzas = new HashSet<>(Arrays.asList(
            "Chicken", "Bacon", "Ground Beef", "Pepperoni", "Anchovies"
    ));
    private static final Set<String> veggie_topping_pizzas = new HashSet<>(Arrays.asList(
            "Mushrooms", "Tomatoes", "Onion", "Olives"
    ));
    private static final Set<String> complements = new HashSet<>(Arrays.asList(
            "Calzone", "Fries", "Onion rings", "Coca-cola", "Cola zero"
    ));

    @Test
    public void CreationAndGettersTest() {
        PizzaPlace p1 = new PizzaPlaceImpl(10, "Italiano", 125, traditional_pizzas);
        PizzaPlace p2 = new PizzaPlaceImpl(20, "Hut", 15, veggie_topping_pizzas);
        PizzaPlace p3 = new PizzaPlaceImpl(30, "Dominos", 650, meat_topping_pizzas);

        // getId:
        assertEquals(p1.getId(), 10);
        assertEquals(p2.getId(), 20);
        assertEquals(p3.getId(), 30);

        // distance:
        assertEquals(p1.distance(), 125);
        assertEquals(p2.distance(), 15);
        assertEquals(p3.distance(), 650);
    }


    @Test
    public void RatingsTest() {
        final double epsilon = 0.0001; // for double comparisons
        PizzaPlace pp = new PizzaPlaceImpl(10, "Italiano", 125, traditional_pizzas);
        PizzaLover lovers[] = {
                new PizzaLoverImpl(12345, "Leonardo"),
                new PizzaLoverImpl(23456, "Donatello"),
                new PizzaLoverImpl(56789, "Michelangelo"),
                new PizzaLoverImpl(45678, "Rafael"),
        };
        int ratings_before[] = {4, 5, 4, 5};
        int ratings_after[] = {2, 3, 4, 1};

        // Initial state:
        assertEquals(pp.numberOfRates(), 0);
        assertEquals(pp.averageRating(), 0.0, epsilon);

        // Test bad path:
        try {
            pp.rate(lovers[0], -1);
            fail("rate() with negative rating should throw");
        } catch (Exception e) {
            assertTrue(e instanceof RateRangeException);
        }
        try {
            pp.rate(lovers[0], 6);
            fail("rate() with rating 6 or more should throw");
        } catch (Exception e) {
            assertTrue(e instanceof RateRangeException);
        }

        // error cases shouldn't add ratings
        assertEquals(pp.numberOfRates(), 0);
        assertEquals(pp.averageRating(), 0.0, epsilon);


        // rate pp for the 1st time (ratings_before)
        OptionalDouble avg = null;
        try {
            for (int i=0; i<lovers.length; i++) {
                pp.rate(lovers[i], ratings_before[i]);
                assertEquals(pp.numberOfRates(), i+1);
            }
        } catch (RateRangeException e){
            fail();
        }
        avg = Arrays.stream(ratings_before).average();
        assertEquals(pp.averageRating(), avg.getAsDouble(), epsilon);

        // update existing ratings (with ratings_after)
        avg = null;
        try {
            for (int i=0; i<lovers.length; i++) {
                pp.rate(lovers[i], ratings_after[i]);
                // number of ratings should stay as before (we are only updating existing ones)
                assertEquals(pp.numberOfRates(), lovers.length);
            }
        } catch (RateRangeException e){
            fail();
        }
        avg = Arrays.stream(ratings_after).average();
        assertEquals(pp.averageRating(), avg.getAsDouble(), epsilon);
    }

    @Test
    public void EqualsTest() {
        PizzaPlace italiano =               new PizzaPlaceImpl(10, "Italiano", 125, traditional_pizzas);
        PizzaPlace italianoSameEverything = new PizzaPlaceImpl(10, "Italiano", 125, traditional_pizzas);
        PizzaPlace italianoSameIdOnly =     new PizzaPlaceImpl(10, "Totally not Italiano", 11250, complements);
        PizzaPlace dominos =                new PizzaPlaceImpl(30, "Dominos", 650, meat_topping_pizzas);
        PizzaPlace noSuchPlace = null;

        // general case:
        assertTrue(italiano.equals(italianoSameEverything));
        assertTrue(italianoSameEverything.equals(italiano));

        assertFalse(italiano.equals(dominos));
        assertFalse(dominos.equals(italiano));

        // nothing equals null:
        assertFalse(italiano.equals(noSuchPlace));
        assertFalse(dominos.equals(noSuchPlace));

        // unrelated classes comparison:
        assertFalse(italiano.equals(new Double(0.0)));
        assertFalse(italiano.equals(new PizzaLoverImpl(italiano.getId(), "Pizzza lover, not a place")));

        // compare id's only:
        assertTrue(italiano.equals(italianoSameIdOnly));

        // reflexivity:
        assertTrue(italiano.equals(italiano));
        assertTrue(dominos.equals(dominos));

        // commutativity:
        assertTrue(italiano.equals(italianoSameIdOnly));
        assertTrue(italianoSameIdOnly.equals(italiano));
    }

    @Test
    public void ToStringTest() {
        PizzaPlace p1 = new PizzaPlaceImpl(10, "Italiano", 125, traditional_pizzas);

        String traditional_pizzas_sorted = traditional_pizzas.stream()
                .sorted()
                .collect(Collectors.joining(", "));
        assertTrue(traditional_pizzas_sorted.equals("Greek, Hawaiian, Margarita, Marinara, Napoli, Sicilian"));// sanity check
        String p1Expected = "PizzaPlace: Italiano.\n" +
                "Id: 10.\n" +
                "Distance: 125.\n" +
                "Menu: " + traditional_pizzas_sorted + ".";
        assertEquals(p1.toString(), p1Expected);
    }
}
