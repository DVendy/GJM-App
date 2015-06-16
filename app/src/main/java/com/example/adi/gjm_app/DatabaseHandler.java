package com.example.adi.gjm_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Adi on 4/22/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper  {

    private static final String DB_PATH = "data/data/com.example.adi.gjm_app/databases";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final int DATABASE_VERSION = 14;

    /*Table array*/
    private ArrayList<tableDesc> tableCollection;
    private tableDesc tableVersion;
    private tableDesc tableProduct;
    private tableDesc tableUser;
    private tableDesc tableLoginHistory;
    private tableDesc tableNews;

    private static final String DATABASE_NAME = "GJM_DB";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        initializeTableAttr();
    }

    public void createDataBase() {
        boolean dbExist;
        try {
            dbExist = checkDataBase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw new Error("database dose not exist");
        }
        if(!dbExist){
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH +"/"+ DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
            // throw new Error("database does't exist yet.");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{
        //copyDataBase();
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH +"/"+ DATABASE_NAME;
        File databaseFile = new File( DB_PATH);
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()){
            databaseFile.mkdir();
        }

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //execute query
        for(int i=0; i<tableCollection.size(); i++)
        {
            db.execSQL(tableCollection.get(i).queryBuilderCreateTable());
        }

        //initial action, adding 1 row to version table
        ContentValues values = new ContentValues();
        values.put(tableVersion.column.get(1).name, "2015-01-01 00:00:00");
        db.insert(tableVersion.name, null, values);
    }

    public void initializeTableAttr()
    {
        if(tableCollection==null) {
            tableCollection = new ArrayList<tableDesc>();

            /*Initialize table*/
            tableVersion = new tableDesc("VERSION");
            tableVersion.column.add(new columnDesc("id", "INTEGER", true));
            tableVersion.column.add(new columnDesc("date", "TEXT"));
            tableCollection.add(tableVersion);

            tableProduct = new tableDesc("PRODUCT");
            tableProduct.column.add(new columnDesc("id", "INTEGER", true));
            tableProduct.column.add(new columnDesc("itemcode", "TEXT"));
            tableProduct.column.add(new columnDesc("itemname", "TEXT"));
            tableProduct.column.add(new columnDesc("name", "TEXT"));
            tableProduct.column.add(new columnDesc("merek", "TEXT"));
            tableProduct.column.add(new columnDesc("model", "TEXT"));
            tableProduct.column.add(new columnDesc("registrasi", "TEXT"));
            tableProduct.column.add(new columnDesc("kurs", "TEXT"));
            tableProduct.column.add(new columnDesc("spec", "TEXT"));
            tableProduct.column.add(new columnDesc("price", "TEXT"));
            tableProduct.column.add(new columnDesc("lastupdate", "TEXT"));
            tableCollection.add(tableProduct);

            tableUser = new tableDesc("USER");
            tableUser.column.add(new columnDesc("id", "INTEGER", true));
            tableUser.column.add(new columnDesc("username", "TEXT"));
            tableUser.column.add(new columnDesc("password", "TEXT"));
            tableUser.column.add(new columnDesc("name", "TEXT"));
            tableUser.column.add(new columnDesc("email", "TEXT"));
            tableUser.column.add(new columnDesc("phone", "TEXT"));
            tableCollection.add(tableUser);

            tableLoginHistory = new tableDesc("LOGIN_HISTORY");
            tableLoginHistory.column.add(new columnDesc("id", "INTEGER", true));
            tableLoginHistory.column.add(new columnDesc("date", "TEXT"));
            tableLoginHistory.column.add(new columnDesc("user_id", "INTEGER"));
            tableCollection.add(tableLoginHistory);

            tableNews = new tableDesc("News");
            tableNews.column.add(new columnDesc("id", "INTEGER", true));
            tableNews.column.add(new columnDesc("title", "TEXT"));
            tableNews.column.add(new columnDesc("body", "TEXT"));
            tableNews.column.add(new columnDesc("date", "TEXT"));
            tableCollection.add(tableNews);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion)
    {
        for(int i=0; i<tableCollection.size(); i++)
        {
            db.execSQL("DROP TABLE IF EXISTS "+ tableCollection.get(i).name +";");
        }
        onCreate(db);
    }

    /*VERSION TABLE*/
    //get GJM-App version
    public String getVersion()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor resultSet = db.rawQuery("SELECT * FROM " + tableVersion.name + " LIMIT 1", null);
        resultSet.moveToFirst();
        String result = resultSet.getString(1);
        resultSet.close();

        return result;
    }
    //update GJM-App version
    public void updateVersion(String version)
    {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE "+ tableVersion.name +" SET date='" + version + "' WHERE id = 1 ");
        db.close();
    }

    /*PRODUCT TABLE*/
    //insert a product to database
    public void createProduct(Product product)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tableProduct.column.get(1).name, product.getItemcode());
        values.put(tableProduct.column.get(2).name, product.getItemname());
        values.put(tableProduct.column.get(3).name, product.getName());
        values.put(tableProduct.column.get(4).name, product.getMerek());
        values.put(tableProduct.column.get(5).name, product.getModel());
        values.put(tableProduct.column.get(6).name, product.getRegistrasi());
        values.put(tableProduct.column.get(7).name, product.getKurs());
        values.put(tableProduct.column.get(8).name, product.getSpec());
        values.put(tableProduct.column.get(9).name, product.getPrice());
        values.put(tableProduct.column.get(10).name, product.getLastUpdate());

        db.insert(tableProduct.name, null, values);
        db.close();
    }
    //get products by limit and offset
    public ArrayList<Product> getProducts(int limit, int offset, String param)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //System.out.println(param);
        String whereClause = "";
        if(param.length()>0) //if has param
            whereClause = " WHERE "+ param;

        Cursor cursor = db.rawQuery("SELECT * FROM "+ tableProduct.name + whereClause +" LIMIT "+ limit +" OFFSET "+ offset, null);
        cursor.moveToFirst();

        ArrayList<Product> productList = new ArrayList<Product>();
        while(!cursor.isAfterLast()){
            productList.add(new Product(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(8),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(9),
                    cursor.getString(10)));
            cursor.moveToNext();
        }
        return productList;
    }
    //get product by its id
    public Product getProductById(int id)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor resultSet = db.rawQuery("SELECT * FROM " + tableProduct.name + " WHERE " + tableProduct.getPrimaryColumnName() + " = " + id, null);
        resultSet.moveToFirst();
        Product product = new Product(
                Integer.parseInt(resultSet.getString(0)),
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(8),
                resultSet.getString(6),
                resultSet.getString(7),
                Double.parseDouble(resultSet.getString(9)),
                resultSet.getString(10));
        resultSet.close();

        return product;
    }
    //get all merk
    public ArrayList<String> getAllMerk() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT "+ tableProduct.column.get(4).name +" FROM " + tableProduct.name, null);
        cursor.moveToFirst();

        ArrayList<String> merkList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            merkList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return merkList;
    }
    //update product data
    public void updateProduct(Product product)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tableProduct.column.get(1).name, product.getItemcode());
        values.put(tableProduct.column.get(2).name, product.getItemname());
        values.put(tableProduct.column.get(3).name, product.getName());
        values.put(tableProduct.column.get(4).name, product.getMerek());
        values.put(tableProduct.column.get(5).name, product.getModel());
        values.put(tableProduct.column.get(6).name, product.getRegistrasi());
        values.put(tableProduct.column.get(7).name, product.getKurs());
        values.put(tableProduct.column.get(8).name, product.getSpec());
        values.put(tableProduct.column.get(9).name, product.getPrice());
        values.put(tableProduct.column.get(10).name, product.getLastUpdate());

        db.update(tableProduct.name, values, tableProduct.getPrimaryColumnName() + "=?", new String[]{String.valueOf(product.getId())});
        db.close();
    }
    //count products in database
    public int countProducts(String param)
    {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "";
        if(param.length()>0) //if has param
            whereClause = " WHERE "+ param;

        Cursor resultSet = db.rawQuery("SELECT COUNT(*) FROM " + tableProduct.name + whereClause, null);
        resultSet.moveToFirst();
        int result = Integer.parseInt(resultSet.getString(0));
        resultSet.close();

        return result;
    }
    //delete current product
    public void deleteProduct(Product product)
    {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM "+ tableProduct.name +" WHERE itemcode = '"+ product.getItemcode() +"'");
        db.close();
    }
    //delete list of product
    public void deleteProducts(List<Product> products){
        for(Iterator<Product> i = products.iterator(); i.hasNext(); ) {
            Product item = i.next();
            deleteProduct(item);
        }
    }

    /*USER TABLE*/
    //insert an user to user table
    public void createUser(User user)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tableUser.column.get(0).name, user.getId());
        values.put(tableUser.column.get(1).name, user.getUsername());
        values.put(tableUser.column.get(2).name, user.getPassword());
        values.put(tableUser.column.get(3).name, user.getName());
        values.put(tableUser.column.get(4).name, user.getEmail());
        values.put(tableUser.column.get(5).name, user.getPhone());

        db.insert(tableUser.name, null, values);
        db.close();
    }
    //get an user from database
    public User getUser(String username){
        SQLiteDatabase db = getReadableDatabase();

        Cursor resultSet = db.rawQuery("SELECT * FROM " + "user" + " WHERE username = '" + username + "'", null);
        if(resultSet!=null) {
            if(resultSet.moveToFirst()) {
                User user = new User(Integer.parseInt(resultSet.getString(0)),
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5)
                );
                return user;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    //update an user
    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE "+ tableUser.name +" SET name = '"+ user.getName() +"', password = '"+ user.getPassword() +"' WHERE id = "+ user.getId() +";");
        db.close();
    }
    //delete an user from database
    public void deleteUser(int id)
    {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM "+ tableUser.name +" WHERE "+ tableUser.getPrimaryColumnName() +" = "+ id +";");
        db.execSQL("VACUUM;");

        db.close();
    }

    /*LOGIN HISTORY TABLE*/
    //create a single row login history
    public void createLoginHistory(String date, int user_id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tableLoginHistory.column.get(1).name, date);
        values.put(tableLoginHistory.column.get(2).name, user_id);

        db.insert(tableLoginHistory.name, null, values);
        db.close();
    }
    //get all row in login history
    public ArrayList<LoginHistory> getAllLoginHistory(){
        ArrayList<LoginHistory> loginData = new ArrayList<LoginHistory>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM "+ tableLoginHistory.name, null);

        if(resultSet!=null) {
            if(resultSet.moveToFirst()) {
                while (!resultSet.isAfterLast()) {
                    loginData.add(new LoginHistory(
                            resultSet.getString(1),
                            Integer.parseInt(resultSet.getString(2))));
                    resultSet.moveToNext();
                }
                return loginData;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    //delete all row in login history
    public void deleteAllLoginHistory(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM "+ tableLoginHistory.name +";");
        db.execSQL("VACUUM;");

        db.close();
    }

    /*NEWS TABLE*/
    //insert an user to user table
    public void createNews(News news)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tableNews.column.get(0).name, news.getId());
        values.put(tableNews.column.get(1).name, news.getTitle());
        values.put(tableNews.column.get(2).name, news.getContent());
        values.put(tableNews.column.get(3).name, news.getDate());

        db.insert(tableNews.name, null, values);
        db.close();
    }
    //get all news
    public ArrayList<News> getNews(int limit, int offset)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ tableNews.name +" ORDER BY "+ tableNews.column.get(0).name +" DESC LIMIT "+ limit +" OFFSET "+ offset*limit, null);
        cursor.moveToFirst();

        ArrayList<News> newsList = new ArrayList<News>();
        while(!cursor.isAfterLast()){
            newsList.add(new News(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)));
            cursor.moveToNext();
        }

        return newsList;
    }
    //count products in database
    public int getLatestNewsId()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor resultSet = db.rawQuery("SELECT MAX(" + tableNews.column.get(0).name + ") FROM " + tableNews.name, null);
        resultSet.moveToFirst();
        int result;

        if(resultSet.getString(0)!=null)
            result = Integer.parseInt(resultSet.getString(0));
        else
            result = 0;

        resultSet.close();

        return result;
    }
}