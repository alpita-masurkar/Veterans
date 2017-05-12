package edu.sdsu.androidfinal.androidfinalproject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anish_Bivalkar on 5/10/17.
 */

public class User {

    public String nickname;
    public String email;
    public String password;
    public String role;

    public Map<String, Boolean> stars = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public User(String nickname, String email, String password, String role) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nickname", nickname);
        result.put("email", email);
        result.put("password", password);
        result.put("role", role);
      //  result.put("badges", badges);
       // result.put("level", level);

        return result;
    }

}
