package model;

public class ExistingCredentials {
    private String email = "liyjgjkjhgblk@gmail.com";
    private String password;
    private String name;

    public ExistingCredentials(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // конструктор без параметров
    public ExistingCredentials() {
    }

    public static ExistingCredentials from(User user) {
        return new ExistingCredentials(user.getEmail(), user.getName(), user.getPassword());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
