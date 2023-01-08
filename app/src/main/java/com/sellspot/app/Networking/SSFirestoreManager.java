package com.sellspot.app.Networking;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCartHandler;
import com.sellspot.app.Handlers.SSCartProductHandler;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSFollowersHandler;
import com.sellspot.app.Handlers.SSReviewsHandler;
import com.sellspot.app.Handlers.SSFollowingsHandler;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Handlers.SSUsersHandler;
import com.sellspot.app.Handlers.SSPostHandler;
import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Models.Review;
import com.sellspot.app.Models.User;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.HomeLastProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSFirestoreManager {
    public static SSFirestoreManager firestoreHelper = new SSFirestoreManager();

    public void checkUserNameExistence(String username, final SSFirebaseHandler handler) {
        Query queryRef =  SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).whereEqualTo(SSFirestoreConstants.username, username);
        queryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    handler.completionHandler(false, "Username already exists.");
                } else {
                    handler.completionHandler(true, "");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void addUserInformation(User user, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.users+"/"+user.getUserid();
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, e.getMessage());
            }
        });
    }

    public void getUser(String userid, Context context, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.users+"/"+userid;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                CurrentUser.shared.setCurrentUser(user);
                CurrentUser.shared.saveCurrentUser(context, user);
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, e.getMessage());
            }
        });
    }

    public void getProductById(String userid,String productId ,final SSPostHandler handler) {
        String documentID = SSFirestoreConstants.products+"/"+userid+"/"+SSFirestoreConstants.userproducts+"/"+ productId;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                handler.completionHandler(true, product, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false,null, e.getMessage());
            }
        });
    }

    public void setUserProfileImage(String image) {
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).document(CurrentUser.shared.getCurrentUser().getUserid());
        docRef.update(SSFirestoreConstants.image, image).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void setProductImage(List<String> images, String productID) {
        String documentID = SSFirestoreConstants.products+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts).document(productID);

        docRef.update(SSFirestoreConstants.images, images).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void checkIfItemInCart(Product product, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners+"/"+product.getUserid()+"/"+SSFirestoreConstants.product_ids+"/"+product.getProductid();
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    handler.completionHandler(true, "");
                } else {
                    handler.completionHandler(false, "");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later use
            }
        });

    }

    public void getItemsInCart(final SSCartHandler handler) {
        String collection = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collection(collection);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> ids = new ArrayList<>();
                for(int i =0; i< queryDocumentSnapshots.getDocuments().size();i++) {
                    ids.add( queryDocumentSnapshots.getDocuments().get(i).getId());
                }
                handler.completionHandler(true, ids, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              handler.completionHandler(false, null, SSConstants.processingError);
            }
        });
    }

    public void getCartPerStore(String id, final SSCartProductHandler handler) {
        String collection = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners+"/"+id+"/"+SSFirestoreConstants.product_ids;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collection(collection);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<CartPerStore> cps = queryDocumentSnapshots.toObjects(CartPerStore.class);
                handler.completionHandler(true, cps, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void deleteCartProductsObject(String id, final List <String> productIds, final SSFirebaseHandler handler) {
        for(int i = 0; i<productIds.size();i++) {
            final int index=i;
            String document = SSFirestoreConstants.cart_per_store + "/" + CurrentUser.shared.getCurrentUser().getUserid() + "/"
                    + SSFirestoreConstants.product_owners + "/" + id + "/" + SSFirestoreConstants.product_ids+ "/" +productIds.get(i);
            DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(document);
            docRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(index== productIds.size()-1) {
                                handler.completionHandler(true, "");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handler.completionHandler(false, SSConstants.processingError);
                        }
                    });
            String document1 = SSFirestoreConstants.products+"/"+id + "/" +SSFirestoreConstants.userproducts+"/"+productIds.get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);

            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.placed).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
    }

    public void crossCartProductsObject(String id, final List <String> productIds, final SSFirebaseHandler handler) {
        for(int i = 0; i<productIds.size();i++) {
            final int index=i;
            String document = SSFirestoreConstants.cart_per_store + "/" + CurrentUser.shared.getCurrentUser().getUserid() + "/"
                    + SSFirestoreConstants.product_owners + "/" + id + "/" + SSFirestoreConstants.product_ids+ "/" +productIds.get(i);
            DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(document);
            docRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(index== productIds.size()-1) {
                                handler.completionHandler(true, "");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handler.completionHandler(false, SSConstants.processingError);
                        }
                    });
        }
    }

    public void deleteCartObject(String id, final SSFirebaseHandler handler) {
        String doc= SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners+"/"+id;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(doc);

        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.completionHandler(true,"");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.completionHandler(false, SSConstants.processingError);
                    }
                });

    }

    public void addOrder(Order order, final SSFirebaseHandler handler) {
        String collectionId = SSFirestoreConstants.orders+"/";
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(collectionId).document();
        String orderId = docRef.getId();
        order.setOrderId(orderId);
        docRef.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void addProductToCart(CartPerStore cps, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        documentExistsCheck(documentID);
        documentID = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners+"/"+cps.getUserid();
        documentExistsCheck(documentID);
        documentID = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.product_owners+"/"+cps.getUserid()+"/"+SSFirestoreConstants.product_ids+"/"+cps.getProductid();
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.set(cps).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void uploadGuestUserCart(final SSFirebaseHandler handler)
    {
        final List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
        String documentID = SSFirestoreConstants.cart_per_store+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        documentExistsCheck(documentID);
        if(cartsArray != null) {
            for (int i = 0; i < cartsArray.size(); i++) {
                if (cartsArray.get(i).size() > 0) {
                    if (!cartsArray.get(i).get(0).getUserid().equals(CurrentUser.shared.getCurrentUser().getUserid())) {
                        documentID = SSFirestoreConstants.cart_per_store + "/" + CurrentUser.shared.getCurrentUser().getUserid() + "/"
                                + SSFirestoreConstants.product_owners + "/" + cartsArray.get(i).get(0).getUserid();
                        documentExistsCheck(documentID);
                        for (int j = 0; j < cartsArray.get(i).size(); j++) {
                            documentID = SSFirestoreConstants.cart_per_store + "/" + CurrentUser.shared.getCurrentUser().getUserid() + "/"
                                    + SSFirestoreConstants.product_owners + "/" + cartsArray.get(i).get(j).getUserid() + "/"
                                    + SSFirestoreConstants.product_ids + "/" + cartsArray.get(i).get(j).getProductid();
                            DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
                            final int finalI = i;
                            final int finalJ = j;
                            docRef.set(cartsArray.get(i).get(j)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (finalI == cartsArray.size() - 1 && finalJ == cartsArray.get(finalI).size() - 1) {
                                        handler.completionHandler(true, "");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    handler.completionHandler(false, SSConstants.processingError);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public void addNewProduct(Product product, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.products+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        documentExistsCheck(documentID);
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts).document();
        String productid = docRef.getId();
        product.setProductid(productid);
        docRef.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void getUserProfile(String userid, final SSUserHandler handler) {
        String documentID = SSFirestoreConstants.users+"/"+userid;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                handler.completionHandler(true, user, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false,null, e.getMessage());
            }
        });
    }

    public void getFollowStatus(String userid, final SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.following+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"+SSFirestoreConstants.followingids+"/"+userid;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    handler.completionHandler(true, "");
                } else {
                    handler.completionHandler(false, "");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later use
            }
        });
    }

    public void followUser(String userid, String username, final SSFirebaseHandler handler) {
        String documentIDFollowing = SSFirestoreConstants.following+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"+SSFirestoreConstants.followingids+"/"+userid;
        documentExistsCheck( SSFirestoreConstants.following+"/"+CurrentUser.shared.getCurrentUser().getUserid());
        String documentIDFollower = SSFirestoreConstants.followers+"/"+userid+"/"+SSFirestoreConstants.followerids+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        documentExistsCheck( SSFirestoreConstants.followers+"/"+userid);
        DocumentReference docRefFollow = SSFirebaseManager.firestoreDatabase.document(documentIDFollowing);
        final DocumentReference docRefFollower = SSFirebaseManager.firestoreDatabase.document(documentIDFollower);
        FollowerAndFollowing following = new FollowerAndFollowing(userid, username);
        final FollowerAndFollowing follower = new FollowerAndFollowing(CurrentUser.shared.getCurrentUser().getUserid(), CurrentUser.shared.getCurrentUser().getUsername());

        docRefFollow.set(following).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                docRefFollower.set(follower).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.completionHandler(true, "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.completionHandler(false, SSConstants.processingError);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void unfollowUser(String userid, final SSFirebaseHandler handler) {
        String documentIDFollowing = SSFirestoreConstants.following+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"+SSFirestoreConstants.followingids+"/"+userid;
        String documentIDFollower = SSFirestoreConstants.followers+"/"+userid+"/"+SSFirestoreConstants.followerids+"/"+CurrentUser.shared.getCurrentUser().getUserid();
        DocumentReference docRefFollow = SSFirebaseManager.firestoreDatabase.document(documentIDFollowing);
        final DocumentReference docRefFollower = SSFirebaseManager.firestoreDatabase.document(documentIDFollower);

        docRefFollow.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                docRefFollower.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.completionHandler(true, "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.completionHandler(false, SSConstants.processingError);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void getUserProducts(String userid, final SSPostsHandler handler) {
        String documentID = SSFirestoreConstants.products+"/"+userid;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts).orderBy("time", Query.Direction.DESCENDING);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });
    }

    public void getSoldOutUserProducts(String userid ,  List <String> productIds , final SSPostsHandler handler) {
        String documentID = SSFirestoreConstants.products+"/"+userid;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts)
                .whereIn("productid", productIds).whereNotEqualTo(SSFirestoreConstants.status, SSFirestoreConstants.forsale);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });
    }

    public void deleteSoldOutProductFromCart(String userid, final List <String> productIds, final SSFirebaseHandler handler) {
        final Integer[] j = {0};
        for(int i = 0; i<productIds.size();i++) {
            String document = SSFirestoreConstants.cart_per_store + "/" + CurrentUser.shared.getCurrentUser().getUserid() + "/"
                    + SSFirestoreConstants.product_owners + "/" + userid + "/" + SSFirestoreConstants.product_ids+ "/" +productIds.get(i);
            DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(document);
            docRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            j[0] += 1;
                            if (j[0] == productIds.size()) {
                                handler.completionHandler(true, "");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            handler.completionHandler(false, SSConstants.processingError);
                        }
                    });

        }
    }

    public void getUserReviews(String userid,final SSReviewsHandler handler) {
        String documentID = SSFirestoreConstants.reviews+"/"+userid;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.reviewers);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Review> reviews = queryDocumentSnapshots.toObjects(Review.class);
                handler.completionHandler(true, reviews, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });
    }

    public void getUserFollowers(String userid, final SSFollowersHandler handler) {
        String documentID = SSFirestoreConstants.followers+"/"+userid;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.followerids);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<FollowerAndFollowing> followers = queryDocumentSnapshots.toObjects(FollowerAndFollowing.class);
                handler.completionHandler(true, followers, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void getUserFollowings(String userid, final SSFollowingsHandler handler) {
        String documentID = SSFirestoreConstants.following+"/"+userid;
        Query collectionRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.followingids);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<FollowerAndFollowing> followings = queryDocumentSnapshots.toObjects(FollowerAndFollowing.class);
                handler.completionHandler(true, followings, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void searchUser(String searchString, final SSUsersHandler handler) {
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users)
                .whereGreaterThanOrEqualTo(SSFirestoreConstants.username,searchString).whereLessThan(SSFirestoreConstants.username,searchString+"z");
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> users = queryDocumentSnapshots.toObjects(User.class);
                handler.completionHandler(true, users, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });
    }

    public void searchProductByMaterial(String searchString, final SSPostsHandler handler) {
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                .whereGreaterThanOrEqualTo(SSFirestoreConstants.material,searchString).whereLessThan(SSFirestoreConstants.material,searchString+"z");
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void searchProductBySize(String searchString, final SSPostsHandler handler) {
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                .whereGreaterThanOrEqualTo(SSFirestoreConstants.size,searchString).whereLessThan(SSFirestoreConstants.size,searchString+"z");
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void searchProductByTags(String searchString, final SSPostsHandler handler) {
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                .whereArrayContains(SSFirestoreConstants.tags,searchString);
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null, SSConstants.processingError);
            }
        });

    }

    public void deleteProduct(final String productId, final SSFirebaseHandler handler)
    {
        String doc= SSFirestoreConstants.products+"/"+CurrentUser.shared.getCurrentUser().getUserid()+"/"
                +SSFirestoreConstants.userproducts+"/"+productId;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(doc);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.product_ids)
                        .whereEqualTo(SSFirestoreConstants.productid, productId);
                collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                doc.getReference().delete();
                            }
                        }
                        handler.completionHandler(true, "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.completionHandler(false,e.getLocalizedMessage());
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void updateUserPassword(String password) {
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).document(CurrentUser.shared.getCurrentUser().getUserid());
        docRef.update(SSFirestoreConstants.password, password).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void updateUserEmail(String email) {
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).document(CurrentUser.shared.getCurrentUser().getUserid());
        docRef.update(SSFirestoreConstants.email, email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void updateProductShareableLink(String productID, String userID, String link) {
        String documentID = SSFirestoreConstants.products+"/"+userID;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts).document(productID);

        docRef.update(SSFirestoreConstants.shareablelink, link).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void createAndSaveDynamicLink(String productID, Product p, Uri imageURL) {
        String title = "Hey! Check out this Product on Sell Spot for RS " + Integer.toString(p.getPrice());
        SSFirebaseDynamicLinksManager.shared.createDynamicLink(productID, p.getUserid(), title, p.getDescription(), imageURL, new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    SSFirestoreManager.firestoreHelper.updateProductShareableLink(productID, p.getUserid(), error);
                }
            }
        });
    }

    public void updateUserPhone(String phone) {
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).document(CurrentUser.shared.getCurrentUser().getUserid());
        docRef.update(SSFirestoreConstants.phonenumber, phone).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    public void updateUserAddress(String address) {
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).document(CurrentUser.shared.getCurrentUser().getUserid());
        docRef.update(SSFirestoreConstants.address, address).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // for later user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // for later user
            }
        });
    }

    private void documentExistsCheck(final String documentID) {
        DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(documentID);
        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Document exists");
                    } else {
                        DocumentReference docRef2 = SSFirebaseManager.firestoreDatabase.document(documentID);
                        Map<String ,Object> dummyMap= new HashMap<>();
                        docRef2.set(dummyMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("COMING IN SUCCESS");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("COMIN IN FAIL");
                            }
                        });
                        //if document does not exist then make one
                    }
                } else {
                    System.out.println(task.getException());
                }
            }
        });
    }

    public void getHomeScreenProducts(final SSPostsHandler handler) {
        Query collectionRef;
        if(HomeLastProduct.shared.getHomeLastProduct() != null) {
             collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                    .orderBy(SSFirestoreConstants.time, Query.Direction.DESCENDING)
                    .whereEqualTo(SSFirestoreConstants.status, SSFirestoreConstants.forsale).startAfter(HomeLastProduct.shared.getHomeLastProduct()).limit(16);
        }
        else{
             collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                    .orderBy(SSFirestoreConstants.time, Query.Direction.DESCENDING)
                    .whereEqualTo(SSFirestoreConstants.status, SSFirestoreConstants.forsale).limit(16);
        }

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0)
                {
                    HomeLastProduct.shared.setHomeLastProduct(queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1));
                }
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                handler.completionHandler(true, products, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, null,e.getLocalizedMessage());
            }
        });

    }

    public void updateProduct(Product product, SSFirebaseHandler handler) {
        String documentID = SSFirestoreConstants.products+"/"+product.getUserid();
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(documentID).collection(SSFirestoreConstants.userproducts).document(product.getProductid());
        docRef.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
        updateCartItems(product);
    }

    public void updateCartItems(Product product) {
        Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.product_ids)
                .whereEqualTo(SSFirestoreConstants.productid, product.getProductid());
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() > 0) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(SSFirestoreConstants.size, product.getSize());
                        map.put(SSFirestoreConstants.price, product.getPrice());
                        doc.getReference().update(map);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
