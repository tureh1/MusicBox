package onetomany.Album;

import onetomany.Rating.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final RatingRepository ratingRepo;

    @Autowired
    public AlbumService(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    public double getAverageRating(int albumId) {
        return ratingRepo.getAverageRating(albumId);
    }
}