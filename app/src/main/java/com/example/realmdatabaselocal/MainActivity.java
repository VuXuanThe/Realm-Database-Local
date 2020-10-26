package com.example.realmdatabaselocal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realmdatabaselocal.databinding.ActivityMainBinding;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        showData();
        binding.insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsertDialog();
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
    }

    private void showInsertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.data_input_dialog, null);

        builder.setView(view);

        EditText name = view.findViewById(R.id.name);
        EditText age = view.findViewById(R.id.age);
        Spinner gender = view.findViewById(R.id.gender);
        Button save = view.findViewById(R.id.save);
        AlertDialog alertDialog = builder.show();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(age.getText().toString().isEmpty() || name.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Chua nhap du du lieu", Toast.LENGTH_SHORT).show();
                }
                else {
                    DataModel dataModel = new DataModel();

                    Number current_id = realm.where(DataModel.class).max("id");
                    long next_id;
                    if(current_id == null){
                        next_id = 1;
                    }
                    else{
                        next_id = current_id.intValue() + 1;
                    }
                    dataModel.setId(next_id);
                    dataModel.setAge(Integer.parseInt(age.getText().toString()));
                    dataModel.setGender(gender.getSelectedItem().toString());
                    dataModel.setName(name.getText().toString());

                    realm.beginTransaction();
                    realm.insert(dataModel);
                    realm.commitTransaction();
                    alertDialog.dismiss();
                    showData();
                }
            }
        });
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        builder.setView(view);

        EditText idText = view.findViewById(R.id.id);
        Button delete = view.findViewById(R.id.delete);
        AlertDialog alertDialog = builder.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "chua nhap id", Toast.LENGTH_SHORT).show();
                }
                else{
                    long id = Long.parseLong(idText.getText().toString());
                    if(checkId(id)){
                        DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
                        realm.beginTransaction();
                        dataModel.deleteFromRealm();
                        realm.commitTransaction();
                        showData();
                        alertDialog.dismiss();
                    }
                    else {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this, "khong ton tai id nay", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void showSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.search_dialog, null);

        builder.setView(view);

        EditText idText = view.findViewById(R.id.id);
        Button search = view.findViewById(R.id.search);
        Button ok = view.findViewById(R.id.ok);
        TextView result = view.findViewById(R.id.result);
        AlertDialog alertDialog = builder.show();

        result.setText("");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Chua nhap id", Toast.LENGTH_SHORT).show();
                }
                else{
                    long id = Long.parseLong(idText.getText().toString());
                    if(checkId(id)){
                        result.setVisibility(View.VISIBLE);
                        ok.setVisibility(View.VISIBLE);
                        DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
                        result.append("id: " + dataModel.getId() + "\t Name: " + dataModel.getName() + "\t Age: " + dataModel.getAge()
                                + "\t Gender: " + dataModel.getGender());
                    }
                    else {
                        Toast.makeText(MainActivity.this, "khong ton tai id nay", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void showData(){
        List<DataModel> dataModels = realm.where(DataModel.class).findAll();
        binding.showData.setText("");
        for (DataModel dataModel: dataModels) {
            binding.showData.append("id: " + dataModel.getId() + "\t Name: " + dataModel.getName() + "\t Age: " + dataModel.getAge()
            + "\t Gender: " + dataModel.getGender() + "\n");
        }
    }

    private void showUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);

        builder.setView(view);

        EditText idText = view.findViewById(R.id.id);
        EditText name = view.findViewById(R.id.name);
        EditText age = view.findViewById(R.id.age);
        Spinner gender = view.findViewById(R.id.gender);
        Button update = view.findViewById(R.id.update);
        AlertDialog alertDialog = builder.show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(idText.getText().toString());
                    if(checkId(id)){
                        DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
                        realm.beginTransaction();
                        dataModel.setAge(Integer.parseInt(age.getText().toString()));
                        dataModel.setGender(gender.getSelectedItem().toString());
                        dataModel.setName(name.getText().toString());
                        realm.commitTransaction();
                        showData();
                        alertDialog.dismiss();
                    }
                    else {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Khong ton tai id nay", Toast.LENGTH_SHORT).show();
                    }

                }
        });
    }

    private boolean checkId( long id){
        List<DataModel> dataModels = realm.where(DataModel.class).findAll();
            for (DataModel dataModel: dataModels) {
                if(dataModel.getId() == id)
                    return true;
            }
            return false;
    }

}