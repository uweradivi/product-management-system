abstract class Food {
    abstract void prepare();
}

// ==========================
// CLASS + ENCAPSULATION
// ==========================
class Burger extends Food {
    private String bread;
    private String meat;
    private String sauce;

    // CONSTRUCTOR
    Burger(String bread, String meat, String sauce) {
        this.bread = bread;
        this.meat = meat;
        this.sauce = sauce;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    // ABSTRACTION IMPLEMENTATION
    @Override
    void prepare() {
        System.out.println("Preparing basic burger with " + bread + ", " + meat + ", " + sauce);
    }

    // METHOD OVERLOADING
    void orderBurger() {
        System.out.println("Ordering one burger");
    }

    void orderBurger(int quantity) {
        System.out.println("Ordering " + quantity + " burgers");
    }
}

// ==========================
// INHERITANCE + POLYMORPHISM
// ==========================
class CheeseBurger extends Burger {
    private String cheese;

    CheeseBurger(String bread, String meat, String sauce, String cheese) {
        super(bread, meat, sauce);
        this.cheese = cheese;
    }

    @Override
    void prepare() {
        System.out.println("Preparing cheese burger with extra " + cheese);
    }
}

// ==========================
// MAIN CLASS
// ==========================
public class Main {
    public static void main(String[] args) {

        Burger b1 = new Burger("White bread", "Beef", "Mayo");

        b1.prepare();

        b1.setSauce("Ketchup");
        System.out.println("Sauce: " + b1.getSauce());

        b1.orderBurger();
        b1.orderBurger(3);

        System.out.println("------------------");

        Burger b2 = new CheeseBurger("Brown bread", "Chicken", "BBQ", "Cheddar");

        b2.prepare();
    }
}
