package onetomany.Album;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/{albumID}")
    public Optional<Album> getAlbumById(@PathVariable int albumID) { // Changed from String to int
        return albumRepository.findById(albumID);
    }
}
