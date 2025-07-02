package dev.seteoliveiras.setesocial.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.seteoliveiras.setesocial.controller.dto.CreatePostDto;
import dev.seteoliveiras.setesocial.entities.Post;
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

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDto dto, JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var post = new Post();
        post.setUser(user.get());
        post.setContent(dto.content());

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }
}
