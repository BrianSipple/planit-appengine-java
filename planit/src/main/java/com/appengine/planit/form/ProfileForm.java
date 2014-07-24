package com.appengine.planit.form;

/**
 * Pojo representing a profile form on the client side.
 */
public class ProfileForm {
    /**
     * Any string user wants us to display him/her on this system.
     */
    private String displayName;

    /**
     * Notification email address
     */
    private String mainEmail;

    /**
     * Age of the user
     */
    private int age;

    /**
     * T shirt size.
     */
    private TeeShirtSize teeShirtSize;
    
    private PizzaTopping pizzaTopping;


    private ProfileForm () {}

    /**
     * Constructor for ProfileForm, solely for unit test.
     * @param displayName A String for displaying the user on this system.
     * @param notificationEmail An e-mail address for getting notifications from this system.
     * @param teeShirtSize t-shirt size... because t-shirts
     */
    public ProfileForm(String displayName, String mainEmail, int age, 
    					TeeShirtSize teeShirtSize, PizzaTopping pizzaTopping) {
        this.displayName = displayName;
        this.mainEmail = mainEmail;
        this.age = age;
        this.teeShirtSize = teeShirtSize;
        this.pizzaTopping = pizzaTopping;
    }


    public String getDisplayName() {
        return this.displayName;
    }

    public String mainEmail() {
    	return this.mainEmail;
    }

    public int getAge() {
    	return this.age;
    }

    public TeeShirtSize getTeeShirtSize() {
        return this.teeShirtSize;
    }

    
    public static enum TeeShirtSize {
    	NOT_SPECIFIED,
        XS,
        S,
        M,
        L, 
        XL, 
        XXL,
        XXXL
    }
    
    public PizzaTopping getPizzaTopping() {
    	return this.pizzaTopping;
    }
    
    public static enum PizzaTopping {
    	NOT_SPECIFIED,
    	CHEESE,
    	PEPPERONI,
    	SAUSAGE,
    	VEGGIE,
    	MUSHROOM,
    	OTHER
    }
    
    
}
