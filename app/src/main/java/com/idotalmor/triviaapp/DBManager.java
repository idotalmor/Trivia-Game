package com.idotalmor.triviaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {//singleton


    public static final String DB_NAME="triviatatabase";
    private static DBManager dbManager;

    private DBManager(Context context){

        super(context,DB_NAME,null,1);
    }

    public static DBManager GetDBHelper(Context context){
        if (dbManager == null) {
            dbManager=new DBManager(context);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Food Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Food Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Food Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('What is the state dessert of Arizona?', 'Ice Cream', 'Chocolate Cake', 'Lane Cake','Creme Brulee',3)," +
                "('In 1963, which state sold the first fried dill pickle?', 'Texas', 'Arkansas', 'California','Florida',2)," +
                "('What is the national dish of germany?', 'Pot Roast', 'Baked Potato', 'Chicken Legs','Minestrone Soup',1)," +
                "('What is the state vegetable of Oklahoma?', 'Tomato', 'Strawberry', 'Watermelon','Cucumber',3)," +
                "('Who invented Coca-Cola?', 'John Pemberton', 'William Herschel', 'Joseph Priestley','Charles Alderton',1)," +
                "('What is the state food of South Carolina?', 'Chili con carne', 'Mashed Potato', 'Hamburger','Boiled Peanuts',4)," +
                "('Named the first Pizzeria in United States?', 'Pizzeria Mozza', 'Lombardi`s Pizza', 'Pizzeria Bianco','Sally`s Apizza',2);");

        //Movie Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Movie Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Movie Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('Which park is the most filmed location in the world?', 'Yellowstone', 'Yosemite', 'Grand Canyon','Central Park',4)," +
                "('In the Harry Potter series, what is the name of Harry pet owl?', 'Hedwig', 'Fluffy', 'Norbert','Dobby',1)," +
                "('Who played the female lead role in the 1986 sci-fi movie “alien” ?', 'Julia Roberts', 'Sigourney Weaver', 'Cate Blanchett','Natalie Portman',2)," +
                "('What is the name of the actress who played the Unsinkable Molly Brown in the 1997 movie Titanic?', 'Reese Witherspoon', 'Amy Adams', 'Kathy Bates','Glenn Close',3)," +
                "('What is the name of Mickey Mouse´s dog?', 'Pluto', 'Jupiter', 'Sun','Mars',1)," +
                "('Tom Hanks played “Captain Miller” in what legendary World War II movie?', 'April 9th', 'Inglourious Basterds', 'Fury','Saving Private Ryan',4)," +
                "('The original Ghostbusters movie was released in June of what year?', '1986', '1984', '1980','1979',2);");

        //Music Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Music Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Music Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('Who was the first woman to be inducted into the Rock and Roll Hall of Fame?', 'LaVern Baker', 'Aretha Franklin', 'Ruth Brown','Etta James',2)," +
                "('Who was the first performer at the 1969 Woodstock festival?', 'Grateful Dead', 'Janis Joplin', 'Richie Havens','Jimi Hendrix',3)," +
                "('How old was American musician Jimi Hendrix when he passed away in 1970?', '27', '25', '32','35',1)," +
                "('The first Eurovision song contest was held in what year?', '1950', '1956', '1962','1970',2)," +
                "('American singer-songwriter Johny Cash passed away in what year?', '2007', '1999', '2001','2003',4)," +
                "('What was the name of Taylor Swift’s first album?', '1989', 'Speak Now', 'Taylor Swift','Fearless',3)," +
                "('What song by Michael Jackson contains the lyrics “Annie are you OK?”', 'Smooth Criminal', 'Thriller', 'You Are Not Alone','Black or White',1);");

        //Animal Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Animal Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Animal Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('What is the name for the offspring of a male lion and a female tiger?', 'Liger', 'Tion', 'Kigter','Tigon',1)," +
                "('What is the only bird known to fly backwards?', 'Sparrow', 'Albatross', 'Swallow','Hummingbird',4)," +
                "('A panda’s daily diet consists almost entirely of what plant?', 'kohlrabi', 'Wild Tubers', 'Bamboo','Grass',3)," +
                "('What is the largest parrot in the world?', 'Umbrella Cockatoo', 'Hyacinth Macaw', 'African Grey','Amazon',2)," +
                "('What is the national animal of Scotland?', 'Unicorn', 'Parrot', 'Cat','Scottish Deerhound',1)," +
                "('How many hearts does an octopus have?', 'Three', 'One', 'Four','Six',1)," +
                "('What is a group of rhinoceros called?', 'Pod', 'Pride', 'Brood','Crash',4);");

        //Art Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Art Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Art Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('What is the most visited museum in Europe?', 'Dalí Theatre and Museum', 'Louvre', 'Van Gogh Museum','Royal Museums of Fine Arts',2)," +
                "('Who painted a late 15th-century mural known as the Last Supper?', 'Pablo Picasso', 'Rembrandt', 'Leonardo da Vinci','Vincent van Gogh',3)," +
                "('Which artist is credited with developing linear perspective?', 'Masaccio', 'Andrea Mantegna', 'William Hogarth','Brunelleschi',4)," +
                "('What French sculptor created the Statue of Liberty?', 'François-Joseph Duret', 'Frédéric Auguste Bartholdi', 'Antoine Étex','Ferdinand Faivre',2)," +
                "('How many paintings did Vincent Van Gogh sell during his lifetime?', 'One', 'Three', 'Ten','Zero',1)," +
                "('Which painter started the impressionist movement?', 'Mary Cassatt', 'Édouard Manet', 'Edgar Degas','Claude Monet',4)," +
                "('Who painted the Sistine Chapel?', 'Raphael', 'Michelangelo', 'John Singer Sargent','Titian',2);");

        //Computer Trivia

        db.execSQL("CREATE TABLE IF NOT EXISTS 'Computer Trivia' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,`question` VARCHAR NOT NULL , `answer1` VARCHAR NOT NULL," +
                " `answer2` VARCHAR NOT NULL, `answer3` VARCHAR NOT NULL, `answer4` VARCHAR NOT NULL,`correct_answer` INTEGER, `chosen_answer` INTEGER)");
        db.execSQL("INSERT INTO 'Computer Trivia' (question, answer1, answer2, answer3,answer4,'correct_answer') VALUES" +
                " ('Mark Zuckerberg was one of the founders of which social networking site?', 'Facebook', 'Myspace', 'Instagram','Snapchat',1)," +
                "('When did Apple start using Intel chips?', '2002', '2006', '2005','2003',3)," +
                "('HTML and CSS are computer languages used to create what?', 'Websites', 'Android Applications', 'Windows Programs','Linux Kernels',1)," +
                "('In what year was the first Apple computer released?', '1972', '1976', '1980','1982',2)," +
                "('Who is the father of computer?', 'Adam Osborne', 'Henry Edward Roberts', 'Steve Wozniak','Charles Babbage',4)," +
                "('In what year was the iPhone first released?', '2009', '2006', '2007','2004',3)," +
                "('Who was the first person to come up with a theory for software?', 'Ada Lovelace', 'Alan Turing', 'Evgenii Landis','Georgy Adelson-Velsky',2);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
