package com.appengine.planit.domain;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.appengine.planit.form.ProfileForm.PizzaTopping;
import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Tests for Profile POJO.
 */
public class ProfileTest {

    private static final String EMAIL = "example@gmail.com";

    private static final String USER_ID = "123456789";
    
    private static final int AGE = 0;

    private static final TeeShirtSize TEE_SHIRT_SIZE = TeeShirtSize.M;

    private static final String DISPLAY_NAME = "Your Name Here";
    
    private static final PizzaTopping PIZZA_TOPPING = PizzaTopping.VEGGIE;  // obviously

    private Profile profile;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        profile = new Profile(USER_ID, DISPLAY_NAME, EMAIL, AGE, TEE_SHIRT_SIZE, PIZZA_TOPPING);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(USER_ID, profile.getUserId());
        assertEquals(DISPLAY_NAME, profile.getDisplayName());
        assertEquals(EMAIL, profile.getMainEmail());
        assertEquals(AGE, profile.getAge());
        assertEquals(TEE_SHIRT_SIZE, profile.getTeeShirtSize());
        assertEquals(PIZZA_TOPPING, profile.getPizzaTopping());
    }

    @Test
    public void testUpdate() throws Exception {
        String newDisplayName = "New Display Name";
        TeeShirtSize newTeeShirtSize = TeeShirtSize.M;
        int newAge = 22;
        PizzaTopping newPizzaTopping = PizzaTopping.CHEESE;
        profile.update(newDisplayName, newAge, newTeeShirtSize, newPizzaTopping);
        assertEquals(USER_ID, profile.getUserId());
        assertEquals(newDisplayName, profile.getDisplayName());
        assertEquals(EMAIL, profile.getMainEmail());
        assertEquals(newAge, profile.getAge());
        assertEquals(newTeeShirtSize, profile.getTeeShirtSize());
        assertEquals(newPizzaTopping, profile.getPizzaTopping());
    }

    /*
    @Test
    public void testListValues() throws Exception {
        List<String> conferenceKeys = new ArrayList<>();
        assertEquals(conferenceKeys, profile.getConferenceKeysToAttend());
        Key<Conference> conferenceKey = Key.create(Conference.class, 123L);
        profile.addToConferenceKeysToAttend(conferenceKey.getString());
        conferenceKeys.add(conferenceKey.getString());
        assertEquals(conferenceKeys, profile.getConferenceKeysToAttend());
    }
    */
}
