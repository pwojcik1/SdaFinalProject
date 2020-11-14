package pl.sda.demo.web.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SearchEndpointITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Test
    @WithMockUser(username = "username")
    void testShouldReturnRecipesByProducts() throws Exception {
        //given
        ProductEntity productEntity1 = new ProductEntity(1, "product1");
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        ProductEntity productEntity3 = new ProductEntity(3, "product3");

        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);

        Set<ProductEntity> productEntities1 = new HashSet<>();
        productEntities1.add(productEntity1);

        Set<ProductEntity> productEntities2 = new HashSet<>();
        productEntities2.add(productEntity1);
        productEntities2.add(productEntity2);

        Set<ProductEntity> productEntities3 = new HashSet<>();
        productEntities3.add(productEntity3);


        RecipeEntity recipeEntity1 = new RecipeEntity(1, "recipe1", "desc1", productEntities1);
        RecipeEntity recipeEntity2 = new RecipeEntity(2, "recipe2", "desc2", productEntities2);
        RecipeEntity recipeEntity3 = new RecipeEntity(3, "recipe3", "desc3", productEntities3);

        jpaRecipeRepository.save(recipeEntity1);
        jpaRecipeRepository.save(recipeEntity2);
        jpaRecipeRepository.save(recipeEntity3);

        UserEntity userEntity = new UserEntity(1, "username", "pass", "USER", new HashSet<>(), productEntities2);
        jpaUserRepository.save(userEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("recipe1"));
    }

}