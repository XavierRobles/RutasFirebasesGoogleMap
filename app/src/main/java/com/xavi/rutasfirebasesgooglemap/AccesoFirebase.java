package com.xavi.rutasfirebasesgooglemap;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccesoFirebase {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance("https://rutasfirebasesmap-default-rtdb.europe-west1.firebasedatabase.app/");

    public static void grabarRuta(Ruta r) {

        DatabaseReference myRef = database.getReference("Rutas");
        myRef.push().setValue(r);

    }

    public static void pedirRutasFirebase(iRecuperarDatos callback) {
        DatabaseReference myRef = database.getReference("Rutas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ruta> rutas = new ArrayList<>();
                Iterable<DataSnapshot> datos = snapshot.getChildren();
                while (datos.iterator().hasNext()) {
                    DataSnapshot d = datos.iterator().next();
                    Ruta r = d.getValue(Ruta.class);
                    rutas.add(r);
                    Log.d("RUTA", r.toString());
                }
                callback.recuperarRutas(rutas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface iRecuperarDatos {
        public void recuperarRutas(ArrayList<Ruta> lista_rutas);
    }
}
