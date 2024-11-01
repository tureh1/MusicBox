package onetomany.Album;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/{albumID}")
    public Album getAlbumById(@PathVariable Long albumID) {
        return albumRepository.findById(albumID)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with ID: " + albumID));
    }
}