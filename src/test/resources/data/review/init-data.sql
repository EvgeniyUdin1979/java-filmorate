MERGE INTO film
    (id,name,description,release_date,duration,rating_id)
VALUES
    (100,'New film #1','New film about friends','1999-04-30',120,3),
    (101,'New film #2','New film about friends','1949-04-30',1320,2),
    (102,'New film #3','New film about friends','1949-04-30',420,1);
MERGE INTO users
    (id,name,login,email,birthday)
VALUES
    (100,'Stas Name','dolore_1','mail@mail.ru','1946-08-20'),
    (101,'Nick Name','dolore_2','mail@mail.ru','1946-08-20'),
    (102,'Petr Name','dolore_3','mail@mail.ru','1946-08-20');
MERGE INTO reviews
    (review_id,content,is_positive,user_id,film_id)
VALUES
    (100,'Some content 1',-1,100,100),
    (101,'Some content 1',-1,101,101),
    (102,'Some content 1',1,102,102);
MERGE INTO review_likes
    (review_id,user_id,is_like)
VALUES
    (101,100,-1),
    (101,102,-1),
    (102,101,1),
    (102,102,1);

