package dev.seteoliveiras.setesocial.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.seteoliveiras.setesocial.controller.dto.CreatePostDto;
import dev.seteoliveiras.setesocial.controller.dto.FeedDto;
import dev.seteoliveiras.setesocial.controller.dto.FeedItemDto;
import dev.seteoliveiras.setesocial.entities.Post;
import dev.seteoliveiras.setesocial.entities.Role;
import dev.seteoliveiras.setesocial.repositories.PostRepository;
import dev.seteoliveiras.setesocial.repositories.UserRepository;

@RestController
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        var posts = postRepository
                    .findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                    .map(post -> new FeedItemDto(post.getPostId(), post.getContent(), post.getUser().getUsername()));
                    
        return ResponseEntity.ok(new FeedDto(posts.getContent(), page, pageSize, posts.getTotalPages(), posts.getTotalElements()));
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDto dto, JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var post = new Post();
        post.setUser(user.get());
        post.setContent(dto.content());

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        var isAdmin = user.get().getRoles()
                .stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));            

        if(isAdmin || post.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            postRepository.deleteById(postId);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok().build();
    }
}
