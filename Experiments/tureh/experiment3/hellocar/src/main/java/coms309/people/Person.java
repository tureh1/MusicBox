package coms309.people;


/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class Person {

    private String carName;

    private String carModel;

    private String carBrand;

    private String carYear;

    private String carColor;

    public Person(){
        
    }

    public Person(String carName, String carModel, String carBrand, String carYear, String carColor) {
        this.carName = carName;
        this.carModel = carModel;
        this.carBrand = carBrand;
        this.carYear = carYear;
        this.carColor = carColor;
    }

    public String getCarName() {
        return this.carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarModel() {
        return this.carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarBrand() {
        return this.carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarYear() {
        return this.carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getCarColor() { return this.carColor; }

    public void setCarColor(String carColor) { this.carColor = carColor; }

    @Override
    public String toString() {
        return carName + " "
               + carModel + " "
               + carBrand + " "
               + carYear + " "
               + carColor + " ";
    }
}
