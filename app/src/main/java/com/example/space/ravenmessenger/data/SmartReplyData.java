package com.example.space.ravenmessenger.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SmartReplyData {

    public static final List<String> defaultReplies = Collections.unmodifiableList(
            new ArrayList<String>(Arrays.asList("Ok", "Yes", "No", "\ud83d\udc4d", "\ud83d\udc4e", "\ud83d\ude04", "\ud83d\ude1e", "\u2764\ufe0f", "Lol", "Thanks", "Got it", "Done", "Nice", "I don't know", "What ?", "Why ?", "What's up ?"))
    );

    public static final Map<String, List<String>> repliesMap = Collections.unmodifiableMap(
            new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER) {{
                put("any chance ur free tonight", new ArrayList<>(Arrays.asList("Maybe not", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("any updates?", new ArrayList<>(Arrays.asList("No update yet", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("happy birthday!", new ArrayList<>(Arrays.asList("Hey, thanks", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("how was your weekend?", new ArrayList<>(Arrays.asList("It was real good", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("how you doing", new ArrayList<>(Arrays.asList("Okay and you", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm sick", new ArrayList<>(Arrays.asList("Sorry to hear that", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm so happy for you", new ArrayList<>(Arrays.asList("Thanks me too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm so hungry", new ArrayList<>(Arrays.asList("Haha me too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am sick", new ArrayList<>(Arrays.asList("Sorry to hear that", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am so happy for you", new ArrayList<>(Arrays.asList("Thanks me too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am so hungry", new ArrayList<>(Arrays.asList("Haha me too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("lunch?", new ArrayList<>(Arrays.asList("Yes coming", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("omg amazing", new ArrayList<>(Arrays.asList("So amazing", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("so sorry", new ArrayList<>(Arrays.asList("So sorry", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("sorry, i can't do saturday", new ArrayList<>(Arrays.asList("No worries at all", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("thanks for coming", new ArrayList<>(Arrays.asList("It was my pleasure", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("tomorrow would be ideal", new ArrayList<>(Arrays.asList("Yes it would", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("tried calling", new ArrayList<>(Arrays.asList("Try again?", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("ugh, my flight is delayed", new ArrayList<>(Arrays.asList("Ugh indeed", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("what are you guys up to tonight?", new ArrayList<>(Arrays.asList("Nothing planned", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("what day works best for you", new ArrayList<>(Arrays.asList("Any day", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("what time will you be home?", new ArrayList<>(Arrays.asList("Not sure why", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("where are you?!?", new ArrayList<>(Arrays.asList("At my house", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you're marvelous", new ArrayList<>(Arrays.asList("You are too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you are marvelous", new ArrayList<>(Arrays.asList("You are too", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("congratulations", new ArrayList<>(Arrays.asList("Thanks thanks", "Congratulations", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("hang in there, you'll be okay", new ArrayList<>(Arrays.asList("Doing my best", "Of course we will", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("happy new year!", new ArrayList<>(Arrays.asList("Wish you the same", "Thanks and same to you", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("have a safe flight", new ArrayList<>(Arrays.asList("Thanks, love you too", "Safe travels", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("how are you doing?", new ArrayList<>(Arrays.asList("Great and you?", "I am doing great", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm planning on coming next week. let me know if that works", new ArrayList<>(Arrays.asList("Works", "Perfect, thanks", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am planning on coming next week. let me know if that works", new ArrayList<>(Arrays.asList("Works", "Perfect, thanks", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("is there anything i can do to help?", new ArrayList<>(Arrays.asList("No, but thanks", "No, but thanks for asking", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("okay. lemme know as soon as you find out", new ArrayList<>(Arrays.asList("Any more questions?", "It is done", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("safe travels", new ArrayList<>(Arrays.asList("Thanks, love you too", "Safe travels", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("sorry, i can't", new ArrayList<>(Arrays.asList("No worries at all", "Sorry what?", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("sorry, i can not", new ArrayList<>(Arrays.asList("No worries at all", "Sorry what?", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("thanks, this has been great", new ArrayList<>(Arrays.asList("Glad to help", "So happy for you", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("what do you want for dinner", new ArrayList<>(Arrays.asList("Your call", "Whatever is fine", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("wish you were here", new ArrayList<>(Arrays.asList("I wish the same", "Me too honey", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("anything i can do to help?", new ArrayList<>(Arrays.asList("No, but thanks", "No, but thank you", "No, but thanks for asking", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("be safe", new ArrayList<>(Arrays.asList("I will be", "Will do my best", "Thanks, I will", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("cool, let me know when you have time", new ArrayList<>(Arrays.asList("Cool", "Yes very cool", "Yeah, cool", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("drive safe", new ArrayList<>(Arrays.asList("Thank you, I will", "Home now", "I will thanks", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("hey", new ArrayList<>(Arrays.asList("What is up?", "How it going?", "Can I help you?", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("hey, got a sec?", new ArrayList<>(Arrays.asList("What is up?", "How it going?", "Can I help you?", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("how are you feeling", new ArrayList<>(Arrays.asList("Feeling okay", "A little better", "Much much better", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("hugs", new ArrayList<>(Arrays.asList("So sweet", "Thanks sweetie", "Take care of yourself", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm bored", new ArrayList<>(Arrays.asList("Sorry to hear that", "Join the club", "No you are not", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm sorry", new ArrayList<>(Arrays.asList("No I am sorry", "Why sorry?", "No worries love", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i'm sorry, i'm going to have to cancel", new ArrayList<>(Arrays.asList("No I am sorry", "Why sorry?", "No worries love", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am bored", new ArrayList<>(Arrays.asList("Sorry to hear that", "Join the club", "No you are not", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am sorry", new ArrayList<>(Arrays.asList("No I am sorry", "Why sorry?", "No worries love", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("i am sorry, i am going to have to cancel", new ArrayList<>(Arrays.asList("No I am sorry", "Why sorry?", "No worries love", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("on my way", new ArrayList<>(Arrays.asList("Okay see you soon", "Cool, see you soon", "Oh wow, ok", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("oops, mistexted", new ArrayList<>(Arrays.asList("Oops", "Haha, oh well", "That was funny", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("thank you so much", new ArrayList<>(Arrays.asList("You are so welcome", "You are so very welcome", "You are most welcome", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you're amazing", new ArrayList<>(Arrays.asList("You are too", "You are amazing", "I am", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you're the best", new ArrayList<>(Arrays.asList("I do my best", "You are the best", "Well, I try", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you are amazing", new ArrayList<>(Arrays.asList("You are too", "You are amazing", "I am", "\ud83d\udc4d", "\ud83d\udc4e")));
                put("you are the best", new ArrayList<>(Arrays.asList("I do my best", "You are the best", "Well, I try", "\ud83d\udc4d", "\ud83d\udc4e")));
            }}
    );
}
