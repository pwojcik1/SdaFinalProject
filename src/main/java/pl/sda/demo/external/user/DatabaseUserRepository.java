package pl.sda.demo.external.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;

import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public void createUser(User user) {
        UserEntity userEntity = UserEntity.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .favorite(jpaUserRepository.findAllRecipesByIdInList(user.getRecipeId()))
                .products(jpaUserRepository.findAllProductsByIdInList(user.getProductId()))
                .build();
        jpaUserRepository.save(userEntity);
    }

    @Override
    public void updateUser(User user) {
        jpaUserRepository.findById(user.getId())
                .ifPresent(userEntity -> {
                    userEntity.updateFromDomain(user);
                    jpaUserRepository.save(userEntity);
                });
    }

    @Override
    public void deleteUser(int id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public void addProductToFridge(Product product, User user) {
        ProductEntity productEntity = ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        userEntity.getProducts().add(productEntity);
        jpaUserRepository.save(userEntity);

    }

    @Override
    public void removeProductFromFridge(int id, User user) {
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        Optional<ProductEntity> productEntity = jpaUserRepository.findProductById(id);
        if (productEntity.isPresent()) {
            userEntity.getProducts().remove(productEntity.get());
            jpaUserRepository.save(userEntity);
        } else {
            throw new IllegalStateException("You cannot remove nonexistent product");
        }
    }

    @Override
    public void addRecipeToFavourites(Recipe recipe, User user) {
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .products(jpaUserRepository.findAllProductsByIdInList(recipe.getProductId()))
                .build();
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        userEntity.getFavorite().add(recipeEntity);
        jpaUserRepository.save(userEntity);
    }

    @Override
    public void deleteRecipeFromFavourites(int id, User user) {
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        Optional<RecipeEntity> recipeEntity = jpaUserRepository.findRecipeById(id);
        if (recipeEntity.isPresent()) {
            userEntity.getFavorite().remove(recipeEntity.get());
            jpaUserRepository.save(userEntity);
        } else {
            throw new IllegalStateException("You dont have this recipe in favourites");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(ent -> User.builder()
                        .id(ent.getId())
                        .username(ent.getUsername())
                        .password(ent.getPassword())
                        .productId(jpaUserRepository.findAllProductsIdFromCollection(ent.getProducts()))
                        .recipeId(jpaUserRepository.findAllRecipesIdFromCollection(ent.getFavorite()))
                        .build());
    }

    @Override
    public Optional<Product> getProductFromFridgeByName(String name, User user) {
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        Optional<ProductEntity> productEntity = jpaUserRepository.getProductByName(name);
        if (productEntity.isPresent()) {
            if (userEntity.getProducts().contains(productEntity.get())) {
                return productEntity.map(ent -> Product.builder()
                        .id(ent.getId())
                        .name(ent.getName())
                        .build());
            }
            throw new IllegalStateException("You dont have this product in fridge");
        }
        throw new IllegalStateException("Product with given name doesnt exist");
    }
}
