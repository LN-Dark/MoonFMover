package com.lua.fmover;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.os.FileUtils;
import android.preference.Preference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainActivity extends AppCompatActivity {
    private DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private TextInputEditText origem;
    private TextInputEditText destino;
    private Switch apagarOrigem, guardarLocalizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        setSupportActionBar(toolbar);
        MaterialButton moveBTN = findViewById(R.id.material_unelevated_button);
        apagarOrigem = findViewById(R.id.apagarorigem);
        guardarLocalizacao = findViewById(R.id.guardarLocalizacoes);
        ImageView doarPaypal = findViewById(R.id.paypal);
        doarPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getString(R.string.doar));
                final TextView textoshare = new TextView(MainActivity.this);
                textoshare.setText(getString(R.string.muitoobrigado));
                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textoshare.setTextSize(15);
                layout.addView(textoshare);
                final TextView espaco4 = new TextView(MainActivity.this);
                espaco4.setText(" ");
                layout.addView(espaco4);
                builder3.setCancelable(false);
                builder3.setView(layout);
                AlertDialog alert = builder3.create();
                final AlertDialog finalAlert = alert;
                builder3.setPositiveButton(getString(R.string.doar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/pedrocruz77"));
                        startActivity(browserIntent);
                    }
                });
                builder3.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalAlert.dismiss();
                    }
                });
                alert = builder3.create();
                alert.show();
            }
        });
         origem = findViewById(R.id.origem);
         destino = findViewById(R.id.destino);
        SharedPreferences prefslocal = this.getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
        String perfslocalResult = prefslocal.getString("LFMOVER_LOCALIZACAOPREF", " ");
        if(perfslocalResult.equals("true")){
            guardarLocalizacao.setChecked(true);
            SharedPreferences prefs = this.getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
            String origemSaved = prefs.getString("LFMOVER_ORIGEM", " ");
            if(!origemSaved.equals(" ")){
                origem.setText(origemSaved);
            }
            SharedPreferences prefs2 = this.getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
            String destinoSaved = prefs2.getString("LFMOVER_DESTINO", " ");
            if(!destinoSaved.equals(" ")){
                destino.setText(destinoSaved);
            }
        }

        origem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();
                chooser.show();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        origem.setText(path);
                        SharedPreferences prefs = getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("LFMOVER_ORIGEM", path);
                        editor.apply();
                    }
                });
            }
        });

        destino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();
                chooser.show();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        destino.setText(path);
                        SharedPreferences prefs = getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("LFMOVER_DESTINO", path);
                        editor.apply();
                    }
                });
            }
        });

        ImageView image_telegram = findViewById(R.id.telegram);
        image_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/LN_DarK"));
                startActivity(browserIntent);
            }
        });
        moveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.requireNonNull(origem.getText()).toString().equals("")){
                    if(!Objects.requireNonNull(destino.getText()).toString().equals("")){
                            final View view1 = findViewById(android.R.id.content);
                        final AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                        builder3.setTitle(getString(R.string.acopiar));
                        final TextView textoshare = new TextView(MainActivity.this);
                        textoshare.setText(getString(R.string.porfavoraguarde));
                        textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textoshare.setTextSize(15);
                        layout.addView(textoshare);
                        final TextView espaco4 = new TextView(MainActivity.this);
                        espaco4.setText(" ");
                        layout.addView(espaco4);
                        builder3.setCancelable(false);
                        builder3.setView(layout);
                        AlertDialog alert = builder3.create();
                        final AlertDialog finalAlert = alert;
                        builder3.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(apagarOrigem.isChecked()){
                                    copyFileOrDirectory(origem.getText().toString(), destino.getText().toString(), view1, true, finalAlert);
                                }else {
                                    copyFileOrDirectory(origem.getText().toString(), destino.getText().toString(), view1, false, finalAlert);
                                }
                            }
                        });
                        builder3.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalAlert.dismiss();
                            }
                        });
                        alert = builder3.create();
                        alert.show();
                    }
                }
            }
        });
        guardarLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(guardarLocalizacao.isChecked()){
                    SharedPreferences prefs = getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("LFMOVER_LOCALIZACAOPREF", "true");
                    editor.apply();
                }else {
                    SharedPreferences prefs = getSharedPreferences("LFMOVER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("LFMOVER_LOCALIZACAOPREF", "false");
                    editor.apply();
                }
            }
        });
        getPermissions();
    }

    private void getPermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir, View context, Boolean apagarOrig, AlertDialog alert) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String[] files = src.list();
                int filesLength = 0;
                if (files != null) {
                    filesLength = files.length;
                }
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1, context, apagarOrig, alert);
                    if(i < filesLength){
                        alert.dismiss();
                    }
                }
            } else {
                copyFile(src, dst, context, apagarOrig, alert);
            }
        } catch (Exception e) {
            Snackbar.make(context, context.getResources().getString(R.string.erro) + " " + e.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public static void copyFile(File sourceFile, File destFile, View context, Boolean apagarOrig, AlertDialog alert) throws IOException {
        if (!Objects.requireNonNull(destFile.getParentFile()).exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            alert.dismiss();
            Snackbar.make(context, context.getResources().getString(R.string.erro) + " " + e.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } finally {

            Snackbar.make(context, context.getResources().getString(R.string.copiacompleta), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            if (apagarOrig) {
                sourceFile.delete();
            }
        }
    }

}
