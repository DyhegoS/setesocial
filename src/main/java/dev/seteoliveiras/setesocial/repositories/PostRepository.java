package dev.seteoliveiras.setesocial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.seteoliveiras.setesocial.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
}
