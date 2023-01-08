package com.sellspot.app.Models;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GuestUser {

    private List<List<CartPerStore>> cartperstoreproducts;
    public static final GuestUser sharedGuest = new GuestUser();

    private GuestUser() {
    }



    public List<List<CartPerStore>> getCartperstoreproducts() {
        return cartperstoreproducts;
    }

    public void setCartperstoreproducts(List<List<CartPerStore>> cartperstoreproducts, Context context) throws IOException {
        this.cartperstoreproducts = cartperstoreproducts;
        FileOutputStream fos = context.openFileOutput("guestuser", Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(cartperstoreproducts);
        os.close();
        fos.close();
    }

    public void saveToFile(CartPerStore cps, Context context) throws IOException, ClassNotFoundException {
        Boolean flag= false;
        if(cartperstoreproducts != null) {
            for (int i = 0; i < cartperstoreproducts.size(); i++) {
                if (cartperstoreproducts.get(i).size() > 0) {
                    if (cartperstoreproducts.get(i).get(0).getUserid().equals(cps.getUserid())) {
                        cartperstoreproducts.get(i).add(cps);
                        flag = true;
                        break;
                    }
                }
            }
            if(flag == false)
            {
                List<CartPerStore> cpsArray =  new ArrayList<>();
                cpsArray.add(cps);
                cartperstoreproducts.add(cpsArray);
            }
        }
        else{
            cartperstoreproducts = new ArrayList<>();
            List<CartPerStore> cpsArray =  new ArrayList<>();
            cpsArray.add(cps);
            cartperstoreproducts.add(cpsArray);
        }
        FileOutputStream fos = context.openFileOutput("guestuser", Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(cartperstoreproducts);
        os.close();
        fos.close();
    }

    public void readFromFile(Context context) throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = context.openFileInput("guestuser");
            ObjectInputStream is = new ObjectInputStream(fis);
            cartperstoreproducts = (List<List<CartPerStore>>) is.readObject();
            is.close();
            fis.close();
        }
        catch (IOException e)
        {

        }
    }
}
