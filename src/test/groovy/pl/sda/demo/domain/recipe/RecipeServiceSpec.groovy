package pl.sda.demo.domain.recipe

import spock.lang.Specification

class RecipeServiceSpec extends Specification {

    def recipeRepository = Mock(RecipeRepository)
    RecipeService recipeService = new RecipeService(recipeRepository)

    def "should add recipe to db"() {
        given:
        Recipe recipe = new Recipe(null, "recipeName", "recipeDescription", new ArrayList<>())

        when:
        recipeService.addRecipeToDb(recipe)

        then:
        1 * recipeRepository.findRecipeByName(recipe.getName()) >> Optional.empty()
        1 * recipeRepository.addRecipeToDb(recipe)
    }

    def "should throw exception if recipe with given name is already in db"() {
        given:
        Recipe recipe = new Recipe(null, "recipeName", "recipeDescription", new ArrayList<>())

        when:
        recipeService.addRecipeToDb(recipe)

        then:
        1 * recipeRepository.findRecipeByName(recipe.getName()) >> Optional.of(new Recipe())
        def e = thrown(IllegalStateException)
        e.message == "Recipe with same name already exists"
        0 * recipeRepository.addRecipeToDb(recipe)
    }

    def "should update recipe in db"() {
        given:
        Recipe recipe = new Recipe(1, "newRecipeName", "newRecipeDescription", new ArrayList<>())
        recipe.productId << 1

        when:
        recipeService.updateRecipeInDb(recipe)
        then:
        1 * recipeRepository.findRecipeById(recipe.getId()) >> Optional.of(new Recipe(1, "recipeName", "recipeDescription", new ArrayList<>()))
        1 * recipeRepository.updateRecipeInDb(recipe)
    }

    def "should throw exception when id is different"() {
        given:
        Recipe recipe = new Recipe(1, "newRecipeName", "newRecipeDescription", new ArrayList<>())
        when:
        recipeService.updateRecipeInDb(recipe)
        then:
        1 * recipeRepository.findRecipeById(recipe.getId()) >> Optional.of(new Recipe(2, "recipeName", "recipeDescription", new ArrayList<>()))
        def e = thrown(IllegalStateException)
        e.message == "Cannot update product with different id"
        0 * recipeRepository.updateRecipeInDb(recipe)
    }

    def "should throw exception when recipe with given name doesnt exist"() {
        when:
        recipeService.findRecipeByName("recipeName")
        then:
        1 * recipeRepository.findRecipeByName("recipeName") >> Optional.empty()
        def e = thrown(IllegalStateException)
        e.message == "recipe with given name doesnt exist"
    }
    def "should throw exception when recipe with given id doesnt exist"() {
        when:
        recipeService.findRecipeById(1)
        then:
        1 * recipeRepository.findRecipeById(1) >> Optional.empty()
        def e = thrown(IllegalStateException)
        e.message == "Recipe with given id doesnt exist"
    }
}
