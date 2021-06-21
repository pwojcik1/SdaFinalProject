INSERT INTO products
VALUES (1, 'Product1'),
       (2, 'Product2'),
       (3, 'Product3'),
       (4, 'Product4'),
       (5, 'Product5'),
       (6, 'Product6'),
       (7, 'Product7'),
       (8, 'Product8'),
       (9, 'Product9');

INSERT INTO recipes
VALUES (1, 'description for recipe1', 'Recipe1'),
       (2, 'description for recipe2', 'Recipe2'),
       (3, 'description for recipe3', 'Recipe3'),
       (4, 'description for recipe4', 'Recipe4');

INSERT INTO recipe_product
VALUES (1, 1),
       (1, 4),
       (1, 6),
       (2, 1),
       (2, 2),
       (2, 8),
       (2, 9),
       (3, 2),
       (3, 4),
       (4, 3),
       (4, 5),
       (4, 7);

INSERT INTO users
VALUES (1, 'password1', 'USER', 'user1'),
       (2, 'password2', 'USER', 'user2'),
       (3, 'password3', 'ADMIN', 'admin');

INSERT INTO user_recipe
values (1, 1),
       (1, 2),
       (2, 2),
       (2, 4),
       (3, 2),
       (3, 3),
       (3, 4);

INSERT INTO user_product
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (2, 1),
       (2, 2),
       (3, 1),
       (3, 2),
       (3, 3),
       (3, 4),
       (3, 5),
       (3, 6),
       (3, 7),
       (3, 8),
       (3, 9);