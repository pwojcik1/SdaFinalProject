package pl.sda.demo.domain.recipe;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServiceTest {
    private final RecipeRepository recipeRepository = Mockito.mock(RecipeRepository.class);
    private final RecipeService recipeService = new RecipeService(recipeRepository);
    private final ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);

    @Test
    void testShouldAddRecipeToDb(){
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        Recipe recipe = new Recipe(null, "recipe", "description", ids);
        //when
        Mockito.when(recipeRepository.findByRecipeName(recipe.getName())).thenReturn(Optional.empty());
        recipeService.addRecipeToDb(recipe);
        Mockito.verify(recipeRepository).addRecipeToDb(argumentCaptor.capture());
        Recipe result = argumentCaptor.getValue();
        //then
        Mockito.verify(recipeRepository).findByRecipeName("recipe");
        Mockito.verify(recipeRepository).addRecipeToDb(recipe);
        assertEquals(1, result.getProductId().size());
        assertTrue(result.getProductId().contains(1));
    }
    @Test
    void testShouldThrowExceptionIfRecipeAlreadyExists() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        Recipe recipe = new Recipe(1, "recipe", "description", ids);
        Mockito.when(recipeRepository.findByRecipeName(recipe.getName())).thenReturn(Optional.of(recipe));
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> recipeService.addRecipeToDb(recipe));
        //then
        Mockito.verify(recipeRepository).findByRecipeName("recipe");
        assertEquals("Recipe with same name already exists", ex.getMessage());
        Mockito.verify(recipeRepository, Mockito.never()).addRecipeToDb(recipe);
    }

    @Test
    void testShouldUpdateRecipe(){
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        List<Integer> newIds = new ArrayList<>();
        newIds.add(1);
        newIds.add(2);
        Recipe recipe = new Recipe(1, "anotherRecipe", "newDescription", newIds);
        //when
        Mockito.when(recipeRepository.findByRecipeId(recipe.getId())).thenReturn(Optional.of(new Recipe(1, "recipe", "description", ids )));
        recipeService.updateRecipeInDb(recipe);

        Mockito.verify(recipeRepository).updateRecipeInDb(argumentCaptor.capture());
        Recipe result = argumentCaptor.getValue();
        //then
        Mockito.verify(recipeRepository).findByRecipeId(recipe.getId());
        assertEquals(1, result.getId());
        assertEquals("anotherRecipe", result.getName());
        assertEquals("newDescription", result.getDescription());
        assertEquals(2, result.getProductId().size());
        assertTrue(result.getProductId().contains(1));
        assertTrue(result.getProductId().contains(2));
    }
    @Test
    void testShouldThrowExceptionIfIdIsDifferent(){
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        Recipe recipe = new Recipe(1, "recipe", "description", ids);
        //when
        Mockito.when(recipeRepository.findByRecipeId(recipe.getId())).thenReturn(Optional.of(new Recipe(2, "recipe", "description", ids )));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> recipeService.updateRecipeInDb(recipe));
        //then
        Mockito.verify(recipeRepository).findByRecipeId(recipe.getId());
        assertEquals("Cannot update product with different id", ex.getMessage());
        Mockito.verify(recipeRepository, Mockito.never()).updateRecipeInDb(recipe);
    }

    @Test
    void testShouldFindRecipeByName(){
        //given
        String name = "testRecipe";
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        Recipe recipe = new Recipe(1, "testRecipe", "description", ids);
        //when
        Mockito.when(recipeRepository.findByRecipeName(name)).thenReturn(Optional.of(recipe));
        Recipe result = recipeService.findByRecipeName(name);
        //then
        Mockito.verify(recipeRepository).findByRecipeName(name);
        assertEquals(1, result.getId());
        assertEquals("testRecipe", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(1, result.getProductId().size());
        assertTrue(result.getProductId().contains(1));
    }

    @Test
    void testShouldThrowExceptionForNonexistentRecipe(){
        //given
        String name = "nonexistentRecipe";
        //when
        Mockito.when(recipeRepository.findByRecipeName(name)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()->recipeService.findByRecipeName(name));
        //then
        assertEquals("recipe with given name doesnt exist", ex.getMessage());
    }

    @Test
    void testShouldReturnOneRecipe(){
        //given
        //when
        Mockito.when(recipeRepository.findByRecipeId(1)).thenReturn(Optional.of(new Recipe(1,"recipe1", "descrition", new ArrayList<>())));
        Recipe result = recipeService.getOne(1);
        //then
        Mockito.verify(recipeRepository).findByRecipeId(1);
        assertEquals(1, result.getId());
        assertEquals("recipe1", result.getName());
        assertEquals("description", result.getDescription());
        assertTrue(result.getProductId().isEmpty());
    }

    @Test
    void testShouldThrowExceptionIfIdIsIncorrect(){
        //given
        //when
        Mockito.when(recipeRepository.findByRecipeId(1)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()-> recipeService.getOne(1));
        //then
        Mockito.verify(recipeRepository).findByRecipeId(1);
        assertEquals("Recipe with given id doesnt exist", ex.getMessage());
    }

}