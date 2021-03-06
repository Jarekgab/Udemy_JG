package pl.nauka.jarek.udemy_mg;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import org.angmarch.views.NiceSpinner;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import pl.nauka.jarek.udemy_mg.adapter.ShoppingListAdapter;
import pl.nauka.jarek.udemy_mg.model.ShoppingListElement;
import pl.nauka.jarek.udemy_mg.util.SharedPreferencesSaver;

public class ShoppingListActivity extends AppCompatActivity {

    @BindView(R.id.itemList)
    ListView itemList;
    @BindView(R.id.itemSpinner)
    NiceSpinner itemSpinner;
    @BindView(R.id.deleteButton)
    ImageButton deleteButton;
    @BindView(R.id.itemName_ET)
    BootstrapEditText itemName;
    @BindView(R.id.addButton)
    Button addButton;

    private List<String> spinnerItems;  //lista rozwijana
    private List<List<ShoppingListElement>> spinnerList;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerItems = new ArrayList<>();

        if (spinnerItems.isEmpty()) {
            spinnerItems.add("Lista główna");
            spinnerList = new ArrayList<>();
            spinnerList.add(new ArrayList<ShoppingListElement>());
        }

        List<List<ShoppingListElement>> newSpinnerList = SharedPreferencesSaver.loadSpinnerL(getPreferences(MODE_PRIVATE));
        List<String> newSpinnerItems = SharedPreferencesSaver.loadSpinnerI(getPreferences(MODE_PRIVATE));

        if (newSpinnerList != null) {
            spinnerList = newSpinnerList;
        }

        if (newSpinnerItems != null) {
            spinnerItems = newSpinnerItems;
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerItems);
        itemSpinner.setAdapter(spinnerAdapter);

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {       //przenoszenie pozycji ze spinnera do listy zwykłej

                listAdapter = new ShoppingListAdapter(ShoppingListActivity.this, R.layout.row_shopping_list, spinnerList.get(position));
                itemList.setAdapter(listAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemName.getText() != null && !itemName.getText().toString().trim().isEmpty() && !itemName.getText().toString().equals("Podaj nazwę")) {

                    ShoppingListElement shoppingListElement = new ShoppingListElement(itemName.getText().toString(), Color.BLACK, false);
                    itemName.setText("");

                    spinnerList.get(itemSpinner.getSelectedIndex()).add(shoppingListElement);
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        listAdapter = new ShoppingListAdapter(this, R.layout.row_shopping_list, spinnerList.get(0));
        itemList.setAdapter(listAdapter);   //ustawienie adaptera na itemList //wrzucenie danych do zwyklej listy w aplikacji za pomocą ShoppingListAdapter
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferencesSaver.saveTo(spinnerList, spinnerItems, getPreferences(MODE_PRIVATE));
    }

    @OnClick(R.id.deleteButton)
    public void onClickDelete() {

        List<ShoppingListElement> selectedView = spinnerList.get(itemSpinner.getSelectedIndex());
        for (int i = 0; i < selectedView.size(); i++) {
            if (selectedView.get(i).getClassChecked() == true) {
                selectedView.remove(i);
                i--;
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    @OnFocusChange(R.id.itemName_ET)
    void onFocusChanged(boolean focused) {

        if (focused == true) {
            itemName.setText("");
        } else {
            itemName.setText("Podaj nazwę");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_new_list) {

            createDialog();
            return true;
        }

        if (id == R.id.action_remove_list) {

            if (!spinnerItems.isEmpty() && itemSpinner.getSelectedIndex() > 0) {
                spinnerItems.remove(itemSpinner.getSelectedIndex());
                spinnerList.remove(itemSpinner.getSelectedIndex());

                itemSpinner.setSelectedIndex(0);
                listAdapter = new ShoppingListAdapter(ShoppingListActivity.this, R.layout.row_shopping_list, spinnerList.get(0));
                itemList.setAdapter(listAdapter);

            } else if (!spinnerItems.isEmpty() && itemSpinner.getSelectedIndex() == 0) {

                spinnerList.get(itemSpinner.getSelectedIndex()).clear();
                itemSpinner.setSelectedIndex(0);
                listAdapter = new ShoppingListAdapter(ShoppingListActivity.this, R.layout.row_shopping_list, spinnerList.get(0));
                itemList.setAdapter(listAdapter);

                Toast toast = Toast.makeText(this, "Nie można usuwać głównej listy", Toast.LENGTH_LONG);
                View view = toast.getView();

                view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                toast.show();
            }
            listAdapter.notifyDataSetChanged();
            spinnerAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.input_box);
        TextView dialogListNameTV = (TextView) dialog.findViewById(R.id.listNameTextView);
        final EditText dialogListNameET = (EditText) dialog.findViewById(R.id.listNameEditText);

        dialogListNameTV.setText("Nazwa listy");
        dialogListNameTV.setTextColor(Color.WHITE);

        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button saveButton = (Button) dialog.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerItems.add(dialogListNameET.getText().toString());
                spinnerList.add(new ArrayList<ShoppingListElement>());

                int newField = spinnerItems.size() - 1;

                itemSpinner.setSelectedIndex(newField);
                listAdapter = new ShoppingListAdapter(ShoppingListActivity.this, R.layout.row_shopping_list, spinnerList.get(newField));
                itemList.setAdapter(listAdapter);

                dialog.dismiss();
                Toast.makeText(ShoppingListActivity.this, "Dodano nową liste: " + dialogListNameET.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });
        dialog.show();
    }
}

