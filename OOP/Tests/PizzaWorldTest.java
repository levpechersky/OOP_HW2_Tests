package OOP.Tests;

import OOP.Provided.PizzaLover;
import OOP.Provided.PizzaLover.PizzaLoverAlreadyInSystemException;
import OOP.Provided.PizzaPlace;
import OOP.Provided.PizzaPlace.PizzaPlaceAlreadyInSystemException;
import OOP.Provided.PizzaWorld;
import OOP.Solution.PizzaWorldImpl;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;

public class PizzaWorldTest {
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

	private final double epsilon = 0.00001; // for double comparisons

	// Users and PizzaPlaces. I want to be able to access those anywhere:
	private PizzaPlace italiano = null, pizzaHut = null, dominos = null, pizzaBad = null,
            pizzaRat = null, pizzaWhat = null, nuna = null;
	private PizzaLover leonardo = null, donatello = null, michelangelo = null, rafael = null,
            splinter = null, shredder = null, krang = null, student = null, foreverAlone=null;


    private void addUsers(PizzaWorld world){
        try { // Now add users:
            leonardo     = world.joinNetwork(12345, "Leonardo");
            donatello    = world.joinNetwork(23456, "Donatello");
            michelangelo = world.joinNetwork(56789, "Michelangelo");
            rafael       = world.joinNetwork(45678, "Rafael");

            splinter = world.joinNetwork(67891, "Splinter");

            shredder = world.joinNetwork(78910, "Shredder");
            krang    = world.joinNetwork(89101, "Krang");

            student = world.joinNetwork(1, "Student");

            foreverAlone = world.joinNetwork(300, "Forever Alone"); // has no friend connections
        } catch (PizzaLoverAlreadyInSystemException e){
            fail();
        }
        // check setup:
        assertEquals(9, world.registeredPizzaLovers().size());
    }

    private void addPlaces(PizzaWorld world){
        try { // Let's add places:
            italiano  = world.addPizzaPlace(40, "Italiano", 125, traditional_pizzas);
            pizzaHut  = world.addPizzaPlace(50, "Pizza Hut", 15, veggie_topping_pizzas);
            dominos   = world.addPizzaPlace(60, "Dominos", 650, meat_topping_pizzas);

            // Note, that following 3 are at the same distance from Technion:
            pizzaBad  = world.addPizzaPlace(10, "Pizza Bad", 350, complements);
            pizzaRat  = world.addPizzaPlace(20, "Pizza Rat", 350, traditional_pizzas);
            pizzaWhat = world.addPizzaPlace(30, "Pizza What?", 350, meat_topping_pizzas);

            nuna      = world.addPizzaPlace(70, "Nuna", 40, veggie_topping_pizzas);
        } catch (PizzaPlaceAlreadyInSystemException e){
            fail();
        }
        // check setup:
        assertEquals(7, world.registeredPizzaPlaces().size());
    }

    private void populateWorld(PizzaWorld world){
        cleanUpPlacesAndUsers(); // in case someone will forget to clean up.

        addPlaces(world);
        addUsers(world);
    }

    private void cleanUpPlacesAndUsers() {
		italiano = null;
		pizzaHut = null;
		dominos = null;
		pizzaBad = null;
		pizzaRat = null;
		pizzaWhat = null;
		nuna = null;

		leonardo = null;
		donatello = null;
		michelangelo = null;
		rafael = null;
		splinter = null;
		shredder = null;
		krang = null;
		student = null;
		foreverAlone = null;
	}

	private void setUpConnections(PizzaWorld world){
        try { // Now let's make friends:
            // Make TNMT a clique:
            world.addConnection(leonardo, donatello);
            world.addConnection(leonardo, michelangelo);
            world.addConnection(leonardo, rafael);
            world.addConnection(donatello, michelangelo);
            world.addConnection(donatello, rafael);
            world.addConnection(michelangelo, rafael);
            assertEquals(3, leonardo.getFriends().size());
            assertEquals(3, michelangelo.getFriends().size());
            assertEquals(3, donatello.getFriends().size());
            assertEquals(3, rafael.getFriends().size());

            // all TNMT are friends of Student:
            world.addConnection(student, leonardo);
            world.addConnection(student, donatello);
            world.addConnection(student, michelangelo);
            world.addConnection(student, rafael);
            assertEquals(4, student.getFriends().size());

            // Krang is a friend of Shredder, but not the other way around:
            world.addConnection(shredder, krang);
            assertEquals(1, shredder.getFriends().size());

            // Splinter is a friend of Turtles, but not other way around
            world.addConnection(leonardo, splinter);
            world.addConnection(michelangelo, splinter);
            world.addConnection(donatello, splinter);
            world.addConnection(rafael, splinter);
            assertEquals(5, leonardo.getFriends().size());
            assertEquals(5, donatello.getFriends().size());
            assertEquals(5, michelangelo.getFriends().size());
            assertEquals(5, rafael.getFriends().size());

            // Splinter is a friend of Shredder. It's kinda weird, but I need it for the test (for deeper graph).
            world.addConnection(splinter, shredder);
            assertEquals(5, splinter.getFriends().size());
        } catch (Exception e) {
            fail();
        }
    }

    private void setUpRatings_FocusOnRating(PizzaWorld world) {
        // Hut and Dominos have same rating, different distance
        // Bad and What have same rating and same distance
        try {
            italiano.rate(leonardo, 5).rate(donatello, 4);
            pizzaHut.rate(leonardo, 4).rate(michelangelo, 2);
            dominos.rate(leonardo, 4).rate(donatello, 2);
            pizzaBad.rate(donatello, 1).rate(michelangelo, 2);
            pizzaWhat.rate(donatello, 1).rate(foreverAlone, 2);
            nuna.rate(rafael, 5).rate(shredder, 5);
            pizzaRat.rate(splinter, 5);
        } catch (Exception e){
            fail();
        }

        try {
            leonardo.favorite(dominos).favorite(pizzaHut).favorite(italiano);
            donatello.favorite(dominos).favorite(italiano).favorite(pizzaWhat).favorite(pizzaBad);
            michelangelo.favorite(pizzaHut).favorite(pizzaBad);
            rafael.favorite(nuna);
            splinter.favorite(pizzaRat);
        } catch (Exception e){
            fail();
        }

        // Check that the setup did what we intended it to do:
        assertEquals(italiano.averageRating(), 4.5, epsilon);
        assertEquals(pizzaHut.averageRating(), 3, epsilon);
        assertEquals(dominos.averageRating(), 3, epsilon);
        assertEquals(pizzaBad.averageRating(), 1.5, epsilon);
        assertEquals(pizzaWhat.averageRating(), 1.5, epsilon);
        assertEquals(nuna.averageRating(), 5, epsilon);
    }

    private void setUpRatings_FocusOnDistance(PizzaWorld world) {
        // Hut and Dominos have same rating, different distance
        // Bad, Rat and What have same distance
        // Bad and What also have have same rating
        try {
            italiano.rate(splinter, 5).rate(foreverAlone, 5); // Spliter doesn't like Rat Pizza anymore.
            nuna.rate(leonardo, 5).rate(shredder, 5).rate(michelangelo, 5);
            dominos.rate(rafael, 3).rate(foreverAlone, 2).rate(leonardo, 4);
            pizzaBad.rate(donatello, 1).rate(shredder, 3).rate(michelangelo, 2);
            pizzaWhat.rate(donatello, 1).rate(shredder, 3).rate(michelangelo, 2);
            pizzaHut.rate(rafael, 3).rate(shredder, 5).rate(leonardo, 4);
            pizzaRat.rate(donatello, 3).rate(michelangelo, 3);
        } catch (Exception e){
            fail();
        }

        try {
            leonardo.favorite(nuna).favorite(pizzaHut).favorite(dominos);
            donatello.favorite(pizzaRat).favorite(pizzaWhat).favorite(pizzaBad);
            michelangelo.favorite(nuna).favorite(pizzaRat).favorite(pizzaWhat).favorite(pizzaBad);
            rafael.favorite(pizzaHut).favorite(dominos);
            splinter.favorite(italiano);
        } catch (Exception e){
            fail();
        }

        // Check that the setup did what we intended it to do:
        assertEquals(pizzaHut.averageRating(), 4, epsilon);
        assertEquals(dominos.averageRating(), 3, epsilon);
        assertEquals(pizzaRat.averageRating(), 3, epsilon);
        assertEquals(pizzaBad.averageRating(), 2, epsilon);
        assertEquals(pizzaWhat.averageRating(), 2, epsilon);
        assertEquals(nuna.averageRating(), 5, epsilon);
    }

	@Test
	public void CreationTest() {
		PizzaWorld world = new PizzaWorldImpl();
		assertTrue(world.registeredPizzaLovers().isEmpty());
		assertTrue(world.registeredPizzaPlaces().isEmpty());
	}

    @Test
	public void AddGetLoversTest() {
        PizzaWorld world = new PizzaWorldImpl();

        addUsers(world);

        Set<PizzaLover> expectedUsers = new TreeSet<>(Arrays.asList(
                leonardo, donatello, rafael, michelangelo, splinter, shredder, krang, student, foreverAlone
        ));
        Collection<PizzaLover> actualUsers = world.registeredPizzaLovers();
        assertTrue(actualUsers.containsAll(expectedUsers) && expectedUsers.containsAll(actualUsers));
        try {
            assertEquals(world.getPizzaLover(rafael.getId()), rafael);
            assertEquals(world.getPizzaLover(student.getId()), student);
            assertEquals(world.getPizzaLover(foreverAlone.getId()), foreverAlone);
        } catch (Exception e){
            fail();
        }

        // Now let's check that we've received actualUsers as a copy:
        actualUsers.clear();
        actualUsers = world.registeredPizzaLovers();
        assertTrue(actualUsers.containsAll(expectedUsers) && expectedUsers.containsAll(actualUsers));
    }

    @Test
	public void AddGetPlacesTest() {
        PizzaWorld world = new PizzaWorldImpl();

        addPlaces(world);

        Set<PizzaPlace> expectedPlaces = new TreeSet<>(Arrays.asList(
                dominos, pizzaHut, pizzaBad, pizzaRat, pizzaWhat, italiano, nuna
        ));
        Collection<PizzaPlace> actualPlaces = world.registeredPizzaPlaces();
        assertTrue(actualPlaces.containsAll(expectedPlaces) &&
                expectedPlaces.containsAll(actualPlaces));
        try {
            assertEquals(world.getPizzaPlace(nuna.getId()), nuna);
            assertEquals(world.getPizzaPlace(italiano.getId()), italiano);
            assertEquals(world.getPizzaPlace(pizzaHut.getId()), pizzaHut);
        } catch (Exception e){
            fail();
        }

        // Now let's check that we've received actualPlaces as a copy:
        actualPlaces.clear();
        actualPlaces = world.registeredPizzaPlaces();
        assertTrue(actualPlaces.containsAll(expectedPlaces) && expectedPlaces.containsAll(actualPlaces));
    }

	/*
	*  I have 2 setups of ratings - one is more suitable for  favoritesByDist(), and other - for favoritesByRating().
	*  Anyway, I check both functions with both setups.
	*/

    @Test
	public void FavoritesByRatingTest() {
        PizzaWorld world = new PizzaWorldImpl();

        populateWorld(world);
        setUpConnections(world);

        setUpRatings_FocusOnRating(world); // check this in case of failed test

        Collection<PizzaPlace> actual=null;
        try {
            actual = world.favoritesByRating(student);
        } catch (PizzaLover.PizzaLoverNotInSystemException e){
            fail();
        }
        assertFalse("Splinter isn't direct friend of student, Pizza Rat shouldn't appear",
                actual.contains(pizzaRat));
        // equals() on lists checks contents and order:
        assertEquals(Arrays.asList(italiano, pizzaHut, dominos, pizzaBad, pizzaWhat, nuna), actual);

        cleanUpPlacesAndUsers();
	}

    @Test
    public void FavoritesByDistTest() {
        PizzaWorld world = new PizzaWorldImpl();

        populateWorld(world);
        setUpConnections(world);
        setUpRatings_FocusOnRating(world);

        Collection<PizzaPlace> actual=null;
        try {
            actual = world.favoritesByDist(student);
        } catch (PizzaLover.PizzaLoverNotInSystemException e){
            fail();
        }
        assertFalse("Splinter isn't direct friend of student, Pizza Rat shouldn't appear",
                actual.contains(pizzaRat));
        // equals() on lists checks contents and order:
        assertEquals(Arrays.asList(pizzaHut, italiano, dominos, pizzaBad, pizzaWhat, nuna), actual);

        cleanUpPlacesAndUsers();
    }

    @Test
	public void FavoritesByRatingTest2() {
        PizzaWorld world = new PizzaWorldImpl();

        populateWorld(world);
        setUpConnections(world);

        setUpRatings_FocusOnDistance(world); // <--- note the big difference

        Collection<PizzaPlace> actual=null;
        try {
            actual = world.favoritesByRating(student);
        } catch (PizzaLover.PizzaLoverNotInSystemException e){
            fail();
        }
        assertFalse("Splinter isn't direct friend of student, italiano shouldn't appear",
                actual.contains(italiano));
        // equals() on lists checks contents and order:
        assertEquals(Arrays.asList(nuna, pizzaHut, dominos, pizzaRat, pizzaBad, pizzaWhat), actual);

        cleanUpPlacesAndUsers();
	}

	@Test
	public void FavoritesByDistTest2() {
		PizzaWorld world = new PizzaWorldImpl();

		populateWorld(world);
		setUpConnections(world);
        setUpRatings_FocusOnDistance(world); // <--- note the big difference

        Collection<PizzaPlace> actual=null;
        try {
            actual = world.favoritesByDist(student);
        } catch (PizzaLover.PizzaLoverNotInSystemException e){
            fail();
        }
        assertFalse("Splinter isn't direct friend of student, italiano shouldn't appear",
                actual.contains(italiano));
        // equals() on lists checks contents and order:
        assertEquals(Arrays.asList(pizzaHut, nuna, dominos, pizzaRat, pizzaBad, pizzaWhat), actual);

		cleanUpPlacesAndUsers();
	}

	@Test
	public void ToStringTest() {
		PizzaWorld world = new PizzaWorldImpl();

		// Initial state:
		String emptyWorldExpectedStr = "Registered pizza lovers: .\n" +
				"Registered pizza places: .\n" +
				"Pizza lovers:\n" +
				"End pizza lovers.";
		assertEquals(world.toString(), emptyWorldExpectedStr);

		// Now, let's add some contents to the world:
        populateWorld(world);
        setUpConnections(world);

		String expectedStr = "Registered pizza lovers: 1, 300, 12345, 23456, 45678, 56789, 67891, 78910, 89101.\n" +
				"Registered pizza places: 10, 20, 30, 40, 50, 60, 70.\n" +
				"Pizza lovers:\n" +
				"1 -> [12345, 23456, 45678, 56789].\n" +
				"300 -> [].\n" +
				"12345 -> [1, 23456, 45678, 56789, 67891].\n" +
				"23456 -> [1, 12345, 45678, 56789, 67891].\n" +
				"45678 -> [1, 12345, 23456, 56789, 67891].\n" +
				"56789 -> [1, 12345, 23456, 45678, 67891].\n" +
				"67891 -> [12345, 23456, 45678, 56789, 78910].\n" +
				"78910 -> [67891, 89101].\n" +
				"89101 -> [78910].\n" +
				"End pizza lovers.";
		assertEquals(world.toString(), expectedStr);

		cleanUpPlacesAndUsers();
	}

    private void setUpRatings_ForGetRecommendation(PizzaWorld world) {
        try {
            italiano.rate(shredder, 5);
            pizzaHut.rate(michelangelo, 5);
            dominos.rate(leonardo, 5);
            pizzaBad.rate(foreverAlone, 5);
            pizzaWhat.rate(donatello, 5);
            nuna.rate(krang, 5);
            pizzaRat.rate(splinter, 5);
        } catch (Exception e){
            fail();
        }

        try {
            //                                 distance from student
            leonardo.favorite(dominos); //     1
            michelangelo.favorite(pizzaHut);// 1
            donatello.favorite(pizzaWhat);//   1

            splinter.favorite(pizzaRat);//     2
            shredder.favorite(italiano);//     3
            krang.favorite(nuna);//            4
            foreverAlone.favorite(pizzaBad);// infinity (not reachable)
        } catch (Exception e){
            fail();
        }
    }

    @Test
	public void GetRecommendationTest() {
        PizzaWorld world = new PizzaWorldImpl();

        populateWorld(world);
        setUpConnections(world);
        setUpRatings_ForGetRecommendation(world);

        try {
            // Exact distance:
            assertTrue(world.getRecommendation(student, dominos, 1));
            assertTrue(world.getRecommendation(student, pizzaHut, 1));
            assertTrue(world.getRecommendation(student, pizzaWhat, 1));
            assertTrue(world.getRecommendation(student, pizzaRat, 2));
            assertTrue(world.getRecommendation(student, italiano, 3));
            assertTrue(world.getRecommendation(student, nuna, 4));
            // Larger t:
            assertTrue(world.getRecommendation(student, pizzaHut, Integer.MAX_VALUE));
            assertTrue(world.getRecommendation(student, dominos, 2));
            assertTrue(world.getRecommendation(student, pizzaRat, 3));
            assertTrue(world.getRecommendation(student, italiano, 4));
            assertTrue(world.getRecommendation(student, nuna, 5));
            // Smaller than needed distance:
            assertFalse(world.getRecommendation(student, pizzaWhat, 0));
            assertFalse(world.getRecommendation(student, pizzaHut, 0));
            assertFalse(world.getRecommendation(student, pizzaWhat, 0));
            assertFalse(world.getRecommendation(student, pizzaRat, 1));
            assertFalse(world.getRecommendation(student, italiano, 2));
            assertFalse(world.getRecommendation(student, nuna, 3));
            // Unreachable node:
            assertFalse(world.getRecommendation(student, pizzaBad, 1));
            assertFalse(world.getRecommendation(student, pizzaBad, Integer.MAX_VALUE));
        } catch (Exception e){
            fail();
        }

        cleanUpPlacesAndUsers();
	}

}