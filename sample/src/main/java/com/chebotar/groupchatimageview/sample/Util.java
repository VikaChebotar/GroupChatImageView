package com.chebotar.groupchatimageview.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by vika on 23.05.16.
 */
public class Util {
    public static final List<ChatRoom> chatRooms = new ArrayList<ChatRoom>();

    static {
        List<User> users = new ArrayList<User>();
        users.add(new User(1, "User 1", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTw-wr7AmIkxWVM31LNuf8OXMaBE5TQnAWyYwc1FFM8Q5i6bYysGaD64zc"));
        users.add(new User(2, "User 2", "http://pets.petsmart.com/services/_images/grooming/dog/m_t/dog-aromatherapy.jpg"));
        users.add(new User(3, "User 3", "http://img1.rnkr-static.com/list_img_v2/5795/1825795/C480/best-of-awwducational-fun-facts-about-adorable-animals-u1.jpg"));
        users.add(new User(4, "User 4", "https://pixabay.com/static/uploads/photo/2015/09/25/21/33/giraffe-958243_960_720.jpg"));
        users.add(new User(5, "User 5", "http://f.tqn.com/y/animals/1/S/r/P/1/GettyImages-578682305.jpg"));
        users.add(new User(6, "User 6", "http://www.activesustainability.com/media/80502/leopardo-de-las-nievess_g.jpg"));
        users.add(new User(7, "User 7", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcS7tbZqyl2-3u2oH5O3kt1M7T5MPaMdxX0anbecnBF-W2kEAlmo"));
        users.add(new User(8, "User 8", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTj1GHvzdJjlrJijpJinHqUDjagyXqD75cO4nRFL1zI2zpxY1qo"));

        Random random = new Random();
        int min = 1, max = 4;


        for (int i = 0; i < 30; i++) {
            int randomNum = random.nextInt((max - min) + 1) + min;
            chatRooms.add(new ChatRoom(i + 1, pickNRandom(users, randomNum), "message " + (i + 1), "23/05/2016"));
        }
    }

    public static List<User> pickNRandom(List<User> lst, int n) {
        List<User> copy = new LinkedList<User>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }
}
