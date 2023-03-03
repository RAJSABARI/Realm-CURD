package com.rajsabari.details;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import java.util.UUID;



public class MainActivity extends AppCompatActivity  {
    Realm realm;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      realm=Realm.getDefaultInstance();
        Button insert= findViewById(R.id.insert);
        Button update= findViewById(R.id.update);
        Button read= findViewById(R.id.read);
        Button delete= findViewById(R.id.delete);
         output=findViewById(R.id.show_data);
        String uniqueID = UUID.randomUUID().toString();

      insert.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
              ShowInsertDialog();
          }
      });
      update.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                ShowUpdateDialog();
          }
      });
      read.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(MainActivity.this, "Read", Toast.LENGTH_SHORT).show();
                showData();
          }
      });
      delete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            ShowDeleteDialog();
          }
      });


    }

    private void ShowUpdateDialog() {
        AlertDialog.Builder al= new AlertDialog.Builder(MainActivity.this);
        View view =getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final EditText data_id=view.findViewById(R.id.data_id);

        Button delete= view.findViewById(R.id.delete);
        final AlertDialog alertDialog=al.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                long id = Long.parseLong(data_id.getText().toString());
                final DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
            ShowUpdateDialog(dataModel);
            }});

    }

    private void ShowDeleteDialog() {
        AlertDialog.Builder al= new AlertDialog.Builder(MainActivity.this);
        View view =getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

         final EditText data_id=view.findViewById(R.id.data_id);

         Button delete= view.findViewById(R.id.delete);
            final AlertDialog alertDialog=al.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id= Long.parseLong(data_id.getText().toString());
             final   DataModel dataModel= realm.where(DataModel.class).equalTo("id",id).findFirst();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alertDialog.dismiss();
                        dataModel.deleteFromRealm();
                    }
                });
            }
        });

    }
    private void ShowUpdateDialog( final DataModel dataModel) {
        AlertDialog.Builder al= new AlertDialog.Builder(MainActivity.this);
        View view =getLayoutInflater().inflate(R.layout.data_input_dialog,null);
        al.setView(view);

      final   EditText name=view.findViewById(R.id.name);
     final    EditText age=view.findViewById(R.id.age);
      final   Spinner gender= view.findViewById(R.id.gender);

        Button save= view.findViewById(R.id.save);
      final   AlertDialog alertDialog=al.show();
       name.setText(dataModel.getName());
        age.setText(""+dataModel.getAge());
        if(dataModel.getGender().equalsIgnoreCase("Male")) {
            gender.setSelection(0);
        }
        else{
            gender.setSelection(1);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //final DataModel dataModel=new DataModel();




                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        dataModel.setAge(Integer.parseInt(age.getText().toString()));
                        dataModel.setName(name.getText().toString());
                        dataModel.setGender(gender.getSelectedItem().toString());
                        realm.copyToRealmOrUpdate(dataModel);
                    }

                });




            }
        });

    }

    private void ShowInsertDialog() {
        AlertDialog.Builder al= new AlertDialog.Builder(MainActivity.this);
        View view =getLayoutInflater().inflate(R.layout.data_input_dialog,null);
        al.setView(view);

        EditText name=view.findViewById(R.id.name);
        EditText age=view.findViewById(R.id.age);
        Spinner gender= view.findViewById(R.id.gender);
        AlertDialog alertDialog=al.show();
        Button save= view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name!=null&&gender!=null&&age!=null) {
                    alertDialog.dismiss();
                    final DataModel dataModel = new DataModel();
                    Number current_id = realm.where(DataModel.class).max("id");
                    long nextId = 0;
                    if (current_id == null) {
                        nextId = 1;
                    } else {
                        nextId = current_id.intValue() + 1;
                    }
//                    //dataModel.setId(uniqueID);
//                    import java.util.UUID;
//
//// Generate a unique ID for the app
                    //and edit in datamodel also
//                    String uniqueID = UUID.randomUUID().toString();
                    dataModel.setId(nextId);
                    dataModel.setAge(Integer.parseInt(age.getText().toString()));
                    dataModel.setName(name.getText().toString());
                    dataModel.setGender(gender.getSelectedItem().toString());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(dataModel);
                        }
                    });


                }
                else{
                    Toast.makeText(MainActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void showData(){
        List<DataModel> dataModels=realm.where(DataModel.class).findAll();
      output.setText(" ");
        for(int i=0;i<dataModels.size();i++){
output.append("ID:"+dataModels.get(i).getId()+" Name: "+dataModels.get(i).getName()+" Age:"+dataModels.get(i).getAge()+" Gender:"+dataModels.get(i).getGender()+"\n");
        }
    }
}