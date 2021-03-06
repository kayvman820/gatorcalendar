package com.group18.app.calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.group18.app.calendar.database.CommitmentHelper;
import com.group18.app.calendar.database.CommitmentSchema;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//this is the Activity that is launched when app is started, see manifest file
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mytoolbar;
    private NavigationView myNavView;
    private DrawerLayout myDrawerLayout;
    private Button startaddClass;
    private ArrayList<Commitments> myCommits = new ArrayList<>();
    private boolean mScheduleVisible = true;
    private static final String SAVED_SCHEDULE_VISIBLE = "schedule";
    private static final int AddClassCode = 0; //code used to identify result information coming from AddClassActivity
    private Context mContext;
    private SQLiteDatabase mDatabase;
    CommitmentHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        mytoolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(mytoolbar);
        mytoolbar.setTitle(R.string.app_name);


        //upon rotation, activity is recreated, retrieve icon status from savedInstanceState
        if(savedInstanceState != null){
            mScheduleVisible = savedInstanceState.getBoolean(SAVED_SCHEDULE_VISIBLE);
        }

//myCommits.add()
        myNavView = findViewById(R.id.nav_view);
        myNavView.setNavigationItemSelectedListener(this);

        myDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,myDrawerLayout,mytoolbar,R.string.open_drawer,R.string.close_drawer);
        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        File dbtest = new File("/data/data/com.group18.app.calendar/databases/commitments");
        Context ctx = MainActivity.this; // for Activity, or Service. Otherwise simply get the context.
        String dbname = "commitments.db";
        File dbpath = ctx.getDatabasePath(dbname);
        if(dbpath.exists()) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] projections = {CommitmentSchema.CommitmentTable.Cols.PROFESSOR,
                    CommitmentSchema.CommitmentTable.Cols.CNAME,
                    CommitmentSchema.CommitmentTable.Cols.ID,
                    CommitmentSchema.CommitmentTable.Cols.ONTHESEDAYS,
                    CommitmentSchema.CommitmentTable.Cols.START,
                    CommitmentSchema.CommitmentTable.Cols.END
            };
            Cursor cursor = db.query(CommitmentSchema.CommitmentTable.NAME, projections,
                    null, null, null, null, null);


            while (cursor.moveToNext()) {
                String professor = cursor.getString(cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.PROFESSOR));
                String cname = cursor.getString(
                        cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.CNAME));
//            String id = cursor.getString(
//                    cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.ID));
                String days = cursor.getString(
                        cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.ONTHESEDAYS));
                //gotta check if i need to convert date to string
//            String start = cursor.getString(cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.START));
//
//            String end = cursor.getString(cursor.getColumnIndexOrThrow(CommitmentSchema.CommitmentTable.Cols.END));

                Commitments obj1 = new Commitments(professor, cname, days);
                //we need 2 constructors one that takes in arguments to reconstruct the object and
                //one that just generates the random id by itself
                // also constructor doesnt have start and end string days being instantiated
                myCommits.add(obj1);
            }
//            Context context = getApplicationContext();
//            CharSequence text = "Hello toast!";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
            cursor.close();
        }

        //startaddClass will start AddClassActivity for result
        startaddClass = findViewById(R.id.start_add_class);
        startaddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddClassActivity.class);
                startActivityForResult(intent,AddClassCode);
            }
        });


//        setContentView(R.layout.row_view);
        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Commitments com1 = new Commitments("Fox", "Soft Eng", "MWF");
        //myCommits.add(com1);
        //commitmentsAdapter cCommitmentsAdapter = new commitmentsAdapter(getApplicationContext(), myCommits);
        //recyclerView.setAdapter(cCommitmentsAdapter);
        //displayListView();
    }

    //called when Activity is being destroyed and relevant data should be saved
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SCHEDULE_VISIBLE, mScheduleVisible);
    }

    //called before menu is shown
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!mScheduleVisible)
            menu.findItem(R.id.schedule).setIcon(R.drawable.calendar);
        return super.onPrepareOptionsMenu(menu);
    }

    //create Optionsmenu on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //what do we do if an item on the menu bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.nav:
                Intent intent = new Intent (this, MapActivity.class);
                startActivity(intent);
                Toast.makeText(this, "We are still building this ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.schedule:
                Toast.makeText(this, "What setting are we including here? ", Toast.LENGTH_SHORT).show();
                if(mScheduleVisible)
                item.setIcon(R.drawable.calendar);
                else
                    item.setIcon(R.drawable.schedule);
                mScheduleVisible = !mScheduleVisible;
                break;

        }
        return super.onOptionsItemSelected(item);
    }

//what do we do if an option is selected on the navigation drawer, answer below
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.home:
                Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cal_view:
                Toast.makeText(this,"Calendar",Toast.LENGTH_SHORT).show();
                break;
            case R.id.classes:
                Toast.makeText(this,"Classes",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tasks:
                Toast.makeText(this,"Tasks",Toast.LENGTH_SHORT).show();
                break;
            case R.id.commute:
                Toast.makeText(this,"Commute",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings_id:
                Intent intent = new Intent (this, SettingsActivity.class);
                startActivity(intent);
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Help_id:
                Toast.makeText(this,"Help",Toast.LENGTH_SHORT).show();
                break;
        }
        myDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //closes drawer after an option is pressed on navigation drawer or if area not on navigation drawer is pressed
    @Override
    public void onBackPressed() {
        if(myDrawerLayout.isDrawerOpen(GravityCompat.START)){
            myDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        super.onBackPressed();
    }

    //@Override //Function called after a commitment is committed. It adds the commitment to the myCommits array
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode != Activity.RESULT_OK)
                return;
            if(requestCode == AddClassCode){
                Commitments tempclass =  data.getParcelableExtra("retrieveUFClass");
                myCommits.add(tempclass);
            }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Commitments com1 = new Commitments("Fox", "Soft Eng", "MWF");
        //myCommits.add(com1);
        commitmentsAdapter cCommitmentsAdapter = new commitmentsAdapter(getApplicationContext(), myCommits);
        mDbHelper = new CommitmentHelper(MainActivity.this);
        mDatabase = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i = 0; i<myCommits.size(); ++i){
            Commitments obj1 = myCommits.get(i);
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = formatter.format(obj1.getStart());
            String end = formatter.format(obj1.getEnd());
            String suuid = obj1.getProfessor().toString();


            values.put(CommitmentSchema.CommitmentTable.Cols.PROFESSOR, obj1.getProfessor());
            values.put(CommitmentSchema.CommitmentTable.Cols.CNAME, obj1.getCname());
            values.put(CommitmentSchema.CommitmentTable.Cols.ID, suuid);
            values.put(CommitmentSchema.CommitmentTable.Cols.ONTHESEDAYS, obj1.getOnTheseDays());
            values.put(CommitmentSchema.CommitmentTable.Cols.START, start);
            values.put(CommitmentSchema.CommitmentTable.Cols.END, end);
            mDatabase.insert(CommitmentSchema.CommitmentTable.NAME, null, values);


        }
        recyclerView.setAdapter(cCommitmentsAdapter);
    }
}
