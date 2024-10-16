/*package onetomany.Posts;

import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";



    @GetMapping(path = "/posts/getAll")
    public List<Post> getAllPosts(){
        return PostRepository.findAll();
    }

    @GetMapping(path = "/posts/byPostId/{postId}")
    Post getPostByPostId(@PathVariable int postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            return postOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }



    @GetMapping(path = "/posts/user/{userId}")
    public List<Post> getPostsByUserId(@PathVariable String userId) {
        return PostRepository.findByUserId(userId);
    }



    @GetMapping(path = "/posts/album/{albumId}")
    public List<Post> getPostsByMovieId(@PathVariable int albumId) {
        return postRepository.findByMovieId(albumId);
    }



    @PostMapping(path = "users/{userId}/create/post/album/{albumId}")
    public String createPost(@PathVariable("userId") String userId, @PathVariable("albumId") int albumId, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return failure;
        }
        User user = userOptional.get();

        post.setUser(user);
        post.setAlbumId(albumId);

        postRepository.save(post);
        return success;
    }




    @PutMapping(path = "users/{userId}/update/post/{postId}")
    Post updatePost(@RequestBody Post request, @PathVariable int postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            post.setTitle(request.getTitle());
            post.setContents(request.getContents());
            post.setRating(request.getRating());

            postRepository.save(post);

            return post;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }




    @DeleteMapping(path = "users/delete/post/{postId}")
    String deletePost(@PathVariable int postId){
        Optional<Post> postOptional = PostRepository.findById(postId);
        if (postOptional.isPresent()) {
            PostRepository.delete(postOptional.get());
            return success;
        }
        return failure;

    }

}
*/