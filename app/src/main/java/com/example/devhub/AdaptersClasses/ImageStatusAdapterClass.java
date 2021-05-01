package com.example.devhub.AdaptersClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.devhub.Activities.ImageCommentPage;
import com.example.devhub.AppClasses.AddNotifications;
import com.example.devhub.ModelClasses.Model_ImageStatus;
import com.example.devhub.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageStatusAdapterClass extends FirestoreRecyclerAdapter<Model_ImageStatus, ImageStatusAdapterClass.ImageStatusViewHolderClass> {

    public ImageStatusAdapterClass(@NonNull FirestoreRecyclerOptions<Model_ImageStatus> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ImageStatusViewHolderClass imageStatusViewHolderClass, int position, @NonNull Model_ImageStatus model) {
        imageStatusViewHolderClass.objectProgressBar.setVisibility(View.VISIBLE);
        imageStatusViewHolderClass.userEmailTV.setText(model.getUseremail());

        imageStatusViewHolderClass.statusDateTV.setText(model.getCurrendatetime());
        imageStatusViewHolderClass.imageStatusDescriptionTV.setText(model.getStatus());

        imageStatusViewHolderClass.heartCountTV.setText(Integer.toString(model.getNooflove()));
        imageStatusViewHolderClass.hahaCountTV.setText(Integer.toString(model.getNoofhaha()));

        imageStatusViewHolderClass.sadCountTV.setText(Integer.toString(model.getNoofsad()));
        imageStatusViewHolderClass.noOfComments.setText(Integer.toString(model.getNoofcomments()));

        String linkOfProfileImage = model.getProfileurl();
        String linkOfImageStatus = model.getStatusimageurl();

        Glide.with(imageStatusViewHolderClass.userProfileIV.getContext()).load(linkOfProfileImage)
                .into(imageStatusViewHolderClass.userProfileIV);

        Glide.with(imageStatusViewHolderClass.imageStatusIV.getContext()).load(linkOfImageStatus)
                .into(imageStatusViewHolderClass.imageStatusIV);

        imageStatusViewHolderClass.objectProgressBar.setVisibility(View.INVISIBLE);
        AddNotifications objectAddNotifications=new AddNotifications();

        imageStatusViewHolderClass.heartIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth objFirebaseAuth = FirebaseAuth.getInstance();
                if (objFirebaseAuth!=null) {
                    final String userEmail = Objects.requireNonNull(objFirebaseAuth.getCurrentUser()).getEmail();
                    final String documentID = getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId();

                    FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();
                    assert userEmail != null;
                    DocumentReference objectDocumentReference = objectFirebaseFirestore.collection
                            ("ImageStatus").document(documentID).collection("Emotions")
                            .document(userEmail);

                    objectDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (Objects.requireNonNull(task.getResult()).exists()) {
                                String currentFlag = task.getResult().getString("currentFlag");
                                objectAddNotifications.generateNotification(userEmail,"love","image status",model.getUseremail());

                                if(currentFlag.equals("love")) {
                                    objectDocumentReference.update("currentFlag", "love");

                                } else if (currentFlag.equals("haha"))
                                {
                                    Long totalHearts = (Long) getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).get("nooflove");
                                    totalHearts++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("nooflove", totalHearts);

                                    objectDocumentReference.update("currentFlag", "love");

                                    Long totalHaha = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofhaha"
                                    );
                                    totalHaha--;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("noofhaha", totalHaha);

                                } else if (currentFlag.equals("sad")) {
                                    Long totalHearts = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("nooflove");
                                    totalHearts++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("nooflove", totalHearts);

                                    objectDocumentReference.update("currentFlag", "love");

                                    Long totalSad = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofsad");

                                    totalSad--;

                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getReference()
                                            .update("noofsad", totalSad);

                                }

                            } else {
                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("currentFlag", "love");

                                objectFirebaseFirestore.collection("ImageStatus").document(
                                        documentID).collection("Emotions").document(
                                        userEmail).set(objectMap);

                                Long totalHearts = (Long) getSnapshots().getSnapshot(
                                        imageStatusViewHolderClass.getAdapterPosition()).get("nooflove"
                                );
                                totalHearts++;
                                getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                        .getReference().update("nooflove", totalHearts);

                                objectDocumentReference.update("currentFlag", "love");
                                objectAddNotifications.generateNotification(userEmail,"love","image status",model.getUseremail());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(imageStatusViewHolderClass.heartIV.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(imageStatusViewHolderClass.heartIV.getContext(), R.string.no_user_online, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageStatusViewHolderClass.hahaIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth objFirebaseAuth = FirebaseAuth.getInstance();
                if (objFirebaseAuth != null) {
                    String userEmail = objFirebaseAuth.getCurrentUser().getEmail();
                    String documentID = getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId();

                    FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference objectDocumentReference = objectFirebaseFirestore.collection
                            ("ImageStatus").document(documentID).collection("Emotions")
                            .document(userEmail);

                    objectDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String currentFlag = task.getResult().getString("currentFlag");
                                objectAddNotifications.generateNotification(userEmail,"haha","image status",model.getUseremail());

                                if (currentFlag.equals("haha")) {
                                    objectDocumentReference.update("currentFlag", "haha");

                                } else if (currentFlag.equals("love")) {
                                    Long totalHaha = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofhaha"
                                    );
                                    totalHaha++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("noofhaha", totalHaha);

                                    objectDocumentReference.update("currentFlag", "haha");

                                    Long totalLove = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("nooflove");
                                    totalLove--;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("nooflove", totalLove);

                                } else if (currentFlag.equals("sad")) {
                                    Long totalHaha = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofhaha"
                                    );
                                    totalHaha++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("noofhaha", totalHaha);

                                    objectDocumentReference.update("currentFlag", "haha");

                                    Long totalSad = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofsad");

                                    totalSad--;

                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getReference()
                                            .update("noofsad", totalSad);

                                }

                            } else {
                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("currentFlag", "haha");

                                objectFirebaseFirestore.collection("ImageStatus").document(
                                        documentID).collection("Emotions").document(
                                        userEmail).set(objectMap);

                                Long totalHaha = (Long) getSnapshots().getSnapshot(
                                        imageStatusViewHolderClass.getAdapterPosition()).get("noofhaha"
                                );
                                totalHaha++;
                                getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                        .getReference().update("noofhaha", totalHaha);

                                objectDocumentReference.update("currentFlag", "haha");
                                objectAddNotifications.generateNotification(userEmail,"haha","image status",model.getUseremail());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(imageStatusViewHolderClass.hahaIV.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(imageStatusViewHolderClass.hahaIV.getContext(), R.string.no_user_online, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageStatusViewHolderClass.sadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth objFirebaseAuth = FirebaseAuth.getInstance();
                if (objFirebaseAuth != null) {
                    String userEmail = objFirebaseAuth.getCurrentUser().getEmail();
                    String documentID = getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId();

                    FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference objectDocumentReference = objectFirebaseFirestore.collection
                            ("ImageStatus").document(documentID).collection("Emotions")
                            .document(userEmail);

                    objectDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String currentFlag = task.getResult().getString("currentFlag");
                                objectAddNotifications.generateNotification(userEmail,"sad","image status",model.getUseremail());

                                if (currentFlag.equals("sad")) {
                                    objectDocumentReference.update("currentFlag", "sad");

                                } else if (currentFlag.equals("love")) {
                                    Long totalSad = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofsad");
                                    totalSad++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("noofsad", totalSad);

                                    objectDocumentReference.update("currentFlag", "sad");

                                    Long totalLove = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("nooflove");
                                    totalLove--;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("nooflove", totalLove);

                                } else if (currentFlag.equals("haha")) {
                                    Long totalSad = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofsad");
                                    totalSad++;
                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                            .getReference().update("noofsad", totalSad);

                                    objectDocumentReference.update("currentFlag", "sad");

                                    Long totalHaha = (Long) getSnapshots().getSnapshot(
                                            imageStatusViewHolderClass.getAdapterPosition()).get("noofhaha");

                                    totalHaha--;

                                    getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getReference()
                                            .update("noofhaha", totalHaha);

                                }

                            } else {
                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("currentFlag", "sad");

                                objectFirebaseFirestore.collection("ImageStatus ").document(
                                        documentID).collection("Emotions").document(
                                        userEmail).set(objectMap);

                                Long totalSad = (Long) getSnapshots().getSnapshot(
                                        imageStatusViewHolderClass.getAdapterPosition()).get("noofsad"
                                );
                                totalSad++;
                                getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition())
                                        .getReference().update("noofsad", totalSad);

                                objectDocumentReference.update("currentFlag", "sad");
                                objectAddNotifications.generateNotification(userEmail,"sad","image status",model.getUseremail());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(imageStatusViewHolderClass.sadIV.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(imageStatusViewHolderClass.sadIV.getContext(), R.string.no_user_online, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageStatusViewHolderClass.commentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String documentID = getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId();
                Context objectContext = imageStatusViewHolderClass.commentIV.getContext();

                Intent objectIntent = new Intent(objectContext, ImageCommentPage.class);
                objectIntent.putExtra("documentId", documentID);

                objectIntent.putExtra("userEmailID",model.getUseremail());
                objectContext.startActivity(objectIntent);
        }
        });

        imageStatusViewHolderClass.deleteImageStatusIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth objectFirebaseAuth = FirebaseAuth.getInstance();
                if (objectFirebaseAuth.getCurrentUser().getEmail().equals(model.getUseremail()))
                {
                    FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();
                    objectFirebaseFirestore.collection("ImageStatus")
                            .document(getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(imageStatusViewHolderClass.deleteImageStatusIV.getContext(), R.string.status_deleted, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(imageStatusViewHolderClass.deleteImageStatusIV.getContext(), R.string.failed_to_delete_status, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(imageStatusViewHolderClass.deleteImageStatusIV.getContext(), R.string.cant_delete_this_status, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageStatusViewHolderClass.favouriteImageStatusIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String documentId = getSnapshots().getSnapshot(imageStatusViewHolderClass.getAdapterPosition()).getId();
                FirebaseFirestore objectFirebaseFirestore = FirebaseFirestore.getInstance();

                DocumentReference objectDocumentReference = objectFirebaseFirestore.collection("ImageStatus")
                        .document(documentId);

                objectDocumentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String emailOfUser = documentSnapshot.getString("useremail");
                                String statusImageURL = documentSnapshot.getString("statusimageurl");
                                String status = documentSnapshot.getString("status");
                                String profileURLofUser = documentSnapshot.getString("profileurl");
                                String dateOfStatus = documentSnapshot.getString("currentdatetime");

                                FirebaseAuth objectFirebaseAuth = FirebaseAuth.getInstance();

                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("useremail", emailOfUser);
                                objectMap.put("statusimageurl", statusImageURL);
                                objectMap.put("status", status);
                                objectMap.put("profileurl", profileURLofUser);
                                objectMap.put("currentdatetime", dateOfStatus);

                                if (objectFirebaseAuth!=null){
                                    String currentUserEmail = objectFirebaseAuth.getCurrentUser().getEmail();
                                    objectFirebaseFirestore.collection("UserFavorite")
                                            .document(currentUserEmail).collection("FavouriteImageStatus")
                                            .document(documentId).set(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(imageStatusViewHolderClass.favouriteImageStatusIV.getContext(),R.string.status_added_to_favorites,Toast.LENGTH_LONG).show();
                                            objectAddNotifications.generateNotification(emailOfUser,"favorite","image status",model.getUseremail());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(imageStatusViewHolderClass.favouriteImageStatusIV.getContext(),R.string.failed_to_add_status_to_favorites,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(imageStatusViewHolderClass.favouriteImageStatusIV.getContext(), R.string.no_user_online, Toast.LENGTH_LONG ).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public ImageStatusViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageStatusViewHolderClass(LayoutInflater.from(parent.getContext()).inflate(R.layout.model_image_status, parent, false));
    }

    public static class ImageStatusViewHolderClass extends RecyclerView.ViewHolder {
        ImageView imageStatusIV;
        TextView userEmailTV, statusDateTV, imageStatusDescriptionTV, heartCountTV, sadCountTV, hahaCountTV, noOfComments;

        ImageView favouriteImageStatusIV, deleteImageStatusIV, userProfileIV;
        ImageView heartIV, hahaIV, sadIV, commentIV;

        ProgressBar objectProgressBar;

        public ImageStatusViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageStatusIV = itemView.findViewById(R.id.model_image_status_imageStatusIV);
            userEmailTV = itemView.findViewById(R.id.model_image_status_userEmailTV);
            statusDateTV = itemView.findViewById(R.id.model_image_status_dateOfStatusTV);
            imageStatusDescriptionTV = itemView.findViewById(R.id.model_image_status_imageStatusDesTV);
            heartCountTV = itemView.findViewById(R.id.model_image_status_heartCountTV);
            sadCountTV = itemView.findViewById(R.id.model_image_status_sadCountTV);
            hahaCountTV = itemView.findViewById(R.id.model_image_status_hahaCountTV);
            noOfComments = itemView.findViewById(R.id.model_image_status_commentCountTV);

            favouriteImageStatusIV = itemView.findViewById(R.id.model_image_status_favouriteImageStatusIV);
            deleteImageStatusIV = itemView.findViewById(R.id.model_image_status_deleteIV);
            userProfileIV = itemView.findViewById(R.id.model_image_status_profilePicIV);
            hahaIV = itemView.findViewById(R.id.model_image_status_hahaIV);
            heartIV = itemView.findViewById(R.id.model_image_status_heartIV);
            sadIV = itemView.findViewById(R.id.model_image_status_sadIV);
            commentIV = itemView.findViewById(R.id.model_image_status_commentIV);

            objectProgressBar = itemView.findViewById(R.id.model_image_status_progressBar);

        }
    }
}
