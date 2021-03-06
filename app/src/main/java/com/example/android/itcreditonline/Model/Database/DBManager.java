package com.example.android.itcreditonline.Model.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.itcreditonline.Model.Credit;
import com.example.android.itcreditonline.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aydin on 29.9.2016 г..
 */

public class DBManager extends SQLiteOpenHelper {
    private static DBManager ourInstance;
    private static int version = 1;
    private Activity activity;
    private HashMap<String, User> registerredUsers;//username -> User


    public static DBManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DBManager(context);
        }
        return ourInstance;
    }

    private DBManager(Context context) {
        super(context, "CreditAppDB", null, version);
        registerredUsers = new HashMap<>();
        this.activity = (Activity) context;
        loadUsers();
    }

    private void loadUsers() {
        if (registerredUsers.isEmpty()) {
            //select all users from db
            Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM TABLE_USERS", null);
            while (cursor.moveToNext()) {

                String username = cursor.getString(0);
                String name = cursor.getString(1);
                String surname = cursor.getString(2);
                String password = cursor.getString(3);
                String email = cursor.getString(4);
                String phoneNumber = cursor.getString(5);
                String address = cursor.getString(6);
                String id = cursor.getString(7);
                User user = new User(username, name, surname, password, email, phoneNumber, address, id);
                registerredUsers.put(username, user);
            }
            Cursor cursor2 = getWritableDatabase().rawQuery("SELECT * FROM TABLE_CREDITS ", null);
            while (cursor2.moveToNext()) {
                int credit_id = cursor2.getInt(0);
                int duration = cursor2.getInt(1);
                double amount = cursor2.getDouble(2);
                String owner = cursor2.getString(3);
                Credit credit = new Credit(credit_id, duration, amount, owner);
                registerredUsers.get(owner).addCredit(credit);

            }


        }

        List<User> users = new ArrayList<>();
        for (User u : registerredUsers.values()) {
            users.add(u);
            Log.e("Credits", u.getCredits().toString());

            Log.e("Register users", users.toString());

        }


    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE TABLE_USERS (USERS_USERNAME text PRIMARY KEY,USERS_NAME text, USERS_SURNAME text, USERS_PASSWORD text, USERS_EMAIL text, USERS_PHONE_NUMBER text,USERS_ADDRESS text, USERS_ID text)");
        db.execSQL("CREATE TABLE TABLE_CREDITS (CREDITS_ID INTEGER PRIMARY KEY AUTOINCREMENT , CREDITS_DURATION text, CREDITS_AMOUNT,CREDITS_OWNER text, FOREIGN KEY(CREDITS_OWNER) REFERENCES TABLE_USERS(USERS_USERNAME))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE users");
        onCreate(db);
    }

    public void registerUser(String username, String name, String surname, String pass, String email, String phoneNumber, String address, String id) {
        ContentValues values = new ContentValues();
        values.put("USERS_USERNAME", username);
        values.put("USERS_NAME", name);
        values.put("USERS_SURNAME", surname);
        values.put("USERS_PASSWORD", pass);
        values.put("USERS_EMAIL", email);
        values.put("USERS_PHONE_NUMBER", phoneNumber);
        values.put("USERS_ADDRESS", address);
        values.put("USERS_ID", id);
        getWritableDatabase().insert("TABLE_USERS", null, values);
        registerredUsers.put(username, new User(username, name, surname, pass, email, phoneNumber, address, id));

    }

    public void addCredit(int duration, double amount, String owner) {
        ContentValues values = new ContentValues();
        values.put("CREDITS_DURATION", duration);
        values.put("CREDITS_AMOUNT", amount);
        values.put("CREDITS_OWNER", owner);
        getWritableDatabase().insert("TABLE_CREDITS", null, values);
        Credit credit = new Credit(duration, amount, owner);
        getUser(owner).addCredit(credit);
        Log.e("List size", getUser(owner).getCredits().size() + "");

    }


    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean validatePhone(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public boolean validateUser(String username, String password) {
        if (existsUser(username)) {
            return registerredUsers.get(username).getPass().equals(password);
        }
        return false;
    }

    public void changeSurname(String username, String surname) {
        ContentValues newValues = new ContentValues();
        newValues.put("USERS_SURNAME", surname);
        getWritableDatabase().update("TABLE_USERS", newValues, "USERS_USERNAME='" + username + "'", null);
        getUser(username).setSurname(surname);
    }

    public void changeEmail(String username, String email) {
        ContentValues newValues = new ContentValues();
        newValues.put("USERS_EMAIL", email);
        getWritableDatabase().update("TABLE_USERS", newValues, "USERS_USERNAME='" + username + "'", null);
        getUser(username).setEmail(email);
    }

    public void changePhone(String username, String phone) {
        ContentValues newValues = new ContentValues();
        newValues.put("USERS_PHONE_NUMBER", phone);
        getWritableDatabase().update("TABLE_USERS", newValues, "USERS_USERNAME='" + username + "'", null);
        getUser(username).setPhoneNumber(phone);
    }

    public void changeAddress(String username, String address) {
        ContentValues newValues = new ContentValues();
        newValues.put("USERS_ADDRESS", address);
        getWritableDatabase().update("TABLE_USERS", newValues, "USERS_USERNAME='" + username + "'", null);
        getUser(username).setAddress(address);
    }

    public User getUser(String username) {
        return registerredUsers.get(username);

    }

    public void saveLastLoggedUser(String loggedUser) {
        SharedPreferences prefs = activity.getSharedPreferences("ITCreditsOnline", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = "lastLoggedUser";
        editor.putString(key, loggedUser);
        editor.apply();
    }

    public String getLastLoggedUser() {
        SharedPreferences prefs = activity.getSharedPreferences("ITCreditsOnline", Context.MODE_PRIVATE);
        return prefs.getString("lastLoggedUser", "No logged user!");
    }

    public void logout() {
        SharedPreferences prefs = activity.getSharedPreferences("ITCreditsOnline", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = "lastLoggedUser";
        editor.putString(key, "No logged user!");
        editor.apply();
    }

    public boolean existsUser(String username) {
        return registerredUsers.containsKey(username);
    }

    public void deleteUser(String username) {
        getWritableDatabase().delete("TABLE_USERS", "USERS_USERNAME = ?", new String[]{username});
    }
}


