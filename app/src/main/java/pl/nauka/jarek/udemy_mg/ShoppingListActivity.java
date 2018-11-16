package pl.nauka.jarek.udemy_mg;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import pl.nauka.jarek.udemy_mg.adapter.ShoppingListAdapter;
import pl.nauka.jarek.udemy_mg.model.NameColor;

public class ShoppingListActivity extends AppCompatActivity {


    @BindView(R.id.itemList)
    ListView itemList;
    @BindView(R.id.itemSpinner)
    Spinner itemSpinner;
    @BindView(R.id.deleteButton)
    ImageButton deleteButton;
    @BindView(R.id.itemName_ET)
    BootstrapEditText itemName;
    @BindView(R.id.deleteItemButton)
    ImageButton deleteItemButton;


    private List<NameColor> listItems;     //lista zwykła
    private List<String> spinnerItems;  //lista rozwijana

    private static final String LIST_ITEMS_KEY = "LIST_ITEMS_KEY";
    private static final String SPINNER_ITEMS_KEY = "SPINNER_ITEMS_KEY";
    private static final String shopping_list_key = "SHOPPING_LIST_KEY";
    private ShoppingListAdapter listAdapter; //łączy 2 różne interfejsy: List<String> listItems i ListView itemList
    private ArrayAdapter<String> spinnerAdapter; //tutaj wystarczy zwykły adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypefaceProvider.registerDefaultIconSets();

        listItems = new ArrayList<>();
        spinnerItems = new ArrayList<>();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemName.getText() != null && !itemName.getText().toString().trim().isEmpty() && !itemName.getText().toString().equals("Podaj nazwę produktu")){
                    listItems.add(new NameColor(itemName.getText().toString(), Color.BLACK,false));
                    itemName.setText("");
                    listAdapter.notifyDataSetChanged();     //powiadamia adapter o zmienie danych
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        SharedPreferences sp = getPreferences(MODE_PRIVATE);
//        String newListItemsString = sp.getString(LIST_ITEMS_KEY, null);     //String z listItems
//        String newSpinnerItemsString = sp.getString(SPINNER_ITEMS_KEY, null);     //String z spinnerItems
//        Gson gson = new Gson();
//        ArrayList newListItems = gson.fromJson(newListItemsString, new TypeToken<ArrayList>() {
//        }.getType());              //zamiana Stringa na ArrayList
//        ArrayList newSpinnerItems = gson.fromJson(newSpinnerItemsString, new TypeToken<ArrayList>() {
//        }.getType());        //zamiana Stringa na ArrayList
//
//        if (newListItems != null) {
//            listItems = newListItems;
//        }
//
//        if (newSpinnerItems != null) {
//            spinnerItems = newSpinnerItems;
//        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerItems);

        itemSpinner.setAdapter(spinnerAdapter);
//        itemSpinner.setSelection(spinnerItems.indexOf(""));         //TODO Naprawiony bład

//        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {       //przenoszenie pozycji ze spinnera do listy zwykłej
//
//                itemName.setText(spinnerItems.get(position));
//                spinnerItems.remove(position);
//                spinnerAdapter.notifyDataSetChanged();
////                if (!spinnerItems.get(position).equals("")){
////                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        listAdapter = new ShoppingListAdapter(this, R.layout.row_shopping_list, listItems, spinnerAdapter, spinnerItems, itemSpinner, itemList);
        itemList.setAdapter(listAdapter);   //ustawienie adaptera na itemList //wrzucenie danych do zwyklej listy w aplikacji za pomocą ShoppingListAdapter
    }


    @Override
    protected void onPause() {
        super.onPause();

//        SharedPreferences sp = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//
//        Gson gson = new Gson();
//        editor.putString(LIST_ITEMS_KEY, gson.toJson(listItems));           //Gson zamienia na String
//        editor.putString(SPINNER_ITEMS_KEY, gson.toJson(spinnerItems));     //Gson zamienia na String
//        editor.apply();                                                     //zapis równoległy bez blokady wątku
    }

    @OnClick(R.id.deleteButton)
    public void onClickDelete() {

        for (int i = 0; i < listItems.size(); i++) {
            listAdapter.notifyDataSetChanged();
            if (listItems.get(i).getClassChecked() == true) {
                listItems.remove(i);
                i--;
            }
        }
    }


    @OnFocusChange(R.id.itemName_ET)
    void onFocusChanged(boolean focused) {

        if (focused == true) {
            itemName.setText("");
        } else {
            itemName.setText("Podaj nazwę produktu");
        }
    }

    @OnClick(R.id.deleteItemButton)
    public void onClickItemDelete() {
        if (!itemName.getText().toString().equals("Podaj nazwę produktu"))
        itemName.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Dialog dialog = new Dialog(this);
            dialog.setTitle("Input Box");
            dialog.setContentView(R.layout.input_box);
            TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
            txtMessage.setText("Update item");
            txtMessage.setTextColor(Color.parseColor("#ff2222"));
            final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);

            dialog.show();



            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

