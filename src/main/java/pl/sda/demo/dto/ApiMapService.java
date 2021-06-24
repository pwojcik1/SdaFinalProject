package pl.sda.demo.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.external.product.DatabaseProductRepository;
import pl.sda.demo.external.recipe.DatabaseRecipeRepository;
import pl.sda.demo.external.user.DatabaseUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiMapService {

    private final DatabaseUserRepository databaseUserRepository;
    private final DatabaseProductRepository databaseProductRepository;

    public List<RecipeDto> mapToRecipeDto(List<Recipe> recipes){
        List<RecipeDto> recipeDtos = new ArrayList<>();
        for(Recipe r: recipes){
            List<Product> collect = r.getProductId().stream().map(n->databaseProductRepository.findProductById(n).get()).collect(Collectors.toList());
            RecipeDto recipeDto = new RecipeDto(r.getId(), r.getName(),r.getDescription(),collect);
            recipeDtos.add(recipeDto);
        }
        return recipeDtos;
    }

    public LoginRq convertUserToLoginRq(String username) {
        Optional<User> user = databaseUserRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User doesnt exists");
        }
        return new LoginRq(user.get().getUsername(), user.get().getPassword(), user.get().getRole());
    }

    public User convertToUser(LoginRq loginRq) {
        return new User(null, loginRq.getUsername(), loginRq.getPassword(), null, null, loginRq.getRole());
    }
}
