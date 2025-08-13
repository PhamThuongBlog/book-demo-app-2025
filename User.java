/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fontend_app_call_api;

//class model cho user
public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String city;

    public User(int id, String name, String username, String email, String city) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.city = city;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
}

