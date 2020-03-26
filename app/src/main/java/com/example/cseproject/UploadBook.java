package com.example.cseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadBook extends AppCompatActivity {
    EditText editPDFName;
    Button uploadBtn;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        progressBar=findViewById(R.id.progressbarId2);

        uploadBtn=(Button)findViewById(R.id._UploadBook);
        editPDFName =(EditText)findViewById(R.id.textPdfName);

        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDFFile();
            }
        });

    }

    private void selectPDFFile() {
        Intent intent =new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"), 1);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null ){

            uploadPDFFile(data.getData());
        }
    }

    private void uploadPDFFile(Uri data) {
    progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(UploadBook.this, "Uploading Bro keep Patience", Toast.LENGTH_SHORT).show();
        StorageReference reference=storageReference.child("upload/"+System.currentTimeMillis()+".pdf");
            reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
            while (!uri.isComplete());
                Uri url=uri.getResult();
                uploadPdf uploadPdf=new uploadPdf(editPDFName.getText().toString(),url.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(uploadPdf);

        }
    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(UploadBook.this, "Chill Bro", Toast.LENGTH_SHORT).show();
        }
    });
        Toast.makeText(UploadBook.this, "Uploaded SuccessFully", Toast.LENGTH_SHORT).show();

    }
}