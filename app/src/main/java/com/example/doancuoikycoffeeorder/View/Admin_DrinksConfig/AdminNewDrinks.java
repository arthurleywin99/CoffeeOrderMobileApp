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
                Toast.makeText(AdminNewDrinks.this, "Tên thức uống không được bỏ trống", Toast.LENGTH_SHORT).show();
            } else if (!edPrice.getText().toString().matches("[0-9]+")) {
                Toast.makeText(AdminNewDrinks.this, "Phải nhập dữ liệu là số", Toast.LENGTH_SHORT).show();
            } else if (Strings.isEmptyOrWhitespace(edPrice.getText().toString())) {
                Toast.makeText(AdminNewDrinks.this, "Giá tiền không được bỏ trống", Toast.LENGTH_SHORT).show();
            } else {
                /*Lấy dữ liệu và lưu vào model Drinks*/
                DrinksModel drinksModel = getDataFromView();
                /*Kiểm tra tên đồ uống có tồn tại trong cơ sở dữ liệu hay chưa*/
                if (checkAlreadyDrinkName(drinksModel.getDrinkName())) {
                    Toast.makeText(AdminNewDrinks.this, "Thức uống với tên này đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    /*Thêm đồ uống mới vào database*/
                    reference = FirebaseDatabase.getInstance().getReference();
                    uploadImage();
                    reference.child(DBNAME).child("Drinks").child(edDrinkID.getText().toString()).setValue(drinksModel, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Toast.makeText(AdminNewDrinks.this, "Thêm thức uống hoàn tất", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminNewDrinks.this, "Lỗi khi thêm mới", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AdminNewDrinks.this.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ View và trả về một đối tượng Drinks*/
    private DrinksModel getDataFromView() {
        DrinksModel drinksModel = new DrinksModel();
        drinksModel.setDrinkID(Integer.parseInt(edDrinkID.getText().toString()));
        drinksModel.setDrinkName(edDrinkName.getText().toString());
        drinksModel.setPrice(Integer.parseInt(edPrice.getText().toString()));
        drinksModel.setImage(drinksModel.getDrinkID() + ".jpg");
        return drinksModel;
    }

    /*Kiểm tra tên đồ uống có tồn tại trong cơ sở dữ liệu hay chưa*/
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
            /*Lấy danh sách đồ uống từ database*/
            drinks = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            drinks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Nếu database đã có đồ uống sẵn*/
                    if (dataSnapshot.getValue() != null) {
                        /*Xóa danh sách đồ uống để cập nhật lại từ database*/
                        drinkList.clear();
                        /*Duyệt các Node con của Drinks để lấy danh sách các đối tượng đồ uống*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Tạo drink Model để lưu các đối tượng này*/
                            DrinksModel drinkModel = ds.getValue(DrinksModel.class);
                            drinkList.add(drinkModel);
                        }
                        /*Tự động tăng ID cho đồ uống mới*/
                        setNextDrinkID();
                    } else { /*Nếu database không có đồ uống tự động set đồ uống có ID bắt đầu là 1*/
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

    /*Tạo ID tự động tăng 1*/
    public void setNextDrinkID() {
        /*Lấy ID hiện tại và tăng 1 đơn vị*/
        edDrinkID.setText(String.valueOf(getMaxIDDrink() + 1));
    }

    /*Lấy ID lớn nhất hiện tại của list đồ uống*/
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

    /*Mở bộ nhớ lấy hình*/
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

    /*Lấy phần mở rộng của file ảnh*/
    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*Upload ảnh lên Firebase Storage*/
    private void uploadImage() {
        StorageReference ref = storageRef.child(edDrinkID.getText().toString() + "." + getExtension(mImageUri));
        ref.putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(AdminNewDrinks.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminNewDrinks.this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show());
    }
}
