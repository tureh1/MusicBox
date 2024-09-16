package coms309.people;


/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class Person {

    //private String firstName;
    private String artist;

    //private String lastName;
    private String album;

    //private String address;
    private String releaseDate;

    //private String telephone;
    private String rating;

    //private String socialSecurity;

    public Person(){
        
    }

    public Person(String firstName, String lastName, String address, String telephone){
        this.artist = artist;
        this.album = album;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setAddress(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return this.rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return artist + " "
               + album + " "
               + releaseDate + " "
               + rating;
    }
}
