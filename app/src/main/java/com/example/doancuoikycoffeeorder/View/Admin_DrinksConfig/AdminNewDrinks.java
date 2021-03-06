package com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STORAGE;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.PICK_IMAGE_REQUEST;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.R;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminNewDrinks extends AppCompatActivity implements View.OnClickListener {
    protected List<DrinksModel> drinkList = new ArrayList<>();
    private EditText edDrinkID, edDrinkName, edPrice;
    protected Button btnCreate, btnCancel;
    protected Uri mImageUri;
    private ImageView imDrink;
    DatabaseReference drinks, reference;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_new_drink);

        edDrinkID = findViewById(R.id.edDrinkID);
        edDrinkName = findViewById(R.id.edDrinkName);
        edPrice = findViewById(R.id.edPrice);
        imDrink = findViewById(R.id.imDrink);
        imDrink.setOnClickListener(this);
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        storageRef = FirebaseStorage.getInstance().getReference("images");

        getDrinkListCurrent();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imDrink: {
                openFileChooser();
                break;
            }
            case R.id.btnCreate: {
                addNewDrink();
                break;
            }
            case R.id.btnCancel: {
                AdminNewDrinks.this.finish();
                break;
            }
        }
    }

    public void addNewDrink() {
        try {
            if (Strings.isEmptyOrWhitespace(edDrinkName.getText().toString())) {
                Toast.makeText(AdminNewDrinks.this, "T??n th???c u???ng kh??ng ???????c b??? tr???ng", Toast.LENGTH_SHORT).show();
            } else if (!edPrice.getText().toString().matches("[0-9]+")) {
                Toast.makeText(AdminNewDrinks.this, "Ph???i nh???p d??? li???u l?? s???", Toast.LENGTH_SHORT).show();
            } else if (Strings.isEmptyOrWhitespace(edPrice.getText().toString())) {
                Toast.makeText(AdminNewDrinks.this, "Gi?? ti???n kh??ng ???????c b??? tr???ng", Toast.LENGTH_SHORT).show();
            } else {
                /*L???y d??? li???u v?? l??u v??o model Drinks*/
                DrinksModel drinksModel = getDataFromView();
                /*Ki???m tra t??n ????? u???ng c?? t???n t???i trong c?? s??? d??? li???u hay ch??a*/
                if (checkAlreadyDrinkName(drinksModel.getDrinkName())) {
                    Toast.makeText(AdminNewDrinks.this, "Th???c u???ng v???i t??n n??y ???? t???n t???i", Toast.LENGTH_SHORT).show();
                } else {
                    /*Th??m ????? u???ng m???i v??o database*/
                    reference = FirebaseDatabase.getInstance().getReference();
                    uploadImage();
                    reference.child(DBNAME).child("Drinks").child(edDrinkID.getText().toString()).setValue(drinksModel, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Toast.makeText(AdminNewDrinks.this, "Th??m th???c u???ng ho??n t???t", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminNewDrinks.this, "L???i khi th??m m???i", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AdminNewDrinks.this.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y d??? li???u t??? View v?? tr??? v??? m???t ?????i t?????ng Drinks*/
    private DrinksModel getDataFromView() {
        DrinksModel drinksModel = new DrinksModel();
        drinksModel.setDrinkID(Integer.parseInt(edDrinkID.getText().toString()));
        drinksModel.setDrinkName(edDrinkName.getText().toString());
        drinksModel.setPrice(Integer.parseInt(edPrice.getText().toString()));
        drinksModel.setImage(drinksModel.getDrinkID() + ".jpg");
        return drinksModel;
    }

    /*Ki???m tra t??n ????? u???ng c?? t???n t???i trong c?? s??? d??? li???u hay ch??a*/
    public boolean checkAlreadyDrinkName(String drinkName) {
        try {
            for (DrinksModel model : drinkList) {
                if (drinkName.equals(model.getDrinkName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getDrinkListCurrent() {
        try {
            /*L???y danh s??ch ????? u???ng t??? database*/
            drinks = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            drinks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*N???u database ???? c?? ????? u???ng s???n*/
                    if (dataSnapshot.getValue() != null) {
                        /*X??a danh s??ch ????? u???ng ????? c???p nh???t l???i t??? database*/
                        drinkList.clear();
                        /*Duy???t c??c Node con c???a Drinks ????? l???y danh s??ch c??c ?????i t?????ng ????? u???ng*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*T???o drink Model ????? l??u c??c ?????i t?????ng n??y*/
                            DrinksModel drinkModel = ds.getValue(DrinksModel.class);
                            drinkList.add(drinkModel);
                        }
                        /*T??? ?????ng t??ng ID cho ????? u???ng m???i*/
                        setNextDrinkID();
                    } else { /*N???u database kh??ng c?? ????? u???ng t??? ?????ng set ????? u???ng c?? ID b???t ?????u l?? 1*/
                        edDrinkID.setText("1");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*T???o ID t??? ?????ng t??ng 1*/
    public void setNextDrinkID() {
        /*L???y ID hi???n t???i v?? t??ng 1 ????n v???*/
        edDrinkID.setText(String.valueOf(getMaxIDDrink() + 1));
    }

    /*L???y ID l???n nh???t hi???n t???i c???a list ????? u???ng*/
    public int getMaxIDDrink() {
        int drinkID = 1;
        try {
            for (DrinksModel model : drinkList) {
                if (model.getDrinkID() >= drinkID) {
                    drinkID = model.getDrinkID();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drinkID;
    }

    /*M??? b??? nh??? l???y h??nh*/
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imDrink);
        }
    }

    /*L???y ph???n m??? r???ng c???a file ???nh*/
    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*Upload ???nh l??n Firebase Storage*/
    private void uploadImage() {
        StorageReference ref = storageRef.child(edDrinkID.getText().toString() + "." + getExtension(mImageUri));
        ref.putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(AdminNewDrinks.this, "T???i ???nh th??nh c??ng", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminNewDrinks.this, "T???i ???nh th???t b???i", Toast.LENGTH_SHORT).show());
    }
}
