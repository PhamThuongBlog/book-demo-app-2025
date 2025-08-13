/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fontend_app_call_api;

// gọi API và parse JSON

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";

    public List<User> fetchUsers() {
        List<User> users = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder jsonSB = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonSB.append(line);
            }
            reader.close();

            JSONArray arr = new JSONArray(jsonSB.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                String username = obj.getString("username");
                String email = obj.getString("email");
                String city = obj.getJSONObject("address").getString("city");

                users.add(new User(id, name, username, email, city));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}

