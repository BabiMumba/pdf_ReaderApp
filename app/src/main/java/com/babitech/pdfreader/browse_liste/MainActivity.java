package com.babitech.pdfreader.browse_liste;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.babitech.pdfreader.R;

import java.io.File;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    String path;
    ArrayList<String> pather = new ArrayList<>();
    Stack<String> stack = new Stack<>();
    TextView _pathStack;
    HorizontalScrollView _pathScroll;
    public static ArrayList<Model> theList = new ArrayList<>();
    String root = (Environment.getExternalStorageDirectory().getPath());
    static viewAdapter adapter;
    final int MY_PERMISSIONS_REQUEST = 2203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        ListView listView = findViewById(R.id.local_listview);
        _pathStack = findViewById(R.id.pathStack);
        _pathScroll = findViewById(R.id.pathScroll);
        adapter = new viewAdapter(this, theList);
        listView.setAdapter(adapter);

        //Request Storage permission at the beginning of the app.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //If permission is not granted request it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
        } else {
            //If permission was granted list folders.
            if (stack.toString().equals("[]")) {
                path = root + "/";
                listFolder(new File(path));
            }
            updatePathStack(_pathScroll, _pathStack, path, root, "", pather);
        }

    }

    //On permission request result if permission was granted list folders.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (stack.toString().equals("[]")) {
                path = root + "/";
                listFolder(new File(path));
            }
            updatePathStack(_pathScroll, _pathStack, path, root, "", pather);
        }
    }


    //Define a model for files and folders
    static class Model {
        String name, size, perm, date;
        Drawable icon;

        Model(String _name, String _size, String _perm, String _date, Drawable _icon) {
            this.name = _name;
            this.size = _size;
            this.perm = _perm;
            this.date = _date;
            this.icon = _icon;
        }
    }

    //Define an adapter for each file and folder.
    public class viewAdapter extends ArrayAdapter<Model> {

        public viewAdapter(Context context, ArrayList<Model> model) {
            super(context, 0, model);
        }

        public View getView(final int position, View theView, ViewGroup parent) {
            final Model model = getItem(position);
            if (theView == null) {
                theView = LayoutInflater.from(getContext()).inflate(R.layout._local_files_row, parent, false);
            }

            final TextView name = theView.findViewById(R.id.displayName);
            final TextView perm = theView.findViewById(R.id.permission);
            final TextView date = theView.findViewById(R.id.timeText);
            final TextView size = theView.findViewById(R.id.size);
            final ImageView icon = theView.findViewById(R.id.local_image);

            name.setText(model.name);
            size.setText(model.size);
            date.setText(model.date);
            perm.setText(model.perm);
            icon.setImageDrawable(model.icon);

            final File ff = new File(path + "/" + model.name);
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ff.isFile()) {
                        Toast.makeText(MainActivity.this, "This is a file.", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.clear();
                        stack.push(model.name);
                        path = root + stack.toString().replace("[", "/").replace("]", "")
                                .replace(", ", "/").replace("/..", "") + "/";
                        listFolder(new File(path));
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            if (model.name.equals("..")) {
                theView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _getBack();
                    }
                });
            }

            return theView;
        }
    }

    //Return to parent directory
    public void _getBack() {
        adapter.clear();
        if (!(stack.size() == 0))
            stack.remove(stack.size() - 1);
        path = root + stack.toString().replace("[", "/").replace("]", "")
                .replace(", ", "/") + "/";
        pather.remove(_pathStack.getText().toString());
        if (pather.toString().equals("[]"))
            pather.add("/");
        listFolder(new File(path));
        adapter.notifyDataSetChanged();
    }

    //Updates the Path in HorizontalScrollView
    static void updatePathStack(final HorizontalScrollView _pathScroll, TextView _pathStack, String path, String root, String files, ArrayList list) {
        _pathStack.setText(path.replace(root, "").replace("//", "/").replace("/", " / ") + files);
        int index = list.indexOf(_pathStack.getText().toString());
        if (String.valueOf(index).equals("-1")) {
            list.add(_pathStack.getText().toString());
        }
        _pathScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                //This will automatically scroll to the end of the ScrollView
                _pathScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
    }

    //List folders first and then files.
    void listFolder(File _directory) {
        Drawable drawable = getResources().getDrawable(R.drawable.afolder);
        ;
        Drawable drawable1;
        Model content;
        File[] fileList = _directory.listFiles();

        if (fileList != null) {
            content = new Model("..", "", "", "", drawable);
            adapter.add(content);
            //This allows you to sort the file list alphabetic;
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    String name1 = (file.getName().toLowerCase());
                    String name2 = (t1.getName().toLowerCase());
                    return name1.compareTo(name2);
                }
            });

            //This will sort by Size
            /*
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long res = o1.length() - o2.length();
                    if (res > 0) return -1;
                    else if (res < 0) return 1;
                    return 0;
                }

            });

            //This will sort by Date;
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long res = o1.lastModified() - o2.lastModified();
                    if (res > 0) return -1;
                    else if (res < 0) return 1;
                    return 0;
                }
            });

            //This will sort by file type;
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    String name1 = (o1.getName().toLowerCase());
                    String name2 = (o2.getName().toLowerCase());
                    return name1.compareTo(name2);
                }
            });

             */

            for (File file : fileList) {
                Date date = new Date(file.lastModified());
                DateFormat formatter = new SimpleDateFormat("d.M.yyyy HH:mm a");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateFormatted = formatter.format(date);
                String fileName = file.getName();
                String perm = "d";
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        perm = perm + "r";
                    }
                    if (file.canWrite()) {
                        perm = perm + "w";
                    }
                    if (file.canExecute()) {
                        perm = perm + "x";
                    }
                    if (file.getName().startsWith(".")) {
                        //File is hidden
                    }
                    content = new Model(fileName, "", perm, dateFormatted, drawable);
                    adapter.add(content);

                }
            }
            for (File file : fileList) {
                if (file.isFile()) {
                    String theFile = file.getName();
                    Date date = new Date(file.lastModified());
                    DateFormat formatter = new SimpleDateFormat("d.M.yyyy HH:mm a");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String dateFormatted = formatter.format(date);
                    String size = String.valueOf(file.length());
                    String perm = null;
                    if (file.canRead()) {
                        perm = "-r";
                    }
                    if (file.canWrite()) {
                        perm = perm + "w";
                    }
                    if (file.canExecute() || !file.canExecute()) {
                        perm = perm + "-";
                    }

                if (file.getName().endsWith(".pdf")) {
                    //This will change icon to MP3 file icon. You need to create and move an icon called mp3 to the drawable folder.
                    drawable1 = getResources().getDrawable(R.drawable.pdf_file);
                    Model content1 = new Model(theFile, longByte(Integer.parseInt(size)),
                            perm, dateFormatted, drawable1);
                    adapter.add(content1);
                } else if (file.getName().endsWith(".mp4")) {
                    drawable1 = getResources().getDrawable(R.drawable.ic_baseline_music_note_24);
                    Model content1 = new Model(theFile, longByte(Integer.parseInt(size)),
                            perm, dateFormatted, drawable1);
                    adapter.add(content1);
                }

                   // drawable1 = getResources().getDrawable(R.drawable.afile);


                }
            }
        }
    }

    //This will conver Long size to human readable size in Byte, KB, MB, GB ...
    static String longByte(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

}