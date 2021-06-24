package pl.sda.demo.external.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JpaUserRepositoryIntegrationTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;
    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    void testShouldGetUserByName() {
        //given
        //when
        Optional<UserEntity> user = jpaUserRepository.findUserByName("user2");
        //then
        assertTrue(user.isPresent());
        UserEntity result = user.get();
        assertEquals("user2", result.getUsername());
        assertEquals("password2", result.getPassword());
        assertEquals(2, result.getId());

        assertEquals(2, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(4)));

        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
    }
}

