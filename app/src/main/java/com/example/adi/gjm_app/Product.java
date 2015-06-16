package com.example.adi.gjm_app;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adi on 4/22/2015.
 */
public class Product implements Serializable {

    public final String nullLabel = "###";
    private String itemcode, itemname, name, merek, model, registrasi, kurs, spec, lastUpdate, price;
    private int id;

    public Product(int id, String itemcode, String itemname, String name, String merek, String model, String spec, String registrasi, String kurs, double price, String lastUpdate) {
        this.id = id;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.name = name;
        this.merek = merek;
        this.model = model;
        this.registrasi = registrasi;
        this.kurs = kurs;
        this.spec = spec;
        this.lastUpdate = lastUpdate;

        if(isDecimal(price)) {
            this.price = formatPrice(price);
        }
        else
        {
            this.price = Integer.toString((int) price);
        }
    }

    public Product(int id, String itemcode, String itemname, String name, String merek, String model, String spec, String registrasi, String kurs, String price, String lastUpdate) {
        this.id = id;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.name = name;
        this.merek = merek;
        this.model = model;
        this.registrasi = registrasi;
        this.kurs = kurs;
        this.spec = spec;
        this.lastUpdate = lastUpdate;
        this.price = price;
    }

    public String getKurs() {
        if(this.kurs.equals("NULL"))
            return nullLabel;
        else
            return kurs;
    }

    public void setKurs(String kurs) {
        this.kurs = kurs;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerek() {
        return merek;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrasi() {
        if(this.registrasi.equals("NULL"))
            return nullLabel;
        else
            return registrasi;
    }

    public void setRegistrasi(String registrasi) {
        this.registrasi = registrasi;
    }

    public String getSpec() {
        if(spec.equals("NULL"))
            return nullLabel;
        else
            return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //price
    public String getPrice()
    {
        if(price.equals("0"))
            return "silahkan hubungi kami";
        else
            return price;
    }
    public void setPrice(String price)
    {
        this.price = price;
    }
    public boolean isDecimal(double price)
    {
        double upCeil = Math.ceil(price);
        double difference = upCeil-price;
        if(difference==0)
            return false;
        else
            return true;
    }
    private String formatPrice(double price)
    {
        DecimalFormat df = new DecimalFormat("#,###,###.00");
        return df.format(price);
    }

    //date
    public String getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getFormatedDate(int pil){
        String format;
        String dateString = null;

        switch (pil){
            case 1 : format = "d MMM yyyy";
                break;
            case 2 : format = "d-MMM-yyyy";
                break;
            default: format = "d MMM yyyy";
                break;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(this.getLastUpdate());
            //sampe sini date = tanggal dalam date

            dateFormat = new SimpleDateFormat(format);

            dateString = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }
}
