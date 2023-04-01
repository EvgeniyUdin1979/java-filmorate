INSERT INTO PUBLIC.RATING(NAME) VALUES ('G'),
    	('PG'),
	    ('PG-13'),
	    ('R'),
	    ('NC-17')
    ON CONFLICT DO NOTHING ;

INSERT INTO PUBLIC.GENRE
	(NAME) VALUES ('Комедия'),
                    ('Драма'),
                    ('Мультфильм'),
                    ('Триллер'),
                    ('Документальный'),
                    ('Боевик')
    ON CONFLICT DO NOTHING ;





    
    
    
    
    
    





