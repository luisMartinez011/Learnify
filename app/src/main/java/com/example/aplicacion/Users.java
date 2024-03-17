package com.example.aplicacion;

public class Users {
    private String Email;
    private String Password;
    private String Usertype;

    private String Uid;

    private String Bio;


    private String Nombre;

    private String Apellido;

    private String Usuario;

    public Users() {
    }

    public Users(String uid,String nombre, String apellido, String usuario, String bio,  String email, String password, String usertype) {
        Password = password;
        Email = email;
        Usertype = usertype;
        Nombre= nombre;
        Apellido= apellido;
        Usuario= usuario;
        Bio=bio;
        Uid=uid;
    }

    public String getPassword() {
        return Password;
    }

    public void setName(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}